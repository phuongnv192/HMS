package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.request.BookingRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.BookingTransaction;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;

    public HmsResponse<Booking> booking(BookingRequest request) {
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

    public HmsResponse<Object> confirmBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant booking is not existed on system"));
        booking.setStatus(ConfirmStatus.CONFIRMED.name());
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
            scheduleService.processBookingRegular(booking, request, bookingTransaction, userId);
        } else {
            scheduleService.processBookingPeriod(booking, request, bookingTransaction, userId);
        }
    }

    public static void main(String[] args) {
        Calendar month = Calendar.getInstance();
        month.add(Calendar.MONTH, 6);
        System.out.println(HMSUtil.monthsInCalendar(HMSUtil.convertDateToLocalDate(Calendar.getInstance().getTime()), HMSUtil.convertDateToLocalDate(month.getTime())));
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
