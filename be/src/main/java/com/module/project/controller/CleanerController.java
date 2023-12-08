package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.dto.request.ScheduleConfirmRequest;
import com.module.project.service.CleanerService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.time.LocalDate;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.CLEANER;
import static com.module.project.dto.Constant.CLEANERS;
import static com.module.project.dto.Constant.CLEANER_AVAILABLE;
import static com.module.project.dto.Constant.CLEANER_HISTORY_DETAIL;
import static com.module.project.dto.Constant.CLEANER_SCHEDULES;
import static com.module.project.dto.Constant.CLEANER_SCHEDULE_STATUS;
import static com.module.project.dto.Constant.CLEANER_STATUS;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class CleanerController {

    private final CleanerService cleanerService;

    @GetMapping(CLEANERS)
    public ResponseEntity<Object> getListCleaner(@RequestParam(name = "page") Integer page,
                                                 @RequestParam(name = "size") Integer size) {
        return ResponseEntity.ok(cleanerService.getCleaners(page, size));
    }

//    @GetMapping(CLEANER_HISTORY)
//    public ResponseEntity<Object> getCleanerHistory(@RequestParam(name = "page") Integer page,
//                                                    @RequestParam(name = "size") Integer size) {
//        return ResponseEntity.ok(cleanerService.getCleanerHistory(page, size));
//    }

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

    @PostMapping(CLEANER_SCHEDULE_STATUS)
    public ResponseEntity<Object> updateStatusSchedule(@RequestBody ScheduleConfirmRequest request, HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(cleanerService.updateStatusSchedule(request, userId));
    }

    @GetMapping(CLEANER_SCHEDULES)
    public ResponseEntity<Object> getCleanerSchedule(@RequestParam(name = "cleanerId") Long cleanerId,
                                                     @RequestParam(name = "page") Integer page,
                                                     @RequestParam(name = "size") Integer size,
                                                     HttpServletRequest httpServletRequest) {
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(cleanerService.getCleanerSchedule(cleanerId, page, size, userId, roleName));
    }

    @GetMapping(CLEANER_AVAILABLE)
    public ResponseEntity<Object> getListCleanerAvailable(@RequestParam(name = "workDate") LocalDate workDate,
                                                          @RequestParam(name = "serviceTypeId") Long serviceTypeId,
                                                          @RequestParam(name = "servicePackageId") Long servicePackageId) {
        return ResponseEntity.ok(cleanerService.getListCleanerAvailable(workDate, serviceTypeId, servicePackageId));
    }
}
