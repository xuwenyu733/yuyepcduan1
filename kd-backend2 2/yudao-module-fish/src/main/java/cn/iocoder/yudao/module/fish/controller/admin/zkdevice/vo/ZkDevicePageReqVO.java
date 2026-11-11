package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设备分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDevicePageReqVO extends PageParam {

    @Schema(description = "设备名称", example = "张三")
    private String deviceName;
    private String deviceSn;
    private String deviceMetricId;

    @Schema(description = "所在车间", example = "李四")
    private String workshopName;
    private BigDecimal standardStart;
    private String startComputer;
    private BigDecimal standardEnd;
    private String endComputer;
    private String deviceUnit;
    private BigDecimal currValue;
    private String standInterval;

    @Schema(description = "所在车间id", example = "3782")
    private Long workshopId;

    @Schema(description = "负责人", example = "赵六")
    private String userName;

    @Schema(description = "状态(0:关闭中;1:开启中;-1:已废弃;)", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "2238")
    private Long userId;

    @Schema(description = "所属部门", example = "31352")
    private Long deptId;
    private Long tenantId;
    private List<Long> deptIds;

    @Schema(description = "文件id", example = "28742")
    private String fid;

    private String pondNum; // 系统编号(车间序号_存塘序号)
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String deviceType; // 设备类型(0:监控数值;1:监控视频;)
    private Long pondSystemId; // 存塘系统id
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号
    private Integer ruleStatus; // 规则状态(0:关闭中;1:开启中;-1:已废弃;)
}
