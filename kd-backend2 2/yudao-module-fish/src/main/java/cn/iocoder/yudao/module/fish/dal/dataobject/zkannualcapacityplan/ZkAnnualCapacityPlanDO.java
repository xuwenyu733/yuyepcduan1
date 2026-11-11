package cn.iocoder.yudao.module.fish.dal.dataobject.zkannualcapacityplan;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 年度产能计划 DO
 *
 * @author admin2
 */
@TableName("zk_annual_capacity_plan")
@KeySequence("zk_annual_capacity_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkAnnualCapacityPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 计划年份 (如:2026)
     */
    private Integer planYear;
    /**
     * 关联的品种ID
     */
    private Long speciesId;
    /**
     * 品种名称冗余字段 (如:大西洋鲑鱼)
     */
    private String speciesName;
    /**
     * 当前产能/产量 (单位:吨)
     */
    private BigDecimal currentCapacity;
    /**
     * 目标产能 (单位:吨)
     */
    private BigDecimal targetCapacity;
    /**
     * 所属部门
     */
    private Long deptId;


}