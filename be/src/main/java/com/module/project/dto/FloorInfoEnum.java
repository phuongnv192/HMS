package com.module.project.dto;

import lombok.Getter;

@Getter
public enum FloorInfoEnum {
    M260(60, 2, 3, 500000),
    M280(80, 2, 4, 680000),
    M2100(100, 3, 3, 780000),
    M2150(150, 3, 4, 1000000),
    M2200(200, 4, 4, 1400000),
    M2400(400, 4, 8, 3000000),
    ;

    private final int floorArea; //m2
    private final int cleanerNum;
    private final int duration; // hour
    private final long price; // dong

    FloorInfoEnum(int floorArea, int cleanerNum, int duration, long price) {
        this.floorArea = floorArea;
        this.cleanerNum = cleanerNum;
        this.duration = duration;
        this.price = price;
    }

    public static FloorInfoEnum lookUp(String name) throws IllegalArgumentException {
        for (FloorInfoEnum val : values()) {
            if (val.name().equalsIgnoreCase(name))
                return val;
        }
        return null;
    }
}
