package cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 车间 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkWorkshopExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("车间名称")
    private String workshopName;

    @ExcelProperty("具体位置")
    private String workshopPosition;

    @ExcelProperty("经度")
    private String longitude;

    @ExcelProperty("维度")
    private String dimension;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)")
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
    private String tenantName; // 租户名称
    private String deptName; // 部门名称

}
