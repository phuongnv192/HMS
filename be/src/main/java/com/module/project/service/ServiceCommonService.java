package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.response.ViewServiceAddOnResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServiceType;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServicePackageRepository;
import com.module.project.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCommonService {
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServicePackageRepository servicePackageRepository;

    public List<ViewServiceAddOnResponse> getAllServiceAddOn(Long addOnId) {
        if (addOnId != -1) {
            ServiceAddOn serviceAddOn = serviceAddOnRepository.findById(addOnId)
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "getAllServiceAddOn: can't find any service add-on by id: ".concat(addOnId.toString())));
            List<ServiceAddOn> children = serviceAddOnRepository.findAllByParentIdAndStatusEquals(serviceAddOn.getId(), Constant.COMMON_STATUS.ACTIVE);
            return List.of(ViewServiceAddOnResponse.builder()
                    .parent(serviceAddOn)
                    .children(children)
                    .build());
        } else {
            List<ServiceAddOn> serviceAddOnList = serviceAddOnRepository.findAllByStatusEquals(Constant.COMMON_STATUS.ACTIVE);
            List<ViewServiceAddOnResponse> response = new ArrayList<>();
            serviceAddOnList.forEach(service -> {
                List<ServiceAddOn> children = serviceAddOnList.stream()
                        .filter(e -> service.getId().equals(e.getParentId()))
                        .toList();
                if (!children.isEmpty()) {
                    response.add(ViewServiceAddOnResponse.builder()
                            .parent(service)
                            .children(children)
                            .build());
                }
            });
            return response;
        }
    }

    public List<ServiceType> getAllServiceType() {
        return serviceTypeRepository.findAll();
    }
}
