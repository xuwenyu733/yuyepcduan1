package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 试卷 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPaperBaseVO {

    @Schema(description = "试卷名称", example = "张三")
    private String name;

    @Schema(description = "考试说明")
    private String content;

    @Schema(description = "开始考试时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    @Schema(description = "结束考试时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;

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

    @Schema(description = "负责人id", example = "20641")
    private Long userId;

    @Schema(description = "所属部门", example = "6654")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "20285")
    @NotNull(message = "文件id不能为空")
    private String fid;

}
