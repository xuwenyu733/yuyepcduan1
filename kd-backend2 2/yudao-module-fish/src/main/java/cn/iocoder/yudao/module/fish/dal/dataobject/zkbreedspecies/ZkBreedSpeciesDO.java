package cn.iocoder.yudao.module.fish.dal.dataobject.zkbreedspecies;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 养殖品种 DO
 *
 * @author admin2
 */
@TableName("zk_breed_species")
@KeySequence("zk_breed_species_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkBreedSpeciesDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 品种名称 (如:大西洋鲑鱼)
     */
    private String speciesName;
    /**
     * 质量等级标签 (如:生食级、优质级)
     */
    private String qualityGrade;
    /**
     * 学名 (如:Salmo salar)
     */
    private String scientificName;
    /**
     * 适宜水温 (如:10-16°C)
     */
    private String suitableTemp;
    /**
     * 养殖周期 (如:18-24月)
     */
    private String breedingCycle;
    /**
     * 所属部门
     */
    private Long deptId;


}