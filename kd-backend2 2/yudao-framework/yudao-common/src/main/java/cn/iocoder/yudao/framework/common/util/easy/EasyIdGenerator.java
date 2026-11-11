package cn.iocoder.yudao.framework.common.util.easy;

import java.util.Calendar;
import java.util.Random;

public class EasyIdGenerator {

    /**
     * 生成一个20位的唯一ID字符串，纯数字
     *
     * @return String 唯一的ID
     */
    //    一个常见的方法是使用当前的时间戳再加上一些随机数。但是，时间戳通常只有14位(年月日时分秒)，所以需要额外增加6位随机数（随机数是在时间戳的右边拼接的）。

    public static String generateUniqueID() {
        // 创建Calendar对象
        Calendar calendar = Calendar.getInstance();

        // 获取当前时间
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // 拆分时间,去除分隔符,补0格式化
        String time = String.format("%04d年", year)
                + String.format("%02d月", month)
                + String.format("%02d日", day)
                + String.format("%02d时", hour)
                + String.format("%02d分", minute)
                + String.format("%02d秒", second);

        // 生成6位的随机数
        int randomNumber = new Random().nextInt(900000) + 100000;

        // 合并时间和随机数,生成20位的唯一ID
        String uniqueID = time + randomNumber;
        return uniqueID;
    }

    public static void main(String[] args) {
        // 生成一个唯一ID并打印
        String uniqueID = generateUniqueID();
        System.out.println("生成的唯一ID: " + uniqueID);
    }

}





