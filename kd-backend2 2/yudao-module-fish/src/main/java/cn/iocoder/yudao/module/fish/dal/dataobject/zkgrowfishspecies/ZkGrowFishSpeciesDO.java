package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 鱼类品种 DO
 *
 * @author admin2
 */
@TableName("zk_grow_fish_species")
@KeySequence("zk_grow_fish_species_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowFishSpeciesDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 品种名称
     */
    private String name;
    /**
     * 俗名
     */
    private String commonName;
    /**
     * 学名
     */
    private String scientificName;
    /**
     * 品种描述
     */
    private String description;
    private Long deptId;
    private Long tenantId;


}
