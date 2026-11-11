package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 设备 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDeviceExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("设备名称")
    private String deviceName;
    private String deviceSn;
    private String deviceMetricId;

    @ExcelProperty("所在车间")
    private String workshopName;
    private BigDecimal standardStart;
    private String startComputer;
    private BigDecimal standardEnd;
    private String endComputer;
    private String deviceUnit;
    private BigDecimal currValue;
    private String standInterval;

    @ExcelProperty("所在车间id")
    private Long workshopId;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("状态(0:关闭中;1:开启中;-1:已废弃;)")
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

    private String pondNum; // 系统编号(车间序号_存塘序号)
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String deviceType; // 设备类型(0:监控数值;1:监控视频;)
    private Long pondSystemId; // 存塘系统id
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    private Integer ruleStatus; // 规则状态(0:关闭中;1:开启中;-1:已废弃;)
}
