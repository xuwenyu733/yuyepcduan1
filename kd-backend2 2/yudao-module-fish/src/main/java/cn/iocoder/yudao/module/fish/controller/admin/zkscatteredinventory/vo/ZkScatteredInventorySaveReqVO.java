package cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 零星库存新增/修改 Request VO")
@Data
public class ZkScatteredInventorySaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4491")
    private Long id;

    @Schema(description = "商品id", example = "25904")
    private Long productId;

    @Schema(description = "仓库id", example = "1337")
    private Long warehouseId;

    @Schema(description = "单位id", example = "2966")
    private Long unitId;

    @Schema(description = "剩余数量")
    private BigDecimal remainingQuantity;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "零星库存类型(1:手动出库;2:手动入库零星;)", example = "1")
    private String busType;

    @Schema(description = "所属部门", example = "16895")
    private Long deptId;
    private Long busId; // 业务id

    private String productName; // 商品名称
    private String unitName; // 单位名称
    private String warehouseName; // 仓库名称
    private String categoryName; // 品类名称
    private Long categoryId; // 品类id
}