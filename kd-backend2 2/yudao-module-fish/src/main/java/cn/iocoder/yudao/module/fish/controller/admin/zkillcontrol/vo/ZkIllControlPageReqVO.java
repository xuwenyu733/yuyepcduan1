package cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 疾病诊断与防治记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIllControlPageReqVO extends PageParam {

    @Schema(description = "病害简称")
    private String illTitle;

    @Schema(description = "病害描述")
    private String illContent;

    @Schema(description = "诊断标题")
    private String controlTitle;

    @Schema(description = "诊断描述")
    private String controlContent;

    @Schema(description = "状态(0:正常;-1:已删除;)", example = "1")
    private String status;

    @Schema(description = "附件")
    private String fids;

    @Schema(description = "流程实例", example = "12194")
    private String processInstanceId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "12600")
    private Long userId;
    private String creator;

    @Schema(description = "所属部门", example = "13883")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "26450")
    private String fid;
    private String agentId;

}
