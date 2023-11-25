package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.BookingStatusRequest;
import com.module.project.service.BookingService;
import com.module.project.util.HMSUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.BOOKING;
import static com.module.project.dto.Constant.BOOKING_CANCEL;
import static com.module.project.dto.Constant.BOOKING_CONFIRM;
import static com.module.project.dto.Constant.BOOKING_DETAIL;
import static com.module.project.dto.Constant.UPDATE_WITHDRAW;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(BOOKING)
    public ResponseEntity<Object> getBookings(@RequestParam(name = "page") Integer page,
                                              @RequestParam(name = "size") Integer size,
                                              HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(bookingService.getBookingList(page, size, roleName));
    }

    @PostMapping(BOOKING)
    public ResponseEntity<Object> booking(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.booking(request));
    }

    @PutMapping(BOOKING)
    public ResponseEntity<Object> updateBooking(@RequestBody BookingRequest request, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(bookingService.updateBooking(request, userId));
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

    @GetMapping(BOOKING_DETAIL)
    public ResponseEntity<Object> getBookingDetail(@RequestParam(name = "bookingId") Long bookingId, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(HMSUtil.buildResponse(ResponseCode.SUCCESS, bookingService.getBookingDetail(bookingId, userId, roleName, true)));
    }
}
