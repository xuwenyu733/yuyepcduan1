package cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;

/**
 * 投喂记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_feeding_log")
@KeySequence("zk_feeding_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkFeedingLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 系统编号(车间序号_存塘序号)
     */
    private String pondNum;
    private String productNo; // 商品编号
    private Long productId; // 饲料商品id
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 存塘id
     */
    private Long pondId;
    private Long workshopId; // 所属车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    /**
     * 存塘规模
     */
    private String pondScale;
    /**
     * 存塘鱼数
     */
    private Integer fishNum;
    /**
     * 品牌
     */
    private String feedBrand;
    private String feedName;
    /**
     * 规格
     */
    private String feedSpec;
    /**
     * 数量
     */
    private BigDecimal feedCount = new BigDecimal(0);
    /**
     * 投喂时间
     */
    private LocalDateTime feedTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 类型(1:人工;2:机器;)
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
    private String differenceBefore; // 与前5天对比
    private BigDecimal personScore; // 人对摄食状态评分
    private BigDecimal sumScore; // 综合评分
    private String reviseLog; // 修改值的历史值
    private BigDecimal feedingPrice; // 饲料价格
    private BigDecimal feedingCost; // 投喂成本

    private Long taskDetailId; // 任务明细id
    private Long warehouseId; // 饲料仓库id

    public ZkFeedingLogDO(String feedBrand, BigDecimal feedCount) {
        this.feedBrand = feedBrand;
        this.feedCount = feedCount;
    }

    public ZkFeedingLogDO build_reviseLog(String reviseLog) {
        if(StringUtils.isEmpty(this.getReviseLog())) {
            this.setReviseLog(reviseLog);
        } else {
            String temp = new StringBuilder(this.getReviseLog()).append(",").append(reviseLog).toString();
            this.setReviseLog(temp);
        }
        return this;
    }

    public ZkFeedingLogDO(Long pondId, Integer fishNum, String feedBrand, String feedName, String feedSpec, BigDecimal feedCount) {
        this.pondId = pondId;
        this.fishNum = fishNum;
        this.feedBrand = feedBrand;
        this.feedName = feedName;
        this.feedSpec = feedSpec;
        this.feedCount = feedCount;
    }
    public ZkFeedingLogDO build_info(Long pondId, Integer fishNum, String feedBrand, String feedName, String feedSpec, BigDecimal feedCount) {
        this.pondId = pondId;
        this.fishNum = fishNum;
        this.feedBrand = feedBrand;
        this.feedName = feedName;
        this.feedSpec = feedSpec;
        this.feedCount = EasyBigDecimalUtil.add(this.feedCount, feedCount);
        return this;
    }

    public ZkFeedingLogDO(Long workshopId, String workshopName, String pondSystemName, String pondNum,
                          String pondDetailNum, String pondName, Long pondId, BigDecimal feedCount, BigDecimal personScore) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondNum = pondNum;
        this.pondDetailNum = pondDetailNum;

        this.pondName = pondName;
        this.pondId = pondId;
        this.feedCount = feedCount;
        this.personScore = personScore;
    }
}
