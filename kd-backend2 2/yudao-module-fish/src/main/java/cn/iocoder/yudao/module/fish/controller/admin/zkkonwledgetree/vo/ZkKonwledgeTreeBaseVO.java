package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 知识树 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkKonwledgeTreeBaseVO {

    @Schema(description = "父编号", example = "14552")
    private Long parentId;

    @Schema(description = "节点内容")
    private String nodeTitle;

    @Schema(description = "节点tag(多个英文逗号隔开)")
    private String nodeTag;

    @Schema(description = "节点类型 cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowledgeNodeTypeEnum", required = true, example = "2")
    @NotNull(message = "节点类型 cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowledgeNodeTypeEnum不能为空")
    private String nodeType;

    @Schema(description = "记录人", example = "3176")
    private Long userId;

    @Schema(description = "所属部门", example = "25724")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "16093")
    private String fid;

    @Schema(description = "文件url", example = "https://www.iocoder.cn")
    private String fileUrl;
    private Float sort;
    private String handleContent; // 解决思路
    private String illName; // 疾病名称
    private String nodeDescribe; // 节点描述

}
