package com.module.project.dto;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    ON_MOVING("On moving"),
    MATCHED("Matched"),
    ON_PROCESS("On process"),
    DONE("Done"),
    CANCELLED("Cancelled");

    private final String name;

    TransactionStatus(String name) {
        this.name = name;
    }

    public static TransactionStatus lookUp(String name) throws IllegalArgumentException {
        for (TransactionStatus val : values()) {
            if (val.name().equalsIgnoreCase(name))
                return val;
        }
        return null;
    }
}
