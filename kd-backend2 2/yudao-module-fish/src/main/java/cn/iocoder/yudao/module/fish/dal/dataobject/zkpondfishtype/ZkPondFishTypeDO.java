package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishtype;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 鱼苗种类 DO
 *
 * @author admin2
 */
@TableName("zk_pond_fish_type")
@KeySequence("zk_pond_fish_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondFishTypeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 批次编号
     */
    private String batchNumber;
    /**
     * 鱼苗数量
     */
    private Long fishNum;
    /**
     * 鱼苗规格
     */
    private String fishSpec;
    /**
     * 统计年
     */
    private Integer staticYear;
    /**
     * 统计月份
     */
    private Integer staticMonth;
    /**
     * 统计日期
     */
    private Integer staticDay;
    /**
     * 鱼苗种类
     */
    private String fishType;
    /**
     * 所属部门
     */
    private Long deptId;


}