package cn.iocoder.yudao.framework.common.util.easy;

import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EasyRegexMatcher {
    /**
     * 根据正则表达式匹配字符串并提取指定数量的捕获组值
     * @param regex 正则表达式（需包含捕获组）
     * @param input 待匹配字符串
     * @param expectedGroups 预期捕获组数量
     * @return 包含捕获组值的List（匹配失败返回空列表）
     */
    public static List<String> getInfo(String regex, String input, int expectedGroups) {
        try {
            List<String> result = new ArrayList<>();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);

            if (matcher.matches() && matcher.groupCount() >= expectedGroups) {
                for (int i = 1; i <= expectedGroups; i++) {
                    result.add(matcher.group(i));
                }
            }
            // System.out.println(String.format("%s===========>%s", input, JSON.toJSONString(result)));
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 从字符串中提取数字部分
     * @param input 任意包含数字的字符串
     * @return 提取到的数字字符串，如果没有数字则返回空字符串
     */
    public static String extractNumbers(String input) {
        try {
            if (input == null || input.isEmpty()) {
                return "";
            }

            // 正则表达式匹配连续的数字
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                return matcher.group();
            }

            return "";
        } catch (Exception e) {
            log.error("【字符串转数字异常】===>str:{}, error:{}", input, ExceptionUtil.getStackTrace(e));
            return "";
        }
    }

    // 示例用法
    public static void main(String[] args) {
        List<String> info = getInfo("do_oxyper_(\\d+)", "do_oxyper_1", 1);
        System.out.println(info); // 输出 [1, 2]

        // 测试示例
        String test1 = "400g/尾巴";
        String test2 = "abc123def456";
        String test3 = "没有数字的字符串";

        System.out.println(extractNumbers(test1)); // 输出: 400
        System.out.println(extractNumbers(test2)); // 输出: 123
        System.out.println(extractNumbers(test3)); // 输出: (空字符串)
    }
}

