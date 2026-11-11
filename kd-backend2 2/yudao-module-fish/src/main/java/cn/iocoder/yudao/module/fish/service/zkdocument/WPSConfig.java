package cn.iocoder.yudao.module.fish.service.zkdocument;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * WPS开放平台配置
 */
@Component
@Data
public class WPSConfig {
    
    /**
     * WPS AppID（从WPS开放平台获取）
     */
    @Value("${wps.app.id:}")
    private String appId;
    
    /**
     * WPS AppSecret（从WPS开放平台获取）
     */
    @Value("${wps.app.secret:}")
    private String appSecret;
    
    /**
     * WPS API基础URL
     */
    @Value("${wps.api.base-url:https://wwo.wps.cn}")
    private String baseUrl;
    
    /**
     * Token有效期（秒），默认1小时
     */
    @Value("${wps.token.expire:3600}")
    private Integer tokenExpire;
    
    /**
     * 是否启用WPS开放平台
     */
    @Value("${wps.enabled:false}")
    private Boolean enabled;
    
    /**
     * 检查WPS配置是否完整
     */
    public boolean isConfigValid() {
        return enabled && appId != null && !appId.isEmpty() 
                && appSecret != null && !appSecret.isEmpty();
    }
}



