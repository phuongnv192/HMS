package com.module.project.util;

import com.module.project.dto.ResponseCode;
import com.module.project.exception.HmsResponse;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class HMSUtil {

    public HMSUtil() {
    }

    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String DDMMYYYY_FORMAT = "dd/MM/yyyy";
    public static final String DDMMYYYYHHMMSS_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String MMYYYY_FORMAT = "MM/yyyy";

    public static String convertToFullName(String firstName, String lastName) {
        return firstName.trim() + SPACE + lastName.trim();
    }

    public static String formatDate(Date date, String format) {
        String rs = null;
        try {
            rs = new SimpleDateFormat(format).format(date);
        } catch (Exception ex) {
            log.warn("Exception when format date {} with detail: {}", date, ex.getMessage());
        }
        return rs;
    }

    public static int calculateActivityYear(Date from, Date to) {
        if (from == null || to == null || to.before(from)) {
            return 0;
        }
        Calendar toC = Calendar.getInstance();
        toC.setTime(to);
        Calendar fromC = Calendar.getInstance();
        fromC.setTime(from);
        return toC.get(Calendar.YEAR) - fromC.get(Calendar.YEAR);
    }

    public static <T> HmsResponse<T> buildResponse(ResponseCode responseCode, T data) {
        HmsResponse<T> response = new HmsResponse<>();
        response.setCode(responseCode.getCode());
        response.setMessage(responseCode.getMessage());
        response.setData(data);
        return response;
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static List<LocalDate> getDatesBetweenFromDate(Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return getDatesBetween(start, end);
    }

    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate).collect(Collectors.toList());
    }

    public static List<LocalDate> weeksInCalendar(LocalDate now, LocalDate month) {
        List<LocalDate> daysOfWeeks = new ArrayList<>();
        for (LocalDate day = now; stillInCalendar(month, day); day = day.plusWeeks(1)) {
            daysOfWeeks.add(day);
        }
        return daysOfWeeks;
    }

    public static List<LocalDate> monthsInCalendar(LocalDate now, LocalDate month) {
        List<LocalDate> daysOfMonth = new ArrayList<>();
        for (LocalDate day = now; stillInCalendar(month, day); day = day.plusMonths(1)) {
            daysOfMonth.add(day);
        }
        return daysOfMonth;
    }

    public static Date convertLocalDateToDate(LocalDate localDate, Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return Date.from(localDate.atTime(localDateTime.toLocalTime()).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Calendar setLocalDateToCalendar(Calendar calendar, LocalDate localDate) {
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.MONTH, localDate.getMonthValue() - 1);
        calendar.set(Calendar.DATE, localDate.getDayOfMonth());
        return calendar;
    }

    private static boolean stillInCalendar(LocalDate month, LocalDate day) {
        return !day.isAfter(month);
    }
}
