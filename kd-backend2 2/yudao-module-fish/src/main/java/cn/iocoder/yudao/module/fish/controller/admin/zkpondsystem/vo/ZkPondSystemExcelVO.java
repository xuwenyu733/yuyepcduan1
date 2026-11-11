package cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 存塘系统 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkPondSystemExcelVO {

    @ExcelProperty("id")
    private Long id;

    @ExcelProperty("系统编号")
    private String systemNum;

    @ExcelProperty("系统名称")
    private String systemName;

    @ExcelProperty("所在车间")
    private String workshopName;

    @ExcelProperty("所在车间id")
    private Long workshopId;

    @ExcelProperty("负责人")
    private String userName;

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
