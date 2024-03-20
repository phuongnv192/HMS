package com.module.project.dto;

import lombok.Getter;

@Getter
public enum FloorInfoEnum {

    M230("Dưới 30m2", 1, 3, 550000),
    M250("Dưới 50m2", 2, 3, 18000),
    M2100("Dưới 100m2", 2, 4, 15000),
    M2200("Dưới 200m2", 3, 3, 12000),
    M2U200("Trên 200m2", 3, 4, 10000),
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

    public static FloorInfoEnum lookUp(int floorNumber) throws IllegalArgumentException {
        if (floorNumber < 30) {
            return FloorInfoEnum.M230;
        } else if (floorNumber < 50) {
            return FloorInfoEnum.M250;
        } else if (floorNumber < 100) {
            return FloorInfoEnum.M2100;
        } else if (floorNumber < 200) {
            return FloorInfoEnum.M2200;
        } else {
            return FloorInfoEnum.M2U200;
        }
    }
}
