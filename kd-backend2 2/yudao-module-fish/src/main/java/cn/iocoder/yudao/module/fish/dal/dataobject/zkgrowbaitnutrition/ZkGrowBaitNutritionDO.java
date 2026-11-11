package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 饵料营养成分 DO
 *
 * @author admin2
 */
@TableName("zk_grow_bait_nutrition")
@KeySequence("zk_grow_bait_nutrition_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowBaitNutritionDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 饵料名称
     */
    private String baitName;
    /**
     * 颗粒大小(mm)
     */
    private String pelletSize;
    /**
     * 粗蛋白(%)
     */
    private String crudeProtein;
    /**
     * 粗脂肪(%)
     */
    private String crudeFat;
    /**
     * 无氮浸出物(%)
     */
    private String nitrogenFreeExtract;
    /**
     * 灰分(%)
     */
    private String ashContent;
    /**
     * 粗纤维(%)
     */
    private String crudeFiber;
    /**
     * 磷(%)
     */
    private String phosphorus;
    /**
     * 总能(MJ)
     */
    private String grossEnergy;
    /**
     * 消化能(MJ)
     */
    private String digestibleEnergy;
    /**
     * 所属部门
     */
    private Long deptId;

    private String manufacturer; // 厂家名称
    private String moisture; // 水分(%)
    private String calcium; // 钙(%)
    private String lysine; // 赖氨酸(%)
    private String methionine; // 蛋氨酸(%)

}