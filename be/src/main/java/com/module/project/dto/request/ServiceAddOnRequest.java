package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAddOnRequest {
    private Long serviceAddOnId;
    private String name;
    private Long parentId;
    private long price;
    private String status;
}
