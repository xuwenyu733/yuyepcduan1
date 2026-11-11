package cn.iocoder.yudao.module.fish.dal.dataobject.zkdatarevise;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 数据修正记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_data_revise")
@KeySequence("zk_data_revise_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDataReviseDO extends BaseDO {

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
     * 操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)
     */
    private String reviseType;
    private String reviseTypeName; // 操作类型名称
    /**
     * 原数据
     */
    private String originData;
    /**
     * 差值
     */
    private String differenceData;
    /**
     * 变化比例
     */
    private String changeRatio;
    /**
     * 原因
     */
    private String reason;
    /**
     * 技术总监意见
     */
    private String technicalDirectorsOpinion;
    /**
     * 厂长意见
     */
    private String directorsOpinion;
    /**
     * 业务id
     */
    private Long busId;
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
    private String status; // 状态(0:已申请;1:已批准;2:技术总监已审批;3:厂长已审批;4:已完成;-1:已拒绝;)
    private String processInstanceId; // 流程实例

    public ZkDataReviseDO(Long workshopId, String workshopName, String pondSystemName, Long pondSystemId, String pondNum,
                          String pondName, Long pondId, String pondDetailNum, String reviseType, String reviseTypeName,
                          String originData, String differenceData, String changeRatio, String reason) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemName = pondSystemName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        this.reviseType = reviseType;
        this.reviseTypeName = reviseTypeName;
        this.originData = originData;
        this.differenceData = differenceData;
        this.changeRatio = changeRatio;
        this.reason = reason;
    }

    public ZkDataReviseDO build_busId(Long busId) {
        this.setBusId(busId);
        return this;
    }
    public ZkDataReviseDO build_userId(Long userId) {
        this.setUserId(userId);
        return this;
    }
}
