package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 试卷分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPaperPageReqVO extends PageParam {

    @Schema(description = "试卷名称", example = "张三")
    private String name;

    @Schema(description = "考试说明")
    private String content;

    @Schema(description = "开始考试时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束考试时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "时长（分钟）")
    private Integer duration;

    @Schema(description = "是否允许查看答案 1是 0否")
    private String lookAnswer;

    @Schema(description = "是否打乱试题 1是 0否")
    private String upset;

    @Schema(description = "及格分数")
    private Double passScore;

    @Schema(description = "试卷总分")
    private Double totalScore;

    @Schema(description = "状态 1正常 0删除", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "20641")
    private Long userId;

    @Schema(description = "所属部门", example = "6654")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "20285")
    private String fid;

}
