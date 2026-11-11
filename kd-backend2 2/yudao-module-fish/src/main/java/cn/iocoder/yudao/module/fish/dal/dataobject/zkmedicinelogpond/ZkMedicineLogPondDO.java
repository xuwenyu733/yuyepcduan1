package cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond;

import lombok.*;
import java.util.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用药存塘关联 DO
 *
 * @author admin2
 */
@TableName("zk_medicine_log_pond")
@KeySequence("zk_medicine_log_pond_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkMedicineLogPondDO extends BaseDO {

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
     * 用药表id( 表: zk_use_medicine_log.id)
     */
    private Long medicineLogId;

    public ZkMedicineLogPondDO(Long pondId, Long medicineLogId) {
        this.pondId = pondId;
        this.medicineLogId = medicineLogId;
    }
}
