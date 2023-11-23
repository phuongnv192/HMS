package com.module.project.dto;

public enum TransactionStatus {
    ON_MOVING,
    MATCHED,
    ON_PROCESS,
    DONE,
    CANCELLED;

    public static TransactionStatus lookUp(String name) throws IllegalArgumentException {
        for (TransactionStatus val : values()) {
            if (val.name().equalsIgnoreCase(name))
                return val;
        }
        return null;
    }
}
