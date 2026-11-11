package cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicemetric;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 设备指标 DO
 *
 * @author 系统管理员
 */
@TableName("zk_device_metric")
@KeySequence("zk_device_metric_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkDeviceMetricDO extends BaseDO {

    /**
     * 指标ID
     */
    @TableId
    private Long id;
    /**
     * 指标编码（如：temperature、humidity、voltage）
     */
    private String metricCode;
    /**
     * 指标名称（如：温度、湿度、电压）
     */
    private String metricName;
    /**
     * 指标单位（如：℃、%、V）
     */
    private String unit;
    /**
     * 数据类型（如：int、float、double）
     */
    private String dataType;
    /**
     * 指标描述
     */
    private String description;
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

}
