package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerOverviewResponse {
    private Long cleanerId;
    private String name;
    private int activityYear;
    private Long averageRating;
    private int ratingNumber;
}
