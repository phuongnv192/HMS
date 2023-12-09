package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.CleanerActivity;
import com.module.project.dto.CleanerReviewInfo;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.Constant;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.BookingStatusRequest;
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
import com.module.project.model.BookingTransaction;
import com.module.project.model.Branch;
import com.module.project.model.Cleaner;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingTransactionRepository;
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
import java.time.ZoneId;
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
    private final MailService mailService;
    private final BookingTransactionRepository bookingTransactionRepository;

    public HmsResponse<List<CleanerOverviewResponse>> getCleaners(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Cleaner> cleaners = cleanerRepository.findAll(pageable).getContent();
        List<CleanerOverviewResponse> response = new ArrayList<>();
        for (Cleaner cleaner : cleaners) {
            response.add(getCleanerOverview(cleaner));
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
    }

//    public HmsResponse<List<CleanerOverviewResponse>> getCleanerHistory(Integer page, Integer size) {
//        List<CleanerOverviewResponse> response = new ArrayList<>();
//        Pageable pageable = PageRequest.of(page, size);
//        List<Cleaner> cleaners = cleanerRepository.findAll(pageable).getContent();
//        for (Cleaner cleaner : cleaners) {
//            Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
//            });
//            if (reviewList == null) {
//                continue;
//            }
//            double sumRating = 0;
//            int ratingNumber = 0;
//            CleanerOverviewResponse history = CleanerOverviewResponse.builder()
//                    .cleanerId(cleaner.getId())
//                    .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
//                    .idCard(cleaner.getIdCard())
//                    .email(cleaner.getUser().getEmail())
//                    .phoneNumber(cleaner.getUser().getPhoneNumber())
//                    .status(cleaner.getStatus())
//                    .branch(cleaner.getBranch())
//                    .activityYear(HMSUtil.calculateActivityYear(cleaner.getCreateDate(), new Date()))
//                    .build();
//            for (Long bookingId : reviewList.keySet()) {
//                Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
//                if (bookingOptional.isEmpty()) {
//                    continue;
//                }
//                CleanerReviewInfo cleanerReviewInfo = reviewList.get(bookingId);
//                if (cleanerReviewInfo != null
//                        && cleanerReviewInfo.getCleanerActivities() != null
//                        && !cleanerReviewInfo.getCleanerActivities().isEmpty()) {
//                    sumRating += cleanerReviewInfo.getCleanerActivities().stream().mapToDouble(CleanerActivity::getRatingScore).sum();
//                    ratingNumber += cleanerReviewInfo.getCleanerActivities().size();
//                }
//            }
//            history.setAverageRating(ratingNumber != 0 ? Math.round(sumRating / ratingNumber) : ratingNumber);
//            history.setRatingNumber(ratingNumber);
//            response.add(history);
//        }
//        return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
//    }

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
                        CleanerHistoryResponse clone = new CleanerHistoryResponse();
                        BeanUtils.copyProperties(item, clone);
                        clone.setReview(info.getReview());
                        clone.setRatingScore(info.getRatingScore());
                        clone.setWorkDate(HMSUtil.formatDate(info.getWorkDate(), HMSUtil.DDMMYYYY_FORMAT));
                        history.add(clone);
                    }
                }
            }
        }
        CleanerOverviewResponse ratingOverview = CleanerOverviewResponse.builder()
                .cleanerId(cleanerId)
                .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
                .gender(cleaner.getUser().getGender())
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
                .dob(cleanerInfoRequest.getDob())
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
        cleaner.setDob(cleanerUpdateRequest.getDob());
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

    public HmsResponse<List<CleanerOverviewResponse>> getListCleanerAvailable(LocalDate workDate, Long serviceTypeId, Long servicePackageId) {
        List<Cleaner> cleaners = scheduleService.getListCleanerAvailable(workDate, serviceTypeId, servicePackageId);
        List<CleanerOverviewResponse> response = new ArrayList<>();
        for (Cleaner cleaner : cleaners) {
            response.add(getCleanerOverview(cleaner));
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
    }

    public HmsResponse<Object> rejectBooking(BookingStatusRequest request, String userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant booking is not existed on system"));
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        Cleaner cleaner = cleanerRepository.findByUser(user)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute"));
        List<Long> cleanerIds = booking.getCleaners().stream().map(Cleaner::getId).toList();
        if (!cleanerIds.contains(cleaner.getId())) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
        BookingTransaction bookingTransaction = bookingTransactionRepository.findByBooking(booking)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "can't find any booking transaction by ".concat(booking.getId().toString())));
        List<String> status = List.of(ConfirmStatus.RECEIVED.name());
        if (!bookingService.checkBookingToBeUpdated(bookingTransaction, status)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "booking is no longer allowed to be rejected");
        }
        booking.setRejectedReason(request.getRejectedReason());
        scheduleService.cancelBooking(booking, user);
        mailService.sendMailCancelOfBookingToCustomer(booking.getUser().getEmail(), request.getRejectedReason(), booking.getHostName(),
                HMSUtil.formatDate(booking.getCreateDate(), HMSUtil.DDMMYYYYHHMMSS_FORMAT),
                HMSUtil.formatDate(new Date(), HMSUtil.DDMMYYYYHHMMSS_FORMAT));
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    private CleanerOverviewResponse getCleanerOverview(Cleaner cleaner) {
        Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
        });
        CleanerOverviewResponse history = CleanerOverviewResponse.builder()
                .cleanerId(cleaner.getId())
                .name(HMSUtil.convertToFullName(cleaner.getUser().getFirstName(), cleaner.getUser().getLastName()))
                .gender(cleaner.getUser().getGender())
                .idCard(cleaner.getIdCard())
                .address(cleaner.getAddress())
                .dob(HMSUtil.formatDate(Date.from(cleaner.getDob().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), HMSUtil.DDMMYYYY_FORMAT))
                .email(cleaner.getUser().getEmail())
                .phoneNumber(cleaner.getUser().getPhoneNumber())
                .status(cleaner.getStatus())
                .branch(cleaner.getBranch())
                .activityYear(HMSUtil.calculateActivityYear(cleaner.getCreateDate(), new Date()))
                .averageRating(0L)
                .ratingNumber(0)
                .build();
        if (reviewList != null) {
            double sumRating = 0;
            int ratingNumber = 0;

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
        }
        return history;
    }
}
