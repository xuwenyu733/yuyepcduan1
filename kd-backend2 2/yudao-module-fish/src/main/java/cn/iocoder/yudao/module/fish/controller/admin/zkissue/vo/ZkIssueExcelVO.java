package cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问题总结 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkIssueExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("问题标题")
    private String issueTitle;

    @ExcelProperty("问题描述")
    private String issueContent;

    @ExcelProperty("总结标题")
    private String summaryTitle;

    @ExcelProperty("总结内容")
    private String summaryContent;

    @ExcelProperty("状态")
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
