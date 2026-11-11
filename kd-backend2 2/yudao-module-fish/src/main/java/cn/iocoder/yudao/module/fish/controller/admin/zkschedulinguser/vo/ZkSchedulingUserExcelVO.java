package cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 排班用户 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkSchedulingUserExcelVO {

    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("排班id")
    private Long schedulingId;

    @ExcelProperty("被排班人id")
    private Long userId;

    @ExcelProperty("被排班人")
    private String userName;

    @ExcelProperty("被排班人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("文件id")
    private String fid;

}
