package com.module.project.controller;

import com.module.project.dto.ClaimEnum;
import com.module.project.dto.request.ServiceAddOnRequest;
import com.module.project.dto.request.ServiceRequest;
import com.module.project.dto.request.UpdateFloorPrice;
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
import static com.module.project.dto.Constant.FLOOR_INFO_UPDATE;
import static com.module.project.dto.Constant.SERVICE;
import static com.module.project.dto.Constant.SERVICE_ADD_ON;
import static com.module.project.dto.Constant.SERVICE_ADD_ONS;
import static com.module.project.dto.Constant.SERVICE_ADD_ON_HISTORY;
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
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(serviceCommonService.insertServiceAddOn(request, roleName, userId));
    }

    @PutMapping(SERVICE_ADD_ON)
    public ResponseEntity<Object> updateServiceAddOn(@RequestBody ServiceAddOnRequest request, HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(serviceCommonService.updateServiceAddOn(request, roleName, userId));
    }

    @GetMapping(SERVICE)
    public ResponseEntity<Object> getAllService() {
        return ResponseEntity.ok(serviceCommonService.getAllService());
    }

    @PostMapping(SERVICE)
    public ResponseEntity<Object> insertService(@RequestBody ServiceRequest request, HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(serviceCommonService.insertService(request, roleName));
    }

    @PutMapping(SERVICE)
    public ResponseEntity<Object> updateService(@RequestBody ServiceRequest request, HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        return ResponseEntity.ok(serviceCommonService.updateService(request, roleName));
    }

    @GetMapping(SERVICE_ADD_ON_HISTORY)
    public ResponseEntity<Object> getServiceAddOnHistory(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return ResponseEntity.ok(serviceCommonService.getServiceAddOnHistory(page, size));
    }

    @PostMapping(FLOOR_INFO_UPDATE)
    public ResponseEntity<Object> updateFloorInfo(@RequestBody UpdateFloorPrice request, HttpServletRequest httpServletRequest) {
        String roleName = (String) httpServletRequest.getAttribute(ClaimEnum.ROLE_NAME.name);
        String userId = (String) httpServletRequest.getAttribute(ClaimEnum.USER_ID.name);
        return ResponseEntity.ok(serviceCommonService.updateFloorPrice(request, roleName, userId));
    }
}
