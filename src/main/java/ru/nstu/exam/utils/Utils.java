package ru.nstu.exam.utils;

import java.time.Instant;
import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

public class Utils {

    public static long toMillis(LocalDateTime localDateTime) {
        return localDateTime.toInstant(UTC).toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), UTC);
    }
}
