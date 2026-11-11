package cn.iocoder.yudao.framework.common.util.easy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EasySetUtil {

    public static String toSetStr(String[] arr, String after) {
        Set<String> set = new HashSet<>();
        for(String str : arr) {
            if(StringUtil.isNotEmptyWith0(str)) set.add(str);
        }
        if(StringUtil.isNotEmptyWith0(after)) set.add(after);
        return String.join(",", set);
    }

}
