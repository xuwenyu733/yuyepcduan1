package cn.iocoder.yudao.module.fish.dal.dataobject.zkfcrmodel;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
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
 * 饵料系数FCR模型 DO
 *
 * @author 系统管理员
 */
@TableName("zk_fcr_model")
@KeySequence("zk_fcr_model_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkFcrModelDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 所属车间id
     */
    private Long workshopId;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 所属系统id
     */
    private Long pondSystemId;
    /**
     * 存塘系统编号
     */
    private String pondNum;
    /**
     * 存塘系统名称
     */
    private String pondSystemName;
    /**
     * 所属存塘id
     */
    private Long pondId;
    /**
     * 存塘编号
     */
    private String pondDetailNum;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 平均打样体长（cm）
     */
    private BigDecimal averageSamplingLength;
    /**
     * 平均打样规格（g）
     */
    private BigDecimal averageSamplingSize;
    /**
     * 平均投喂量（g）
     */
    private BigDecimal averageFeedingAmount;
    /**
     * 平均增重（g）
     */
    private BigDecimal averageWeightGain;
    /**
     * 实际饵料系数
     */
    private BigDecimal actualFcr;
    /**
     * 饵料系数增长率
     */
    private BigDecimal fcrGrowthRate;
    /**
     * 投喂总量
     */
    private BigDecimal totalFeedingAmount;
    /**
     * 存塘量（尾）
     */
    private Integer stockingQuantity;
    /**
     * 总重量（Kg）
     */
    private BigDecimal totalWeight;
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

}
