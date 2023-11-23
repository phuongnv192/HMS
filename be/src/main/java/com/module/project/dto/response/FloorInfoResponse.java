package com.module.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FloorInfoResponse {
    private int floorArea;
    private int cleanerNum;
    private int duration;
    private long price;
}
