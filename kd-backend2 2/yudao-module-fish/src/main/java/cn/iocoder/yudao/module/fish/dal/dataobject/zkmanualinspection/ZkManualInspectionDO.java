package cn.iocoder.yudao.module.fish.dal.dataobject.zkmanualinspection;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 人工巡检 DO
 *
 * @author 系统管理员
 */
@TableName("zk_manual_inspection")
@KeySequence("zk_manual_inspection_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkManualInspectionDO extends BaseDO {

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
    private Long pondId;
    private String pondName;
    private String pondNum;
    /**
     * 巡检名称
     */
    private String inspectionName;
    /**
     * 巡检描述
     */
    private String inspectionContent;
    /**
     * 巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)
     */
    private String status;
    /**
     * 负责人id
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

}
