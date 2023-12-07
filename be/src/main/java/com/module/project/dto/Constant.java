package com.module.project.dto;

public interface Constant {
    String API_V1 = "/api/v1";
    String AUTH = "/auth";
    String REGISTER = "/register";
    String AUTHENTICATE = "/authenticate";
    String USER_INFO = "/user/info";
    String USER_INFO_BY_ID = "/user/{id}";
    String USERS = "/users";
    String USER_SUBMIT_REVIEW = "/user/submit/review";
    String USER_CHANGE_PASSWORD = "/user/change-password";
    String VERIFY = "/verify";
    String BOOKING = "/booking";
    String BOOKING_DETAIL = "/booking/detail";
    String BOOKING_CONFIRM = "/booking/confirm";
    String BOOKING_CANCEL = "/booking/cancel";
    String CLEANER = "/cleaner";
    String CLEANERS = "/cleaners";
    String CLEANER_HISTORY = "/cleaner/history";
    String CLEANER_HISTORY_DETAIL = "/cleaner/history/detail";
    String CLEANER_STATUS = "/cleaner/status";
    String CLEANER_SCHEDULES = "/cleaner/schedules";
    String CLEANER_SCHEDULE_STATUS = "/cleaner/schedule/status";
    String CLEANER_AVAILABLE = "/cleaner/available";
    String SERVICE_TYPE = "/service-types";
    String SERVICE_ADD_ONS = "/service-add-ons";
    String SERVICE_ADD_ON = "/service-add-on";
    String FLOOR_INFO = "/floor-info";
    String UPDATE_WITHDRAW = "/schedule/withdraw";
    String SERVICE = "/service";
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

    interface UNIT {
        String MONTH = "months";
    }
}
