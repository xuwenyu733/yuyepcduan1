package cn.iocoder.yudao.module.fish.controller.admin.zkgrowpool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 生长模型鱼池新增/修改 Request VO")
@Data
public class ZkGrowPoolSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10602")
    private Long id;

    @Schema(description = "池号")
    private String poolNumber;

    @Schema(description = "大小(m³)")
    private BigDecimal size;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "负责人")
    private String manager;

    @Schema(description = "状态(available:空闲, in_use:使用中, maintenance:维护中)", example = "1")
    private String status;

    @Schema(description = "备注")
    private String notes;

    @Schema(description = "所属部门", example = "17021")
    private Long deptId;

}