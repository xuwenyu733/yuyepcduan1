package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJson;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJsonItem;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.infra.websocket.WebSocketConfig;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;

/**
 * 存塘鱼苗明细 DO
 *
 * @author 系统管理员
 */
@TableName("zk_pond_fish_detail")
@KeySequence("zk_pond_fish_detail_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondFishDetailDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 存塘鱼苗表id
     */
    private Long zkPondFishId;
    /**
     * 所属车间id
     */
    private Long workshopId;
    private String workshopName;
    /**
     * 存塘名称
     */
    private String pondName;
    private String pondNum; // 存塘系统编号
    private String pondSystemName; // 存塘系统名称

    /**
     * 所属存塘id
     */
    private Long pondId;
    /**
     * 鱼苗种类
     * 字典类型 ： zk_pond_fish_types
     */
    private String fishTypes;
    private String productNo; // 鱼苗对应库存统计
    // @TableField(exist = false)
    private String fishName;
    /**
     * 鱼苗数量
     */
    private Long fishNum;
    /**
     * 投放类型(-2:死亡;-1:卖出;1:投放;-3:分池;)
     * 字典类型 ： zk_pond_fish_detail_add_type
     */
    private String addType;
    /**
     * 投放人id
     */
    private Long userId;
    private String userName;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

    private String pondScale; // 存塘规格
    private Integer historyFishNum; // 历史鱼数
    private Integer currFishNum; // 现在鱼数
    private String fishSpec; // 鱼苗规格
    private BigDecimal salePrice; // 销售价格
    private BigDecimal saleWeight; // 销售重量 分池场景中，可以作为原池历史重量存储
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

    private Long sevenDayMortalityRate = 0L; // 投放七日死亡量
    private String batchNumber; // 批次编号
    private Long supplierId; // 供应商id
    private String supplier; // 供应商
    private String supplierAddress; // 供应商地址
    @TableField(exist = false)
    private Integer remainingInventory; // 当前批次剩余鱼苗数量
    private String pondDetailNum; // 原存塘编号
    private String targetPondDetailNum; // 目标存塘编号

    private BigDecimal survivalRate; // 成活率
    private BigDecimal targetWeight; // 分池重量
    private Long splitAfterDead = 0L; // 分池后3日死亡量
    private String targetFishSpec; // 目标规格

    private Long taskDetailId; // 任务明细id

    private Long errorValue; // 误差值(尾)
    private String saleDirection; // 销售方向
    private String purchaser; // 购买人
    private String purchaserPhone; // 联系方式
    private BigDecimal splitRadio; // 分池饵料系数

    @TableField(exist = false)
    private String createStr; // 时间 yyyy-MM-dd

    @TableField(exist = false)
    private String operationInfo;
    private String reviseLog; // 修改值的历史值
    @TableField(exist = false)
    private Long differenceData; // 差值

    private String statisticsType; // 统计类型(0:未统计;1:已统计;)  FishStatisticsTypeEnum
    private Long parentId; // zk_pond_fish_parent.id

    @TableField(exist = false)
    private ZkPondFishDO zkPondFishDO; // 存塘信息

    public ZkPondFishDetailDO build_zkPondFishId(Long zkPondFishId) {
        this.setZkPondFishId(zkPondFishId);
        return this;
    }
    public ZkPondFishDetailDO build_statisticsType(String statisticsType) {
        this.statisticsType = statisticsType;
        return this;
    }
    public ZkPondFishDetailDO build_createStr() {
        String format = EasyDateUtil.format(this.getCreateTime(), "yyyy-MM-dd");
        this.setCreateStr(format);
        return this;
    }
    public ZkPondFishDetailDO build_reviseLog(String reviseLog, Long differenceData) {
        if(StringUtils.isEmpty(this.getReviseLog())) {
            this.setReviseLog(reviseLog);
        } else {
            String temp = new StringBuilder(this.getReviseLog()).append(",").append(reviseLog).toString();
            this.setReviseLog(temp);
        }
        this.differenceData = differenceData;
        return this;
    }


    public DetailJson build_detailJson() {
        // String fishType, String fishName, Long fishNum
        DetailJsonItem detailJsonItem = to_detailJsonItem();
        DetailJson build = DetailJson.build(detailJsonItem);
        return build;
    }
    public DetailJsonItem to_detailJsonItem() {
        // String fishType, String fishName, Long fishNum
        DetailJsonItem detailJsonItem = new DetailJsonItem(fishTypes, fishName, fishNum, productNo, fishSpec);
        return detailJsonItem;
    }

    public DetailJson build_targetDetailJson() {
        // String fishType, String fishName, Long fishNum
        DetailJsonItem detailJsonItem = to_targetDetailJsonItem();
        DetailJson build = DetailJson.build(detailJsonItem);
        return build;
    }
    public DetailJsonItem to_targetDetailJsonItem() {
        // String fishType, String fishName, Long fishNum
        DetailJsonItem detailJsonItem = new DetailJsonItem(fishTypes, fishName, fishNum);
        return detailJsonItem;
    }

    public String build_operationInfo() {
        String temp = "";
        String time = EasyDateUtil.format(this.getCreateTime(), "yyyy-MM-dd HH:mm:ss");
        String userName = StringUtils.isEmpty(this.getUserName())? WebSocketConfig.baseUserService.byKey(String.valueOf(this.getUserId()), "nickname")+"":this.getUserName();
        if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), this.getAddType())) {
            temp = String.format("%s 于 %s 统计死掉 %s:%s尾 %s ，对应存塘:%s", userName, time, this.getFishName(), this.getFishNum(), this.getFishSpec(), this.getPondDetailNum());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), this.getAddType())) {
            temp = String.format("%s 于 %s 销售 %s:%s尾 %s，重量:%sKg,价格:%s元/kg,成本:%s元,收入:%s元  ，对应存塘:%s",
                    userName, time, this.getFishName(), this.getFishNum(), this.getFishSpec()
                    ,this.getSaleWeight(), this.getSalePrice(), this.getSaleCost(), this.getSaleIncome(), this.getPondDetailNum());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), this.getAddType())) {
            temp = String.format("%s 于 %s 投放 %s:%s尾 %sg/尾 到 存塘:%s", userName, time, this.getFishName(), this.getFishNum(), this.getFishSpec(), this.getPondDetailNum());
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), this.getAddType())) {
            temp = String.format("%s 于 %s 从 原塘:%s 分掉 %s:%s尾 %s 到 目标塘:%s", userName, time, this.getPondDetailNum()
                    ,this.getFishName(), this.getFishNum(), this.getFishSpec()
                    ,this.getTargetPondDetailNum());
        }
        temp = temp.replaceAll(":null", "").replaceAll("null", "");
        this.setOperationInfo(temp);
        return temp;
    }
    public ZkPondFishDetailDO build_addType(String addType) {
        this.addType = addType;
        return this;
    }

    public ZkPondFishDetailDO(Long zkPondFishId, Long workshopId, String workshopName, String pondName, String pondNum, Long pondId,
                              String fishTypes, String productNo, String fishName, Long fishNum, String addType, Long userId,
                              String userName, Long deptId, String pondScale) {
        this.zkPondFishId = zkPondFishId;
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondName = pondName;
        this.pondNum = pondNum;
        this.pondId = pondId;
        this.targetPondId = pondId;
        this.fishTypes = fishTypes;
        this.productNo = productNo;
        this.fishName = fishName;
        this.fishNum = fishNum;
        this.addType = addType;
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
        this.pondScale = pondScale;
    }

    public String selectOperationName() {
        String temp = "";
        if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), this.getAddType())) {
            temp = PondFishDetailAddTypeEnum.DEAD.getName();
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), this.getAddType())) {
            temp = PondFishDetailAddTypeEnum.SALE.getName();
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), this.getAddType())) {
            temp = PondFishDetailAddTypeEnum.ADD.getName();
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), this.getAddType())) {
            temp = PondFishDetailAddTypeEnum.SPLIT.getName();
        }
        return temp;
    }

    public ZkPondFishDetailDO build_fishNum(Long fishNum) {
        if(this.fishNum == null) this.fishNum = 0L;
        this.fishNum += fishNum;
        return this;
    }
}
