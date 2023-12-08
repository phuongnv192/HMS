package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitReviewRequest {
    private Long bookingId;
    private Long scheduleId;
    private Long cleanerId;
    private Boolean reviewBooking;
    private Integer ratingScore;
    private String review;
}
