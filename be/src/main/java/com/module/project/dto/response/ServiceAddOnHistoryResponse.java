package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAddOnHistoryResponse {

    private String requestBefore;
    private String requestAfter;
    private String changedBy;
    private Date changeDated;
}
