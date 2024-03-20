package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.PriceTypeEnum;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.RoleEnum;
import com.module.project.dto.request.ServiceAddOnRequest;
import com.module.project.dto.request.ServiceRequest;
import com.module.project.dto.request.UpdateFloorPrice;
import com.module.project.dto.response.FloorInfoResponse;
import com.module.project.dto.response.ServiceAddOnHistoryResponse;
import com.module.project.dto.response.ViewServiceAddOnResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.BookingSchedule;
import com.module.project.model.FloorInfo;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServiceAddOnHistory;
import com.module.project.model.ServiceType;
import com.module.project.model.User;
import com.module.project.repository.FloorTypeRepository;
import com.module.project.repository.ServiceAddOnHistoryRepository;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServiceRepository;
import com.module.project.repository.ServiceTypeRepository;
import com.module.project.repository.UserRepository;
import com.module.project.util.HMSUtil;
import com.module.project.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCommonService {
    private final ServiceAddOnRepository serviceAddOnRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceAddOnHistoryRepository serviceAddOnHistoryRepository;
    private final UserRepository userRepository;
    private final FloorTypeRepository floorTypeRepository;

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
                List<ServiceAddOn> children;
                if (service.getParentId() == null) {
                    children = serviceAddOnList.stream()
                            .filter(e -> service.getId().equals(e.getParentId()))
                            .toList();
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
        List<FloorInfo> floorInfoList = floorTypeRepository.findAll();
        floorInfoList.sort(Comparator.comparing(FloorInfo::getPrice).reversed());
        for (FloorInfo item : floorInfoList) {
            PriceTypeEnum priceTypeEnum = PriceTypeEnum.lookUp(item.getPriceType());
            responses.add(FloorInfoResponse.builder()
                    .key(item.getFloorKey())
                    .floorArea(item.getDescription())
                    .cleanerNum(item.getCleanerNumber())
                    .duration(item.getDuration())
                    .price(item.getPrice())
                    .priceType(priceTypeEnum != null ? priceTypeEnum.getDescription() : item.getPriceType())
                    .build());
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, responses);
    }

    public HmsResponse<ServiceAddOn> updateServiceAddOn(ServiceAddOnRequest request, String roleName, String userId) {
        List<String> acceptRole = List.of(RoleEnum.LEADER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }

        ServiceAddOn serviceAddOn = serviceAddOnRepository.findById(request.getServiceAddOnId())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any add on by ".concat(request.getServiceAddOnId().toString())));
        String requestBefore = JsonService.writeStringSkipError(serviceAddOn);
        handleServiceAddOnParent(request, serviceAddOn);
        serviceAddOn.setName(request.getName());
        serviceAddOn.setStatus(request.getStatus());
        serviceAddOn.setPrice(request.getPrice());
        serviceAddOn.setDuration(request.getDuration());
        String requestAfter = JsonService.writeStringSkipError(serviceAddOn);
        ServiceAddOnHistory serviceAddOnHistory = ServiceAddOnHistory.builder()
                .type(Constant.ACTION_TYPE.UPDATE)
                .requestBefore(requestBefore)
                .requestAfter(requestAfter)
                .changedBy(userId)
                .build();
        serviceAddOnHistoryRepository.save(serviceAddOnHistory);
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, serviceAddOnRepository.save(serviceAddOn));
    }

    public HmsResponse<Objects> insertServiceAddOn(ServiceAddOnRequest request, String roleName, String userId) {
        List<String> acceptRole = List.of(RoleEnum.LEADER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        ServiceAddOn serviceAddOn = ServiceAddOn.builder()
                .name(request.getName())
                .status(request.getStatus())
                .price(request.getPrice())
                .duration(request.getDuration())
                .build();
        handleServiceAddOnParent(request, serviceAddOn);
        ServiceAddOnHistory serviceAddOnHistory = ServiceAddOnHistory.builder()
                .type(Constant.ACTION_TYPE.CREATE)
                .requestAfter(JsonService.writeStringSkipError(serviceAddOn))
                .changedBy(userId)
                .build();
        serviceAddOnHistoryRepository.save(serviceAddOnHistory);
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

    public HmsResponse<List<ServiceAddOnHistoryResponse>> getServiceAddOnHistory(Integer page, Integer size) {
        List<ServiceAddOnHistoryResponse> responses = new ArrayList<>();
        List<ServiceAddOnHistory> serviceAddOnHistoryList = serviceAddOnHistoryRepository.findAll(PageRequest.of(page, size)).getContent();
        List<Long> userIds = serviceAddOnHistoryList.stream().map(object -> Long.parseLong(object.getChangedBy())).toList();
        Map<Long, User> userList = userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        for (ServiceAddOnHistory serviceAddOnHistory : serviceAddOnHistoryList) {
            User user = userList.get(Long.parseLong(serviceAddOnHistory.getChangedBy()));
            responses.add(ServiceAddOnHistoryResponse.builder()
                    .actionType(serviceAddOnHistory.getType())
                    .requestAfter(serviceAddOnHistory.getRequestAfter())
                    .requestBefore(serviceAddOnHistory.getRequestBefore())
                    .changeDated(serviceAddOnHistory.getChangeDated())
                    .changedBy(HMSUtil.convertToFullName(user.getFirstName(), user.getLastName()))
                    .build());
        }
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, responses);
    }

    public HmsResponse<FloorInfo> updateFloorPrice(UpdateFloorPrice request, String roleName, String userId) {
        List<String> acceptRole = List.of(RoleEnum.LEADER.name(), RoleEnum.MANAGER.name());
        if (!acceptRole.contains(roleName)) {
            throw new HmsException(HmsErrorCode.INVALID_REQUEST, "privileges access denied");
        }
        FloorInfo floorInfo = floorTypeRepository.findByFloorKey(request.getFloorKey())
                .orElseThrow(() -> new HmsException(HmsErrorCode.INVALID_REQUEST, "can't find any floor type by ".concat(request.getFloorKey())));
        floorInfo.setPrice(request.getPrice());
        floorInfo.setPriceType(request.getPriceType().name());
        floorInfo.setDescription(request.getDescription());
        floorInfo.setCleanerNumber(request.getCleanerNumber());
        floorInfo.setDuration(request.getDuration());
        return HMSUtil.buildResponse(ResponseCode.SUCCESS, floorTypeRepository.save(floorInfo));
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