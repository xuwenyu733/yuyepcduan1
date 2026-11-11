package cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 试题分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPaperQuestionPageReqVO extends PageParam {

    @Schema(description = "试卷ID", example = "32590")
    private Long paperId;

    @Schema(description = "试题类型 1单选题 2多选题 3判断题 ", example = "1")
    private String type;

    @Schema(description = "试题类型", example = "赵六")
    private String typeName;

    @Schema(description = "题干")
    private String question;

    @Schema(description = "答案")
    private String answer;

    @Schema(description = "解析")
    private String analysis;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "分数")
    private Double score;

    @Schema(description = "选项")
    private String options;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "26259")
    private Long userId;

    @Schema(description = "所属部门", example = "6495")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "21701")
    private String fid;

}
