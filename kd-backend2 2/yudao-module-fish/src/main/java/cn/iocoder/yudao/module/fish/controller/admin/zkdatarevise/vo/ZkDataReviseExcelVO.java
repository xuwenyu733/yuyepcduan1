package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 数据修正记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDataReviseExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("系统名称")
    private String pondSystemName;

    @ExcelProperty("系统id")
    private Long pondSystemId;

    @ExcelProperty("系统编号")
    private String pondNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)")
    private String reviseType;
    private String reviseTypeName; // 操作类型名称

    @ExcelProperty("原数据")
    private String originData;

    @ExcelProperty("差值")
    private String differenceData;

    @ExcelProperty("变化比例")
    private String changeRatio;

    @ExcelProperty("原因")
    private String reason;

    @ExcelProperty("技术总监意见")
    private String technicalDirectorsOpinion;

    @ExcelProperty("厂长意见")
    private String directorsOpinion;

    @ExcelProperty("业务id")
    private Long busId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private String status; // 状态(0:已申请;1:已批准;2:技术总监已审批;3:厂长已审批;4:已完成;-1:已拒绝;)
    private String processInstanceId; // 流程实例

}
