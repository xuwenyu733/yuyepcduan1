package cn.iocoder.yudao.module.fish.controller.admin.wps.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * WPS 用户信息 Response VO
 * 对应接口：GET /v3/3rd/users
 */
@Schema(description = "WPS - 用户信息 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WPSUserInfoRespVO {

    @Schema(description = "当前用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "user123")
    private String id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String name;

    @Schema(description = "用户头像URL，需要是https链接", example = "https://example.com/avatar/user123.jpg")
    private String avatar_url;
}



