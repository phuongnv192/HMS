package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.CleanerActivity;
import com.module.project.dto.CleanerReviewInfo;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingScheduleRequest;
import com.module.project.dto.request.ScheduleConfirmRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.Cleaner;
import com.module.project.model.CleanerWorkingDate;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServicePackage;
import com.module.project.model.ServiceType;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.CleanerWorkingDateRepository;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServicePackageRepository;
import com.module.project.repository.ServiceTypeRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final BookingTransactionRepository bookingTransactionRepository;
    private final BookingScheduleRepository bookingScheduleRepository;
    private final BookingRepository bookingRepository;
    private final CleanerWorkingDateRepository cleanerWorkingDateRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final CleanerRepository cleanerRepository;
    private final UserRepository userRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final MailService mailService;

    @Value("${application.choosing-cleaner-price:0}")
    private long choosingCleanerPrice;

    @Value("${application.default-rating:5}")
    private long defaultRating;

    @Transactional(rollbackFor = {Exception.class})
    public void processBookingRegular(Booking booking,
                                      BookingRequest request,
                                      BookingTransaction bookingTransaction,
                                      Long userId) {
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorArea());
        if (floorInfoEnum == null) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "error when look up floor info: ".concat(request.getFloorArea()));
        }

        List<ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByIdInAndStatus(request.getServiceAddOnIds(), Constant.COMMON_STATUS.ACTIVE);
        boolean isAutoChoosing = request.getCleanerIds() == null;
        long totalPriceAddOn = serviceAddOns.stream().mapToLong(ServiceAddOn::getPrice).sum();
        long totalPriceFloorAre = floorInfoEnum.getPrice() * request.getFloorNumber();
        long priceChoosingCleaner = isAutoChoosing ? choosingCleanerPrice : 0;
        long distancePrice = request.getDistancePrice() != null ? request.getDistancePrice() : 0;
        long totalBookingPrice = totalPriceFloorAre + priceChoosingCleaner + distancePrice;
        long totalSchedulePrice = totalPriceFloorAre + totalPriceAddOn;

        BookingSchedule bookingSchedule = BookingSchedule.builder()
                .bookingTransaction(bookingTransaction)
                .serviceAddOns(new HashSet<>(serviceAddOns))
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ConfirmStatus.RECEIVED.name())
                .updateBy(userId)
                .totalSchedulePrice(totalSchedulePrice)
                .build();
        bookingScheduleRepository.save(bookingSchedule);

        bookingTransaction.setTotalBookingPrice(totalBookingPrice);
        bookingTransaction.setTotalBookingDate(1L);
        bookingTransactionRepository.save(bookingTransaction);

        bookingTransaction.setTotalBookingCleaner(processWorkingDateForCleaner(floorInfoEnum, booking, List.of(request.getWorkDate()), request, isAutoChoosing));
        bookingTransactionRepository.save(bookingTransaction);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void processBookingPeriod(Booking booking,
                                     BookingRequest request,
                                     BookingTransaction bookingTransaction,
                                     Long userId) {
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorArea());
        if (floorInfoEnum == null) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "error when look up floor info: ".concat(request.getFloorArea()));
        }
        ServicePackage servicePackage = servicePackageRepository.findById(request.getServicePackageId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service package by ".concat(request.getServicePackageId().toString())));
        bookingTransaction.setServicePackage(servicePackage);
        Calendar startDay = Calendar.getInstance();
        startDay.setTime(Date.from(request.getWorkDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Calendar periodRange = Calendar.getInstance();
        periodRange.setTime(Date.from(request.getWorkDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        periodRange.add(Calendar.MONTH, Integer.parseInt(servicePackage.getServicePackageName()));
        switch (request.getServiceTypeId().toString()) {
            case "1" -> {
                List<LocalDate> periodDate = HMSUtil.getDatesBetweenFromDate(startDay.getTime(), periodRange.getTime());
                processInsertToBookingSchedule(booking, request, bookingTransaction, userId, floorInfoEnum, null, periodDate);
            }
            case "2" -> {
                String dayOfWeek = HMSUtil.convertDateToLocalDate(periodRange.getTime()).getDayOfWeek().toString();
                List<LocalDate> periodDate = HMSUtil.weeksInCalendar(HMSUtil.convertDateToLocalDate(startDay.getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
                processInsertToBookingSchedule(booking, request, bookingTransaction, userId, floorInfoEnum, dayOfWeek, periodDate);
            }
            case "3" -> {
                List<LocalDate> periodDate = HMSUtil.monthsInCalendar(HMSUtil.convertDateToLocalDate(startDay.getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
                processInsertToBookingSchedule(booking, request, bookingTransaction, userId, floorInfoEnum, null, periodDate);
            }
            default -> throw new HmsException(HmsErrorCode.INVALID_REQUEST, "not support service package ".concat(request.getServiceTypeId().toString()));
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateStatusSchedule(ScheduleConfirmRequest request, String userId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId)));
        Cleaner cleaner = cleanerRepository.findByUser(user)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any cleaner by ".concat(user.getId().toString())));
        BookingSchedule bookingSchedule = bookingScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any schedule by ".concat(request.getScheduleId().toString())));
        BookingTransaction bookingTransaction = bookingSchedule.getBookingTransaction();
        Booking booking = bookingTransaction.getBooking();
        if (booking.getCleaners().contains(cleaner) || RoleEnum.LEADER.name().equals(user.getRole().getName())) {
            switch (request.getStatus()) {
                case ON_PROCESS, MATCHED, ON_MOVING -> {
                    String oldStatus = bookingSchedule.getStatus();
                    if (!TransactionStatus.DONE.name().equals(bookingSchedule.getStatus())) {
                        bookingSchedule.setStatus(request.getStatus().name());
                        bookingSchedule.setUpdateBy(Long.parseLong(userId));
                        bookingScheduleRepository.save(bookingSchedule);
                    }
                    if ((TransactionStatus.ON_PROCESS.equals(request.getStatus()) && !oldStatus.equals(request.getStatus().name()))
                            || (TransactionStatus.ON_MOVING.equals(request.getStatus()) && !oldStatus.equals(request.getStatus().name()))) {
                        mailService.sendMailUpdatingStatusOfSchedule(booking.getUser().getEmail(), booking.getHostName(), booking.getHostAddress(), booking.getHostPhone(),
                                HMSUtil.formatDate(Date.from(bookingSchedule.getWorkDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), HMSUtil.DDMMYYYY_FORMAT),
                                request.getStatus().getName());
                    }
                }
                case DONE -> {
//                    if (!TransactionStatus.MATCHED.name().equals(bookingSchedule.getStatus())) {
//                        throw new HmsException(HmsErrorCode.INVALID_REQUEST, "can't execute this request because the status of schedule is not match");
//                    }
                    if (!TransactionStatus.DONE.name().equals(bookingSchedule.getStatus())) {
                        bookingSchedule.setStatus(request.getStatus().name());
                        bookingSchedule.setPaymentStatus(request.getPaymentStatus().name());
                        bookingSchedule.setUpdateBy(Long.parseLong(userId));
                        bookingSchedule.setPaymentNote(request.getNote());
                        bookingScheduleRepository.save(bookingSchedule);
                    }

                    CleanerWorkingDate cleanerWorkingDate = cleanerWorkingDateRepository.findByCleanerIdAndScheduleDateEqualsAndStatusEquals(cleaner.getId(), bookingSchedule.getWorkDate(), Constant.COMMON_STATUS.ACTIVE)
                            .orElse(null);
                    if (cleanerWorkingDate != null) {
                        cleanerWorkingDate.setStatus(Constant.COMMON_STATUS.INACTIVE);
                        cleanerWorkingDateRepository.save(cleanerWorkingDate);
                    }
                    updateReviewOfCleaner(cleaner, booking, bookingSchedule, defaultRating, StringUtils.EMPTY);

                    processIfAllSchedulesAreDone(booking, bookingTransaction);
                    mailService.sendMailUpdatingStatusOfSchedule(booking.getUser().getEmail(), booking.getHostName(), booking.getHostAddress(), booking.getHostPhone(),
                            HMSUtil.formatDate(Date.from(bookingSchedule.getWorkDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), HMSUtil.DDMMYYYY_FORMAT),
                            request.getStatus().getName());
                }
                case CANCELLED -> {
                    // TODO do clearer later
                    bookingSchedule.setStatus(request.getStatus().name());
                    bookingSchedule.setUpdateBy(Long.parseLong(userId));
                    bookingScheduleRepository.save(bookingSchedule);

                    CleanerWorkingDate cleanerWorkingDate = cleanerWorkingDateRepository.findByCleanerIdAndScheduleDateEqualsAndStatusEquals(cleaner.getId(), bookingSchedule.getWorkDate(), Constant.COMMON_STATUS.ACTIVE)
                            .orElse(null);
                    if (cleanerWorkingDate != null) {
                        cleanerWorkingDate.setStatus(Constant.COMMON_STATUS.INACTIVE);
                        cleanerWorkingDateRepository.save(cleanerWorkingDate);
                    }

                    processIfAllSchedulesAreDone(booking, bookingTransaction);
                    mailService.sendMailUpdatingStatusOfSchedule(booking.getUser().getEmail(), booking.getHostName(), booking.getHostAddress(), booking.getHostPhone(),
                            HMSUtil.formatDate(Date.from(bookingSchedule.getWorkDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), HMSUtil.DDMMYYYY_FORMAT),
                            request.getStatus().getName());
                }
                default -> throw new HmsException(HmsErrorCode.INVALID_REQUEST, "status not valid");
            }
        } else {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void cancelBooking(Booking booking, User user) {
        BookingTransaction bookingTransaction = bookingTransactionRepository.findByBooking(booking)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "can't find any booking transaction by ".concat(booking.getId().toString())));
        List<String> notStatus = List.of(TransactionStatus.DONE.name());
        bookingScheduleRepository.updateCancelBooking(bookingTransaction, ConfirmStatus.CANCELLED.name(), notStatus);

        bookingTransaction.setStatus(ConfirmStatus.CANCELLED.name());
        bookingTransactionRepository.save(bookingTransaction);
        booking.setStatus(ConfirmStatus.CANCELLED.name());
        booking.setUserUpdate(user);
        bookingRepository.save(booking);

        List<LocalDate> workDates = bookingScheduleRepository.findAllByBookingTransaction(bookingTransaction)
                .stream().map(BookingSchedule::getWorkDate).collect(Collectors.toList());
        for (Cleaner cleaner : booking.getCleaners()) {
            cleanerWorkingDateRepository.updateCancelBooking(cleaner.getId(), workDates, Constant.COMMON_STATUS.INACTIVE);
        }
    }

    private void processInsertToBookingSchedule(Booking booking, BookingRequest request, BookingTransaction bookingTransaction, Long userId, FloorInfoEnum floorInfoEnum, String dayOfWeek, List<LocalDate> periodDate) {
        List<LocalDate> periodClone = List.copyOf(periodDate);

        Map<Long, ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE)
                .stream().collect(Collectors.toMap(ServiceAddOn::getId, Function.identity()));
        List<BookingSchedule> bookingSchedules = new ArrayList<>();
        if (request.getBookingSchedules() != null && !request.getBookingSchedules().isEmpty()) {
            for (BookingScheduleRequest scheduleRequest : request.getBookingSchedules()) {
                List<ServiceAddOn> addOnList = getAddOnFromAll(serviceAddOns, scheduleRequest.getServiceAddOnIds());
                long totalSchedulePrice = floorInfoEnum.getPrice() * scheduleRequest.getFloorNumber();
                totalSchedulePrice += addOnList.stream().mapToLong(ServiceAddOn::getPrice).sum();
                BookingSchedule item = BookingSchedule.builder()
                        .bookingTransaction(bookingTransaction)
                        .serviceAddOns(new HashSet<>(addOnList))
                        .dayOfTheWeek(dayOfWeek)
                        .workDate(scheduleRequest.getWorkDate())
                        .startTime(scheduleRequest.getStartTime())
                        .endTime(scheduleRequest.getEndTime())
                        .status(ConfirmStatus.RECEIVED.name())
                        .updateBy(userId)
                        .totalSchedulePrice(totalSchedulePrice)
                        .build();
                bookingSchedules.add(item);
                periodDate.remove(scheduleRequest.getWorkDate());
            }
        }

        List<ServiceAddOn> addOnList = getAddOnFromAll(serviceAddOns, request.getServiceAddOnIds());
        long totalPriceFloorAre = floorInfoEnum.getPrice() * request.getFloorNumber();
        long totalSchedulePrice = totalPriceFloorAre + addOnList.stream().mapToLong(ServiceAddOn::getPrice).sum();
        for (LocalDate localDate : periodDate) {
            BookingSchedule item = BookingSchedule.builder()
                    .bookingTransaction(bookingTransaction)
                    .serviceAddOns(new HashSet<>(addOnList))
                    .dayOfTheWeek(dayOfWeek)
                    .workDate(localDate)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .status(ConfirmStatus.RECEIVED.name())
                    .updateBy(userId)
                    .totalSchedulePrice(totalSchedulePrice)
                    .build();
            bookingSchedules.add(item);
        }
        bookingScheduleRepository.saveAll(bookingSchedules);

        bookingTransaction.setTotalBookingDate(periodClone.size());
        double totalBookingPrice = totalPriceFloorAre * bookingTransaction.getTotalBookingDate();
        boolean isAutoChoosing = request.getCleanerIds() == null;
        totalBookingPrice += isAutoChoosing ? choosingCleanerPrice : 0;
        totalBookingPrice += request.getDistancePrice() != null ? request.getDistancePrice() : 0;
        bookingTransaction.setTotalBookingPrice(totalBookingPrice);
        bookingTransactionRepository.save(bookingTransaction);

        // picking cleaner and save the number of cleaner to transaction
        bookingTransaction.setTotalBookingCleaner(processWorkingDateForCleaner(floorInfoEnum, booking, periodClone, request, isAutoChoosing));
        bookingTransactionRepository.save(bookingTransaction);
    }

    private int processWorkingDateForCleaner(FloorInfoEnum floorInfoEnum,
                                             Booking booking,
                                             List<LocalDate> workDate,
                                             BookingRequest request,
                                             boolean isAutoChoosing) {
        List<CleanerWorkingDate> saveList = new ArrayList<>();
        List<Cleaner> cleaners;
        if (isAutoChoosing) {
            cleaners = autoChooseCleaner(floorInfoEnum.getCleanerNum(), workDate);
            if (cleaners.isEmpty()) {
                booking.setStatus(TransactionStatus.CANCELLED.name());
                booking.setRejectedReason("There is no available cleaner right now on the system");
                bookingRepository.saveAndFlush(booking);
                throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "There is no available cleaner right now on the system");
            }
            addWorkDate(saveList, cleaners, workDate);
        } else {
            cleaners = cleanerRepository.findAllByIdInAndStatusEquals(request.getCleanerIds(), Constant.COMMON_STATUS.ACTIVE);
            addWorkDate(saveList, cleaners, workDate);
        }
        booking.setCleaners(new HashSet<>(cleaners));
        bookingRepository.save(booking);
        cleanerWorkingDateRepository.saveAll(saveList);
        return cleaners.size();
    }

    private void addWorkDate(List<CleanerWorkingDate> saveList,
                             List<Cleaner> cleaners,
                             List<LocalDate> workDate) {
        for (Cleaner cleaner : cleaners) {
            for (LocalDate date : workDate) {
                saveList.add(CleanerWorkingDate.builder()
                        .cleanerId(cleaner.getId())
                        .scheduleDate(date)
                        .status(Constant.COMMON_STATUS.ACTIVE)
                        .build());
            }
        }
    }

    private List<ServiceAddOn> getAddOnFromAll(Map<Long, ServiceAddOn> addOnList, List<Long> ids) {
        List<ServiceAddOn> response = new ArrayList<>();
        for (Long id : ids) {
            if (addOnList.containsKey(id)) {
                response.add(addOnList.get(id));
            }
        }
        return response;
    }

    public List<Cleaner> autoChooseCleaner(int number, List<LocalDate> workDate) {
        Random random = new Random();
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

    public List<Cleaner> getListCleanerAvailable(LocalDate workDate, Long serviceTypeId, Long servicePackageId) {
        ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service type by ".concat(serviceTypeId.toString())));
        ServicePackage servicePackage = servicePackageRepository.findById(servicePackageId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service package by ".concat(servicePackageId.toString())));
        List<LocalDate> periodDate;
        Calendar startDay = Calendar.getInstance();
        startDay.setTime(Date.from(workDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        Calendar periodRange = Calendar.getInstance();
        periodRange.setTime(Date.from(workDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        periodRange.add(Calendar.MONTH, Integer.parseInt(servicePackage.getServicePackageName()));
        switch (serviceType.getServiceTypeId().toString()) {
            case "1" -> {
                periodDate = HMSUtil.getDatesBetweenFromDate(startDay.getTime(), periodRange.getTime());
            }
            case "2" -> {
                periodDate = HMSUtil.weeksInCalendar(HMSUtil.convertDateToLocalDate(startDay.getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
            }
            case "3" -> {
                periodDate = HMSUtil.monthsInCalendar(HMSUtil.convertDateToLocalDate(startDay.getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
            }
            default -> throw new HmsException(HmsErrorCode.INVALID_REQUEST, "not support service package ".concat(serviceTypeId.toString()));
        }
        Set<Long> cleanerIds = filterOnlyAvailable(cleanerRepository.countCleanerByStatusEquals(Constant.COMMON_STATUS.ACTIVE), periodDate);
        return cleanerRepository.findAllById(cleanerIds);
    }

    private Set<Long> filterOnlyAvailable(int num, List<LocalDate> workDate) {
        List<CleanerWorkingDate> bookingSchedules = cleanerWorkingDateRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE);
        Set<Long> cleanerIds = cleanerRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE).stream().map(Cleaner::getId).collect(Collectors.toSet());
        for (CleanerWorkingDate cleaner : bookingSchedules) {
            if (workDate.contains(cleaner.getScheduleDate())) {
                cleanerIds.remove(cleaner.getCleanerId());
                if (cleanerIds.isEmpty()) {
                    return cleanerIds;
                }
            }
        }
        return cleanerIds;
    }

    public void updateReviewOfCleaner(Cleaner cleaner,
                                      Booking booking,
                                      BookingSchedule bookingSchedule,
                                      Long rating,
                                      String review) {
        Map<Long, CleanerReviewInfo> reviewList = JsonService.strToObject(cleaner.getReview(), new TypeReference<>() {
        });
        CleanerActivity cleanerActivity = CleanerActivity.builder()
                .bookingScheduleId(bookingSchedule.getScheduleId())
                .workDate(Date.from(bookingSchedule.getWorkDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                .ratingScore(rating) // default rating when schedule is done -> a review request will be sent to customer after this to get actual rating
                .review(review)
                .build();
        CleanerReviewInfo cleanerReviewInfo = CleanerReviewInfo.builder()
                .cleanerActivities(List.of(cleanerActivity))
                .build();
        if (reviewList == null) {
            Map<Long, CleanerReviewInfo> item = new HashMap<>();
            item.put(booking.getId(), cleanerReviewInfo);
            cleaner.setReview(JsonService.writeStringSkipError(item));
        } else {
            if (reviewList.containsKey(booking.getId())) {
                CleanerReviewInfo item = reviewList.get(booking.getId());
                List<CleanerActivity> list = new ArrayList<>(item.getCleanerActivities());
                boolean isExisted = false;
                for (CleanerActivity activity : list) {
                    if (activity.getBookingScheduleId().equals(cleanerActivity.getBookingScheduleId())) {
                        activity.setRatingScore(rating);
                        activity.setReview(review);
                        isExisted = true;
                        break;
                    }
                }
                if (isExisted) {
                    item.setCleanerActivities(list);
                } else {
                    list.add(cleanerActivity);
                    item.setCleanerActivities(list);
                }
                reviewList.put(booking.getId(), item);
            } else {
                reviewList.put(booking.getId(), cleanerReviewInfo);
            }
            cleaner.setReview(JsonService.writeStringSkipError(reviewList));
        }
        cleanerRepository.save(cleaner);
    }

    private void processIfAllSchedulesAreDone(Booking booking, BookingTransaction bookingTransaction) {
        List<String> status = Arrays.asList(TransactionStatus.DONE.name(), TransactionStatus.CANCELLED.name());
        if (bookingScheduleRepository.getScheduleStatusByTransactionId(bookingTransaction, status) == 0) {
            bookingTransaction.setStatus(TransactionStatus.DONE.name());
            bookingTransactionRepository.save(bookingTransaction);

            booking.setStatus(TransactionStatus.DONE.name());
            bookingRepository.save(booking);
        }
    }
}