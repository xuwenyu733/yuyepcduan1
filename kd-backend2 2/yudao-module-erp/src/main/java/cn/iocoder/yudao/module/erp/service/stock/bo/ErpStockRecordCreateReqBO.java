package cn.iocoder.yudao.module.erp.service.stock.bo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 库存明细的创建 Request BO
 *
 * @author 芋道源码
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErpStockRecordCreateReqBO {


    /**
     * 方便更新
     */
    // @NotNull(message = "id")
    private Long id;
    private Long tenantId; // 部门id
    private Long deptId; // 部门id
    private Long userId; // 用户id

    /**
     * 产品编号
     */
    @NotNull(message = "产品编号不能为空")
    private Long productId;
    /**
     * 仓库编号
     */
    @NotNull(message = "仓库编号不能为空")
    private Long warehouseId;
    /**
     * 出入库数量
     *
     * 正数，表示入库；负数，表示出库
     */
    @NotNull(message = "出入库数量不能为空")
    private BigDecimal count;

    /**
     * 业务类型
     */
    @NotNull(message = "业务类型不能为空")
    private Integer bizType;
    /**
     * 业务编号
     */
    // @NotNull(message = "业务编号不能为空")
    private Long bizId;
    /**
     * 业务项编号
     */
    // @NotNull(message = "业务项编号不能为空")
    private Long bizItemId;
    /**
     * 业务单号
     */
    // @NotNull(message = "业务单号不能为空")
    private String bizNo;
    private String remark; // 备注
    private Long busId; // 业务id

    private BigDecimal remainingQuantity; // 零星入库数量
    private Long remainingUnitId; // 零星入库单位

    public ErpStockRecordCreateReqBO(Long productId, Long warehouseId, BigDecimal count, Integer bizType, Long bizId, Long bizItemId, String bizNo) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.count = count;
        this.bizType = bizType;
        this.bizId = bizId;
        this.bizItemId = bizItemId;
        this.bizNo = bizNo;
    }
}
