package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerUpdateRequest {
    private Integer id;
    private String status;
    private String address;
    @NotBlank
    private String idCard;
    @NotBlank
    private String userId;
    private String branchId;
    private Set<String> serviceIds;
}
