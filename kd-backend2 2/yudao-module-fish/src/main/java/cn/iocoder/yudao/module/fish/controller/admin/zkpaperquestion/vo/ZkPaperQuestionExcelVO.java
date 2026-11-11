package cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 试题 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkPaperQuestionExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("试卷ID")
    private Long paperId;

    @ExcelProperty("试题类型 1单选题 2多选题 3判断题 ")
    private String type;

    @ExcelProperty("试题类型")
    private String typeName;

    @ExcelProperty("题干")
    private String question;

    @ExcelProperty("答案")
    private String answer;

    @ExcelProperty("解析")
    private String analysis;

    @ExcelProperty("标签")
    private String tag;

    @ExcelProperty("分数")
    private Double score;

    @ExcelProperty("选项")
    private String options;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
