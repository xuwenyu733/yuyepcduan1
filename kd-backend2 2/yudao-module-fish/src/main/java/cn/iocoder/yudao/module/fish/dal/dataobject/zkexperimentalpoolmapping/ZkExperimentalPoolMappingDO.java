package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping;

import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验与鱼池关联 DO
 *
 * @author admin2
 */
@TableName("zk_experimental_pool_mapping")
@KeySequence("zk_experimental_pool_mapping_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalPoolMappingDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 实验ID
     */
    private Long experimentalId;
    /**
     * 鱼池ID
     */
    private Long poolId;
    /**
     * 池号
     */
    private String poolNumber;
    /**
     * 池类型(1:实验组;2:对照组;)
     */
    private String poolType;
    /**
     * 初始放养数量(尾)
     */
    private Long initFishNum;
    /**
     * 初始平均重量(g)
     */
    private java.math.BigDecimal initAvgWeight;
    /**
     * 描述
     */
    private String description;
    /**
     * 租户编号
     */
    private Long tenantId;

}