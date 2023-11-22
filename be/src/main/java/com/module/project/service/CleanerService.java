package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.CleanerActivity;
import com.module.project.dto.CleanerReviewInfo;
import com.module.project.dto.Constant;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.dto.request.ScheduleConfirmRequest;
import com.module.project.dto.response.CleanerDetailHistoryResponse;
import com.module.project.dto.response.CleanerHistoryResponse;
import com.module.project.dto.response.CleanerOverviewResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.Branch;
import com.module.project.model.Cleaner;
import com.module.project.model.CleanerWorkingDate;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BranchRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.CleanerWorkingDateRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanerService {

    private final CleanerRepository cleanerRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final CleanerWorkingDateRepository cleanerWorkingDateRepository;

    private final Random random = new Random();

    public List<Cleaner> getCleaners(CleanerFilterRequest request) {
        List<Cleaner> cleaners = cleanerRepository.findAll();
        return cleaners;
    }

    public List<Cleaner> autoChooseCleaner(int number, List<LocalDate> workDate) {
        if (number <= 0 || number > 4) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "number of cleaner is invalid");
        }
        List<Long> cleaners = new ArrayList<>(filterOnlyAvailable(number, workDate));
        if (cleaners.size() <= number) {
            return cleanerRepository.findAllById(cleaners);
        }
        List<Long> res = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Long cleaner = cleaners.get(random.nextInt(cleaners.size()));
            res.add(cleaner);
            cleaners.remove(cleaner);
        }
        return cleanerRepository.findAllById(res);
    }

    public HmsResponse<List<CleanerOverviewResponse>> getCleanerHistory(Integer page, Integer size) {
        List<CleanerOverviewResponse> response = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        List<Cleaner> cleaners = cleanerRepository.findAll(pageable).getContent();
        for (Cleaner cleaner : cleaners) {
            Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
            });
            if (reviewList == null) {
                continue;
            }
            double sumRating = 0;
            int ratingNumber = 0;
            CleanerOverviewResponse history = CleanerOverviewResponse.builder()
                    .cleanerId(cleaner.getId())
                    .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
                    .idCard(cleaner.getIdCard())
                    .email(cleaner.getUser().getEmail())
                    .phoneNumber(cleaner.getUser().getPhoneNumber())
                    .status(cleaner.getStatus())
                    .branch(cleaner.getBranch())
                    .activityYear(HMSUtil.calculateActivityYear(cleaner.getCreateDate(), new Date()))
                    .build();
            for (Long bookingId : reviewList.keySet()) {
                Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
                if (bookingOptional.isEmpty()) {
                    continue;
                }
                CleanerReviewInfo cleanerReviewInfo = reviewList.get(bookingId);
                if (cleanerReviewInfo != null
                        && cleanerReviewInfo.getCleanerActivities() != null
                        && !cleanerReviewInfo.getCleanerActivities().isEmpty()) {
                    sumRating += cleanerReviewInfo.getCleanerActivities().stream().mapToDouble(CleanerActivity::getRatingScore).sum();
                    ratingNumber += cleanerReviewInfo.getCleanerActivities().size();
                }
            }
            history.setAverageRating(ratingNumber != 0 ? Math.round(sumRating / ratingNumber) : ratingNumber);
            history.setRatingNumber(ratingNumber);
            response.add(history);
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
    }

    public HmsResponse<CleanerDetailHistoryResponse> getCleanerHistoryDetail(Long cleanerId) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "getCleanerDetailHistory: can't find any cleaner by id: ".concat(cleanerId.toString())));
        Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
        });
        if (reviewList == null) {
            return null;
        }
        double sumRating = 0;
        int ratingNumber = 0;
        List<CleanerHistoryResponse> history = new ArrayList<>();
        for (Long bookingId : reviewList.keySet()) {
            Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
            if (bookingOptional.isEmpty()) {
                continue;
            }
            CleanerReviewInfo cleanerReviewInfo = reviewList.get(bookingId);
            if (cleanerReviewInfo != null
                    && cleanerReviewInfo.getCleanerActivities() != null
                    && !cleanerReviewInfo.getCleanerActivities().isEmpty()) {
                CleanerHistoryResponse item = CleanerHistoryResponse.builder()
                        .name(bookingOptional.get().getHostName())
                        .houseType(bookingOptional.get().getHouseType())
                        .floorNumber(bookingOptional.get().getFloorNumber())
                        .floorArea(bookingOptional.get().getFloorArea())
                        .build();
                ratingNumber += cleanerReviewInfo.getCleanerActivities().size();
                for (CleanerActivity info : cleanerReviewInfo.getCleanerActivities()) {
                    sumRating += info.getRatingScore();
                    item.setReview(info.getReview());
                    item.setRatingScore(info.getRatingScore());
                    item.setWorkDate(HMSUtil.formatDate(info.getWorkDate(), HMSUtil.DDMMYYYY_FORMAT));
                    history.add(item);
                }
            }
        }
        CleanerOverviewResponse ratingOverview = CleanerOverviewResponse.builder()
                .cleanerId(cleanerId)
                .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
                .activityYear(HMSUtil.calculateActivityYear(cleaner.getCreateDate(), new Date()))
                .averageRating(ratingNumber != 0 ? Math.round(sumRating / ratingNumber) : ratingNumber)
                .ratingNumber(ratingNumber)
                .build();
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, CleanerDetailHistoryResponse.builder()
                .ratingOverview(ratingOverview)
                .history(history)
                .build());
    }

    public HmsResponse<Cleaner> insertCleaner(CleanerInfoRequest cleanerInfoRequest) {
        Branch branch = branchRepository.findById(cleanerInfoRequest.getBranchId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "insertCleaner: can't find any branch with id: ".concat(cleanerInfoRequest.getBranchId().toString())));
        User user = userRepository.findById(cleanerInfoRequest.getUserId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "insertCleaner: can't find any user with id".concat(cleanerInfoRequest.getUserId().toString())));
        Set<com.module.project.model.Service> serviceIds = new HashSet<>(
                serviceRepository.findAllById(cleanerInfoRequest.getServiceIds()));
        Cleaner cleaner = Cleaner.builder()
                .address(cleanerInfoRequest.getAddress())
                .idCard(cleanerInfoRequest.getIdCard())
                .branch(branch)
                .status(Constant.COMMON_STATUS.ACTIVE)
                .user(user)
                .services(serviceIds)
                .build();
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, cleanerRepository.save(cleaner));
    }

    public HmsResponse<Cleaner> updateCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId()).get();

        Branch branch = branchRepository.findById(cleanerUpdateRequest.getBranchId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "insertCleaner: can't find any branch with id: ".concat(cleanerUpdateRequest.getBranchId().toString())));
        Set<com.module.project.model.Service> serviceIds = new HashSet<>(
                serviceRepository.findAllById(cleanerUpdateRequest.getServiceIds()));

        cleaner = Cleaner.builder()
                .address(cleanerUpdateRequest.getAddress())
                .idCard(cleanerUpdateRequest.getIdCard())
                .branch(branch)
                .status(Constant.COMMON_STATUS.ACTIVE.equals(cleanerUpdateRequest.getStatus().toUpperCase())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE)
                .services(serviceIds)
                .build();

        return HMSUtil.buildResponse(ResponseCode.SUCCESS, cleanerRepository.save(cleaner));
    }

    public HmsResponse<Cleaner> changeStatusCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId()).get();
        cleaner = Cleaner.builder()
                .status(Constant.COMMON_STATUS.ACTIVE.equals(cleanerUpdateRequest.getStatus().toUpperCase())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE)
                .build();

        return HMSUtil.buildResponse(ResponseCode.SUCCESS, cleanerRepository.save(cleaner));
    }

    public HmsResponse<Object> confirmSchedule(ScheduleConfirmRequest request) {
        switch (request.getStatus()) {
            case ON_PROCESS, MATCHED, ON_MOVING -> {
                // TODO
            }
            case DONE -> {

            }
            default -> throw new HmsException(HmsErrorCode.INVALID_REQUEST, "status not valid");
        }
        return null;
    }

    private List<Cleaner> filterCleaners(List<Cleaner> cleaners, CleanerFilterRequest request) {
        // TODO: update later - filter by gender and sort by rating
        // List<BookingSchedule> bookingSchedules =
        // bookingScheduleRepository.findAllById();
        // cleaners.sort(Comparator.comparing(Clea));
        return cleaners.stream()
                // .filter(cleaner -> StringUtils.isBlank(request.getAge()) ||
                // cleaner.getUser().getId() == Integer.parseInt(request.getAge()))
                // .filter(cleaner -> StringUtils.isBlank(request.getRate()) || cleaner.getId()
                // == Integer.parseInt(request.getRate()))
                .toList();
    }

    private Set<Long> filterOnlyAvailable(int num, List<LocalDate> workDate) {
        List<CleanerWorkingDate> bookingSchedules = cleanerWorkingDateRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE);
        Set<Long> cleanerIds = bookingSchedules.stream().map(CleanerWorkingDate::getCleanerId).collect(Collectors.toSet());
        for (CleanerWorkingDate cleaner : bookingSchedules) {
            if (workDate.contains(cleaner.getScheduleDate())) {
                cleanerIds.remove(cleaner.getCleanerId());
                if (cleanerIds.isEmpty() || cleanerIds.size() <= num) {
                    return cleanerIds;
                }
            }
        }
        return cleanerIds;
    }
}
