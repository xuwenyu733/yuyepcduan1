package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 知识树 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkKonwledgeTreeExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("父编号")
    private Long parentId;

    @ExcelProperty("节点内容")
    private String nodeTitle;

    @ExcelProperty("节点tag(多个英文逗号隔开)")
    private String nodeTag;

    @ExcelProperty("节点类型 cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowledgeNodeTypeEnum")
    private String nodeType;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("记录人")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
    private String fid;

    @ExcelProperty("文件url")
    private String fileUrl;
    private Float sort;
    private String handleContent; // 解决思路
    private String illName; // 疾病名称
    private String nodeDescribe; // 节点描述

}
