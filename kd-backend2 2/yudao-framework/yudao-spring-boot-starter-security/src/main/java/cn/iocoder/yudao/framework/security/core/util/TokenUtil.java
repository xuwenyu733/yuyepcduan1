package cn.iocoder.yudao.framework.security.core.util;

import cn.iocoder.yudao.framework.common.base_api.BaseUserService;
import cn.iocoder.yudao.framework.common.util.easy.EasyRequestUtil;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * @创建人 lr
 * @创建时间 2024-05-03
 * @描述
 */
@Slf4j
@Component
public class TokenUtil {

    public static String STORE_USER_KEY = "store:user:info:";

    public static BaseUserService baseUserService;

    /**
     * 获取用户信息，方便打印日志
     * @return
     */
    public static String userInfo() {
        try {
            LoginUser user = SecurityFrameworkUtils.getLoginUser();
            if(user != null) {
                String temp = new StringBuffer(user.getId()+"")
                         .append("-").append(baseUserService.byKey(user.getId()+"", "nickname"))
                        // .append("-").append(user.getUsername())
                        .append("-").append(EasyRequestUtil.getRequest().getRequestURI())
                        .toString();
                return temp;
            }
            return "";
        } catch (Exception e) {
            try {
                StringBuffer requestURL = EasyRequestUtil.getRequest().getRequestURL();
                log.warn("【TokenUtil获取日志信息失败】middle,url:{},无需任何处理e:{}",requestURL, e.getMessage());
                return "";
            } catch (Exception ex) {
                log.warn("【TokenUtil获取日志信息失败】middle,无需任何处理：{}", ex.getMessage());
                return "";
            }
        }
    }

    public static String selectKey() {
        long time = new Date().getTime();
        return time+"";
    }

    @Autowired
    public void setBaseUserService(BaseUserService baseUserService) {
        TokenUtil.baseUserService = baseUserService;
    }
}
