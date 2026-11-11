package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondgainlog;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 鱼生长 DO
 *
 * @author admin2
 */
@TableName("zk_pond_gain_log")
@KeySequence("zk_pond_gain_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondGainLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间id
     */
    private Long workshopId;
    /**
     * 系统id
     */
    private Long systemId;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 批次编号
     */
    private String batchNumber;
    /**
     * 统计日期
     */
    private String staticDay;
    /**
     * 鱼苗数量
     */
    private Long fishNum;
    /**
     * 鱼苗规格
     */
    private String fishSpec;
    /**
     * 今日鱼增加重量
     */
    private BigDecimal fishGainCount;
    /**
     * 所属部门
     */
    private Long deptId;


}