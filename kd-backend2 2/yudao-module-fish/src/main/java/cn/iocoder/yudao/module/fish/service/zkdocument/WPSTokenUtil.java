package cn.iocoder.yudao.module.fish.service.zkdocument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * WPS Token生成工具类
 * 参考：https://open.wps.cn/docs/office
 */
@Component
@Slf4j
public class WPSTokenUtil {
    
    /**
     * 生成WPS Token
     * 
     * @param appId WPS AppID
     * @param appSecret WPS AppSecret
     * @param fileId 文件ID（文档ID）
     * @param userId 用户ID
     * @param expire 过期时间（秒）
     * @return WPS Token
     */
    public String generateToken(String appId, String appSecret, String fileId, Long userId, Integer expire) {
        try {
            // 1. 构建参数Map
            Map<String, String> params = new TreeMap<>();
            params.put("appid", appId);
            params.put("fileid", fileId);
            params.put("userid", String.valueOf(userId));
            params.put("expire", String.valueOf(expire));
            params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            
            // 2. 构建待签名字符串
            StringBuilder signStr = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (signStr.length() > 0) {
                    signStr.append("&");
                }
                signStr.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            // 3. 使用HMAC-SHA256生成签名
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(signStr.toString().getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getEncoder().encodeToString(hash);
            
            // 4. 构建Token（包含签名）
            params.put("signature", signature);
            StringBuilder tokenStr = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (tokenStr.length() > 0) {
                    tokenStr.append("&");
                }
                tokenStr.append(entry.getKey()).append("=").append(entry.getValue());
            }
            
            // 5. Base64编码
            return Base64.getEncoder().encodeToString(
                    tokenStr.toString().getBytes(StandardCharsets.UTF_8));
                    
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("生成WPS Token失败", e);
            throw new RuntimeException("生成WPS Token失败", e);
        }
    }
    
    /**
     * 生成WPS Token（使用默认过期时间）
     */
    public String generateToken(String appId, String appSecret, String fileId, Long userId) {
        return generateToken(appId, appSecret, fileId, userId, 3600); // 默认1小时
    }
}

