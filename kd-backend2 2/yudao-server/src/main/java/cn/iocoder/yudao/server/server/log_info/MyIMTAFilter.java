package cn.iocoder.yudao.server.server.log_info;

/**
 * @创建人 lr
 * @创建时间 2024-05-02
 * @描述
 */

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.easy.EasyRequestUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.framework.web.config.WebProperties;
import cn.iocoder.yudao.framework.web.core.filter.ApiRequestFilter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 请求多次获取请求流，报错，加强就不会报错
 * @author zhoule
 * @description: 过滤器，处理request
 */
@Slf4j
@WebFilter(filterName = "MyIMTAFilter", urlPatterns = "/*")
@Order(99)
public class MyIMTAFilter extends ApiRequestFilter {

    public List<String> excludeList = Arrays.asList("/favicon.ico"
        ,"/query"
        ,"/robots.txt"
        ,"/webui/"
        ,"/dns-query"
        ,".php"
        ,"/portal"
        ,"/resolve"
    );


    public static List<String> excluedePatterns = Arrays.asList(
            "/admin-api/infra/file/upload"
            ,"/admin-api/system/auth/login"
            ,"/admin-api/system/notify-message/get-unread-count"
            ,"/admin-api/system/captcha/get"
            ,"/admin-api/system/auth/refresh-token"
            ,"/admin-api/pl/product/import"
            ,"/admin-api/pl/product/image"
            ,"/admin-api/infra/job/update-status"
            ,"/admin-api/pl/zk-ai-agent/getModelInfo"
            ,"/admin-api/pl/zk-app-log/page"
            // ,"/admin-api/pl/zk-daping/getWarn"
    );

    public MyIMTAFilter(WebProperties webProperties) {
        super(webProperties);
    }

    @PostConstruct
    public void init() {
        log.warn("=======================>IMTAFilter生效，加强request");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(excluedePatterns.contains(servletRequest.getRequestURI()) || isMultipart(servletRequest)){
            filterChain.doFilter( servletRequest, servletResponse);
            return;
        }
        // 1. 请求包装，方便重复获取参数
        ServletRequest requestWrapper = null;
        ResponseWrapper mResp = null;
        if(servletRequest instanceof HttpServletRequest) {
            requestWrapper = new CustomHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        }
        if(servletResponse instanceof HttpServletResponse) {
            mResp = new ResponseWrapper((HttpServletResponse) servletResponse); // 包装响应对象 resp 并缓存响应数据
        }
        // 2. 包装后的请求放行，部分请求报错，跳过去
        if(!exclude(servletRequest)) {
            filterChain.doFilter(requestWrapper==null?servletRequest:requestWrapper, mResp==null?servletResponse:mResp);
        } else {
            String clientIP = JakartaServletUtil.getClientIP((HttpServletRequest) requestWrapper);
            log.warn("【收到请求-{}】end ，非法请求id:{}, url:{}", TokenUtil.userInfo(), clientIP, ((CustomHttpServletRequestWrapper) requestWrapper).getRequestURL());
            EasyRequestUtil.writeJSON(mResp==null?(HttpServletResponse)servletResponse:mResp, CommonResult.error(403, "非法请求！"));
        }
        // 3. 返回打印日志
        if(mResp != null) {
            byte[] bytes = mResp.getBytes();
            if(bytes != null && bytes.length > 0) {
                String res = new String(bytes);
                if(res.length() > 200) {
                    res = res.substring(0, 43);
                }
                log.warn("【收到请求-{}】end ，接口返回:{}", TokenUtil.userInfo(), res);
                servletResponse.getOutputStream().write(bytes);
            }else {
                log.warn("【收到请求-{}】end ，接口无返回", TokenUtil.userInfo());
            }
        }
    }

    /**
     * 部分请求报错，排除这部分请求
     * @param request
     * @return
     */
    public Boolean exclude(ServletRequest request) {
        if(request instanceof HttpServletRequest) {
            HttpServletRequest requestWrapper = (HttpServletRequest) request;

            String url = requestWrapper.getRequestURL().toString();
            for(String item : excludeList) {
                if(url.contains(item))
                    return true;
            }
        }
        return false;
    }

    public static boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase(java.util.Locale.ROOT).startsWith("multipart/");
    }
}

