package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 试卷 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkPaperExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("试卷名称")
    private String name;

    @ExcelProperty("考试说明")
    private String content;

    @ExcelProperty("开始考试时间")
    private LocalDateTime startTime;

    @ExcelProperty("结束考试时间")
    private LocalDateTime endTime;

    @ExcelProperty("时长（分钟）")
    private Integer duration;

    @ExcelProperty("是否允许查看答案 1是 0否")
    private String lookAnswer;

    @ExcelProperty("是否打乱试题 1是 0否")
    private String upset;

    @ExcelProperty("及格分数")
    private Double passScore;

    @ExcelProperty("试卷总分")
    private Double totalScore;

    @ExcelProperty("状态 1正常 0删除")
    private String status;

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
