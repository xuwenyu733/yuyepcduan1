package cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 设备报警 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDeviceAlarmExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("所属车间id")
    private Long workshopId;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("所属系统id")
    private Long pondSystemId;

    @ExcelProperty("存塘系统编号")
    private String pondNum;

    @ExcelProperty("存塘系统名称")
    private String pondSystemName;

    @ExcelProperty("所属存塘id")
    private Long pondId;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("存塘名称")
    private String pondName;

    @ExcelProperty("批次编号")
    private String batchNumber;

    @ExcelProperty("报警设备id")
    private Long warnDeviceId;

    @ExcelProperty("告警设备名")
    private String warnDevice;

    @ExcelProperty("告警设备类型(0:系统设备;1:池塘传感器;)")
    private Integer warnDeviceType;

    @ExcelProperty("告警对象")
    private String warnName;

    @ExcelProperty("告警类型(0:环境告警;1:事故告警;-2病虫告警;)")
    private Integer warnType;

    @ExcelProperty("告警描述")
    private String warnContent;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("状态(0:待处理;1:已处理;)")
    private Integer status;

    @ExcelProperty("处理描述")
    private String handleInfo;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

    private String metricId; // 设备测量项传感器和控制单元编码
    private String deviceId; // 报警设备id
}
