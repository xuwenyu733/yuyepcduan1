package cn.iocoder.yudao.module.fish.controller.admin.zkchatlog.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 聊天日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChatLogPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件id", example = "12322")
    private String fid;
    private BigDecimal askScore; // 评分

}
