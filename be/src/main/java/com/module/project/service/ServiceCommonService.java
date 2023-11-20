package com.module.project.service;

import com.module.project.dto.response.ViewServiceAddOnResponse;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServiceType;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServicePackageRepository;
import com.module.project.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCommonService {
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServicePackageRepository servicePackageRepository;

    public List<ViewServiceAddOnResponse> getAllServiceAddOn(Long addOnId) {
        if (addOnId != null) {
            ServiceAddOn serviceAddOn = serviceAddOnRepository.findById(addOnId)
                    .orElseThrow(() -> new InternalError("getAllServiceAddOn: can't find any service add-on by id: ".concat(addOnId.toString())));
            List<ServiceAddOn> children = serviceAddOnRepository.findAllByParentId(serviceAddOn.getId());
            return List.of(ViewServiceAddOnResponse.builder()
                    .parent(serviceAddOn)
                    .children(children)
                    .build());
        } else {
            List<ServiceAddOn> serviceAddOnList = serviceAddOnRepository.findAll();
            return serviceAddOnList.stream().map(service -> {
                List<ServiceAddOn> children = serviceAddOnList.stream()
                        .filter(e -> e.getParentId().equals(service.getId()))
                        .toList();
                return ViewServiceAddOnResponse.builder()
                        .parent(service)
                        .children(children)
                        .build();
            }).toList();
        }
    }

    public List<ServiceType> getAllServiceType() {
        return serviceTypeRepository.findAll();
    }
}
