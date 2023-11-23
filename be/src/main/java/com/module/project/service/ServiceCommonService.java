package com.module.project.service;

import com.module.project.dto.Constant;
import com.module.project.dto.FloorInfoEnum;
import com.module.project.dto.ResponseCode;
import com.module.project.dto.response.FloorInfoResponse;
import com.module.project.dto.response.ViewServiceAddOnResponse;
import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsException;
import com.module.project.exception.HmsResponse;
import com.module.project.model.ServiceAddOn;
import com.module.project.model.ServiceType;
import com.module.project.repository.ServiceAddOnRepository;
import com.module.project.repository.ServiceTypeRepository;
import com.module.project.util.HMSUtil;
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
}
