package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingStatusRequest;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.BOOKING;
import static com.module.project.dto.Constant.BOOKING_CANCEL;
import static com.module.project.dto.Constant.BOOKING_CONFIRM;
import static com.module.project.dto.Constant.UPDATE_WITHDRAW;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(BOOKING)
    public ResponseEntity<Object> booking(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.booking(request));
    }

    @PostMapping(BOOKING_CONFIRM)
    public ResponseEntity<Object> customerConfirmBooking(@RequestBody BookingStatusRequest request, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(bookingService.confirmBooking(request, Long.parseLong(userId)));
    }

    @PostMapping(BOOKING_CANCEL)
    public ResponseEntity<Object> customerCancelBooking(@RequestBody BookingStatusRequest request, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(bookingService.cancelBooking(request, Long.parseLong(userId)));
    }

    @PostMapping(UPDATE_WITHDRAW)
    public ResponseEntity<Object> updateWithdraw(@RequestBody BookingStatusRequest request, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(bookingService.updateWithdraw(request.getBookingId(), userId, roleName));
    }

    @GetMapping(BOOKING)
    public ResponseEntity<Map<String, Object>> getBookingDetail(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(bookingService.getBookingDetail(new HashMap<>()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(new HashMap<>());
        }
    }
}
