package cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 问题总结分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIssuePageReqVO extends PageParam {

    @Schema(description = "问题标题")
    private String issueTitle;

    @Schema(description = "问题描述")
    private String issueContent;

    @Schema(description = "总结标题")
    private String summaryTitle;

    @Schema(description = "总结内容")
    private String summaryContent;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "附件")
    private String fids;

    @Schema(description = "流程实例", example = "27220")
    private String processInstanceId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "31703")
    private Long userId;
    private String creator;

    @Schema(description = "所属部门", example = "3732")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "8247")
    private String fid;
    private String agentId;

}
