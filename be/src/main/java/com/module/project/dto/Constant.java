package com.module.project.dto;

public interface Constant {
    String API_V1 = "/api/v1";
    String AUTH = "/auth";
    String REGISTER = "/register";
    String AUTHENTICATE = "/authenticate";
    String BOOK = "/book";
    String CLEANER = "/cleaner";

    String CLEANERS = "/cleaners";
    String CHOOSE_CLEANER = "/cleaner/choose";
    String CLEANER_HISTORY = "/cleaner/history";
    String CLEANER_STATUS = "/cleaner/status";

    interface CHOOSE_TYPE {
        String AUTO = "AUTO";
        String MANUAL = "MANUAL";
    }

    interface COMMON_STATUS {
        String ACTIVE = "ACTIVE";
        String INACTIVE = "INACTIVE";
    }
}
