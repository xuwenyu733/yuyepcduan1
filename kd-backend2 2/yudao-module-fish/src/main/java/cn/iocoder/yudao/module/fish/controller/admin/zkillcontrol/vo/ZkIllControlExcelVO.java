package cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 疾病诊断与防治记录 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkIllControlExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("病害简称")
    private String illTitle;

    @ExcelProperty("病害描述")
    private String illContent;

    @ExcelProperty("诊断标题")
    private String controlTitle;

    @ExcelProperty("诊断描述")
    private String controlContent;

    @ExcelProperty("状态(0:正常;-1:已删除;)")
    private String status;

    @ExcelProperty("附件")
    private String fids;

    @ExcelProperty("流程实例")
    private String processInstanceId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;
    private String agentId;

}
