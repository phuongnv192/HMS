package com.module.project.dto;

public interface Constant {
    String API_V1 = "/api/v1";
    String AUTH = "/auth";
    String REGISTER = "/register";
    String AUTHENTICATE = "/authenticate";
    String USER_SUBMIT_REVIEW = "/user/submit/review";
    String VERIFY = "/verify";
    String BOOKING = "/booking";
    String BOOKING_CONFIRM = "/booking/confirm";
    String BOOKING_CANCEL = "/booking/cancel";
    String CLEANER = "/cleaner";
    String CLEANERS = "/cleaners";
    String CLEANER_HISTORY = "/cleaner/history";
    String CLEANER_HISTORY_DETAIL = "/cleaner/history/detail";
    String CLEANER_STATUS = "/cleaner/status";
    String CLEANER_SCHEDULE_STATUS = "/cleaner/schedule/status";
    String CLEANER_AVAILABLE = "/cleaner/available";
    String SERVICE_TYPE = "/service-types";
    String SERVICE_ADD_ON = "/service-add-ons";
    String FLOOR_INFO = "/floor-info";
    String UPDATE_WITHDRAW = "/schedule/withdraw";

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
