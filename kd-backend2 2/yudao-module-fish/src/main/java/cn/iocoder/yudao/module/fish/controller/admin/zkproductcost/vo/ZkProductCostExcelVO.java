package cn.iocoder.yudao.module.fish.controller.admin.zkproductcost.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 成本 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkProductCostExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("品牌")
    private String productBrand;

    @ExcelProperty("规格")
    private String productSpec;

    @ExcelProperty("单价")
    private BigDecimal productPrice;

    @ExcelProperty("成本")
    private BigDecimal productCost;

    @ExcelProperty("最终成本")
    private BigDecimal finalCost;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("类型(1:饲料;2:药品;3:设备;4:能源;5:额外;6:固定资产;7:库存;)")
    private String productType;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("负责人所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    private String productUnit; // 商品单位
    private BigDecimal productCount; // 商品数量
    private Long outOrderProductId; // 入库单id
    private String productNo; // 商品编号
    private String productName; // 商品名称

    private Long workshopId; // 所属车间id
    private Long systemId; // 所属系统id
    private Long pondId; // 所属存塘id
    private String workshopName; // 所属车间id
    private String pondNum; // 所属系统编号
    private String pondDetailNum; // 所属存塘编号
    private LocalDateTime recordTime; // 记录时间
}
