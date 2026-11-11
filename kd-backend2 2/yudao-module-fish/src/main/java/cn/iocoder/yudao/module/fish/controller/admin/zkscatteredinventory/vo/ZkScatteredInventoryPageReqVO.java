package cn.iocoder.yudao.module.fish.controller.admin.zkscatteredinventory.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 零星库存分页 Request VO")
@Data
public class ZkScatteredInventoryPageReqVO extends PageParam {

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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "16895")
    private Long deptId;
    private Long busId; // 业务id

    private String productName; // 商品名称
    private String unitName; // 单位名称
    private String warehouseName; // 仓库名称
    private String categoryName; // 品类名称
    private Long categoryId; // 品类id

}