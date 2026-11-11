package cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 文档分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDocumentPageReqVO extends PageParam {

    @Schema(description = "搜索关键词（文档标题、作者）", example = "农业项目")
    private String keyword;

    @Schema(description = "文档类型", example = "申报文档")
    private String type;

    @Schema(description = "作者ID", example = "1")
    private Long authorId;

    @Schema(description = "视图模式：all-全部文档, personnel-按人员分类, business-按业务分类", example = "all")
    private String viewMode;
}



