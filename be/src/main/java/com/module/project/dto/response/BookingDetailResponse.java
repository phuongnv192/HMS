package com.module.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.module.project.model.BookingSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDetailResponse {
    private Long bookingId;

    private String hostName;
    private String hostPhone;
    private String hostAddress;
    private String hostDistance;
    private String houseType;
    private int floorNumber;
    private float floorArea;

    private Long bookingTransactionId;
    private String serviceTypeName;
    private String servicePackageName;
    private double totalBookingPrice;
    private int totalBookingCleaner;
    private float totalBookingDate;
    private Date createDate;
    private Date updateDate;
    private String status;
    private String review;
    private String rejectedReason;

    private List<BookingSchedule> scheduleList;
}
