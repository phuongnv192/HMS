package com.module.project.service;

import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingScheduleRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.Cleaner;
import com.module.project.model.CleanerWorkingDate;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServicePackage;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.CleanerRepository;
import com.module.project.repository.CleanerWorkingDateRepository;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServicePackageRepository;
import com.module.project.util.HMSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final CleanerService cleanerService;
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final BookingTransactionRepository bookingTransactionRepository;
    private final BookingScheduleRepository bookingScheduleRepository;
    private final BookingRepository bookingRepository;
    private final CleanerWorkingDateRepository cleanerWorkingDateRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final CleanerRepository cleanerRepository;

    @Value("${application.choosing-cleaner-price:0}")
    private long choosingCleanerPrice;

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
        boolean isAutoChoosing = request.getCleanerIds().isEmpty();
        long totalPriceAddOn = serviceAddOns.stream().mapToLong(ServiceAddOn::getPrice).sum();
        long totalPriceFloorAre = floorInfoEnum.getPrice() * request.getFloorNumber();
        long priceChoosingCleaner = isAutoChoosing ? choosingCleanerPrice : 0;
        long totalBookingPrice = totalPriceFloorAre + priceChoosingCleaner;
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
        processWorkingDateForCleaner(floorInfoEnum, booking, List.of(request.getWorkDate()), request, isAutoChoosing);
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
        Calendar periodRange = Calendar.getInstance();
        periodRange.add(Calendar.MONTH, Integer.parseInt(servicePackage.getServicePackageName()));
        switch (request.getServiceTypeId().toString()) {
            case "1" -> {
                List<LocalDate> periodDate = HMSUtil.getDatesBetweenFromDate(Calendar.getInstance().getTime(), periodRange.getTime());
                processInsertToBookingSchedule(booking, request, bookingTransaction, userId, floorInfoEnum, null, periodDate);
            }
            case "2" -> {
                String dayOfWeek = HMSUtil.convertDateToLocalDate(periodRange.getTime()).getDayOfWeek().toString();
                List<LocalDate> periodDate = HMSUtil.weeksInCalendar(HMSUtil.convertDateToLocalDate(Calendar.getInstance().getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
                processInsertToBookingSchedule(booking, request, bookingTransaction, userId, floorInfoEnum, dayOfWeek, periodDate);
            }
            case "3" -> {
                List<LocalDate> periodDate = HMSUtil.monthsInCalendar(HMSUtil.convertDateToLocalDate(Calendar.getInstance().getTime()), HMSUtil.convertDateToLocalDate(periodRange.getTime()));
                processInsertToBookingSchedule(booking, request, bookingTransaction, userId, floorInfoEnum, null, periodDate);
            }
            default ->
                    throw new HmsException(HmsErrorCode.INVALID_REQUEST, "not support service package ".concat(request.getServiceTypeId().toString()));
        }
    }

    private void processInsertToBookingSchedule(Booking booking, BookingRequest request, BookingTransaction bookingTransaction, Long userId, FloorInfoEnum floorInfoEnum, String dayOfWeek, List<LocalDate> periodDate) {
        List<LocalDate> periodClone = new ArrayList<>();
        Collections.copy(periodDate, periodClone);

        Map<Long, ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE)
                .stream().collect(Collectors.toMap(ServiceAddOn::getId, Function.identity()));
        List<BookingSchedule> bookingSchedules = new ArrayList<>();
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
        boolean isAutoChoosing = request.getCleanerIds().isEmpty();
        totalBookingPrice += isAutoChoosing ? choosingCleanerPrice : 0;
        bookingTransaction.setTotalBookingPrice(totalBookingPrice);
        bookingTransactionRepository.save(bookingTransaction);

        processWorkingDateForCleaner(floorInfoEnum, booking, periodClone, request, isAutoChoosing);
    }

    private void processWorkingDateForCleaner(FloorInfoEnum floorInfoEnum,
                                              Booking booking,
                                              List<LocalDate> workDate,
                                              BookingRequest request,
                                              boolean isAutoChoosing) {
        List<CleanerWorkingDate> saveList = new ArrayList<>();
        List<Cleaner> cleaners;
        if (isAutoChoosing) {
            cleaners = cleanerService.autoChooseCleaner(floorInfoEnum.getCleanerNum(), workDate);
            addWorkDate(saveList, cleaners, workDate);
        } else {
            cleaners = cleanerRepository.findAllByIdInAndStatusEquals(request.getCleanerIds(), Constant.COMMON_STATUS.ACTIVE);
            addWorkDate(saveList, cleaners, workDate);
        }
        booking.setCleaners(new HashSet<>(cleaners));
        bookingRepository.save(booking);
        cleanerWorkingDateRepository.saveAll(saveList);
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
}