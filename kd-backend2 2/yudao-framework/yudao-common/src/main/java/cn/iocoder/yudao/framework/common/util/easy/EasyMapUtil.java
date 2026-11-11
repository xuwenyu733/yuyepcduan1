package cn.iocoder.yudao.framework.common.util.easy;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class EasyMapUtil {


    public static Boolean isNotEmpty(Map map) {
        if(map == null) return false;
        if(map.isEmpty()) return false;
        boolean flag = false;
        for(Object item : map.keySet()) {
            if(item != null) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    public static Boolean isEmpty(Map map) {
        return !isNotEmpty(map);
    }

    public static Object getMapValue(Map map, String key) {
        if(map == null) return null;
        return map.get(key);
    }

}
