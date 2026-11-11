package cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 告警 DO
 *
 * @author 系统管理员
 */
@TableName("zk_pond_warn")
@KeySequence("zk_pond_warn_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkPondWarnDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 具体位置
     */
    private String workshopPosition;
    /**
     * 所属车间id
     */
    private Long workshopId;
    private Long pondSystemId; // 存塘系统id
    private String pondSystemName; // 存塘系统名称
    private String pondNum; // 系统编号
    /**
     * 存塘名称
     */
    private String pondName;
    /**
     * 所属存塘id
     */
    private Long pondId;
    private String pondDetailNum; // 存塘编号

    private Long warnDeviceId; // 报警设备id
    /**
     * 告警对象
     */
    private String warnName;
    /**
     * cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.to.WarnTypeEnum
     * 状态(0:环境告警;1:事故告警;-1:病虫告警;)
     */
    private String warnType;
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
    private String status;
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
    private String handleInfo; // 处理描述



    private String iotMessageId; // iot消息唯一标识

    public ZkPondWarnDO(String workshopName, Long workshopId, Long pondSystemId, String pondSystemName, String pondNum, String pondName, Long pondId, String pondDetailNum) {
        this.workshopName = workshopName;
        this.workshopId = workshopId;
        this.pondSystemId = pondSystemId;
        this.pondSystemName = pondSystemName;
        this.pondNum = pondNum;
        this.pondName = pondName;
        this.pondId = pondId;
        this.pondDetailNum = pondDetailNum;
    }

    public ZkPondWarnDO build_info(String warnName, String warnType, String warnContent, String iotMessageId) {
        this.warnName = warnName;
        this.warnType = warnType;
        this.warnContent = warnContent;
        this.iotMessageId = iotMessageId;
        return this;
    }

    public String selectMsg() {
        String str = String.format("告警存塘：%s，告警对象：%s，告警信息：%s", this.getPondDetailNum(), this.getWarnName(), this.getWarnContent());
        return str;
    }

}
