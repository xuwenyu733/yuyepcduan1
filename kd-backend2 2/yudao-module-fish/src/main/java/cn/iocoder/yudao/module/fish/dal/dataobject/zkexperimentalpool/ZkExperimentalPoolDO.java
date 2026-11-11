package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpool;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验鱼池 DO
 *
 * @author admin2
 */
@TableName("zk_experimental_pool")
@KeySequence("zk_experimental_pool_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalPoolDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 池号
     */
    private String poolNumber;
    /**
     * 大小(m³)
     */
    private BigDecimal size;
    /**
     * 位置
     */
    private String location;
    /**
     * 负责人
     */
    private String manager;
    /**
     * 状态(available:空闲, in_use:使用中, maintenance:维护中)
     */
    private String status;
    /**
     * 备注
     */
    private String notes;
    /**
     * 所属部门
     */
    private Long deptId;


}