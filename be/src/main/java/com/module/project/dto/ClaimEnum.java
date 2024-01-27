package com.module.project.dto;

public enum ClaimEnum {

    USER_ID("userId"),
    ROLE_ID("roleId"),
    ROLE_NAME("roleName")
    ;

    public final String name;

    ClaimEnum(String name) {
        this.name = name;
    }
}
