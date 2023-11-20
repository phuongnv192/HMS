package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerHistoryResponse {
    private String name;
    private Long ratingScore;
    private String workDate;
    private String houseType;
    private int floorNumber;
    private float floorArea;
    private String review;
}
