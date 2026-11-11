package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 饵料成分对比 DO
 *
 * @author admin2
 */
@TableName("zk_grow_bait_ingredients")
@KeySequence("zk_grow_bait_ingredients_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowBaitIngredientsDO extends BaseDO {

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
     * 饵料类型
     */
    private String feedType;
    /**
     * 粗蛋白
     */
    private String crudeProtein;
    /**
     * 粗脂肪
     */
    private String crudeFat;
    /**
     * 碳水化合物
     */
    private String carbohydrate;
    /**
     * 粗纤维
     */
    private String crudeFiber;
    /**
     * 水分
     */
    private String moisture;
    /**
     * 灰分
     */
    private String ashContent;
    /**
     * 参考价格
     */
    private String referencePrice;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;


}
