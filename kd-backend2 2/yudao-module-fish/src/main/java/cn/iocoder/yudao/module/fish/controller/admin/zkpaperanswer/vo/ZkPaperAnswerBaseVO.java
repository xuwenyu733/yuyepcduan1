package cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 答题记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPaperAnswerBaseVO {

    @Schema(description = "试卷ID", example = "10267")
    private Long paperId;
    private String paperName;

    @Schema(description = "答案JSON数组 [{questionId:1,answer:'A'}]")
    @NotNull(message = "答案不能为空")
    private String answer;

    @Schema(description = "交卷时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime submitTime;

    @Schema(description = "总得分")
    private Double score;

    @Schema(description = "是否及格 1是 0否")
    private String passed;

    @Schema(description = "考试人id", example = "29496")
    private Long userId;
    private String userName;

    @Schema(description = "考试人所属部门", example = "18837")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "23190")
//    @NotNull(message = "文件id不能为空")
    private String fid;

}
