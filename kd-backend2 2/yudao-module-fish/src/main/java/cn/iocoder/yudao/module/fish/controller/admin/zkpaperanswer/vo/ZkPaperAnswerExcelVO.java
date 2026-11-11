package cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 答题记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkPaperAnswerExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("试卷ID")
    private Long paperId;
    private String paperName;

    @ExcelProperty("答案JSON数组 [{questionId:1,answer:'A'}]")
    private String answer;

    @ExcelProperty("交卷时间")
    private LocalDateTime submitTime;

    @ExcelProperty("总得分")
    private Double score;

    @ExcelProperty("是否及格 1是 0否")
    private String passed;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("考试人id")
    private Long userId;
    private String userName;

    @ExcelProperty("考试人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
