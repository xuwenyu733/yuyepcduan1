package cn.iocoder.yudao.framework.common.base_api;

/**
 * @创建人 lr
 * @创建时间 2025-09-05
 * @描述
 */
public interface BaseOAuth2TokenService {
    /**
     * 校验token
     * @param accessToken
     * @return true:token有效;false:token失效;
     */
    Boolean easyCheckToken(String accessToken);
}
