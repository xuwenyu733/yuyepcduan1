package cn.iocoder.yudao.module.fish.controller.admin.zkpurifytreatment.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 处理措施记录分页 Request VO")
@Data
public class PurifyTreatmentPageReqVO extends PageParam {

    @Schema(description = "关联流程ID", example = "835")
    private Long processId;
    private String recordNumber;

    @Schema(description = "处理日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] treatmentDate;

    @Schema(description = "措施类型(用药/水质调节等)", example = "2")
    private String type;

    @Schema(description = "措施内容")
    private String content;

    @Schema(description = "剂量")
    private String dosage;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "备注说明")
    private String notes;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "6076")
    private Long deptId;

}