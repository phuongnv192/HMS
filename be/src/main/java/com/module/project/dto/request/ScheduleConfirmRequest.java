package com.module.project.dto.request;

import com.module.project.dto.PaymentStatus;
import com.module.project.dto.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleConfirmRequest {
    private Long scheduleId;
    private TransactionStatus status;
    private PaymentStatus paymentStatus;
    private String note;
}
