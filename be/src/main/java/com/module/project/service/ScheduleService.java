package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.CleanerActivity;
import com.module.project.dto.CleanerReviewInfo;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.PriceTypeEnum;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.WorkingTime;
import com.module.project.dto.request.AddOnScheduleStatusRequest;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingScheduleRequest;
import com.module.project.dto.request.CleanerAvailableRequest;
import com.module.project.dto.request.ScheduleConfirmRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.Cleaner;
import com.module.project.model.CleanerWorkingDate;
import com.module.project.model.FloorInfo;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServicePackage;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.CleanerWorkingDateRepository;
import com.module.project.repository.FloorTypeRepository;
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
    private final FloorTypeRepository floorTypeRepository;
    private final MailService mailService;

    @Value("${application.choosing-cleaner-price:0}")
    private long choosingCleanerPrice;

    @Value("${application.default-rating:5}")
    private long defaultRating;

    private static final int GAP_HOUR_BETWEEN_BOOKING = 1;

    @Transactional(rollbackFor = {Exception.class})
    public void processBookingRegular(Booking booking,
                                      BookingRequest request,
                                      BookingTransaction bookingTransaction,
                                      Long userId) {
        FloorInfo floorInfo = floorTypeRepository.findByFloorKey(booking.getFloorKey())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any floor type by ".concat(booking.getFloorKey())));;

        List<ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByIdInAndStatus(request.getServiceAddOnIds(), Constant.COMMON_STATUS.ACTIVE);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(request.getStartTime());
        Calendar actualEndTime = calculateActualEndTime(endTime.getTime(), serviceAddOns, floorInfo.getDuration());

        boolean isAutoChoosing = request.getCleanerIds() == null;
        long totalPriceFloorAre = 0;
        if (PriceTypeEnum.PACKAGE.name().equalsIgnoreCase(floorInfo.getPriceType())) {
            totalPriceFloorAre = floorInfo.getPrice();
        } else {
            totalPriceFloorAre = floorInfo.getPrice() * request.getFloorNumber();;
        }

        long priceChoosingCleaner = isAutoChoosing ? choosingCleanerPrice : 0;
        long distancePrice = request.getDistancePrice() != null ? request.getDistancePrice() : 0;
        long totalBookingPrice = totalPriceFloorAre + priceChoosingCleaner + distancePrice;

        BookingSchedule bookingSchedule = BookingSchedule.builder()
                .bookingTransaction(bookingTransaction)
                .serviceAddOns(new HashSet<>(serviceAddOns))
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(actualEndTime.getTime())
                .status(ConfirmStatus.RECEIVED.name())
                .updateBy(userId)
                .totalSchedulePrice(totalPriceFloorAre)
                .build();
        bookingScheduleRepository.save(bookingSchedule);

        bookingTransaction.setTotalBookingPrice(totalBookingPrice);
        bookingTransaction.setTotalBookingDate(1L);
        bookingTransactionRepository.save(bookingTransaction);

        WorkingTime workingTime = addingGapToWorkingTime(bookingSchedule.getScheduleId(), request.getStartTime(), actualEndTime.getTime());
        bookingTransaction.setTotalBookingCleaner(processWorkingDateForCleaner(floorInfo.getCleanerNumber(), booking, List.of(workingTime), request, isAutoChoosing));
        bookingTransactionRepository.save(bookingTransaction);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void processBookingPeriod(Booking booking,
                                     BookingRequest request,
                                     BookingTransaction bookingTransaction,
                                     Long userId) {
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorNumber());
//        if (floorInfoEnum == null) {
//            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "error when look up floor info: ".concat(request.getFloorArea()));
//        }
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
                    if (!TransactionStatus.DONE.name().equals(bookingSchedule.getStatus())) {
                        double schedulePriceAddOn = 0;
                        if (!request.getAddOns().isEmpty()) {
                            Map<Long, ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE)
                                    .stream().collect(Collectors.toMap(ServiceAddOn::getId, Function.identity()));
                            StringBuilder addOnNote = new StringBuilder();
                            for (AddOnScheduleStatusRequest addOn : request.getAddOns()) {
                                if (serviceAddOns.containsKey(addOn.getServiceAddOnId())) {
                                    addOnNote.append(serviceAddOns.get(addOn.getServiceAddOnId()).getName())
                                            .append(" - ")
                                            .append(addOn.getNote())
                                            .append(" - ")
                                            .append(addOn.getPrice())
                                            .append("\n");
                                } else {
                                    addOnNote.append(addOn.getNote())
                                            .append(" - ")
                                            .append(addOn.getPrice())
                                            .append("\n");
                                }
                                schedulePriceAddOn += addOn.getPrice();
                            }
                            bookingSchedule.setTotalSchedulePrice(bookingSchedule.getTotalSchedulePrice() + schedulePriceAddOn);
                            bookingSchedule.setAddOnNote(addOnNote.toString());
                        }

                        bookingSchedule.setStatus(request.getStatus().name());
                        bookingSchedule.setPaymentStatus(request.getPaymentStatus().name());
                        bookingSchedule.setUpdateBy(Long.parseLong(userId));
                        bookingSchedule.setPaymentNote(request.getNote());
                        bookingScheduleRepository.save(bookingSchedule);

                        bookingTransaction.setTotalBookingPrice(bookingTransaction.getTotalBookingPrice() + schedulePriceAddOn);
                        bookingTransactionRepository.save(bookingTransaction);
                    }

                    CleanerWorkingDate cleanerWorkingDate = cleanerWorkingDateRepository.findByCleanerIdAndScheduleIdAndStatusEquals(cleaner.getId(), bookingSchedule.getScheduleId(), Constant.COMMON_STATUS.ACTIVE)
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

                    double price = bookingTransaction.getTotalBookingPrice();
                    price -= bookingSchedule.getTotalSchedulePrice();
                    bookingTransaction.setTotalBookingPrice(price);
                    bookingTransactionRepository.save(bookingTransaction);

                    CleanerWorkingDate cleanerWorkingDate = cleanerWorkingDateRepository.findByCleanerIdAndScheduleIdAndStatusEquals(cleaner.getId(), bookingSchedule.getScheduleId(), Constant.COMMON_STATUS.ACTIVE)
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

        List<Long> scheduleIds = bookingScheduleRepository.findAllByBookingTransaction(bookingTransaction)
                .stream().map(BookingSchedule::getScheduleId).collect(Collectors.toList());
        for (Cleaner cleaner : booking.getCleaners()) {
            cleanerWorkingDateRepository.updateCancelBooking(cleaner.getId(), scheduleIds, Constant.COMMON_STATUS.INACTIVE);
        }
    }

    private void processInsertToBookingSchedule(Booking booking, BookingRequest request, BookingTransaction bookingTransaction, Long userId, FloorInfoEnum floorInfoEnum, String dayOfWeek, List<LocalDate> periodDate) {
        List<WorkingTime> workingTimes = new ArrayList<>();

        Map<Long, ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE)
                .stream().collect(Collectors.toMap(ServiceAddOn::getId, Function.identity()));
        Calendar startTimeCommon = Calendar.getInstance();
        startTimeCommon.setTime(request.getStartTime());

        List<BookingSchedule> bookingSchedules = new ArrayList<>();
        double totalBookingPrice = 0;
        if (request.getBookingSchedules() != null && !request.getBookingSchedules().isEmpty()) {
            for (BookingScheduleRequest scheduleRequest : request.getBookingSchedules()) {
                List<ServiceAddOn> addOnSchedules = getAddOnFromAll(serviceAddOns, scheduleRequest.getServiceAddOnIds());
                // calculate actual endTime -> endTime = startTime + duration of working + service add on duration (if had)
                // in case startTime of schedule is null => that schedule will have the same startTime with default ones
                Calendar startTime = Calendar.getInstance();
                if (scheduleRequest.getStartTime() == null) {
                    startTime.setTime(request.getStartTime());
                } else {
                    startTime.setTime(scheduleRequest.getStartTime());
                }
                Calendar actualEndTime = calculateActualEndTime(startTime.getTime(), addOnSchedules, floorInfoEnum.getDuration());
                long totalSchedulePrice = floorInfoEnum.getPrice() * scheduleRequest.getFloorNumber();

                BookingSchedule item = BookingSchedule.builder()
                        .bookingTransaction(bookingTransaction)
                        .serviceAddOns(new HashSet<>(addOnSchedules))
                        .dayOfTheWeek(dayOfWeek)
                        .workDate(scheduleRequest.getWorkDate())
                        .startTime(HMSUtil.setLocalDateToCalendar(startTime, scheduleRequest.getWorkDate()).getTime())
                        .endTime(HMSUtil.setLocalDateToCalendar(actualEndTime, scheduleRequest.getWorkDate()).getTime())
                        .status(ConfirmStatus.RECEIVED.name())
                        .updateBy(userId)
                        .totalSchedulePrice(totalSchedulePrice)
                        .build();
                bookingSchedules.add(item);
                totalBookingPrice += totalSchedulePrice;
                periodDate.remove(scheduleRequest.getWorkDate());
            }
        }

        long totalPriceFloorAre = floorInfoEnum.getPrice() * request.getFloorNumber();
        List<ServiceAddOn> addOnList = getAddOnFromAll(serviceAddOns, request.getServiceAddOnIds());
        Calendar actualEndTimeAll = calculateActualEndTime(startTimeCommon.getTime(), addOnList, floorInfoEnum.getDuration());
        for (LocalDate localDate : periodDate) {
            BookingSchedule item = BookingSchedule.builder()
                    .bookingTransaction(bookingTransaction)
                    .serviceAddOns(new HashSet<>(addOnList))
                    .dayOfTheWeek(dayOfWeek)
                    .workDate(localDate)
                    .startTime(HMSUtil.setLocalDateToCalendar(startTimeCommon, localDate).getTime())
                    .endTime(HMSUtil.setLocalDateToCalendar(actualEndTimeAll, localDate).getTime())
                    .status(ConfirmStatus.RECEIVED.name())
                    .updateBy(userId)
                    .totalSchedulePrice(totalPriceFloorAre)
                    .build();
            bookingSchedules.add(item);
            totalBookingPrice += totalPriceFloorAre;
        }
        bookingScheduleRepository.saveAll(bookingSchedules);
        // initialize list working time to looking for cleaners
        initializeWorkingTime(bookingSchedules, workingTimes);

        bookingTransaction.setTotalBookingDate(bookingSchedules.size());
        boolean isAutoChoosing = request.getCleanerIds() == null;
        totalBookingPrice += isAutoChoosing ? choosingCleanerPrice : 0;
        totalBookingPrice += request.getDistancePrice() != null ? request.getDistancePrice() : 0;
        bookingTransaction.setTotalBookingPrice(totalBookingPrice);
        bookingTransactionRepository.save(bookingTransaction);

        // picking cleaner and save the number of cleaner to transaction
        bookingTransaction.setTotalBookingCleaner(processWorkingDateForCleaner(floorInfoEnum.getCleanerNum(), booking, workingTimes, request, isAutoChoosing));
        bookingTransactionRepository.save(bookingTransaction);
    }

    private void initializeWorkingTime(List<BookingSchedule> bookingSchedules,
                                       List<WorkingTime> workingTimes) {
        for (BookingSchedule bookingSchedule : bookingSchedules) {
            workingTimes.add(addingGapToWorkingTime(bookingSchedule.getScheduleId(),
                    HMSUtil.convertLocalDateToDate(bookingSchedule.getWorkDate(), bookingSchedule.getStartTime()),
                    HMSUtil.convertLocalDateToDate(bookingSchedule.getWorkDate(), bookingSchedule.getEndTime())));
        }
    }

    private int processWorkingDateForCleaner(int cleanerNum,
                                             Booking booking,
                                             List<WorkingTime> workDate,
                                             BookingRequest request,
                                             boolean isAutoChoosing) {
        List<CleanerWorkingDate> saveList = new ArrayList<>();
        List<Cleaner> cleaners;
        if (isAutoChoosing) {
            cleaners = autoChooseCleaner(cleanerNum, workDate);
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
                             List<WorkingTime> workDate) {
        for (Cleaner cleaner : cleaners) {
            for (WorkingTime date : workDate) {
                saveList.add(CleanerWorkingDate.builder()
                        .cleanerId(cleaner.getId())
                        .scheduleId(date.getScheduleId())
                        .startTime(date.getFrom())
                        .endTime(date.getTo())
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

    public List<Cleaner> autoChooseCleaner(int number, List<WorkingTime> workDate) {
        Random random = new Random();
        if (number <= 0 || number > 4) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "number of cleaner is invalid");
        }
        List<Long> cleaners = new ArrayList<>(filterOnlyAvailable(workDate));
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

    public List<Cleaner> getListCleanerAvailable(CleanerAvailableRequest request) {
//        List<LocalDate> periodDate = new ArrayList<>();
//        Calendar startDay = Calendar.getInstance();
//        startDay.setTime(Date.from(workDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
//        if (serviceTypeId != null && servicePackageId != null) {
//            ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
//                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service type by ".concat(serviceTypeId.toString())));
//            ServicePackage servicePackage = servicePackageRepository.findById(servicePackageId)
//                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service package by ".concat(servicePackageId.toString())));
//            Calendar periodRange = Calendar.getInstance();
//            periodRange.setTime(Date.from(workDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
//            periodRange.add(Calendar.MONTH, Integer.parseInt(servicePackage.getServicePackageName()));
//            switch (serviceType.getServiceTypeId().toString()) {
//                case "1" -> {
//                    periodDate = HMSUtil.getDatesBetweenFromDate(startDay.getTime(), periodRange.getTime());
//                }
//                case "2" -> {
//                    periodDate = HMSUtil.weeksInCalendar(HMSUtil.convertDateToLocalDate(startDay.getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
//                }
//                case "3" -> {
//                    periodDate = HMSUtil.monthsInCalendar(HMSUtil.convertDateToLocalDate(startDay.getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
//                }
//                default ->
//                        throw new HmsException(HmsErrorCode.INVALID_REQUEST, "not support service package ".concat(serviceTypeId.toString()));
//            }
//        } else {
//            periodDate.add(workDate);
//        }
        Set<Long> cleanerIds = filterOnlyAvailable(request.getWorkingTimes());
        return cleanerRepository.findAllById(cleanerIds);
    }

    private Set<Long> filterOnlyAvailable(List<WorkingTime> workDate) {
        List<CleanerWorkingDate> bookingSchedules = cleanerWorkingDateRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE);
        Set<Long> cleanerIds = cleanerRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE).stream().map(Cleaner::getId).collect(Collectors.toSet());
        for (CleanerWorkingDate cleaner : bookingSchedules) {
            for (WorkingTime workingTime : workDate) {
                if ((cleaner.getStartTime().before(workingTime.getTo()) && cleaner.getEndTime().after(workingTime.getTo()))
                        || (cleaner.getStartTime().before(workingTime.getFrom()) && cleaner.getEndTime().after(workingTime.getTo()))
                        || (cleaner.getStartTime().before(workingTime.getFrom()) && cleaner.getEndTime().after(workingTime.getFrom()))) {
                    cleanerIds.remove(cleaner.getCleanerId());
                }
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

    private Calendar calculateActualEndTime(Date endTime, List<ServiceAddOn> addOnList, int duration) {
        int addOnDuration = addOnList.stream().mapToInt(ServiceAddOn::getDuration).sum();
        Calendar actualEnd = Calendar.getInstance();
        actualEnd.setTime(endTime);
        actualEnd.add(Calendar.HOUR_OF_DAY, addOnDuration + duration);
        return actualEnd;
    }

    private WorkingTime addingGapToWorkingTime(Long scheduleId, Date startTime, Date endTime) {
        WorkingTime workingTime = new WorkingTime();
        if (scheduleId != null) {
            workingTime.setScheduleId(scheduleId);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.HOUR_OF_DAY, -GAP_HOUR_BETWEEN_BOOKING);
        workingTime.setFrom(calendar.getTime());

        calendar.setTime(endTime);
        calendar.add(Calendar.HOUR_OF_DAY, GAP_HOUR_BETWEEN_BOOKING);
        workingTime.setTo(calendar.getTime());
        return workingTime;
    }
}