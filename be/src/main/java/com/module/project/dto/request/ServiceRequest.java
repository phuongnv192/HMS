package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    private Long serviceId;
    private String serviceName;
    private String description;
    private String status;
    private String paymentMethod;

    private Long serviceTypeId;

    private List<Long> addOnIds;
}
