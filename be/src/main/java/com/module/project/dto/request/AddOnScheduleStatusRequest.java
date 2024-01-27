package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AddOnScheduleStatusRequest {
    private Long serviceAddOnId;
    private Long price;
    private String note;
}
