package com.module.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.module.project.dto.ConfirmStatus;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.PaymentStatus;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingStatusRequest;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.Booking;
import com.module.project.model.BookingSchedule;
import com.module.project.model.BookingTransaction;
import com.module.project.model.User;
import com.module.project.repository.BookingRepository;
import com.module.project.repository.BookingScheduleRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;
    private final BookingScheduleRepository bookingScheduleRepository;

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

    public HmsResponse<Object> confirmBooking(BookingStatusRequest request, Long userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant booking is not existed on system"));
        if (!ConfirmStatus.RECEIVED.name().equals(booking.getStatus())) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "can't execute this request because status of schedule is not match");
        }
        booking.setStatus(ConfirmStatus.CONFIRMED.name());
        processBookingSchedule(booking, userId);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    public HmsResponse<Object> cancelBooking(BookingStatusRequest request, Long userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant booking is not existed on system"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any user by ".concat(userId.toString())));
        List<String> acceptRole = Arrays.asList(RoleEnum.ADMIN.name(), RoleEnum.MANAGER.name());
        if (booking.getUser().getId().equals(userId) || acceptRole.contains(user.getRole().getName())) {
            scheduleService.cancelBooking(booking, user);
            return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
        } else {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
    }

    public HmsResponse<Object> updateWithdraw(Long bookScheduleId, String userId, String roleName) {
        List<String> approveRole = Arrays.asList(RoleEnum.MANAGER.name(), RoleEnum.LEADER.name());
        if (approveRole.contains(roleName)) {
            BookingSchedule bookingSchedule = bookingScheduleRepository.findById(bookScheduleId)
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "relevant schedule is not existed on system"));
            bookingSchedule.setPaymentStatus(PaymentStatus.WITHDRAW.name());
            bookingSchedule.setUpdateBy(Long.parseLong(userId));
            bookingScheduleRepository.save(bookingSchedule);
            return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
        } else {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "user dont have permission to execute");
        }
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
