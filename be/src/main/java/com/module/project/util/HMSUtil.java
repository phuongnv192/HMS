package com.module.project.util;

import com.module.project.model.Cleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class HMSUtil {

    public HMSUtil() {
    }

    private static final String COMMA = ",";
    private static final String SPACE = " ";

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
}
