package org.gdsccau.team5.safebridge.common.redis.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class RedisUtil {

    public final static Integer TTL = 9;
    public final static String FULL_DATE_FORMAT = "yyyyMMddHHmmssSSS";
    public final static String HOUR_DATE_FORMAT = "yyyyMMddHH";

    public static Double convertToDateFormat(final String format, final LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return Double.parseDouble(time.atZone(ZoneId.of("Asia/Seoul")).format(formatter));
    }
}
