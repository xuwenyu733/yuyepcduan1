package cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologicalasset;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 生物资产评估 DO
 *
 * @author 系统管理员
 */
@TableName("zk_biological_asset")
@KeySequence("zk_biological_asset_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkBiologicalAssetDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 所属车间id
     */
    private Long workshopId;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 所属系统id
     */
    private Long pondSystemId;
    /**
     * 存塘系统编号
     */
    private String pondNum;
    /**
     * 存塘系统名称
     */
    private String pondSystemName;
    /**
     * 所属存塘id
     */
    private Long pondId;
    /**
     * 存塘编号
     */
    private String pondDetailNum;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 批次编号
     */
    private String batchNumber;
    /**
     * 鱼苗种类
     */
    private String fishTypes;
    /**
     * 鱼存量
     */
    private Long fishNum;
    /**
     * 鱼规格克每尾
     */
    private Integer fishSpecification;
    /**
     * 总重量
     */
    private BigDecimal totalWeight;
    /**
     * 鱼价格
     */
    private BigDecimal fishPrice;
    /**
     * 生物资产
     */
    private BigDecimal biologicalAsset;
    /**
     * 负责人id
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

}
