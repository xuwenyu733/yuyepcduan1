package cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 各阶段营养需求 DO
 *
 * @author admin2
 */
@TableName("zk_grow_nutritional_requirements")
@KeySequence("zk_grow_nutritional_requirements_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkGrowNutritionalRequirementsDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 生长阶段
     */
    private String growthStage;
    /**
     * 日龄
     */
    private String dayold;
    /**
     * 体重范围
     */
    private String weightRange;
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
     * 钙
     */
    private String calcium;
    /**
     * 磷
     */
    private String phosphorus;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;


}
