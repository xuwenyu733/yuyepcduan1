package cn.iocoder.yudao.module.fish.dal.dataobject.zkinspectionreport;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 巡检智能分析报告 DO
 *
 * @author 系统管理员
 */
@TableName("zk_inspection_report")
@KeySequence("zk_inspection_report_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkInspectionReportDO extends BaseDO {

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
     * 智能体报告
     */
    private String aiAgentReport;
    /**
     * 人工报告是否同意不同意写原因分析
     */
    private String humanReport;
    /**
     * 对比分析人工和智能体报告
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
    private Long taskDetailId; // 巡检明细id(zk_task_detail.id)
    private String htmlUrl; // html路径
    private String pdfUrl; // pdf路径

    public ZkInspectionReportDO(Long workshopId, String workshopName, Long pondSystemId, String pondNum, String pondSystemName, Long pondId,
                                String pondDetailNum, String pondName, String aiAgentReport, Long userId, Long deptId, Long taskDetailId) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondSystemName = pondSystemName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        this.pondName = pondName;
        this.aiAgentReport = aiAgentReport;
        this.userId = userId;
        this.deptId = deptId;
        this.taskDetailId = taskDetailId;
    }
    public ZkInspectionReportDO build_info(Long workshopId, String workshopName, Long pondSystemId, String pondNum, String pondSystemName, Long pondId,
                                String pondDetailNum, String pondName, String aiAgentReport, Long userId, Long deptId, Long taskDetailId) {
        this.workshopId = workshopId;
        this.workshopName = workshopName;
        this.pondSystemId = pondSystemId;
        this.pondNum = pondNum;
        this.pondSystemName = pondSystemName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
        this.pondName = pondName;
        this.aiAgentReport = aiAgentReport;
        this.userId = userId;
        this.deptId = deptId;
        this.taskDetailId = taskDetailId;
        return this;
    }
}
