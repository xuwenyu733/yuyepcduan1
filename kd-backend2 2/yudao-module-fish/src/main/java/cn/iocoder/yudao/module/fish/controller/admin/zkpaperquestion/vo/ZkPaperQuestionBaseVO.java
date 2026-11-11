package cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 试题 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPaperQuestionBaseVO {

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

    @Schema(description = "负责人id", example = "26259")
    private Long userId;

    @Schema(description = "所属部门", example = "6495")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "21701")
    @NotNull(message = "文件id不能为空")
    private String fid;

}
