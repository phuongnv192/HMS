package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerInfoRequest {
    private String address;
    private String idCard;
    private String userId;
    private String branchId;
    private Set<String> serviceIds;
}
