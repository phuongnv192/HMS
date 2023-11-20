package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CleanerDetailHistoryResponse {
    private CleanerOverviewResponse ratingOverview;
    private List<CleanerHistoryResponse> history;
}
