package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.request.ServiceAddOnRequest;
import com.module.project.service.ServiceCommonService;
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
import static com.module.project.dto.Constant.FLOOR_INFO;
import static com.module.project.dto.Constant.SERVICE_ADD_ON;
import static com.module.project.dto.Constant.SERVICE_ADD_ONS;
import static com.module.project.dto.Constant.SERVICE_TYPE;

@RestController
@RequestMapping(API_V1)
@RequiredArgsConstructor
public class ServiceAddOnController {

    private final ServiceCommonService serviceCommonService;

    @GetMapping(SERVICE_ADD_ONS)
    public ResponseEntity<Object> getServiceAddOns(@RequestParam(name = "addOnId") Long addOnId) {
        return ResponseEntity.ok(serviceCommonService.getAllServiceAddOn(addOnId));
    }

    @GetMapping(SERVICE_TYPE)
    public ResponseEntity<Object> getAllServiceType() {
        return ResponseEntity.ok(serviceCommonService.getAllServiceType());
    }

    @GetMapping(FLOOR_INFO)
    public ResponseEntity<Object> getFloorInfo() {
        return ResponseEntity.ok(serviceCommonService.getFloorInfo());
    }

    @PostMapping(SERVICE_ADD_ON)
    public ResponseEntity<Object> insertServiceAddOn(@RequestBody ServiceAddOnRequest request, HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(serviceCommonService.insertServiceAddOn(request, roleName));
    }

    @PutMapping(SERVICE_ADD_ON)
    public ResponseEntity<Object> updateServiceAddOn(@RequestBody ServiceAddOnRequest request, HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(serviceCommonService.updateServiceAddOn(request, roleName));
    }
}
