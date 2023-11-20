package com.module.project.controller;

import com.module.project.service.ServiceCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.module.project.dto.Constant.API_V1;
import static com.module.project.dto.Constant.SERVICE_ADD_ON;
import static com.module.project.dto.Constant.SERVICE_TYPE;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class ServiceAddOnController {

    private final ServiceCommonService service;

    @GetMapping(SERVICE_ADD_ON)
    public ResponseEntity<Object> getServiceAddOn(@RequestParam(name = "addOnId") Long addOnId) {
        return ResponseEntity.ok(service.getAllServiceAddOn(addOnId));
    }

    @GetMapping(SERVICE_TYPE)
    public ResponseEntity<Object> getAllServiceType() {
        return ResponseEntity.ok(service.getAllServiceType());
    }
}
