package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingScheduleRequest {
    private int floorNumber;
    private LocalDate workDate;
    private Date startTime;
    private Date endTime;
    private List<Long> serviceAddOnIds;
}
