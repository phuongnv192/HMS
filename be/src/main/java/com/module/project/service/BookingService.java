package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.TransactionStatus;
import com.module.project.dto.request.BookingRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.Cleaner;
import com.module.project.model.CleanerWorkingDate;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.BookingTransactionRepository;
import com.module.project.repository.CleanerWorkingDateRepository;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingTransactionRepository bookingTransactionRepository;
    private final BookingScheduleRepository bookingScheduleRepository;
    private final UserRepository userRepository;
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final CleanerService cleanerService;
    private final CleanerWorkingDateRepository cleanerWorkingDateRepository;

    @Value("${application.choosing-cleaner-price:0}")
    private long choosingCleanerPrice;

    public HmsResponse<Booking> book(BookingRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant user is not existed on system"));
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorArea());
        if (floorInfoEnum == null) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "error when look up floor info: ".concat(request.getFloorArea()));
        }
        Booking booking = Booking.builder()
                .hostName(request.getHostName())
                .hostPhone(request.getHostPhone())
                .hostAddress(request.getHostAddress())
                .hostDistance(request.getHostDistance())
                .houseType(request.getHouseType())
                .floorNumber(request.getFloorNumber())
                .floorArea(floorInfoEnum.getFloorArea())
                .user(customer)
                .userUpdate(customer)
                .note(request.getNote())
                .status(ConfirmStatus.RECEIVED.name())
                .rawRequest(JsonService.writeStringSkipError(request))
                .build();
        bookingRepository.save(booking);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, booking);
    }

    public Object confirmBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant booking is not existed on system"));
        booking.setStatus(ConfirmStatus.CONFIRMED.name());
        bookingRepository.save(booking);
        processBookingSchedule(booking, userId);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    private void processBookingSchedule(Booking booking, Long userId) {
        BookingRequest request = JsonService.strToObject(booking.getRawRequest(), new TypeReference<>() {
        });
        if (request == null) {
            throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "error when parse raw request of booking ".concat(booking.getId().toString()));
        }
        BookingTransaction bookingTransaction = BookingTransaction.builder()
                .booking(booking)
                .status(ConfirmStatus.CONFIRMED.name())
                .build();
        if (request.getServiceTypeId() == null) {
            processBookingRegular(booking, request, bookingTransaction, userId);
        } else {
            // TODO: process booking schedule when period
        }
    }

    private void processBookingRegular(Booking booking,
                                       BookingRequest request,
                                       BookingTransaction bookingTransaction,
                                       Long userId) {
        bookingTransaction.setTotalBookingDate(1L);
        bookingTransactionRepository.save(bookingTransaction);
        // TODO: assign available cleaner for schedule
        FloorInfoEnum floorInfoEnum = FloorInfoEnum.lookUp(request.getFloorArea());
        if (floorInfoEnum == null) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "error when look up floor info: ".concat(request.getFloorArea()));
        }
        processWorkingDateForCleaner(floorInfoEnum, booking, List.of(request.getWorkDate()));

        List<ServiceAddOn> serviceAddOns = serviceAddOnRepository.findAllByIdInAndStatus(request.getServiceAddOnIds(), Constant.COMMON_STATUS.ACTIVE);
        BookingSchedule bookingSchedule = BookingSchedule.builder()
                .bookingTransaction(bookingTransaction)
                .serviceAddOns(new HashSet<>(serviceAddOns))
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(TransactionStatus.CONFIRMING.name())
                .updateBy(userId)
                .build();
        bookingScheduleRepository.save(bookingSchedule);

        long totalPriceAddOn = serviceAddOns.stream().mapToLong(ServiceAddOn::getPrice).sum();
        long totalPriceFloorAre = floorInfoEnum.getPrice() * request.getFloorNumber();
        long priceChoosingCleaner = request.getCleanerIds() == null ? 0 : choosingCleanerPrice;
        long totalPrice = totalPriceAddOn + totalPriceFloorAre + priceChoosingCleaner;
        bookingTransaction.setTotalBookingPrice(totalPrice);
        bookingTransactionRepository.save(bookingTransaction);
    }

    private void processBookingPeriod(Booking booking,
                                      BookingRequest request,
                                      BookingTransaction bookingTransaction,
                                      Long userId) {

    }

    private void processWorkingDateForCleaner(FloorInfoEnum floorInfoEnum, Booking booking, List<LocalDate> workDate) {
        List<Cleaner> cleaners = cleanerService.autoChooseCleaner(floorInfoEnum.getCleanerNum(), workDate);
        booking.setCleaners(new HashSet<>(cleaners));
        bookingRepository.save(booking);
        List<CleanerWorkingDate> saveList = new ArrayList<>();
        for (Cleaner cleaner : cleaners) {
            for (LocalDate date : workDate) {
                saveList.add(CleanerWorkingDate.builder()
                        .cleanerId(cleaner.getId())
                        .scheduleDate(date)
                        .status(Constant.COMMON_STATUS.ACTIVE)
                        .build());
            }
        }
        cleanerWorkingDateRepository.saveAll(saveList);
    }

    public Map<String, Object> updateBooking(Map<String, Object> request) throws Exception {
        return null;
    }

    public Map<String, Object> getBookingDetail(Map<String, Object> request) throws Exception {
        return null;
    }

    public List<Map<String, Object>> getBookingList(Map<String, Object> request) throws Exception {
        return null;
    }
}
