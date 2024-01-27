package com.module.project.dto;

import lombok.Getter;

@Getter
public enum FloorInfoEnum {
//    M260(60, 2, 3, 480000),
//    M280(80, 2, 4, 680000),
//    M2100(100, 3, 3, 780000),
//    M2150(150, 3, 4, 1000000),
//    M2200(200, 4, 4, 1400000),
//    M2400(400, 4, 8, 3000000),

    M230("Dưới 30m2", 1, 3, 240000),
    M23060("30m2 - 60m2", 2, 3, 480000),
    M26080("60m2 - 80m2", 2, 4, 640000),
    M280100("80m2 - 100m2", 3, 3, 720000),
    M2100120("100m2 - 120m2", 3, 4, 960000),
    M2120150("120m2 - 150m2", 4, 3, 1060000),
    M2150200("150m2 - 200m2", 4, 4, 1280000),
    ;

    private final String floorArea; //m2
    private final int cleanerNum;
    private final int duration; // hour
    private final long price; // dong

    FloorInfoEnum(String floorArea, int cleanerNum, int duration, long price) {
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
