package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.CleanerActivity;
import com.module.project.dto.CleanerReviewInfo;
import com.module.project.dto.Constant;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.dto.request.ScheduleConfirmRequest;
import com.module.project.dto.response.BookingDetailResponse;
import com.module.project.dto.response.CleanerDetailHistoryResponse;
import com.module.project.dto.response.CleanerHistoryResponse;
import com.module.project.dto.response.CleanerOverviewResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.Branch;
import com.module.project.model.Cleaner;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BranchRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanerService {

    private final CleanerRepository cleanerRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final ScheduleService scheduleService;
    private final BookingService bookingService;

    public HmsResponse<List<Cleaner>> getCleaners(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, cleanerRepository.findAll(pageable).getContent());
    }

    // public HmsResponse<List<CleanerOverviewResponse>> getCleanerHistory(Integer page, Integer size) {
    //     List<CleanerOverviewResponse> response = new ArrayList<>();
    //     Pageable pageable = PageRequest.of(page, size);
    //     List<Cleaner> cleaners = cleanerRepository.findAll(pageable).getContent();
    //     for (Cleaner cleaner : cleaners) {
    //         Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
    //         });
    //         if (reviewList == null) {
    //             continue;
    //         }
    //         double sumRating = 0;
    //         int ratingNumber = 0;
    //         CleanerOverviewResponse history = CleanerOverviewResponse.builder()
    //                 .cleanerId(cleaner.getId())
    //                 .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
    //                 .idCard(cleaner.getIdCard())
    //                 .email(cleaner.getUser().getEmail())
    //                 .phoneNumber(cleaner.getUser().getPhoneNumber())
    //                 .status(cleaner.getStatus())
    //                 .branch(cleaner.getBranch())
    //                 .activityYear(HMSUtil.calculateActivityYear(cleaner.getCreateDate(), new Date()))
    //                 .build();
    //         for (Long bookingId : reviewList.keySet()) {
    //             Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
    //             if (bookingOptional.isEmpty()) {
    //                 continue;
    //             }
    //             CleanerReviewInfo cleanerReviewInfo = reviewList.get(bookingId);
    //             if (cleanerReviewInfo != null
    //                     && cleanerReviewInfo.getCleanerActivities() != null
    //                     && !cleanerReviewInfo.getCleanerActivities().isEmpty()) {
    //                 sumRating += cleanerReviewInfo.getCleanerActivities().stream().mapToDouble(CleanerActivity::getRatingScore).sum();
    //                 ratingNumber += cleanerReviewInfo.getCleanerActivities().size();
    //             }
    //         }
    //         history.setAverageRating(ratingNumber != 0 ? Math.round(sumRating / ratingNumber) : ratingNumber);
    //         history.setRatingNumber(ratingNumber);
    //         response.add(history);
    //     }
    //     return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
    // }

    public HmsResponse<CleanerDetailHistoryResponse> getCleanerHistoryDetail(Long cleanerId) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "getCleanerDetailHistory: can't find any cleaner by id: ".concat(cleanerId.toString())));
        Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
        });
        double sumRating = 0;
        int ratingNumber = 0;
        List<CleanerHistoryResponse> history = new ArrayList<>();
        if (reviewList != null) {
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
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any cleaner by id: ".concat(cleanerUpdateRequest.getId().toString())));

        Branch branch = branchRepository.findById(cleanerUpdateRequest.getBranchId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST,
                        "insertCleaner: can't find any branch with id: ".concat(cleanerUpdateRequest.getBranchId().toString())));
        Set<com.module.project.model.Service> serviceIds = new HashSet<>(
                serviceRepository.findAllById(cleanerUpdateRequest.getServiceIds()));

        cleaner.setAddress(cleanerUpdateRequest.getAddress());
        cleaner.setIdCard(cleanerUpdateRequest.getIdCard());
        cleaner.setBranch(branch);
        cleaner.setStatus(Constant.COMMON_STATUS.ACTIVE.equals(cleanerUpdateRequest.getStatus())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE);
        cleaner.setServices(serviceIds);

        return HMSUtil.buildResponse(ResponseCode.SUCCESS, cleanerRepository.save(cleaner));
    }

    public HmsResponse<Cleaner> changeStatusCleaner(CleanerUpdateRequest cleanerUpdateRequest) {
        Cleaner cleaner = cleanerRepository.findById(cleanerUpdateRequest.getId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any cleaner by id: ".concat(cleanerUpdateRequest.getId().toString())));
        String status = Constant.COMMON_STATUS.ACTIVE.equalsIgnoreCase(cleanerUpdateRequest.getStatus())
                ? Constant.COMMON_STATUS.ACTIVE
                : Constant.COMMON_STATUS.INACTIVE;
        cleaner.setStatus(status);

        return HMSUtil.buildResponse(ResponseCode.SUCCESS, cleanerRepository.save(cleaner));
    }

    public HmsResponse<Object> updateStatusSchedule(ScheduleConfirmRequest request, String userId) {
        scheduleService.updateStatusSchedule(request, userId);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    public HmsResponse<List<BookingDetailResponse>> getCleanerSchedule(Long cleanerId, Integer page, Integer size,
                                                                       String userId, String roleName) {
        Cleaner cleaner = cleanerRepository.findById(cleanerId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any cleaner by id ".concat(cleanerId.toString())));
        Pageable pageable = PageRequest.of(page, size);
        List<Booking> bookingList = bookingRepository.findAllByCleanersIn(Set.of(cleaner), pageable).getContent();
        List<BookingDetailResponse> responses = new ArrayList<>();
        for (Booking booking : bookingList) {
            if (!TransactionStatus.DONE.name().equals(booking.getStatus())) {
                responses.add(bookingService.getBookingDetail(booking.getId(), userId, roleName, true));
            }
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, responses);
    }
}
