package com.luxx.log.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static Logger logger = LogManager.getLogger(DateTimeUtil.class);

    private static final DateTimeFormatter YYYY_MM = DateTimeFormat.forPattern("yyyy_MM");
    private static final DateTimeFormatter YMD_HMS_SSS = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter YMD_HMS_Z = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static String currentYM() {
        return YYYY_MM.print(DateTime.now());
    }

    public static String getEsString(long timestamp) {
        return YMD_HMS_Z.print(timestamp);
    }

    public static DateTime convertToDateTime(String timestamp) {
        return YMD_HMS_SSS.parseDateTime(timestamp);
    }
}
