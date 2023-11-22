package com.module.project.controller;

import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.dto.request.ScheduleConfirmRequest;
import com.module.project.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.CLEANER;
import static com.module.project.dto.Constant.CLEANERS;
import static com.module.project.dto.Constant.CLEANER_CANCEL_SCHEDULE;
import static com.module.project.dto.Constant.CLEANER_CONFIRM_SCHEDULE;
import static com.module.project.dto.Constant.CLEANER_HISTORY;
import static com.module.project.dto.Constant.CLEANER_HISTORY_DETAIL;
import static com.module.project.dto.Constant.CLEANER_STATUS;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class CleanerController {

    private final CleanerService cleanerService;

    @GetMapping(CLEANERS)
    public ResponseEntity<Object> getCleaners(@RequestBody CleanerFilterRequest request) {
        return ResponseEntity.ok(cleanerService.autoChooseCleaner(request.getNumber(), request.getWorkDate()));
    }

    @GetMapping(CLEANER_HISTORY)
    public ResponseEntity<Object> getCleanerHistory(@RequestParam(name = "page") Integer page,
                                                    @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(cleanerService.getCleanerHistory(page, size));
    }

    @GetMapping(CLEANER_HISTORY_DETAIL)
    public ResponseEntity<Object> getCleanerHistoryDetail(@RequestParam(name = "cleanerId") Long cleanerId) {
        return ResponseEntity.ok(cleanerService.getCleanerHistoryDetail(cleanerId));
    }

    @PostMapping(CLEANER)
    public ResponseEntity<Object> insertCleaner(@RequestBody @Validated CleanerInfoRequest cleanerInfoRequest) {
        return ResponseEntity.ok(cleanerService.insertCleaner(cleanerInfoRequest));
    }

    @PutMapping(CLEANER)
    public ResponseEntity<Object> updateCleaner(@RequestBody @Validated CleanerUpdateRequest cleanerUpdateRequest) {
        return ResponseEntity.ok(cleanerService.updateCleaner(cleanerUpdateRequest));
    }

    @PutMapping(CLEANER_STATUS)
    public ResponseEntity<Object> changeStatusCleaner(
            @RequestBody @Validated CleanerUpdateRequest cleanerUpdateRequest) {
        return ResponseEntity.ok(cleanerService.changeStatusCleaner(cleanerUpdateRequest));
    }

    @PostMapping(CLEANER_CONFIRM_SCHEDULE)
    public ResponseEntity<Object> confirmSchedule(@RequestBody ScheduleConfirmRequest request) {
        return ResponseEntity.ok(cleanerService.confirmSchedule(request));
    }

    @PostMapping(CLEANER_CANCEL_SCHEDULE)
    public ResponseEntity<Object> cancelSchedule(@RequestBody ScheduleConfirmRequest request) {
        return ResponseEntity.ok(cleanerService.confirmSchedule(request));
    }
}
