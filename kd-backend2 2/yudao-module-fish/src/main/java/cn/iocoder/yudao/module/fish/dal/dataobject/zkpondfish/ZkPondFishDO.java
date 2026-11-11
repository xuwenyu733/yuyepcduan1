package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasySetUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJson;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJsonItem;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 存塘鱼苗 DO
 *
 * @author 系统管理员
 */
@TableName("zk_pond_fish")
@KeySequence("zk_pond_fish_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondFishDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 所属车间id
     */
    private Long workshopId;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 所属存塘id
     */
    private Long pondId;
    private String pondNum; // 存塘系统编号
    private String pondDetailNum; // 存塘编号
    private String pondSystemName; // 存塘系统名称
    /**
     * 鱼苗种类
     */
    private String fishTypes;
    /**
     * 鱼苗数量
     */
    private Long fishNum = 0L;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

    /**
     * 鱼在塘的数量统计json
     */
    private String detailJson;
    private Long pushFishNum = 0L; // 投放鱼苗量
    private Long splitReduceNum = 0L; // 分池减少鱼苗量
    private Long splitAddNum = 0L; // 分池鱼增加苗量
    private Long deadFishNum = 0L; // 死亡鱼苗量
    private Long saleFishNum = 0L; // 销售鱼苗量
    private String batchNumber; // 批次编号
    private BigDecimal initFishSpec; // 初始规格
    private BigDecimal currFishSpec; // 当前规格(打样后更新该值)

    private Long splitDayMortalityRate = 0L; // 分池七日死亡量
    private BigDecimal splitDayMortalityRatePer; // 分池七日死亡率
    private Long recentDayMortalityRate = 0L; // 投放七日死亡量
    private BigDecimal recentDayMortalityRatePer; // 投放七日死亡率
    // private Long accumulativeDeathToll; // 累计死亡量
    private BigDecimal mortalityRate; // 累计死亡率
    private BigDecimal accumulatedFeedingAmount; // 累计投喂量
    private BigDecimal accumulatedWeightGain; // 累计增重
    // private Long saleFishNum; // 销售数量
    private BigDecimal saleWeight; // 销售重量
    private BigDecimal avgFishSpec; // 平均规格，目前和fishSpec一个值，现在当销售的规格
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数

    @TableField(exist = false)
    private String flushInfo;
    @TableField(exist = false)
    private String fishName;
    @TableField(exist = false)
    private BigDecimal fishSpec; // 初始规格
    @TableField(exist = false)
    private BigDecimal feedCount; // 累计使用饲料

    public ZkPondFishDO build_specInfo(BigDecimal initFishSpec, BigDecimal currFishSpec) {
        this.initFishSpec = initFishSpec;
        if(this.currFishSpec == null || !EasyBigDecimalUtil.gt0(this.currFishSpec)) {
            this.currFishSpec = currFishSpec;
        }
        if(this.avgFishSpec == null || !EasyBigDecimalUtil.gt0(this.avgFishSpec)) {
            this.avgFishSpec = currFishSpec;
        }
        return this;
    }
    public ZkPondFishDO build_currFishSpec(BigDecimal currFishSpec) {
        if(this.initFishSpec == null || !EasyBigDecimalUtil.gt0(this.initFishSpec)) {
            this.initFishSpec = currFishSpec;
        }
        this.currFishSpec = currFishSpec;
        this.avgFishSpec = currFishSpec;
        return this;
    }
    public ZkPondFishDO build_specInfo(String initFishSpecStr) {
        if(StringUtils.isEmpty(initFishSpecStr)) return this;
        try{
            BigDecimal initFishSpec = EasyBigDecimalUtil.toNumber(initFishSpecStr);
            this.build_specInfo(initFishSpec, initFishSpec); // 初始规格都一样，后面打样会更新 鱼苗规格
        }catch (Exception e) {
            // 不用管，投放的时候如果鱼苗 没有规格后续打样也会更新鱼苗的规格
        }
        return this;
    }
    public ZkPondFishDO build_pushFishNum(Long fishNum) {
        this.pushFishNum += fishNum;
        return this;
    }
    public ZkPondFishDO build_push(Long differenceData) {
        this.pushFishNum += differenceData;
        this.fishNum += differenceData;
        return this;
    }
    public ZkPondFishDO build_deadFishNum(Long deadFishNum) {
        this.deadFishNum += deadFishNum;
        return this;
    }
    public ZkPondFishDO build_accumulatedFeedingAmount(BigDecimal accumulatedFeedingAmount) {
        this.accumulatedFeedingAmount = EasyBigDecimalUtil.add(this.accumulatedFeedingAmount, accumulatedFeedingAmount);
        return this;
    }
    public ZkPondFishDO build_saleFishNum(Long saleFishNum) {
        this.saleFishNum += saleFishNum;
        return this;
    }
    public ZkPondFishDO build_splitReduceNum(Long splitReduceNum) {
        this.splitReduceNum += splitReduceNum;
        return this;
    }
    public ZkPondFishDO build_splitAdd(Long differenceData) {
        this.splitAddNum += differenceData;
        this.fishNum += differenceData;
        return this;
    }
    public ZkPondFishDO build_splitReduce(Long differenceData) {
        if(this.fishNum == null) this.fishNum = 0L;
        if(this.splitReduceNum == null) this.splitReduceNum = 0L;
        this.splitReduceNum -= differenceData;
        this.fishNum -= differenceData;
        return this;
    }
    public ZkPondFishDO build_split(Long differenceData) {
        if(this.splitAddNum == null) this.splitAddNum = 0L;
        if(this.splitReduceNum == null) this.splitReduceNum = 0L;
        this.splitAddNum += differenceData;
        this.splitReduceNum -= differenceData;
        return this;
    }
    public ZkPondFishDO build_splitAddNum(Long splitAddNum) {
        this.splitAddNum += splitAddNum;
        return this;
    }
    public ZkPondFishDO build_fishNum(Long fishNum) {
        this.fishNum += fishNum;
        return this;
    }
    public ZkPondFishDO build_batchNumber(String batchNumber) {
        if(StringUtil.isEmpty(this.batchNumber)) {
            this.batchNumber = batchNumber;
        } else {
            String[] batchNumberArr = this.batchNumber.split(",");
            this.batchNumber = EasySetUtil.toSetStr(batchNumberArr, batchNumber);
            // this.batchNumber += "," + batchNumber;
        }
        return this;
    }
    public ZkPondFishDO build_count(Long fishNum, Long pushFishNum, Long splitAddNum, Long deadFishNum, Long saleFishNum) {
        this.fishNum += fishNum;
        this.pushFishNum += pushFishNum;
        this.splitAddNum += splitAddNum;
        this.deadFishNum += deadFishNum;
        this.saleFishNum += saleFishNum;
        return this;
    }
    public ZkPondFishDO build_recentDayMortalityRate(Long recentDayMortalityRate) {
        this.recentDayMortalityRate = recentDayMortalityRate;
        this.recentDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(recentDayMortalityRate), new BigDecimal(fishNum));
        return this;
    }
    public ZkPondFishDO build_splitDayMortalityRate(Long splitDayMortalityRate) {
        this.splitDayMortalityRate = splitDayMortalityRate;
        this.splitDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(splitDayMortalityRate), new BigDecimal(fishNum));
        return this;
    }

    public ZkPondFishDO(String workshopName, Long workshopId, String pondNum, String pondSystemName) {
        this.workshopName = workshopName;
        this.workshopId = workshopId;
        this.pondNum = pondNum;
        this.pondSystemName = pondSystemName;
    }

    public ZkPondFishDO(String workshopName, Long workshopId, String pondName, Long pondId, String fishTypes, Long fishNum, String detailJson) {
        this.workshopName = workshopName;
        this.workshopId = workshopId;
        this.pondName = pondName;
        this.pondId = pondId;
        this.fishTypes = fishTypes;
        this.fishNum = fishNum;
        this.detailJson = detailJson;
    }
    public ZkPondFishDO build_pondInfo(Long workshopId, String workshopName, String pondSystemName, Long pondSystemId, String pondNum,
                                       String pondName, Long pondId, String pondDetailNum) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        // this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        return this;
    }

    public ZkPondFishDO(String workshopName, Long workshopId, String pondName, Long pondId, String fishTypes, Long fishNum, Long userId, Long deptId) {
        this.workshopName = workshopName;
        this.workshopId = workshopId;
        this.pondName = pondName;
        this.pondId = pondId;
        this.fishTypes = fishTypes;
        this.fishNum = fishNum;
        this.userId = userId;
        this.deptId = deptId;
    }

    public ZkPondFishDO build_info(String fishTypes, Long fishNum, String detailJson) {
        this.fishTypes = fishTypes;
        this.fishNum = fishNum;
        this.detailJson = detailJson;
        return this;
    }

    // 入口： 获取 原塘 鱼统计数据 zk_pond_fish
    public ZkPondFishDO build_detailJson(ZkPondFishDetailDO zkPondFishDetail) {
        // 1. 设置detailJson
        DetailJson json = zkPondFishDetail.build_detailJson();
        // 2. 设置 flushTypes,同时合并相同flushTypes
        flush_detailJson(json);

        String detailJson = JSON.toJSONString(json);
        this.setDetailJson(detailJson);
        return this;
    }
    // 给 统计数据 设置 鱼类型、鱼数量
    public void flush_detailJson(DetailJson localDetailJson) {
        // 1. 去掉为0的
        List<DetailJsonItem> itemList = localDetailJson.getDetailJson();
        if(EasyListUtil.isEmpty(itemList)) return;
        List<DetailJsonItem> collect = itemList.stream().filter(obj -> !Objects.equals(obj.getFishNum(), 0L)).collect(Collectors.toList());
        localDetailJson.setDetailJson(collect);
        // 2. 重新设置flushTypes
        String newFlushTypes = collect.stream().map(obj -> obj.getFishType()).collect(Collectors.joining(","));
        this.setFishTypes(newFlushTypes);
        // 3. 重新设置flushNum -->这个鱼苗数量由 zk_pond_fish_detail 进行维护
        // AtomicReference<Long> res = new AtomicReference<>(0L);
        // collect.stream().forEach(obj -> res.updateAndGet(v -> v + obj.getFishNum()));
        // this.setFishNum(res.get());
    }


    // 入口： 获取 目标塘 鱼统计数据 zk_pond_fish
    public ZkPondFishDO build_for_targetDetailJson(ZkPondFishDetailDO zkPondFishDetail) {
        // 1. 设置detailJson
        DetailJson json = zkPondFishDetail.build_targetDetailJson();
        // 2. 设置 flushTypes
        flush_detailJson(json);

        String detailJson = JSON.toJSONString(json);
        this.setDetailJson(detailJson);
        return this;
    }
    public ZkPondFishDO update_detailJson(ZkPondFishDetailDO zkPondFishDetail) {
        // 1. 获取当前 更新的item
        DetailJsonItem jsonItem = zkPondFishDetail.to_detailJsonItem();
        // 2. 获取当前 detailJson
        DetailJson localDetailJson = JSON.parseObject(detailJson, DetailJson.class);
        if(localDetailJson == null) return this;
        // 3. 执行更新 detailJson
        localDetailJson.update(jsonItem, Integer.parseInt(zkPondFishDetail.getAddType()));
        // 4. 如果为0的，需要去掉
        flush_detailJson(localDetailJson);
        // 5. 设置回去
        String detailJson = JSON.toJSONString(localDetailJson);
        this.setDetailJson(detailJson);
        return this;
    }



    public ZkPondFishDO update_detailJson_forDel(ZkPondFishDetailDO zkPondFishDetail) {
        int addType = Integer.parseInt(zkPondFishDetail.getAddType());
        // 归正 fishNum数量
        fishNumReverse(zkPondFishDetail);
        String newAddType = addType>=0?"-1":"1";
        zkPondFishDetail.setAddType(newAddType);
        return update_detailJson(zkPondFishDetail);
    }
    public void fishNumReverse(ZkPondFishDetailDO zkPondFishDetail) {
        String currAddType = zkPondFishDetail.getAddType();
        Long operationFishNum = zkPondFishDetail.getFishNum();
        Long originPondId = zkPondFishDetail.getPondId();
        Long targetPondId = zkPondFishDetail.getTargetPondId();
        if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), currAddType)) {
            if(Objects.equals(originPondId, this.getPondId())) { // 删除 分池，原塘添加鱼苗数
                this.fishNum = this.fishNum + operationFishNum;
                this.splitReduceNum = this.splitReduceNum - operationFishNum;
            } else if(Objects.equals(targetPondId, this.getPondId())) { // 删除 分池，目标塘减少鱼苗数
                this.fishNum = this.fishNum - operationFishNum;
                this.splitAddNum = this.splitAddNum - operationFishNum;
            }
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), currAddType)) { // 删除 投放,鱼苗数减少
            this.fishNum = this.fishNum - operationFishNum;
            this.pushFishNum = this.pushFishNum - operationFishNum;
        }  else if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), currAddType)) { // 删除 死亡,鱼苗数增加
            this.fishNum = this.fishNum + operationFishNum;
            this.deadFishNum = this.deadFishNum - operationFishNum;
        }  else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), currAddType)) { // 删除 销售,鱼苗数增加
            this.fishNum = this.fishNum + operationFishNum;
            this.saleWeight = EasyBigDecimalUtil.subtract(this.getSaleWeight(), zkPondFishDetail.getSaleWeight());
            this.saleFishNum = this.saleFishNum - operationFishNum;
            if(this.getSaleFishNum() > 0) {
                this.avgFishSpec = EasyBigDecimalUtil.divide(this.saleWeight, new BigDecimal(this.getSaleFishNum()));
            }
        }
    }
    public ZkPondFishDO update_detailJson_forDel_fenchi(ZkPondFishDetailDO zkPondFishDetail) {
        int addType = Integer.parseInt(zkPondFishDetail.getAddType());
        String newAddType = "-1";
        zkPondFishDetail.setAddType(newAddType);
        return update_detailJson(zkPondFishDetail);
    }

    public ZkPondFishDO build_flushInfo() {
         if(StringUtils.isBlank(detailJson)) return this;
         DetailJson json = JSON.parseObject(detailJson, DetailJson.class);
         List<DetailJsonItem> itemList = json.getDetailJson();
         if(EasyListUtil.isEmpty(itemList)) return this;
        String flushInfo = itemList.stream().map(obj ->
                 new StringBuffer(obj.getFishName()!=null?obj.getFishName():"信息缺失").append(":").append(obj.getFishNum()).append("条").toString()
                // new StringBuffer(obj.getFishType()!=null?obj.getFishType():"信息缺失").append(":").append(obj.getFishNum()).append("条").toString()
        ).collect(Collectors.joining(";"));
        this.setFlushInfo(flushInfo);
        return this;
    }

    public void build_for_fenzhi(ZkPondFishDetailDO zkPondFishDetail) {
        this.setPondId(zkPondFishDetail.getTargetPondId());
        this.setPondName(zkPondFishDetail.getTargetPondName());
    }

    public ZkPondFishDO(Long fishNum, Long pushFishNum, String batchNumber) {
        this.fishNum = fishNum;
        this.pushFishNum = pushFishNum;
        this.batchNumber = batchNumber;
    }

    public ZkBiologyCapitalDO transformerToCapital() {
        if(this.getCurrFishSpec() == null || !EasyBigDecimalUtil.gt0(this.getCurrFishSpec())) {
            // 给鱼一个 初始规格 , 方便初始计算不报错，后续打样会 更新该 值
            this.setCurrFishSpec(new BigDecimal(10));
        }
        ZkBiologyCapitalDO capital = new ZkBiologyCapitalDO(workshopId, workshopName, pondSystemName, null, pondNum, pondName, pondId,
                pondDetailNum, fishName, this.getCurrFishSpec()+"", fishNum, userId, deptId).setTenantId(tenantId);
        BigDecimal price = new BigDecimal(50); // 这个后续可以放到配置文件，假定鱼 50元/kg

        return capital.calculateInfo(price, this.getCurrFishSpec());
    }

}
