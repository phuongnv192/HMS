package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CleanerReviewRequest {
    private Long cleanerId;
    private Integer ratingScore;
    private String review;
}
