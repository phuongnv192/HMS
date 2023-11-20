package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.CleanerActivity;
import com.module.project.dto.CleanerReviewInfo;
import com.module.project.dto.Constant;
import com.module.project.dto.request.ChooseCleanerRequest;
import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.dto.response.CleanerDetailHistoryResponse;
import com.module.project.dto.response.CleanerHistoryResponse;
import com.module.project.dto.response.CleanerOverviewResponse;
import com.module.project.model.Booking;
import com.module.project.model.Branch;
import com.module.project.model.Cleaner;
import com.module.project.model.User;
import com.module.project.repository.BookingHistoryRepository;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BranchRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanerService {

    private final CleanerRepository cleanerRepository;
    private final BookingHistoryRepository bookingHistoryRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BookingScheduleRepository bookingScheduleRepository;
    private final BookingRepository bookingRepository;

    private final Random random = new Random();

    public List<Cleaner> getCleaners(CleanerFilterRequest request) {
        List<Cleaner> cleaners = cleanerRepository.findAll();
        return filterCleaners(cleaners, request);
    }

    public List<Cleaner> chooseCleaner(ChooseCleanerRequest chooseCleanerRequest) {
        if (Constant.CHOOSE_TYPE.AUTO.equals(chooseCleanerRequest.getType())) {
            return autoChooseCleaner(chooseCleanerRequest);
        } else if (Constant.CHOOSE_TYPE.MANUAL.equals(chooseCleanerRequest.getType())) {
            return manualChooseCleaner();
        } else {
            throw new InternalError("type of choosing not found");
        }
    }

    private List<Cleaner> autoChooseCleaner(ChooseCleanerRequest chooseCleanerRequest) {
        int number = chooseCleanerRequest.getNumber();
        if (number <= 0 || number > 4) {
            throw new InternalError("number of cleaner with auto type is invalid");
        }
        // TODO: pick randomly in pool of available cleaners
        List<Cleaner> cleaners = filterOnlyAvailable(getCleaners(new CleanerFilterRequest()));
        List<Cleaner> res = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Cleaner cleaner = cleaners.get(random.nextInt(cleaners.size()));
            res.add(cleaner);
            cleaners.remove(cleaner);
        }
        return res;
    }

    public List<CleanerOverviewResponse> getCleanerHistory(Integer page, Integer size) {
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
                        && cleanerReviewInfo.getCleanerActivities().size() != 0) {
                    sumRating += cleanerReviewInfo.getCleanerActivities().stream().mapToDouble(CleanerActivity::getRatingScore).sum();
                    ratingNumber += cleanerReviewInfo.getCleanerActivities().size();
                }
            }
            history.setAverageRating(ratingNumber != 0 ? Math.round(sumRating / ratingNumber) : ratingNumber);
            history.setRatingNumber(ratingNumber);
            response.add(history);
        }
        return response;
    }

    public CleanerDetailHistoryResponse getCleanerHistoryDetail(Long cleanerId) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId)
                .orElseThrow(() -> new InternalError("getCleanerDetailHistory: can't find any cleaner by id: ".concat(cleanerId.toString())));
        Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<Map<Long, CleanerReviewInfo>>() {
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
                    && cleanerReviewInfo.getCleanerActivities().size() != 0) {
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
        return CleanerDetailHistoryResponse.builder()
                .ratingOverview(ratingOverview)
                .history(history)
                .build();
    }

    public Cleaner insertCleaner(CleanerInfoRequest cleanerInfoRequest) {
        Branch branch = branchRepository.findById(cleanerInfoRequest.getBranchId())
                .orElseThrow(() -> new InternalError(
                        "insertCleaner: can't find any branch with id: ".concat(cleanerInfoRequest.getBranchId().toString())));
        User user = userRepository.findById(cleanerInfoRequest.getUserId())
                .orElseThrow(() -> new InternalError(
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
        return cleanerRepository.save(cleaner);
    }

<<<<<<< HEAD
=======
    public Cleaner updateCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId()).get();

        Branch branch = branchRepository.findById(cleanerUpdateRequest.getBranchId())
                .orElseThrow(() -> new InternalError(
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

        return cleanerRepository.save(cleaner);
    }

    public Cleaner changeStatusCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId()).get();
        cleaner = Cleaner.builder()
                .status(Constant.COMMON_STATUS.ACTIVE.equals(cleanerUpdateRequest.getStatus().toUpperCase())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE)
                .build();

        return cleanerRepository.save(cleaner);
    }

>>>>>>> 292f472a951892d6fc32f30271fea27170b52576
    private List<Cleaner> manualChooseCleaner() {
        return null;
    }

    private List<Cleaner> filterCleaners(List<Cleaner> cleaners, CleanerFilterRequest request) {
        // TODO: update later - filter by gender and sort by rating
<<<<<<< HEAD
//        List<BookingSchedule> bookingSchedules = bookingScheduleRepository.findAllById();
//        cleaners.sort(Comparator.comparing(Clea));
        return cleaners.stream()
                .filter(cleaner -> StringUtils.isBlank(request.getGender()) || cleaner.getUser().getGender().equalsIgnoreCase(request.getGender()))
//                .filter(cleaner -> StringUtils.isBlank(request.getAge()) || cleaner.getUser().getId() == Integer.parseInt(request.getAge()))
//                .filter(cleaner -> StringUtils.isBlank(request.getRate()) || cleaner.getId() == Integer.parseInt(request.getRate()))
=======
        // List<BookingSchedule> bookingSchedules =
        // bookingScheduleRepository.findAllById();
        // cleaners.sort(Comparator.comparing(Clea));
        return cleaners.stream()
                .filter(cleaner -> StringUtils.isBlank(request.getGender())
                        || cleaner.getUser().getGender().equalsIgnoreCase(request.getGender()))
                // .filter(cleaner -> StringUtils.isBlank(request.getAge()) ||
                // cleaner.getUser().getId() == Integer.parseInt(request.getAge()))
                // .filter(cleaner -> StringUtils.isBlank(request.getRate()) || cleaner.getId()
                // == Integer.parseInt(request.getRate()))
>>>>>>> 292f472a951892d6fc32f30271fea27170b52576
                .toList();
    }

    private List<Cleaner> filterOnlyAvailable(List<Cleaner> cleaners) {
<<<<<<< HEAD
        // TODO: check in booking schedule table that doesn't exist in with specific date
=======
        // TODO: check in booking schedule table that doesn't exist in with specific
        // date
>>>>>>> 292f472a951892d6fc32f30271fea27170b52576
        return cleaners;
    }
}
