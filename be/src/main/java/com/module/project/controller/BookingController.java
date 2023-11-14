package com.module.project.controller;

import com.module.project.dto.request.RegisterRequest;
import com.module.project.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.BOOK;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @PostMapping(BOOK)
    public ResponseEntity<Map<String, Object>> book(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.book(new HashMap<>()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(new HashMap<>());
        }
    }

    @GetMapping(BOOK)
    public ResponseEntity<Map<String, Object>> getBookingDetail(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(service.getBookingDetail(new HashMap<>()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(new HashMap<>());
        }
    }
}
