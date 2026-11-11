package cn.iocoder.yudao.module.fish.controller.admin.zkworkplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 工作目标与计划 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkWorkPlanExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("日计划")
    private String daylyPlan;

    @ExcelProperty("周计划")
    private String weeklyPlan;

    @ExcelProperty("月度目标")
    private String monthlyGoal;

    @ExcelProperty("季度目标")
    private String quarterlyGoal;

    @ExcelProperty("年度目标")
    private String annualGoal;

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
