package com.module.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardInfoResponse {
    private ItemDetail summary;
    private Map<String, ItemDetail> months;

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemDetail {
        private Integer bookingNumber;
        private Double averageRating;
        private Long newUserNumber;
        private Map<String, Integer> numberByBookingStatus;
        private Integer bookingNumberHaveReview;
        private BigDecimal totalProfit;
        private BigDecimal totalProfitWithdraw;
    }
}
