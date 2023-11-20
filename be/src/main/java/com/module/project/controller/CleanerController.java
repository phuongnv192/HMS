package com.module.project.controller;

import com.module.project.dto.request.ChooseCleanerRequest;
import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.dto.request.CleanerInfoRequest;
import com.module.project.dto.request.CleanerUpdateRequest;
import com.module.project.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.*;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class CleanerController {

    private final CleanerService cleanerService;

    @GetMapping(CLEANERS)
    public ResponseEntity<Object> getCleaners(@RequestBody CleanerFilterRequest request) {
        return ResponseEntity.ok(cleanerService.getCleaners(request));
    }

    @GetMapping(CHOOSE_CLEANER)
    public ResponseEntity<Object> chooseCleaner(@RequestBody ChooseCleanerRequest chooseCleanerRequest) {
        return ResponseEntity.ok(cleanerService.chooseCleaner(chooseCleanerRequest));
    }

    @GetMapping(CLEANER_HISTORY)
    public ResponseEntity<Object> getCleanerHistory(@RequestParam(name = "cleaner_id") @NotBlank Integer cleanerId) {
        return ResponseEntity.ok(cleanerService.getCleanerHistory(cleanerId));
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
}
