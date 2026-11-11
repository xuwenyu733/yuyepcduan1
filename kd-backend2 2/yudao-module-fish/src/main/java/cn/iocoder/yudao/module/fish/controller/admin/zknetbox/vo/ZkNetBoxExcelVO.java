package cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 网盘资源 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkNetBoxExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("资源类型(1:入职培训;2:运营手册;3:规章制度;4:工作任务及流程;)")
    private String fileType;

    @ExcelProperty("资源类型名称")
    private String fileTypeName;

    @ExcelProperty("资源前缀")
    private String prefixPath;

    @ExcelProperty("备份")
    private String remark;

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
