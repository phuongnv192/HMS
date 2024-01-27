package com.module.project.dto.request;

import com.module.project.dto.PaymentStatus;
import com.module.project.dto.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleConfirmRequest {
    private Long scheduleId;
    private TransactionStatus status;
    private PaymentStatus paymentStatus;
    private List<AddOnScheduleStatusRequest> addOns;
    private String note;
}
