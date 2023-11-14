package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerHistoryResponse {
    private String name;
    private String ratingScore;
    private String workDate;
    private String serviceType;
    private String servicePackage;
    private int floorNumber;
    private float floorArea;
    private String review;
}
