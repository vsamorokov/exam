package ru.nstu.exam.utils;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.exception.ExamException.userError;

public class Utils {

    public static long toMillis(LocalDateTime localDateTime) {
        return localDateTime.toInstant(UTC).toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), UTC);
    }

    public static void checkNotNull(Object o, String ifNot) {
        if (o == null) {
            userError(ifNot);
        }
    }

    public static void checkNull(Object o, String ifNot) {
        if (o != null) {
            userError(ifNot);
        }
    }

    public static void checkNotEmpty(String str, String ifNot) {
        if (!StringUtils.hasText(str)) {
            userError(ifNot);
        }
    }

    public static void checkEmpty(String str, String ifNot) {
        if (StringUtils.hasText(str)) {
            userError(ifNot);
        }
    }

    public static void checkNotEmpty(Collection<?> collection, String ifNot) {
        if (CollectionUtils.isEmpty(collection)) {
            userError(ifNot);
        }
    }

    public static void checkEmpty(Collection<?> collection, String ifNot) {
        if (CollectionUtils.isNotEmpty(collection)) {
            userError(ifNot);
        }
    }

    public static void checkTrue(boolean b, String ifNot) {
        if (!b) {
            userError(ifNot);
        }
    }

    public static void checkFalse(boolean b, String ifNot) {
        if (b) {
            userError(ifNot);
        }
    }
}
