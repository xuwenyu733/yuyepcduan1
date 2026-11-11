package cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 答题记录 Excel 导出 Request VO，参数和 ZkPaperAnswerPageReqVO 是一致的")
@Data
public class ZkPaperAnswerExportReqVO {

    @Schema(description = "试卷ID", example = "10267")
    private Long paperId;
    private String paperName;

    @Schema(description = "答案JSON数组 [{questionId:1,answer:'A'}]")
    private String answer;

    @Schema(description = "交卷时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] submitTime;

    @Schema(description = "总得分")
    private Double score;

    @Schema(description = "是否及格 1是 0否")
    private String passed;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "考试人id", example = "29496")
    private Long userId;
    private String userName;

    @Schema(description = "考试人所属部门", example = "18837")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "23190")
    private String fid;

}
