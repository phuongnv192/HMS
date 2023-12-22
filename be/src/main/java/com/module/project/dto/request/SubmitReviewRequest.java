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
public class SubmitReviewRequest {
    private Long bookingId;
    private Long scheduleId;
    private Boolean reviewBooking;
    private List<CleanerReviewRequest> cleaners;
    private String review;
}
