package cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 培训知识分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TrainingKnowledgePageReqVO extends PageParam {

    @Schema(description = "知识名称", example = "李四")
    private String knowledgeName;

    @Schema(description = "知识详情")
    private String knowledgeContent;

    @Schema(description = "知识备注", example = "你说的对")
    private String knowledgeRemark;

    @Schema(description = "状态(0:待入库;1:已入库;-1:已删除;)", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "18268")
    private Long userId;
    private String creator;

    @Schema(description = "所属部门", example = "13858")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "14651")
    private String fid;
    private String agentId;

}
