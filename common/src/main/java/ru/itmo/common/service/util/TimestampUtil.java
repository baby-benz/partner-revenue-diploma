package ru.itmo.common.service.util;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimestampUtil {
    public static String toString(OffsetDateTime timestamp) {
        return timestamp.atZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public static OffsetDateTime fromString(String stringRepresentation) {
        return OffsetDateTime.parse(stringRepresentation, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
