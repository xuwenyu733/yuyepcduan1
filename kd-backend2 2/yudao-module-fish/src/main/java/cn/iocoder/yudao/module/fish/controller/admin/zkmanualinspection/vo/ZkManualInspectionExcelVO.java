package cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 人工巡检 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkManualInspectionExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;
    private Long pondId;
    private String pondName;
    private String pondNum;

    @ExcelProperty("巡检名称")
    private String inspectionName;

    @ExcelProperty("巡检描述")
    private String inspectionContent;

    @ExcelProperty("巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;
    private String userName;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
