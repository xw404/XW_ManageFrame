package com.xiaowu.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * @Author 吴策
 * @Date 2023/12/23 17:26
 * @Description  时间工具类
 */
public class TimeUtils {

    /**
     * 获取当前时间。使用 LocalDateTime.now() 方法获取当前日期和时间。返回一个 LocalDateTime 对象，表示当前的日期和时间
     * @return
     */
    public static LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    /**
     * 将日期时间对象按照指定的格式进行格式化。使用 DateTimeFormatter 类将日期时间对象格式化为指定的字符串格式。
     * 参数 pattern 是日期时间的格式模式，例如："yyyy-MM-dd HH:mm:ss"。返回格式化后的字符串。注意，
     * 这个方法仅适用于将日期时间对象转换为字符串，而不是将字符串转换为日期时间对象。如果需要将字符串转换为日期时间对象，
     * 请使用 parseDateTime() 方法。
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }

    // 将字符串转换为日期时间对象（支持多种日期时间格式）

    /**
     * 将字符串转换为日期时间对象（支持多种日期时间格式）。使用 LocalDateTime.parse() 方法将字符串转换为 LocalDateTime 对象。
     * 该方法支持多种日期时间格式，包括常见的格式如 "yyyy-MM-dd HH:mm:ss" 等。如果字符串的格式不
     * @param dateTimeStr
     * @return
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr);
    }

    // 将日期时间对象转换为Date对象
    public static Date convertToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    // 将Date对象转换为日期时间对象
    public static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    // 获取指定时间之前或之后的时间（单位：天）
    public static LocalDateTime getTimeBeforeAfter(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    // 获取当前时间所在月份的第一天和最后一天的日期时间对象
    public static LocalDateTime[] getFirstLastDayOfMonth() {
        LocalDateTime firstDay = LocalDateTime.of(getCurrentTime().toLocalDate().withDayOfMonth(1), getCurrentTime().toLocalTime());
        LocalDateTime lastDay = LocalDateTime.of(getCurrentTime().toLocalDate().withDayOfYear(365), getCurrentTime().toLocalTime()); // 假设每年有365天，可以根据实际情况调整
        return new LocalDateTime[]{firstDay, lastDay};
    }

    // 获取当前时间所在年份的第一天和最后一天的日期时间对象
    public static LocalDateTime[] getFirstLastDayOfYear() {
        LocalDateTime firstDay = LocalDateTime.of(getCurrentTime().toLocalDate().withDayOfYear(1), getCurrentTime().toLocalTime());
        LocalDateTime lastDay = LocalDateTime.of(getCurrentTime().toLocalDate().withDayOfYear(365), getCurrentTime().toLocalTime()); // 假设每年有365天，可以根据实际情况调整
        return new LocalDateTime[]{firstDay, lastDay};
    }

    // 获取当前时间的年份和月份（例如：2023年7月）
    public static String getYearMonth() {
        return getCurrentTime().toLocalDate().toString(); // 使用默认的日期格式：年-月-日，例如：2023-07-19
    }

    // 获取当前时间的年份和月份（例如：2023年7月）并格式化为指定的字符串格式（例如："2023年07月"）
    public static String getFormattedYearMonth(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(getCurrentTime().toLocalDate()); // 格式化日期为指定的字符串格式，例如："yyyy年MM月" 或 "yyyy年MM月dd日" 等。可以根据需要调整模式字符串。
    }

    /**
     * 日期对象转字符串
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date,String format){
        String result="";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        if(date!=null){
            result=sdf.format(date);
        }
        return result;
    }

    /**
     * 字符串转日期对象
     * @param str
     * @param format
     * @return
     * @throws Exception
     */
    public static Date formatString(String str,String format) throws Exception{
        if(StringUtil.isEmpty(str)){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.parse(str);
    }

    public static String getCurrentDateStr(){
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmssSSSSSSSSS");
        return sdf.format(date);
    }

    public static String getCurrentDatePath()throws Exception{
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd/");
        return sdf.format(date);
    }
}
