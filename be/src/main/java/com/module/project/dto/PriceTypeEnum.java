package com.module.project.dto;

import lombok.Getter;

@Getter
public enum PriceTypeEnum {
    PACKAGE("Giá trọn gói"),
    FLOOR_AREA("Giá theo diện tích sàn")
    ;

    private final String description;

    PriceTypeEnum(String description) {
        this.description = description;
    }

    public static PriceTypeEnum lookUp(String name) throws IllegalArgumentException {
        for (PriceTypeEnum val : values()) {
            if (val.name().equalsIgnoreCase(name))
                return val;
        }
        return null;
    }
}
