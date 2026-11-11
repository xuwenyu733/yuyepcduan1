package cn.iocoder.yudao.module.fish.dal.dataobject.zkpond;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.PondFishLogVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.to.CalculateFishRadioTool;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.qualified.BiometricData;

/**
 * 存塘 DO
 *
 * @author 系统管理员
 */
@TableName("zk_pond")
@KeySequence("zk_pond_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 存塘名称
     */
    private String pondName;
    private String pondNum; // 存塘序号
    private Long fishNum; // 存塘鱼数
    /**
     * 所在车间
     */
    private String workshopName;
    /**
     * 所在车间id
     */
    private Long workshopId;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String dimension;
    /**
     * 负责人
     */
    private String userName;
    /**
     * 状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)
     */
    private String status;
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
    private String pondScale; // 存塘规模

    @TableField(exist = false)
    private PondFishLogVO pondFishLogVO;
    private Long pondSystemId;//存塘系统Id
    private String pondSystemName;//存塘系统名称
    private String pondDetailNum; // 存塘编号(区别于pond_num系统编号)

    private BigDecimal fishSpec; // 规格(g/尾)
    private BigDecimal totalWeight; // 总重量
    private String reviseLog; // 修改值的历史值
    private String batchNumber; // 批次编号

    @TableField(exist = false)
    private BigDecimal currFeedCount; // 当日投喂量(Kg)
    @TableField(exist = false)
    private BigDecimal accumulateFeedCount; // 累计投喂量(Kg)
    @TableField(exist = false)
    private BigDecimal acturalRadio; // 实际饵料系数
    @TableField(exist = false)
    private BigDecimal predictRadio; // 预估饵料系数
    @TableField(exist = false)
    private BigDecimal currCost; // 当前成本
    @TableField(exist = false)
    private BigDecimal biologyCost; // 生物资产
    @TableField(exist = false)
    private String fishTypes; // 鱼种类
    @TableField(exist = false)
    private String tenantName; // 租户名称
    @TableField(exist = false)
    private String deptName; // 部门名称

    public ZkPondDO(Long id, Long fishNum) {
        this.id = id;
        this.fishNum = fishNum;
    }
    public ZkPondDO(Long id, Long fishNum, String batchNumber) {
        this.id = id;
        this.fishNum = fishNum;
        this.batchNumber = batchNumber;
    }

    public ZkPondDO(Long targetPondId, BigDecimal splitAvgFishSpec) {
        this.id = targetPondId;
        this.fishSpec = splitAvgFishSpec;
    }

    public ZkPondDO build_fishTypes(String fishTypes) {
        this.fishTypes = fishTypes;
        return this;
    }
    public ZkPondDO build_fishSpec(BigDecimal fishSpec) {
        this.fishSpec = fishSpec;
        // fishSpec 单位为 g/尾；totalWeight 需要按 Kg 落库：fishSpec * fishNum / 1000
        this.totalWeight = EasyBigDecimalUtil.multiply(fishSpec, new BigDecimal(this.getFishNum()));
        this.totalWeight = EasyBigDecimalUtil.divide(this.totalWeight, new BigDecimal(1000));
        return this;
    }
    public ZkPondDO build_reviseLog(String reviseLog) {
        if(StringUtils.isEmpty(this.getReviseLog())) {
            this.setReviseLog(reviseLog);
        } else {
            String temp = new StringBuilder(this.getReviseLog()).append(",").append(reviseLog).toString();
            this.setReviseLog(temp);
        }
        return this;
    }

    public ZkPondDO build_batchNumber(String old_batchNumber, String batchNumber) {
        String new_batchNumber = old_batchNumber;
        if(StringUtils.isNotBlank(old_batchNumber)) {
            String[] split = old_batchNumber.split(",");
            Set<String> tempSet = new HashSet<>();
            for(String item : split) {
                tempSet.add(item);
            }
            tempSet.add(batchNumber);
            new_batchNumber = tempSet.stream().collect(Collectors.joining(","));
        } else {
            new_batchNumber = batchNumber;
        }
        this.batchNumber = new_batchNumber;
        return this;
    }

    public ZkPondDO build_totalWeight() {
        if(EasyBigDecimalUtil.compare0(this.getFishSpec(), "LE")) {
            return this;
            // this.fishSpec = new BigDecimal(10);
        }
        this.totalWeight = EasyBigDecimalUtil.multiply(this.fishSpec, new BigDecimal(this.fishNum));
        this.totalWeight = EasyBigDecimalUtil.divide(this.totalWeight, new BigDecimal(1000));
        return this;
    }
    public BigDecimal selectRadio() {
        if(EasyBigDecimalUtil.gt0(this.getActuralRadio())) return this.getActuralRadio();
        if(EasyBigDecimalUtil.gt0(this.getPredictRadio())) return this.getPredictRadio();
        return CalculateFishRadioTool.predictRadio;
    }
}
