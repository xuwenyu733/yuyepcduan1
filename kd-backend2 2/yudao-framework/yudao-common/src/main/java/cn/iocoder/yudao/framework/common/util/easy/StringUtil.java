package cn.iocoder.yudao.framework.common.util.easy;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

/**
 *	字符串工具类
 * @author liurui
 */
@Slf4j
public class StringUtil {

    public static String uuid() {
        String str = UUID.randomUUID().toString().replaceAll("-", "");
        return str;
    }

    public static int getRandNum(int min, int max) {
        int randNum = min + (int)(Math.random() * ((max - min) + 1));
        return randNum;
    }


    public static String getFileName() {
        String str = EasyDateUtil.format1(new Date());
        return new StringBuilder(str)
                .append("_")
                .append(getRandNum4()).append("_").toString();
    }


    public static String genApproveNum(String key, Long val) {
        StringBuilder sb = new StringBuilder();
        sb.append("Approve_")
                .append(key).append("_")
                .append(val).append("_").append(getRandNum4());
        return sb.toString();
    }
    public static String genMeetingNum() {
        StringBuilder sb = new StringBuilder();
        sb.append("m_")
                .append(System.currentTimeMillis())
                .append("_").append(getRandNum4());
        return sb.toString();
    }

    public static String getRandNum4(){
        return "" + getRandNum(1,9999);
    }


    public static boolean isEmpty(String str){
        if(str == null || "".equals(str) || "null".equals(str)) return true;
        if("".equals(str.trim())) return true;
        return false;
    }

    public static boolean isEmptyWith0(String str){
        if(str == null || "".equals(str) || "0".equals(str) || "null".equals(str)) return true;
        if("".equals(str.trim()) || "0".equals(str.trim())) return true;
        return false;
    }
    public static boolean isNotEmptyWith0(String str){
        return !isEmptyWith0(str);
    }
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
    
    /**
     * 判断字符串中是否包含空格
     * @param str 目标字符串
     * @return
     */
    public static boolean hasBlank(String str) {
    	if(isEmpty(str)) return true;
    	return str.contains(" ");
    }
    
    /**
     * 去掉字符串中所有空格，去掉前后空格可以使用trim()
     * @param str 目标字符串
     * @return
     */
    public static String removeBlank(String str) {
    	if(isEmpty(str)) return null;
    	return str.replace(" ", "");
    }
    
    /**
     * 拼串
     * @param strings
     * @return
     */
    public static String mergeStr(String...strings) {
    	StringBuilder sb = new StringBuilder();
    	for (String str : strings) {
    		sb.append(str);
		}
    	return sb.toString();
    }

    public static String customMerge(String split,String...strings) {
        StringBuilder sb = new StringBuilder();
        Integer count = 0;
        for (String str : strings) {
            if(count > 0) sb.append(split);
            sb.append(str);
            count++;
        }
        return sb.toString();
    }
    // 追加字符串
    public static String appendJsonStr(Object obj) {
        if(obj == null) return null;
        StringBuffer sb = new StringBuffer("===》");
        sb.append(obj.toString());
        return sb.toString();
    }
    
    /**
     * 日志输入拼串
     * @param strings
     */
    public static void debugShow(String...strings) {
    	log.warn(mergeStr(strings));
    }
    
    public static String[] strSplit(String str) {
    	return isEmpty(str)?null:str.split(",");
    }

    public static Boolean longStrReduceGT(String lastVersion, String newVersion, Long splitDuring) {
        if(StringUtil.isEmpty(lastVersion) || StringUtil.isEmpty(newVersion)) return true;
        Long reduceRes = Long.parseLong(newVersion) - Long.parseLong(lastVersion);
        if(reduceRes > splitDuring) return true;
        return false;
    }
}
