package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 鱼池健康 DO
 *
 * @author admin2
 */
@TableName("zk_pond_health")
@KeySequence("zk_pond_health_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondHealthDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 环境层评分
     */
    private BigDecimal environmentalLayerRating;
    /**
     * 行为层评分
     */
    private BigDecimal behavioralLayerRating;
    /**
     * 机体层评分
     */
    private BigDecimal bodyLayerRating;
    /**
     * 综合评分
     */
    private BigDecimal overallRating;
    /**
     * 环境层信息
     */
    private String environmentalLayerInformation;
    /**
     * 行为层信息
     */
    private String behavioralLayerInformation;
    /**
     * 机体层信息
     */
    private String bodyLayerInformation;
    /**
     * 异常信息
     */
    private String exceptionMessage;
    /**
     * 异常个数
     */
    private Integer numberOfExceptions;
    /**
     * 所属部门
     */
    private Long deptId;

    @TableField(exist = false)
    private String pondDetailNum;

    @TableField(exist = false)
    private String workshopName;

    @TableField(exist = false)
    private String pondSystemName;

    @TableField(exist = false)
    private String pondName;

}