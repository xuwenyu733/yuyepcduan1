package cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用药记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_use_medicine")
@KeySequence("zk_use_medicine_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkUseMedicineDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    private String pondNum; // 系统编号(车间序号存塘序号)
    private String pondScale; // 存塘规模
    private String fishNum; // 存塘鱼数
    private Long pondId; // 存塘Id
    private Long workshopId; // 所属车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    private String pondName; // 存塘名称
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
     * 诊断原因
     */
    private String controlContent;
    /**
     * 用药结果
     */
    private String medicineAfter;
    /**
     * 状态(0:申请中;1:已批准;2:已使用;3:已评估;-1:已拒绝;)
     */
    private String status;
    /**
     * 附件
     */
    private String fids;
    /**
     * 流程实例
     */
    private String processInstanceId;
    /**
     * 用药人
     */
    private Long userId;
    private String userName;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

    @TableField(exist = false)
    private List<ZkUseMedicineLogDO> useMedicineLogList;
    private LocalDateTime stopMedicationTime; // 停药时间
    private String symptomUrls; // 症状(图像)
    private String symptomVideos; // 症状(视频)
    private String waterQuality; // 水质
    private String systemState; // 系统状态
    private String feedBrand; // 饲料品牌
    private String oneBeforeFeed; // 前一天摄食量
    private String twoBeforeFeed; // 前两天摄食量
    private String threeBeforeFeed; // 前三天摄食量
    private Long numberOfLosses; // 损失数量
    private String specifications; // 规格

    private String expertSolution; // 专家方案
    private String actualSolution; // 实际方案

    public ZkUseMedicineDO(String feedBrand, String oneBeforeFeed, String twoBeforeFeed, String threeBeforeFeed) {
        this.feedBrand = feedBrand;
        this.oneBeforeFeed = oneBeforeFeed;
        this.twoBeforeFeed = twoBeforeFeed;
        this.threeBeforeFeed = threeBeforeFeed;
    }
    public ZkUseMedicineDO build_feedInfo(String feedBrand, BigDecimal oneBeforeFeed, BigDecimal twoBeforeFeed, BigDecimal threeBeforeFeed) {
        this.feedBrand = feedBrand;
        this.oneBeforeFeed = String.format("%sKg", oneBeforeFeed);
        this.twoBeforeFeed = String.format("%sKg", twoBeforeFeed);
        this.threeBeforeFeed = String.format("%sKg", threeBeforeFeed);
        return this;
    }
}
