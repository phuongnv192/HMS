package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.request.ServiceAddOnRequest;
import com.module.project.dto.request.ServiceRequest;
import com.module.project.dto.response.FloorInfoResponse;
import com.module.project.dto.response.ViewServiceAddOnResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServiceType;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.ServiceTypeRepository;
import com.module.project.util.HMSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCommonService {
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceRepository serviceRepository;
    
    public HmsResponse<List<ViewServiceAddOnResponse>> getAllServiceAddOn(Long addOnId) {
        if (addOnId != -1) {
            ServiceAddOn serviceAddOn = serviceAddOnRepository.findById(addOnId)
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "getAllServiceAddOn: can't find any service add-on by id: ".concat(addOnId.toString())));
            List<ServiceAddOn> children = serviceAddOnRepository.findAllByParentIdAndStatusEquals(serviceAddOn.getId(), Constant.COMMON_STATUS.ACTIVE);
            return HMSUtil.buildResponse(ResponseCode.SUCCESS, List.of(ViewServiceAddOnResponse.builder()
                    .parent(serviceAddOn)
                    .children(children)
                    .build()));
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
            return HMSUtil.buildResponse(ResponseCode.SUCCESS, response);
        }
    }

    public HmsResponse<List<ServiceType>> getAllServiceType() {
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, serviceTypeRepository.findAll());
    }

    public HmsResponse<List<FloorInfoResponse>> getFloorInfo() {
        List<FloorInfoResponse> responses = new ArrayList<>();
        FloorInfoEnum[] floorInfoEnums = FloorInfoEnum.values();
        for (FloorInfoEnum item : floorInfoEnums) {
            responses.add(FloorInfoResponse.builder()
                    .floorArea(item.getFloorArea())
                    .cleanerNum(item.getCleanerNum())
                    .duration(item.getDuration())
                    .price(item.getPrice())
                    .build());
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, responses);
    }

    public HmsResponse<ServiceAddOn> updateServiceAddOn(ServiceAddOnRequest request, String roleName) {
        List<String> acceptRole = List.of(RoleEnum.LEADER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        ServiceAddOn serviceAddOn = serviceAddOnRepository.findById(request.getServiceAddOnId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any add on by ".concat(request.getServiceAddOnId().toString())));
        handleServiceAddOnParent(request, serviceAddOn);
        serviceAddOn.setName(request.getName());
        serviceAddOn.setStatus(request.getStatus());
        serviceAddOn.setPrice(request.getPrice());
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, serviceAddOnRepository.save(serviceAddOn));
    }

    public HmsResponse<Objects> insertServiceAddOn(ServiceAddOnRequest request, String roleName) {
       List<String> acceptRole = List.of(RoleEnum.LEADER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        ServiceAddOn serviceAddOn = ServiceAddOn.builder()
                .name(request.getName())
                .status(request.getStatus())
                .price(request.getPrice())
                .build();
        handleServiceAddOnParent(request, serviceAddOn);
        serviceAddOnRepository.save(serviceAddOn);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, null);
    }

    public HmsResponse<List<com.module.project.model.Service>> getAllService() {
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, serviceRepository.findAll());
    }

    public HmsResponse<com.module.project.model.Service> insertService(ServiceRequest request, String roleName) {
       if (!RoleEnum.LEADER.name().equals(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service type by ".concat(request.getServiceTypeId().toString())));
        Set<ServiceAddOn> serviceAddOnSet = new HashSet<>(serviceAddOnRepository.findAllById(request.getAddOnIds()));

        com.module.project.model.Service service = com.module.project.model.Service.builder()
                .serviceName(request.getServiceName())
                .description(request.getDescription())
                .status(Constant.COMMON_STATUS.ACTIVE.equalsIgnoreCase(request.getStatus())
                        ? Constant.COMMON_STATUS.ACTIVE
                        : Constant.COMMON_STATUS.INACTIVE)
                .paymentMethod(request.getPaymentMethod())
                .serviceType(serviceType)
                .serviceAddOnSet(serviceAddOnSet)
                .build();
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, serviceRepository.save(service));
    }

    public HmsResponse<com.module.project.model.Service> updateService(ServiceRequest request, String roleName) {
       if (!RoleEnum.LEADER.name().equals(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        com.module.project.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service by ".concat(request.getServiceId().toString())));
        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any service type by ".concat(request.getServiceTypeId().toString())));
        Set<ServiceAddOn> serviceAddOnSet = new HashSet<>(serviceAddOnRepository.findAllById(request.getAddOnIds()));

        service.setServiceName(request.getServiceName());
        service.setDescription(request.getDescription());
        service.setStatus(Constant.COMMON_STATUS.ACTIVE.equalsIgnoreCase(request.getStatus())
                ? Constant.COMMON_STATUS.ACTIVE
                : Constant.COMMON_STATUS.INACTIVE);
        service.setPaymentMethod(request.getPaymentMethod());
        service.setServiceType(serviceType);
        service.setServiceAddOnSet(serviceAddOnSet);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, serviceRepository.save(service));
    }

    private void handleServiceAddOnParent(ServiceAddOnRequest request, ServiceAddOn serviceAddOn) {
        if (request.getParentId() != null) {
            ServiceAddOn parentId = serviceAddOnRepository.findById(request.getParentId())
                    .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "parent id not existed"));
            if (parentId.getParentId() != null) {
                throw new HmsException(HmsErrorCode.INTERNAL_SERVER_ERROR, "can't update request because the data is not match");
            }
            serviceAddOn.setParentId(parentId.getId());
        } else {
            if (serviceAddOn.getParentId() != null) {
                serviceAddOn.setParentId(null);
            }
        }
    }
}
