package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerInfoRequest {
    private String address;
    @NotBlank
    private String idCard;
    @NotBlank
    private Long userId;
    private Long branchId;
    private List<Long> serviceIds;
}
