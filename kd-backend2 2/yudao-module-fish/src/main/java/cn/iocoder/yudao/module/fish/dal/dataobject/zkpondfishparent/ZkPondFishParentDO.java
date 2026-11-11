package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishparent;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 鱼池操作父 DO
 *
 * @author admin2
 */
@TableName("zk_pond_fish_parent")
@KeySequence("zk_pond_fish_parent_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondFishParentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 原塘id
     */
    private Long originPondId;
    @TableField(exist = false)
    private String originPondDetailNum;
    /**
     * 鱼苗种类
     */
    private String fishTypes;
    /**
     * 鱼苗编号(pl_product)
     */
    private String productNo;
    /**
     * 鱼苗名称
     */
    private String fishName;
    /**
     * 鱼苗数量
     */
    private Long fishNum;
    /**
     * 鱼苗数量差距(正多负少)
     */
    private Long fishNumDiff;
    /**
     * 投放类型(-3:分池;-2:死亡;-1:销售;1:投放;)
     */
    private String addType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 所属部门
     */
    private Long deptId;
    private String batchNumber; // 批次编号


}