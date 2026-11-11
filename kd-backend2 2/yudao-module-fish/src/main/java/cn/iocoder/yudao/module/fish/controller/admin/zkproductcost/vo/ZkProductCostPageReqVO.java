package cn.iocoder.yudao.module.fish.controller.admin.zkproductcost.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 成本分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkProductCostPageReqVO extends PageParam {

    @Schema(description = "品牌")
    private String productBrand;

    @Schema(description = "规格")
    private String productSpec;

    @Schema(description = "单价", example = "17384")
    private BigDecimal productPrice;

    @Schema(description = "成本")
    private BigDecimal productCost;

    @Schema(description = "最终成本")
    private BigDecimal finalCost;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "类型(1:饲料;2:药品;3:设备;4:能源;5:额外;6:固定资产;7:库存;)", example = "2")
    private String productType;

    @Schema(description = "负责人id", example = "4778")
    private Long userId;

    @Schema(description = "负责人", example = "李四")
    private String userName;

    @Schema(description = "负责人所属部门", example = "1036")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
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
