package com.module.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerActivity {
    private Long bookingScheduleId;
    private Date workDate;
    private Long ratingScore;
    private String review;
}
