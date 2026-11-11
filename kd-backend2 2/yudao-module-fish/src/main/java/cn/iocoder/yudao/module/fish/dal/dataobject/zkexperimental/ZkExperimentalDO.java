package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping.ZkExperimentalPoolMappingDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验 DO
 *
 * @author admin2
 */
@TableName("zk_experimental")
@KeySequence("zk_experimental_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 实验编号
     */
    private String experimentNumber;
    /**
     * 实验标题
     */
    private String experimentTitle;
    /**
     * 池号
     */
    private String pondDetailNum;
    /**
     * 品种
     */
    private String variety;
    /**
     * 负责人
     */
    private String personInCharge;
    /**
     * 负责人id
     */
    private Long personInChargeId;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 实验状态(0:待开始;1:计划中;2:进行中;3:已完成;4:已总结;)
     */
    private Integer experimentalStatus;
    /**
     * 实验目标
     */
    private String experimentalObjective;
    /**
     * 实验内容
     */
    private String experimentalContent;
    /**
     * 实验方法
     */
    private String experimentalMethod;
    /**
     * 主要结果
     */
    private String mainResults;
    /**
     * 效果评估
     */
    private String effectEvaluation;
    /**
     * 完成人
     */
    private String completedBy;
    /**
     * 完成时间
     */
    private LocalDateTime completionTime;
    /**
     * 所属部门
     */
    private Long deptId;

    // 数据采集map
    @TableField(exist = false)
    private Map<String, ZkExperimentalDataDO> dataMap;
    // 多媒体列表
    @TableField(exist = false)
    private List<ZkExperimentalMediaDO> fileList;

    /**
     * 关联池子ID列表
     */
    @TableField(exist = false)
    private List<Long> poolIds;

    /**
     * 关联池号列表
     */
    @TableField(exist = false)
    private List<String> poolNumbers;

    /**
     * 关联池子详情列表
     */
    @TableField(exist = false)
    private List<ZkExperimentalPoolMappingDO> poolDetails;

}