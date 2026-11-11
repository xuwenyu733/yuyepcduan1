package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo;

import cn.iocoder.yudao.framework.common.util.json.databind.TimestampLocalDateTimeDeserializer;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 存塘鱼苗明细 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPondFishDetailBaseVO {

    @Schema(description = "存塘鱼苗表id", required = true, example = "19778")
//    @NotNull(message = "存塘鱼苗表id不能为空")
    private Long zkPondFishId;

    @Schema(description = "所属车间id", example = "29734")
    private Long workshopId;
    private String workshopName;

    @Schema(description = "存塘名称", example = "李四")
    private String pondName;
    private String pondNum; // 存塘系统编号
    private String pondSystemName; // 存塘系统名称

    @Schema(description = "所属存塘id", example = "13200")
    @NotNull(message = "存塘id不能为空")
    private Long pondId;

    @Schema(description = "鱼苗种类")
    private String fishTypes;
    private String productNo; // 鱼苗对应库存统计
    private String fishName;

    @Schema(description = "鱼苗数量", required = true)
    @NotNull(message = "鱼苗数量不能为空")
    private Long fishNum;

    @Schema(description = "投放类型(-2:损失;-1:卖出;1:投放;)", example = "2")
    private String addType;

    @Schema(description = "负责人id", example = "15132")
    /**
     * 投放人id
     */
    private Long userId;
    private String userName;

    @Schema(description = "所属部门", example = "5410")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "27217")
    // @NotNull(message = "文件id不能为空")
    private String fid;
    /**
     * 鱼在塘的数量统计json
     */
    private String detailJson;

    private String pondScale; // 存塘规格
    private Integer historyFishNum; // 历史鱼数
    private Integer currFishNum; // 现在鱼数
    private String fishSpec; // 鱼苗规格
    private BigDecimal salePrice; // 销售价格
    private BigDecimal saleWeight; // 销售重量
    private BigDecimal pondWeight; // 原池重量
    private BigDecimal currPondWeight; // 当前原池重量
    private BigDecimal saleIncome; // 销售收入
    private BigDecimal saleCost; // 销售成本
    private String targetPondNum; // 目标存塘编号
    private String targetPondSystemName; // 存塘系统名称
    private String targetPondScale; // 目标存塘规格
    private Long targetPondId; // 目标存塘Id
    private String targetPondName; // 目标存塘名称
    private Integer targetHistoryFishNum; // 目标历史鱼数
    private Integer targetCurrFishNum; // 目标现在鱼数
    private String remark; // 备注
    private Long supplierId; // 供应商id

    private Long sevenDayMortalityRate; // 投放七日死亡量
    private String batchNumber; // 批次编号
    private String supplier; // 供应商
    private String supplierAddress; // 供应商地址

    private Integer remainingInventory; // 当前批次剩余鱼苗数量
    private String pondDetailNum; // 原存塘编号
    private String targetPondDetailNum; // 目标存塘编号

    private BigDecimal survivalRate; // 成活率
    private BigDecimal targetWeight; // 分池重量
    private Long splitAfterDead; // 分池后3日死亡量
    private String targetFishSpec; // 目标规格
    @JsonDeserialize(using = TimestampLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    private String reviseLog; // 修改值的历史值

    private Long errorValue; // 误差值(尾)
    private String saleDirection; // 销售方向
    private String purchaser; // 购买人
    private String purchaserPhone; // 联系方式
    private BigDecimal splitRadio; // 分池饵料系数

    private String statisticsType; // 统计类型(0:未统计;1:已统计;)  FishStatisticsTypeEnum

    private Long taskDetailId; // 任务明细id
    private Long parentId; // zk_pond_fish_parent.id

    private BigDecimal splitAvgFishSpec; // 分池后，需要将 (原池重量+分过来重量)/鱼塘鱼数=当前鱼塘的avg fishSpec

    private ZkPondFishDO zkPondFishDO; // 存塘信息

}
