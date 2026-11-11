package cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 投喂记录 Excel 导出 Request VO，参数和 ZkFeedingLogPageReqVO 是一致的")
@Data
public class ZkFeedingLogExportReqVO {

    @Schema(description = "系统编号(车间序号_存塘序号)")
    private String pondNum;

    @Schema(description = "存塘名称", example = "李四")
    private String pondName;

    @Schema(description = "存塘id", example = "26804")
    private Long pondId;
    private Long workshopId; // 所属车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号

    @Schema(description = "存塘规模")
    private String pondScale;

    @Schema(description = "存塘鱼数")
    private Integer fishNum;
    private String feedName;
    private String productNo; // 商品编号

    @Schema(description = "品牌")
    private String feedBrand;

    @Schema(description = "规格")
    private String feedSpec;

    @Schema(description = "数量", example = "24878")
    private BigDecimal feedCount;

    @Schema(description = "投喂时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] feedTime;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "类型(1:人工;2:机器;)", example = "2")
    private String feedType;

    @Schema(description = "负责人id", example = "8392")
    private Long userId;

    @Schema(description = "负责人", example = "赵六")
    private String userName;

    @Schema(description = "负责人所属部门", example = "5776")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    private String differenceBefore; // 与前5天对比
    private BigDecimal personScore; // 人对摄食状态评分
    private BigDecimal sumScore; // 综合评分
    private String reviseLog; // 修改值的历史值
    private BigDecimal feedingPrice; // 饲料价格
    private BigDecimal feedingCost; // 投喂成本

    private Long taskDetailId; // 任务明细id
    private Long warehouseId; // 饲料仓库id
    private Long productId; // 饲料商品id
}
