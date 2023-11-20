package com.module.project.util;

import com.module.project.model.Cleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
public class HMSUtil {

    public HMSUtil() {
    }

    public static final String COMMA = ",";
    public static final String SPACE = " ";
    public static final String DDMMYYYY_FORMAT = "dd/MM/yyyy";

    public static String convertCleanersToIds(List<Cleaner> cleaners) {
        if (cleaners == null || CollectionUtils.isEmpty(cleaners)) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        cleaners.forEach(e -> sb.append(e.getId()).append(COMMA));
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public static String convertToFullName(String firstName, String lastName) {
        return firstName + SPACE + lastName;
    }

    public static String formatDate(Date date, String format) {
        String rs = null;
        try {
            rs = new SimpleDateFormat(DDMMYYYY_FORMAT).format(date);
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
}
