package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 鱼苗统计分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkFishOperationLogPageReqVO extends PageParam {

    @Schema(description = "鱼苗批次")
    private String batchNumber;

    @Schema(description = "所属车间id", example = "12862")
    private Long workshopId;

    @Schema(description = "所属系统id", example = "23657")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "所属存塘id", example = "26097")
    private Long pondId;
    private String pondDetailNum; // 存塘编号(区别于pond_num系统编号)

    @Schema(description = "车间名称", example = "王五")
    private String workshopName;

    @Schema(description = "存塘系统名称", example = "李四")
    private String pondSystemName;

    @Schema(description = "存塘名称", example = "赵六")
    private String pondName;

    @Schema(description = "统计时间(yyyy-MM-dd)")
    private String logDay;

    @Schema(description = "鱼苗编码(pl_product.product_no)")
    private String productNo;

    @Schema(description = "鱼苗种类")
    private String fishTypes;

    @Schema(description = "鱼苗规格")
    private String fishSpec;

    @Schema(description = "操作数量")
    private Long dayFishNum;

    @Schema(description = "累计操作数量")
    private Long accumulateFishNum;

    @Schema(description = "投放数量")
    private Long pushFishNum;
    private Long currFishNum = 0L; // 当前存量

    @Schema(description = "当日操作率")
    private BigDecimal dayRate;

    @Schema(description = "累计操作率")
    private BigDecimal accumulateRate;

    @Schema(description = "成活率")
    private BigDecimal survivalRate;

    @Schema(description = "修正后操作数量")
    private Long newDayFishNum;

    @Schema(description = "修正后累计操作数量")
    private Long newAccumulateFishNum;

    @Schema(description = "修正后投放数量")
    private Long newPushFishNum;
    private Long newCurrFishNum = 0L; // 修正后当前存量

    @Schema(description = "修正后当日操作率")
    private BigDecimal newDayRate;

    @Schema(description = "修正后累计操作率")
    private BigDecimal newAccumulateRate;

    @Schema(description = "修正后成活率")
    private BigDecimal newSurvivalRate;

    @Schema(description = "修正依据")
    private String correctionBasis;

    @Schema(description = "差值率")
    private BigDecimal differencesRate;

    @Schema(description = "差异原因")
    private String reasonsForDifferences;

    @Schema(description = "具体描述", example = "你说的对")
    private String detailedDescription;

    @Schema(description = "危险评级(1:正常;2:持续关注;3:立刻介入;)")
    private String dangerRating;

    @Schema(description = "统计类型(-2.1:批次;-2.2:车间;2.3:系统;2.4:存塘;)", example = "2")
    private String logType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "评级人id", example = "7142")
    private Long userId;

    @Schema(description = "评级人", example = "李四")
    private String userName;

    @Schema(description = "所属部门", example = "8079")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "16499")
    private String fid;

    private BigDecimal currFishWeight; // 当前存量重量(Kg)
    private BigDecimal accumulateFishWeight; // 累计存量重量(Kg)
    private BigDecimal pondScale; // 存塘规模
    private BigDecimal fishDensity; // 密度(Kg/平方米)

    private BigDecimal currFeedCount; // 当日投喂量(Kg)
    private BigDecimal accumulateFeedCount; // 累计投喂量(Kg)
    private BigDecimal acturalRadio; // 实际饵料系数
    private BigDecimal currCost; // 当前成本
    private BigDecimal biologyCost; // 生物资产
    private BigDecimal averageInputCost; // 当前平均成本(元/尾)
    private BigDecimal averageBiologicalAssets; // 平均生物资产(元/尾)

    @TableField(exist = false)
    private List<String> batchNumberList; // 批次编号
    private String fishSpecStr; // 搜索规格区域
    private BigDecimal fishPrice = new BigDecimal(0); // 鱼苗价格

}
