package cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
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
 * 打样记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_fish_proofing")
@KeySequence("zk_fish_proofing_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkFishProofingDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 批次编号
     */
    private String batchNumber;
    /**
     * 车间id
     */
    private Long workshopId;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 系统名称
     */
    private String pondSystemName;
    /**
     * 系统id
     */
    private Long pondSystemId;
    /**
     * 系统编号
     */
    private String pondNum;
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 存塘id
     */
    private Long pondId;
    /**
     * 存塘编号
     */
    private String pondDetailNum;
    /**
     * 初始规格
     */
    private BigDecimal initSpecs;
    /**
     * 平均打样体长(cm)
     */
    private BigDecimal avgBodyLength;
    /**
     * 平均打样规格(g)
     */
    private BigDecimal avgSpecs;
    /**
     * 打样数量(尾)
     */
    private Long currNum;
    /**
     * 任务明细id
     */
    private Long taskDetailId;
    /**
     * 记录人
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
    private String statisticsType; // 统计类型(0:未统计;1:已统计;)

    @TableField(exist = false)
    private TaskDetailDO taskDetailDO; // 打样的数据

    public ZkFishProofingDO(String batchNumber, Long workshopId, String workshopName, String pondSystemName,
                            Long pondSystemId, String pondNum, String pondName, Long pondId,
                            String pondDetailNum, BigDecimal avgBodyLength, BigDecimal avgSpecs,
                            Long currNum, Long taskDetailId) {
        this.batchNumber = batchNumber;
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        this.avgBodyLength = avgBodyLength;
        this.avgSpecs = avgSpecs;
        this.currNum = currNum;
        this.taskDetailId = taskDetailId;
    }

    public BigDecimal selectWeight() {
        BigDecimal weight = EasyBigDecimalUtil.multiply(this.getAvgSpecs(), new BigDecimal(this.getCurrNum()));
        weight = EasyBigDecimalUtil.divide(weight, new BigDecimal(1000));
        return weight;
    }
}
