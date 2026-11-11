package cn.iocoder.yudao.framework.common.util.easy;

/**
 * @创建人 lr
 * @创建时间 2024-03-07
 * @描述
 */
import java.math.BigDecimal;

public class AmountUtils {
    private static final String[] numNames = {
            // 壹（1）、贰（2）、叁（3）、肆（4）、伍（5）、陆（6）、柒（7）、捌（8）、玖（9）、拾（10）
//            "", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九"
            "", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾", "拾壹", "拾贰", "拾叁", "拾肆", "拾伍", "拾陆", "拾柒", "拾捌", "拾玖"
    };

    private static final String[] unitNames = {
            // 佰。千大写：仟。万大写：万。
//            "", "拾", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿", "百亿", "千亿", "万亿"
            "", "拾", "佰", "仟", "万", "拾万", "佰万", "仟万", "亿", "拾亿", "佰亿", "仟亿", "万亿"
    };

    public static String toChinese(BigDecimal amount) {
        StringBuilder result = new StringBuilder();
        String strAmount = amount.toPlainString();
        int dotIndex = strAmount.indexOf('.');
        if (dotIndex >= 0) {
            String integerPart = strAmount.substring(0, dotIndex);
            String decimalPart = strAmount.substring(dotIndex + 1);
            result.append(toChineseIntegerPart(integerPart));
            result.append("点");
            result.append(toChineseDecimalPart(decimalPart));
        } else {
            result.append(toChineseIntegerPart(strAmount));
        }
        return result.toString();
    }

    private static String toChineseIntegerPart(String integerPart) {
        StringBuilder result = new StringBuilder();
        int length = integerPart.length();
        for (int i = 0; i < length; i++) {
            int digit = integerPart.charAt(i) - '0';
            if (digit != 0) {
                result.append(numNames[digit]);
                result.append(unitNames[length - i - 1]);
            } else if (i == length - 1 || integerPart.charAt(i + 1) != '0') {
                result.append(numNames[digit]);
            }
        }
        return result.toString();
    }

    private static String toChineseDecimalPart(String decimalPart) {
        StringBuilder result = new StringBuilder();
        int length = decimalPart.length();
        for (int i = 0; i < length; i++) {
            int digit = decimalPart.charAt(i) - '0';
            if (digit != 0) {
                result.append(numNames[digit]);
            }
        }
        return result.toString();
    }
}
