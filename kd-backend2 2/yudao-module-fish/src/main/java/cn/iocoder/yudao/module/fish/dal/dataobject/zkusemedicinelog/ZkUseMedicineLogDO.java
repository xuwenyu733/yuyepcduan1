package cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用药明细 DO
 *
 * @author 系统管理员
 */
@TableName("zk_use_medicine_log")
@KeySequence("zk_use_medicine_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkUseMedicineLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用药方案id(zk_use_medicine.id)
     */
    private Long zkUseMedicineId;
    /**
     * 系统编号(车间序号_存塘序号)
     */
    private String pondNum;
    private String pondDetailNum;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 存塘规模
     */
    private String pondScale;
    /**
     * 存塘鱼数
     */
    private Integer fishNum;
    /**
     * 商品编号
     */
    private String productNo;
    /**
     * 用药名称
     */
    private String medicineName;
    /**
     * 用药用量
     */
    private String midicineDosage;
    /**
     * 诊断标题
     */
    private String controlTitle;
    /**
     * 用药结果
     */
    private String medicineAfter;
    /**
     * 品牌
     */
    private String medicineBrand;
    /**
     * 规格
     */
    private String medicineSpec;
    /**
     * 用药时间
     */
    private LocalDateTime feedTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 类型(1:集群用药;2:隔离用药;)
     */
    private String feedType;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 负责人
     */
    private String userName;
    /**
     * 负责人所属部门
     */
    private Long deptId;
    private Long tenantId;
    private BigDecimal medicinePrice; // 药品价格
    private BigDecimal medicineCost; // 用药成本

    @TableField(exist = false)
    private List<Long> pondIdArr; // 存塘ids
    @TableField(exist = false)
    private String pondDetailNumStr; // 存塘编号

}
