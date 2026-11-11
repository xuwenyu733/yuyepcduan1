package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 生长模型配置 DO
 *
 * @author admin2
 */
@TableName("zk_grow_model_config")
@KeySequence("zk_grow_model_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowModelConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 所属品种ID
     */
    private Long speciesId;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 极限体长 (cm)
     */
    private BigDecimal lInf;
    /**
     * 极限体重 (g)
     */
    private BigDecimal wInf;
    /**
     * 体长生长系数
     */
    private BigDecimal kLength;
    /**
     * 体重生长系数
     */
    private BigDecimal kWeight;
    /**
     * 初始体重 (g)
     */
    private BigDecimal initialWeight;
    /**
     * 初始体长 (cm)
     */
    private BigDecimal initialLength;
    /**
     * 最适水温最小值
     */
    private BigDecimal optimalTempMin;
    /**
     * 最适水温最大值
     */
    private BigDecimal optimalTempMax;
    /**
     * 最适投饵率 (%)
     */
    private BigDecimal optimalFeedRate;

    private Long deptId;
    private Long tenantId;

}
