package cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;


/**
 * fish系统日志 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class FishSystemLogExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("存塘编号")
    private String pondDetailNum;

    @ExcelProperty("日志标题")
    private String logTitle;

    @ExcelProperty("日志类型(0:fish;1:其他;)")
    private String logType;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("原始数据")
    private String originData;

    @ExcelProperty("目标数据")
    private String targetData;

    @ExcelProperty("原始结果数据")
    private String originRes;

    @ExcelProperty("目标结果数据")
    private String targetRes;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

}
