package cn.iocoder.yudao.module.fish.mq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("zk_device_alarm")
public class ZkDeviceAlarm {
    
    /**
     * 报警记录id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 所属车间id
     */
    @TableField("workshop_id")
    private Long workshopId;
    
    /**
     * 车间名称
     */
    @TableField("workshop_name")
    private String workshopName;
    
    /**
     * 所属系统id
     */
    @TableField("pond_system_id")
    private Long pondSystemId;
    
    /**
     * 存塘系统编号
     */
    @TableField("pond_num")
    private String pondNum;
    
    /**
     * 存塘系统名称
     */
    @TableField("pond_system_name")
    private String pondSystemName;
    
    /**
     * 所属存塘id
     */
    @TableField("pond_id")
    private Long pondId;
    
    /**
     * 存塘编号
     */
    @TableField("pond_detail_num")
    private String pondDetailNum;
    
    /**
     * 存塘名称
     */
    @TableField("pond_name")
    private String pondName;
    
    /**
     * 批次编号
     */
    @TableField("batch_number")
    private String batchNumber;
    
    /**
     * 报警设备id
     */
    @TableField("warn_device_id")
    private Long warnDeviceId;
    
    /**
     * 告警设备名
     */
    @TableField("warn_device")
    private String warnDevice;
    
    /**
     * 告警设备类型(2:系统设备;3:传感器;)
     */
    @TableField("warn_device_type")
    private Integer warnDeviceType;
    
    /**
     * 告警对象
     */
    @TableField("warn_name")
    private String warnName;
    
    /**
     * 告警类型(0:事故告警;1:环境告警;-2病虫告警;)
     */
    @TableField("warn_type")
    private Integer warnType;
    
    /**
     * 告警值
     */
    @TableField("warn_value")
    private Integer warnValue;
    
    /**
     * 告警描述
     */
    @TableField("warn_content")
    private String warnContent;
    
    /**
     * 负责人
     */
    @TableField("user_name")
    private String userName;
    
    /**
     * 状态(0:待处理;1:已处理;)
     */
    @TableField("status")
    private Integer status;
    
    /**
     * 处理描述
     */
    @TableField("handle_info")
    private String handleInfo;
    
    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableField("deleted")
    private Boolean deleted;
    
    /**
     * 租户编号
     */
    @TableField("tenant_id")
    private Long tenantId;
    
    /**
     * 负责人id
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 所属部门
     */
    @TableField("dept_id")
    private Long deptId;
    
    /**
     * 文件id
     */
    @TableField("fid")
    private String fid;
    
    /**
     * 流程实例ID
     */
    @TableField("process_instance_id")
    private String processInstanceId;
    
    /**
     * 审批用户ID
     */
    @TableField("approve_user_id")
    private Long approveUserId;
    
    /**
     * 通知用户ID
     */
    @TableField("notice_user_id")
    private Long noticeUserId;
}