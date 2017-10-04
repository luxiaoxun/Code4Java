package com.es.jdbc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date Format Helper
 * 
 * @author luxiaoxun
 * @version 1.0
 * @since 2015.05.18
 */
public class DateFormatHelper {

    public static String ISO_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
    public static String LEGACY_FORMAT = "EEE MMM dd hh:mm:ss zzz yyyy";
    private static final TimeZone utc = TimeZone.getTimeZone("UTC");
    private static final SimpleDateFormat legacyFormatter = new SimpleDateFormat(LEGACY_FORMAT);
    private static final SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
    private static final SimpleDateFormat isoFormatterUtc = new SimpleDateFormat(ISO_FORMAT_UTC);

    static {
        legacyFormatter.setTimeZone(utc);
        isoFormatter.setTimeZone(utc);
        isoFormatterUtc.setTimeZone(utc);
    }

    /*
     * Convert string "yyyy-MM-dd HH:mm:ss" to date
     */
    public static Date StringToDate(String text) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(utc);
        Date date = null;
        try {
            date = sdf.parse(text);
        } catch (ParseException e) {
            throw e;
        }

        return date;
    }

    /*
     * Convert date to string "yyyy-MM-dd HH:mm:ss"
     */
    public static String DateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(utc);
        return sdf.format(date);
    }

    /*
     * Convert string "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" to date
     */
    public static Date UtcStringToDate(String text) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(utc);

        Date date = null;
        try {
            date = sdf.parse(text);
        } catch (ParseException e) {
            throw e;
        }

        return date;
    }

    /*
     * Convert current date to string "yyyy-MM-dd'T'HH:mm:ss.SSS zzz"
     */
    public static String now() {
        return DateFormatHelper.toString(new Date());
    }

    /*
     * Convert date to string "yyyy-MM-dd'T'HH:mm:ss.SSS zzz"
     */
    public static String toString(final Date date) {
        return isoFormatter.format(date);
    }

    /*
     * Convert date to string "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     */
    public static String toUtcString(final Date date) {
        return isoFormatterUtc.format(date);
    }

    /*
     * Convert date to string "EEE MMM dd hh:mm:ss zzz yyyy"
     */
    public static String toLegacyString(final Date date) {
        return legacyFormatter.format(date);
    }

    /*
     * Convert date to string use format, timezone is "UTC"
     */
    public static String toString(final Date date, final String format) {
        return toString(date, format, "UTC");
    }

    /*
     * Convert date to string use format and timezone
     */
    public static String toString(final Date date, final String format, final String timezone) {
        final TimeZone tz = TimeZone.getTimeZone(timezone);
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(tz);
        return formatter.format(date);
    }
}
