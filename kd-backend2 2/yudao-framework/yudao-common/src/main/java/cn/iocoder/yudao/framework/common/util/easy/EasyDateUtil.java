package cn.iocoder.yudao.framework.common.util.easy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @创建人 lr
 * @创建时间 2024-05-06
 * @描述
 */
public class EasyDateUtil {


    public static String getWeek(Date date) {
        // 指定日期，例如2023-04-01
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 星期几，星期日为1，星期一为2，以此类推
            String dayOfWeekName;
            switch (dayOfWeek) {
                case 1:
                    dayOfWeekName = "星期日";
                    break;
                case 2:
                    dayOfWeekName = "星期一";
                    break;
                case 3:
                    dayOfWeekName = "星期二";
                    break;
                case 4:
                    dayOfWeekName = "星期三";
                    break;
                case 5:
                    dayOfWeekName = "星期四";
                    break;
                case 6:
                    dayOfWeekName = "星期五";
                    break;
                case 7:
                    dayOfWeekName = "星期六";
                    break;
                default:
                    dayOfWeekName = "未知";
                    break;
            }
            return dayOfWeekName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Boolean isWeekend(String weekName) {
        List<String> strings = Arrays.asList("星期日", "星期六");
        return strings.contains(weekName);
    }

    public static LocalDateTime parse(String dateStr) {
        return parseBase(dateStr, "yyyy-MM-dd HH:mm:ss");
    }
    public static LocalDateTime parseBase(String dateStr, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime parse = LocalDateTime.parse(dateStr, dateTimeFormatter);
        return parse;
    }
    public static Date localToDate(LocalDateTime localDateTime) {
        LocalDate localDate = localDateTime.toLocalDate();
        LocalDateTime startOfDay = localDate.atStartOfDay();
        Date date = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * 时间加月份
     * @param time
     * @param month
     * @return
     */
    public static LocalDateTime plusMonth(LocalDateTime time, Integer month) {
        if(time == null)
            time = LocalDateTime.now();
        LocalDateTime targetTime = time.plus(month.longValue(), ChronoUnit.MONTHS);
        return targetTime;
    }


    /**
     * 时间加月份
     * @param time
     * @param day
     * @return
     */
    public static LocalDateTime plusDay(LocalDateTime time, Integer day) {
        if(time == null)
            time = LocalDateTime.now();
        LocalDateTime targetTime = time.plus(day.longValue(), ChronoUnit.DAYS);
        return targetTime;
    }
    public static Date plusDay2(LocalDateTime time, Integer day) {
        if(time == null)
            time = LocalDateTime.now();
        LocalDateTime targetTime = time.plus(day.longValue(), ChronoUnit.DAYS);
        Date res = localToDate(targetTime);
        return res;
    }

    /**
     * 时间加月份
     * @param time
     * @param minute
     * @return
     */
    public static Date plusMinutes(LocalDateTime time, Integer minute) {
        if(time == null)
            time = LocalDateTime.now();
        LocalDateTime targetTime = time.plus(minute.longValue(), ChronoUnit.MINUTES);

        Instant instant = targetTime.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);

        return date;
    }
    public static LocalDateTime plusMinutes1(LocalDateTime time, Integer minute) {
        if(time == null)
            time = LocalDateTime.now();
        LocalDateTime targetTime = time.plus(minute.longValue(), ChronoUnit.MINUTES);

        return targetTime;
    }

    public static String format1(Date date) {
        if(date == null) date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss_SSS");
        return format.format(date);
    }
    public static String format2(Date date) {
        if(date == null) date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    public static String format(Date date, String formatStr) {
        if(date == null) date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }
    public static Date parse(String dateStr, String formatStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public static Integer getYear(Date date) {
        if(date == null) date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return Integer.parseInt(format.format(date));
    }
    public static Integer getMonth(Date date) {
        if(date == null) date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return Integer.parseInt(format.format(date));
    }
    public static Integer getDay(Date date) {
        if(date == null) date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return Integer.parseInt(format.format(date));
    }

    public static String format(LocalDateTime dateTime, String format) {
        if(dateTime == null) dateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(dateTime);
    }

    /**
     * 获取几天前或几天后的开始时间
     * @param days
     * @return
     */
    public static Date plusDaysStart(Integer days) {
        LocalDateTime localDateTime = plusDay(LocalDateTime.now(), days);
        String format = format(localDateTime, "yyyy-MM-dd ");
        String targetStr = new StringBuilder(format).append("00:00:00").toString();
        Date finalRes = parse(targetStr, "yyyy-MM-dd HH:mm:ss");
        return finalRes;
    }
    public static String plusDayStr(String dayStr, Integer days) {
        String targetStr = new StringBuilder(dayStr).append(" 08:00:00").toString();
        LocalDateTime d1 = parse(targetStr);
        LocalDateTime target = plusDay(d1, -1);

        String format = format(target, "yyyy-MM-dd ");
        return format;
    }

    public static List<Date> dayStartAndEnd(String day) {
        String startStr = new StringBuilder(day).append(" 00:00:00").toString();
        String endStr = new StringBuilder(day).append(" 23:59:59").toString();
        Date start = parse(startStr, "yyyy-MM-dd HH:mm:ss");
        Date end = parse(endStr, "yyyy-MM-dd HH:mm:ss");
        return Arrays.asList(start, end);
    }
    public static List<Date> plusDayBetween(LocalDateTime curr, Integer days) {
        LocalDateTime target = plusDay(curr, days);

        String format = format(target, "yyyy-MM-dd ");
        List<Date> dates = dayStartAndEnd(format);

        return dates;
    }
    public static List<Date> plusDaysDuring(LocalDateTime curr, Integer days) {
        String startFormat = format(curr, "yyyy-MM-dd ");
        String startStr = startFormat + " 00:00:00";
        Date start = parse(startStr, "yyyy-MM-dd HH:mm:ss");
        LocalDateTime target = plusDay(curr, days);
        String endFormat = format(target, "yyyy-MM-dd ");
        String endStr = endFormat + " 23:59:59";
        Date end = parse(endStr, "yyyy-MM-dd HH:mm:ss");

        return List.of(start, end);
    }

    /**
     *
     * @param curr 从什么时候开始加
     * @param days 加多少天
     * @param format 格式化什么格式 "yyyy-MM-dd"
     * @return
     */
    public static List<String> plusDayList(LocalDateTime curr, Integer days, String format) {
        List<String> recentDays = new ArrayList<>();
        for(int i=0;i<=days;i++) {
            LocalDateTime target = plusDay(curr, i);
            String f1 = format(target, format);
            recentDays.add(f1);
        }
        return recentDays;
    }

}
