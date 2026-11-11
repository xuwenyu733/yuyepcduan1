package cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 聊天日志 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ChatLogBaseVO {

    @Schema(description = "消息发送人", example = "李四")
    private String roleName;

    @Schema(description = "聊天内容")
    private String chatContent;

    @Schema(description = "对话所属人id", example = "5909")
    private Long userId;

    @Schema(description = "对话所属人", example = "芋艿")
    private String userName;

    @Schema(description = "对话所属人部门", example = "29881")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "12322")
    private String fid;
    private BigDecimal askScore; // 评分

}
