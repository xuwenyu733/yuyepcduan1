package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验数据采集 DO
 *
 * @author admin2
 */
@TableName("zk_experimental_data")
@KeySequence("zk_experimental_data_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalDataDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 实验id
     */
    private Long experimentId;
    /**
     * 实验编号
     */
    private String experimentNumber;
    /**
     * 采集类型
     */
    private String collectionType;
    /**
     * 采集数据
     */
    private String collectData;
    /**
     * 所属部门
     */
    private Long deptId;
    /**
     * 鱼池ID
     */
    private Long poolId;
    /**
     * 池号
     */
    private String poolNumber;


}