package com.module.project.controller;

import com.module.project.dto.request.BookingRequest;
import com.module.project.dto.request.RegisterRequest;
import com.module.project.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.BOOKING;
import static com.module.project.dto.Constant.BOOKING_CONFIRM;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping(BOOKING)
    public ResponseEntity<Object> book(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(service.booking(request));
    }

    @PostMapping(BOOKING_CONFIRM)
    public ResponseEntity<Object> customerConfirmBooking(@RequestParam(name = "bookingId") Long bookingId,
                                                 @RequestParam(name = "userId") Long userId) {
        return ResponseEntity.ok(service.confirmBooking(bookingId, userId));
    }

    @GetMapping(BOOKING)
    public ResponseEntity<Map<String, Object>> getBookingDetail(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.getBookingDetail(new HashMap<>()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(new HashMap<>());
        }
    }
}
