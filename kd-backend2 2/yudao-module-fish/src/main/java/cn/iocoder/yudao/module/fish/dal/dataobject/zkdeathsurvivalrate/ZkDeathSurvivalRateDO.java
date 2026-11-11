package cn.iocoder.yudao.module.fish.dal.dataobject.zkdeathsurvivalrate;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 死亡和存活率 DO
 *
 * @author 系统管理员
 */
@TableName("zk_death_survival_rate")
@KeySequence("zk_death_survival_rate_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDeathSurvivalRateDO extends BaseDO {

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
     * 批次编号
     */
    private String batchNumber;
    /**
     * 鱼苗数量
     */
    private Integer fryQuantity;
    /**
     * 死亡数量
     */
    private Integer deathQuantity;
    /**
     * 销售数量
     */
    private Integer salesQuantity;
    /**
     * 分塘数量
     */
    private Integer pondTransferQuantity;
    /**
     * 存塘数量
     */
    private Integer stockedQuantity;
    /**
     * 存活率
     */
    private Object survivalRate;
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
