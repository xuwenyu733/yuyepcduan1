package cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 培训知识 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class TrainingKnowledgeExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("知识名称")
    private String knowledgeName;

    @ExcelProperty("知识详情")
    private String knowledgeContent;

    @ExcelProperty("知识备注")
    private String knowledgeRemark;

    @ExcelProperty("状态(0:待入库;1:已入库;-1:已删除;)")
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
    private String agentId;

}
