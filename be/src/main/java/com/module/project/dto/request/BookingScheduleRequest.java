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
public class BookingScheduleRequest {
    private int floorNumber;
    private List<Long> serviceAddOnIds;
}
