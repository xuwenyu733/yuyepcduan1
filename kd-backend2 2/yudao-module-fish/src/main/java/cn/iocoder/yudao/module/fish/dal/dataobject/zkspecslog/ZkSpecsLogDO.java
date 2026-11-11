package cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 规格记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_specs_log")
@KeySequence("zk_specs_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkSpecsLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间id
     */
    private Long workshopId;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 系统名称
     */
    private String pondSystemName;
    /**
     * 系统id
     */
    private Long pondSystemId;
    /**
     * 系统编号
     */
    private String pondNum;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 存塘编号
     */
    private String pondDetailNum;
    /**
     * 初始规格
     */
    private BigDecimal initSpecs;
    /**
     * 初始数量
     */
    private Long initNum;
    /**
     * 投喂总量
     */
    private BigDecimal feedCount;
    private BigDecimal currFeedCount; // 当日投喂量(Kg)
    /**
     * 当前存塘数量
     */
    private Long currNum;
    /**
     * 预估饵料系数
     */
    private BigDecimal predictRadio;
    /**
     * 预估规格
     */
    private BigDecimal predictSpecs;
    /**
     * 实际打样规格
     */
    private BigDecimal acturalSpecs;
    /**
     * 实际饵料系数
     */
    private BigDecimal acturalRadio;
    private BigDecimal predictAccumulatedWeightGain; // 预估累计增重
    private BigDecimal acturalAccumulatedWeightGain; // 实际累计增重
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数
    /**
     * 记录人
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
    @TableField(exist = false)
    private BigDecimal tempActuralSpecs; // 计算时，当前打样参考

    public ZkSpecsLogDO(Long workshopId, String workshopName, String pondSystemName, Long pondSystemId, String pondNum,
                        String pondName, Long pondId, String pondDetailNum, BigDecimal initSpecs, Long initNum, Long currNum) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        this.initSpecs = initSpecs;
        this.initNum = initNum;
        this.currNum = currNum;
    }
    public ZkSpecsLogDO build_pondInfo(Long workshopId, String workshopName, String pondSystemName, Long pondSystemId, String pondNum,
                        String pondName, Long pondId, String pondDetailNum) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        return this;
    }

    public ZkSpecsLogDO(BigDecimal initSpecs, Long initNum, Long currNum) {
        this.initSpecs = initSpecs;
        this.initNum = initNum;
        this.currNum = currNum;
    }
    public ZkSpecsLogDO build_num(BigDecimal initSpecs, Long initNum, Long currNum) {
        this.initSpecs = initSpecs;
        this.initNum = initNum;
        this.currNum = currNum;
        return this;
    }
    public ZkSpecsLogDO build_predictInfo(BigDecimal predictRadio, BigDecimal predictSpecs) {
        this.predictRadio = predictRadio;
        // this.predictSpecs = EasyBigDecimalUtil.add(predictSpecs, this.initSpecs);
        this.predictSpecs = predictSpecs;
        return this;
    }
    public ZkSpecsLogDO build_acturalInfo(BigDecimal acturalSpecs) {
        this.acturalSpecs = acturalSpecs;
        this.acturalRadio = EasyBigDecimalUtil.add(predictSpecs, this.initSpecs);
        return this;
    }
    public ZkSpecsLogDO build_feedCount(BigDecimal feedCount) {
        // this.feedCount = EasyBigDecimalUtil.add(this.feedCount, feedCount);
        this.feedCount = feedCount;
        this.currFeedCount = feedCount;
        return this;
    }
    public BigDecimal select_accumulatedWeightGain() {
        BigDecimal accumulatedWeightGain = this.getPredictAccumulatedWeightGain();
        if(EasyBigDecimalUtil.gt0(this.getActuralAccumulatedWeightGain())) {
            accumulatedWeightGain = this.getActuralAccumulatedWeightGain();
        }
        return accumulatedWeightGain;
    }
    public BigDecimal select_fishSpecs() {
        BigDecimal fishSpecs = this.getPredictSpecs();
        if(EasyBigDecimalUtil.gt0(this.getActuralSpecs())) {
            fishSpecs = this.getActuralSpecs();
        }
        return fishSpecs;
    }
}
