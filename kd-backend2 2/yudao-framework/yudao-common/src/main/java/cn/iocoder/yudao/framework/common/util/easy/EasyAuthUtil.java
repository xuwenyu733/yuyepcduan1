package cn.iocoder.yudao.framework.common.util.easy;


import jakarta.servlet.http.HttpServletRequest;

/**
 * @创建人 lr
 * @创建时间 2025-09-15
 * @描述
 */
public class EasyAuthUtil {

    public static String selectAuthorization(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return token;
    }

}
