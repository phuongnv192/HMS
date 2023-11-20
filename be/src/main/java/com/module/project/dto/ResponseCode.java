package com.module.project.dto;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("00", "Success"),
    ;
    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCode lookUp(String name) throws IllegalArgumentException {
        for (ResponseCode val : values()) {
            if (val.name().equalsIgnoreCase(name))
                return val;
        }
        return null;
    }
}
