package com.module.project.dto.request;

import com.module.project.dto.PriceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFloorPrice {
    private String floorKey;
    private Integer cleanerNumber;
    private Integer duration;
    private Long price;
    private PriceTypeEnum priceType;
    private String description;
}
