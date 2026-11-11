package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 知识树分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkKonwledgeTreePageReqVO extends PageParam {

    @Schema(description = "父编号", example = "14552")
    private Long parentId;

    @Schema(description = "节点内容")
    private String nodeTitle;

    @Schema(description = "节点tag(多个英文逗号隔开)")
    private String nodeTag;

    @Schema(description = "节点类型 cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowledgeNodeTypeEnum", example = "2")
    private String nodeType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

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
