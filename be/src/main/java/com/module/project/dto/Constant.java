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
    String CLEANER_HISTORY_DETAIL = "/cleaner/history/detail";
    String CLEANER_STATUS = "/cleaner/status";
    String SERVICE_TYPE = "/service-types";
    String SERVICE_ADD_ON = "/service-add-ons";

    interface CHOOSE_TYPE {
        String AUTO = "AUTO";
        String MANUAL = "MANUAL";
    }

    interface COMMON_STATUS {
        String ACTIVE = "ACTIVE";
        String INACTIVE = "INACTIVE";
    }

    interface PAYMENT_TYPE {
        String CASH = "CASH";
    }

    interface HOUSE_TYPE {
        String APARTMENT = "APARTMENT";
        String HOUSE_VILLA = "HOUSE/VILLA";
    }
}
