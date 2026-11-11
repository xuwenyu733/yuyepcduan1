package cn.iocoder.yudao.server.server.log_info;

/**
 * @创建人 lr
 * @创建时间 2024-05-02
 * @描述
 */

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.iocoder.yudao.framework.common.base_api.BaseUserService;
import cn.iocoder.yudao.framework.common.util.easy.EasyRequestUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import java.util.Map;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminUserApi adminUserApi;
    @Autowired
    private BaseUserService baseUserService;

    @PostConstruct
    public void init() {
        // System.out.println("LoggingInterceptor=================>init ok!");
        TokenUtil.baseUserService = baseUserService;
        // TokenUtil.redisService = redisService;
        // TokenUtil.adminUserApi = adminUserApi;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(MyIMTAFilter.excluedePatterns.contains(EasyRequestUtil.getRequest().getRequestURI()))
            return true;
        CustomHttpServletRequestWrapper wrapper = new CustomHttpServletRequestWrapper(request);
        Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                .put("query", JakartaServletUtil.getParamMap(wrapper))
                .put("body", JakartaServletUtil.getBody(wrapper)).build();
        String method = wrapper.getMethod();
        String clientIP = JakartaServletUtil.getClientIP(wrapper);
        String requestURI = wrapper.getRequestURI();
        String params = JSON.toJSONString(requestParams);
        if(StringUtil.isNotEmpty(params)) {
            params = params.replaceAll("\\r", "").replaceAll("\\n", "");
        }
        // 记录请求的入参
        log.warn("【收到请求-{}】start, ip:{},uri:{},method:{},params:{}",
                TokenUtil.userInfo(), clientIP, requestURI, method, params); //
        return true; // 继续执行后续的拦截器和请求处理
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 这里可以访问到modelAndView中的内容
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(MyIMTAFilter.excluedePatterns.contains(EasyRequestUtil.getRequest().getRequestURI()))
            return;
        CustomHttpServletRequestWrapper wrapper = new CustomHttpServletRequestWrapper(request);
        String method = wrapper.getMethod();
        String clientIP = JakartaServletUtil.getClientIP(wrapper);
        String requestURI = wrapper.getRequestURI();
        // 请求处理完成后执行，可以记录出参或处理异常
        if (ex == null) {
            log.warn("【收到请求-{}】middle, 请求正常，无异常,ip:{},uri:{},method:{}", TokenUtil.userInfo(), clientIP, requestURI, method);
        } else {
            log.warn("【收到请求-{}】middle, 请求异常,ip:{},uri:{},method:{},error:{}", TokenUtil.userInfo(), clientIP, requestURI, method, ex.getMessage());
        }
    }
}