package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerUpdateRequest {
    private Long id;
    private String status;
    private String address;
    private LocalDate dob;
    @NotBlank
    private String idCard;
    @NotBlank
    private Long userId;
    private Long branchId;
    private List<Long> serviceIds;
}
