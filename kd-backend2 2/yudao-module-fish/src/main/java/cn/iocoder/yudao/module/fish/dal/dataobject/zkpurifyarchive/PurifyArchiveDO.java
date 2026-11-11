package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyarchive;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
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
 * 入池档案 DO
 *
 * @author admin2
 */
@TableName("zk_purify_archive")
@KeySequence("zk_purify_archive_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyArchiveDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 关联采购记录ID
     */
    private Long purchaseRecordId;
    /**
     * 关联采购单号
     */
    private String purchaseOrderNo;
    /**
     * 品类
     */
    private String category;
    /**
     * 来源
     */
    private String source;
    /**
     * 入池时间
     */
    private LocalDateTime inPoolTime;
    /**
     * 入池总重量(kg)
     */
    private BigDecimal inPoolWeight;
    /**
     * 规格-单尾重(kg)
     */
    private BigDecimal avgWeight;
    /**
     * 入池尾数
     */
    private Integer inPoolCount;
    /**
     * 池号
     */
    private String poolNumber;
    /**
     * 初始密度(kg/m³)
     */
    private BigDecimal density;
    /**
     * 优化等级(premium/wagyu/tuna)
     */
    private String optimizationLevel;
    /**
     * 检验报告文件名/URL
     */
    private String inspectionReport;
    /**
     * 初始溶氧(mg/L)
     */
    private BigDecimal initialOxygen;
    /**
     * 初始PH值
     */
    private BigDecimal initialPh;
    /**
     * 初始水温(℃)
     */
    private BigDecimal initialTemperature;
    /**
     * 外观评估
     */
    private String appearanceScore;
    /**
     * 活力评估
     */
    private String vitalityScore;
    /**
     * 健康状况
     */
    private String healthStatus;

    private String specUniformity; //规格均匀度
    private String stressLevel; //应激程度
    /**
     * 预期存活率(%)
     */
    private BigDecimal expectedSurvivalRate;
    /**
     * 风险等级
     */
    private String riskLevel;
    /**
     * 评估人
     */
    private String assessor;
    /**
     * 所属部门
     */
    private Long deptId;
    private String fid; // 文件id


}