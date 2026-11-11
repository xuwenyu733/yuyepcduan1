package cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 智能诊断 DO
 *
 * @author 系统管理员
 */
@TableName("zk_intelligent_diagnosis")
@KeySequence("zk_intelligent_diagnosis_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkIntelligentDiagnosisDO extends BaseDO {

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
     * 智能体分析报告
     */
    private String aiAgentReport;
    /**
     * 人工判断是否同意不同意写病因分析
     */
    private String humanReport;
    /**
     * 咨询专家后方案
     */
    private String differentialAnalysis;
    /**
     * 整体结论
     */
    private String conclusion;
    /**
     * 整体评估
     */
    private String evaluation;
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
    /**
     * 分析人
     */
    private String analyst;
    /**
     * 专家
     */
    private String expert;
    /**
     * 上传的图片
     */
    private String uploadedImages;
    /**
     * 流程实例
     */
    private String processInstanceId;

    private String batchNumber; // 批次编号
    private String fishTypes; // 鱼苗种类
    private String supplierName; // 来源
    private String feedType; // 投喂方式
    private Integer illNum; // 发病数量
    private String illName; // 病害名称
    private String illType; // 病害分类
    private String abnormalArea; // 异常部位
    private String abnormalContent; // 异常表现及描述
    private String rongYangNongDu; // 溶氧
    private String suanJianDu; // 酸碱度
    private String anDan; // 氨氮
    private String shuiWen; // 水温
    private String shuiWei; // 水位
    // private String fidImage; // 图片Fid

    private String imageUrls; // 图片地址(多个英文逗号隔开)
    private String videoUrls; // 视频地址(多个英文逗号隔开)

    /**
     * 状态(0:申请中;1:已批准;2:已使用;3:已评估;-1:已拒绝;)
     */
    private String status;
    @TableField(exist = false)
    private String userName;
    private String dissectionArea; // 解剖部位

}
