package cn.iocoder.yudao.framework.common.util.easy;

import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author liurui
 * @date ${Date}
 */
@Slf4j
public class EasyBigDecimalUtil {

    public static List<String> TO_NUMBER_SPLIT = Arrays.asList("g", "kg", "Kg");

    public EasyBigDecimalUtil(BigDecimal orDefault, BigDecimal inCount) {
    }

    /**
     * bigdecimal类型为0或者空
     * @param bigDecimal
     * @return
     */
    public static Boolean isEmptyWith0(BigDecimal bigDecimal) {
        if(bigDecimal == null ) return true;
        if(Objects.equals(new BigDecimal(0).compareTo(bigDecimal), 0)) return true;
        if(Objects.equals(bigDecimal, new BigDecimal(0)) || Objects.equals(bigDecimal, new BigDecimal("0.00"))) return true;
        return false;
    }
    public static Boolean isNotEmptyWith0(BigDecimal bigDecimal) {
        return !isEmptyWith0(bigDecimal);
    }
    // 是否比0大
    public static Boolean gt0(BigDecimal bigDecimal) {
        if(bigDecimal == null) return false;
        return new BigDecimal(0).compareTo(bigDecimal) < 0;
    }
    public static Boolean ge0(BigDecimal bigDecimal) {
        if(bigDecimal == null) return false;
        return new BigDecimal(0).compareTo(bigDecimal) <= 0;
    }
    public static Boolean eq0(BigDecimal bigDecimal) {
        if(bigDecimal == null) return false;
        return new BigDecimal(0).compareTo(bigDecimal) == 0;
    }
    public static BigDecimal toNumber(String numberStr) {
        numberStr = EasyRegexMatcher.extractNumbers(numberStr);
        if(StringUtils.isEmpty(numberStr)) return new BigDecimal(0);
        BigDecimal bigDecimal = new BigDecimal(0);
        try {
            bigDecimal = new BigDecimal(numberStr);
        } catch (Exception e) {
            log.error("【字符串转数字异常】===>str:{}, error:{}", numberStr, ExceptionUtil.getStackTrace(e));
        }
        return bigDecimal;
    }

    /**
     * GE :  param >= 0
     * LT :  param < 0
     * @param bigDecimal
     * @param sign
     * @return
     */
    public static Boolean compare0(BigDecimal bigDecimal, String sign) {
        if(bigDecimal == null) return false;
        if("GE".equals(sign)) return new BigDecimal(0).compareTo(bigDecimal) <= 0;
        if("GT".equals(sign)) return new BigDecimal(0).compareTo(bigDecimal) < 0;
        if("LT".equals(sign)) return new BigDecimal(0).compareTo(bigDecimal) > 0;
        if("LE".equals(sign)) return new BigDecimal(0).compareTo(bigDecimal) >= 0;
        if("NE".equals(sign)) return new BigDecimal(0).compareTo(bigDecimal) != 0;
        if("EQ".equals(sign)) return new BigDecimal(0).compareTo(bigDecimal) == 0;
        return false;
    }
    // 两个数值是否相等
    public static Boolean valueEquals(BigDecimal one, BigDecimal two) {
        return Objects.equals(0, one.compareTo(two));
    }

    public static BigDecimal toScale(BigDecimal one, Integer scale) {
        if(one == null)
            one = new BigDecimal(0);

        return one.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
    // 两个数相加，保留两位小数
    public static BigDecimal add(BigDecimal one, BigDecimal two) {
        if(one == null)
            one = new BigDecimal(0);
        if (two == null)
            two = new BigDecimal(0);
        return one.add(two).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    // 两个数相减，保留两位小数
    public static BigDecimal subtract(BigDecimal one, BigDecimal two) {
        if(one == null)
            one = new BigDecimal(0);
        if (two == null)
            two = new BigDecimal(0);
        return one.subtract(two).setScale(4, BigDecimal.ROUND_HALF_UP);
    }
    // 两个数相乘，保留两位小数
    public static BigDecimal multiply(BigDecimal one, BigDecimal two) {
        if(one == null)
            one = new BigDecimal(1);
        if (two == null)
            two = new BigDecimal(1);
        return one.multiply(two).setScale(4, BigDecimal.ROUND_HALF_UP);
    }
    // 两个数相除，保留两位小数
    public static BigDecimal divide(BigDecimal one, BigDecimal two) {
        if(one == null)
            one = new BigDecimal(0);
        if (two == null || compare0(two, "EQ"))
            two = new BigDecimal(1);
        return one.divide(two, 4, RoundingMode.HALF_UP).setScale(4, RoundingMode.HALF_UP);
    }
    // 多个数相加，保留两位小数
    public static BigDecimal addMany(BigDecimal... params) {
        BigDecimal temp = new BigDecimal(0);
        for(BigDecimal param : params) {
            if(param == null)
                param = new BigDecimal(0);
            temp = temp.add(param).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return temp;
    }

    /**
     * 求百分比
     * @param one
     * @param two
     * @return  举例： one:1  two:3  结果： 33.33%
     */
    public static String getPersent(BigDecimal one, BigDecimal two) {
        String s = EasyBigDecimalUtil.divide(EasyBigDecimalUtil.multiply(one, new BigDecimal(100)), two).toString()+"%";
        return s;
    }



    /**
     * 负数变正数，并且保留两位小数
     * @return
     */
    public static BigDecimal negativeChangePositive(BigDecimal target) {
        return multiply(target, new BigDecimal(-1));
    }



    // 判断当前算子是否匹配
    public static Boolean isMatch(String compareSign, BigDecimal forValue, BigDecimal targetValue) {
        Boolean startFlag = true;
        switch (compareSign) {
            case "GE":
                startFlag = forValue.compareTo(targetValue) >= 0;
                break;
            case "GT":
                startFlag = forValue.compareTo(targetValue) > 0;
                break;
            case "LE":
                startFlag = forValue.compareTo(targetValue) <= 0;
                break;
            case "LT":
                startFlag = forValue.compareTo(targetValue) < 0;
                break;
        }
        return startFlag;
    }

    public static BigDecimal selectByStrAndIndex(String str, BigDecimal index) {
        String[] split = str.split(",");
        if(index == null) index = new BigDecimal(0);
        Integer newIndex = Integer.parseInt(index.add(new BigDecimal(0)).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
        return new BigDecimal(split[newIndex]);
    }

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal(1.31);
        BigDecimal b = new BigDecimal(0.15);
        BigDecimal c = new BigDecimal(0.011);

        BigDecimal bigDecimal = addMany(a, b, c);
        System.out.println(bigDecimal);
    }

    public static Long toLong(String numberStr) {
        if(StringUtil.isEmpty(numberStr)) return 0L;
        numberStr = EasyRegexMatcher.extractNumbers(numberStr);
        if(StringUtil.isEmpty(numberStr)) return 0L;
        numberStr = numberStr.split("\\.")[0];

        return Long.parseLong(numberStr);
    }

    public static BigDecimal getOrDefault(String fishSpec, BigDecimal avgFishSpec) {
        if(StringUtil.isEmpty(fishSpec)) return avgFishSpec;
        return toNumber(fishSpec);
    }
}
