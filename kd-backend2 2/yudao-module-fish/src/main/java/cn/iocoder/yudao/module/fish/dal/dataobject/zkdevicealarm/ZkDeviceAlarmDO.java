package cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicealarm;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 设备报警 DO
 *
 * @author 系统管理员
 */
@TableName("zk_device_alarm")
@KeySequence("zk_device_alarm_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDeviceAlarmDO extends BaseDO {

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
     * 批次编号
     */
    private String batchNumber;
    /**
     * 报警设备id
     */
    private Long warnDeviceId;
    /**
     * 告警设备名
     */
    private String warnDevice;
    /**
     * 告警设备类型(0:系统设备;1:池塘传感器;)
     */
    private Integer warnDeviceType;
    /**
     * 告警对象
     */
    private String warnName;
    /**
     * 告警类型(0:环境告警;1:事故告警;-2病虫告警;)
     */
    private Integer warnType;
    /**
     * 告警描述
     */
    private String warnContent;
    /**
     * 负责人
     */
    private String userName;
    /**
     * 状态(0:待处理;1:已处理;)
     */
    private Integer status;
    /**
     * 处理描述
     */
    private String handleInfo;
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

    private String processInstanceId;

    private String metricId; // 设备测量项传感器和控制单元编码
    private String deviceId; // 报警设备id

}
