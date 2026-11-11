package cn.iocoder.yudao.framework.common.util.exception;

/**
 * @author lr
 * @date 2020/11/10
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ExceptionUtil {

    /**
     * 获取异常的堆栈信息
     *
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        if(t == null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    public static String getTimeStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String format = sdf.format(date);
        return format;
    }
    public static String getTimeStr3(LocalDateTime date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
//        LocalDateTime parse = LocalDateTime.parse("2023-10-27 00:30:20", dateTimeFormatter);
//        System.out.println("1.字符串转时间："+parse);
        String format = dateTimeFormatter.format(date);
        return format;
    }
}
