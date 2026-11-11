package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 生物性资产 Excel 导出 Request VO，参数和 ZkBiologyCapitalPageReqVO 是一致的")
@Data
public class ZkBiologyCapitalExportReqVO {

    @Schema(description = "车间id", example = "13338")
    private Long workshopId;

    @Schema(description = "车间名称", example = "芋艿")
    private String workshopName;

    @Schema(description = "系统名称", example = "芋艿")
    private String pondSystemName;

    @Schema(description = "系统id", example = "4335")
    private Long pondSystemId;

    @Schema(description = "系统编号")
    private String pondNum;

    @Schema(description = "存塘名称", example = "王五")
    private String pondName;

    @Schema(description = "存塘id", example = "20900")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "品名", example = "王五")
    private String productName;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "规格")
    private String specifications;

    @Schema(description = "库存类别")
    private String productClassification;

    @Schema(description = "类别编号")
    private String categoryNo;

    @Schema(description = "数量")
    private Long productNum;

    @Schema(description = "总重量")
    private BigDecimal productWeight;

    @Schema(description = "市场单价", example = "18803")
    private BigDecimal productPrice;

    @Schema(description = "资产总计")
    private BigDecimal capitalTotal;

    @Schema(description = "当前成本")
    private BigDecimal currCost;

    @Schema(description = "饲料成本")
    private BigDecimal feedCost;

    @Schema(description = "药品成本")
    private BigDecimal medicineCost;

    @Schema(description = "设备成本")
    private BigDecimal deviceCost;

    @Schema(description = "能源成本")
    private BigDecimal energyCost;

    @Schema(description = "额外成本")
    private BigDecimal extraCost;

    @Schema(description = "固定资产成本")
    private BigDecimal fixedCost;

    @Schema(description = "生物资产成本")
    private BigDecimal biologyCost;

    @Schema(description = "存塘个数", example = "5315")
    private Long pondCount;

    @Schema(description = "总体盈亏")
    private BigDecimal overallProfitAndLoss;

    @Schema(description = "盈亏比例")
    private BigDecimal profitAndLossRatio;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "25289")
    private Long userId;

    @Schema(description = "所属部门", example = "12426")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "19790")
    private String fid;
    private BigDecimal feedCount = new BigDecimal(0); // 累计饲料重量
    private BigDecimal feedPrice = new BigDecimal(0); // 累计饲料价格
    private BigDecimal fishPrice = new BigDecimal(0); // 鱼苗价格

}
