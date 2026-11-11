package cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 告警 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkPondWarnExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("具体位置")
    private String workshopPosition;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("存塘名称")
    private String pondName;
    private String pondNum; // 存塘编号

    @ExcelProperty("所属存塘id")
    private Long pondId;

    private Long warnDeviceId; // 报警设备id
    @ExcelProperty("告警对象")
    private String warnName;

    @ExcelProperty("状态(0:环境告警;1:事故告警;-1:病虫告警;)")
    private String warnType;

    @ExcelProperty("告警描述")
    private String warnContent;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("状态(0:待处理;1:已处理;)")
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
    private String handleInfo; // 处理描述

    private Long pondSystemId; // 存塘系统id
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号

    private String iotMessageId; // iot消息唯一标识
}
