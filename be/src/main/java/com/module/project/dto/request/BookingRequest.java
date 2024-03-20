package com.module.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long bookingId;
    private String hostName;
    private String hostPhone;
    private String hostAddress;
    private String hostDistance;
    private Long distancePrice;
    private String houseType;
    private int floorNumber;
    private List<Long> cleanerIds;
    private List<BookingScheduleRequest> bookingSchedules;
    private Long serviceTypeId;
    private Long servicePackageId;
    private List<Long> serviceAddOnIds;
    private Date startTime;
    private Integer expectCompletedIn;
    private Date endTime;
    private LocalDate workDate;
    private String note;
    private String paymentType;
}
