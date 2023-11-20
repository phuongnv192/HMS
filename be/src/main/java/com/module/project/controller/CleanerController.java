package com.module.project.controller;

import com.module.project.dto.request.CleanerFilterRequest;
import com.module.project.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Object> chooseCleaner(@RequestParam(name = "type", defaultValue =  CHOOSE_TYPE.AUTO)  String type) {
        return ResponseEntity.ok(cleanerService.chooseCleaner(type));
    }

    @GetMapping(CLEANER_HISTORY)
    public ResponseEntity<Object> getCleanerHistory(@RequestParam(name = "cleaner_id") @NotBlank String cleanerId) {
        return ResponseEntity.ok(cleanerService.chooseCleaner(cleanerId));
    }
}
