package cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 设备报警 Excel 导出 Request VO，参数和 ZkDeviceAlarmPageReqVO 是一致的")
@Data
public class ZkDeviceAlarmExportReqVO {

    @Schema(description = "所属车间id", example = "29235")
    private Long workshopId;

    @Schema(description = "车间名称", example = "张三")
    private String workshopName;

    @Schema(description = "所属系统id", example = "2086")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "李四")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "31261")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "王五")
    private String pondName;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "报警设备id", example = "23049")
    private Long warnDeviceId;

    @Schema(description = "告警设备名")
    private String warnDevice;

    @Schema(description = "告警设备类型(0:系统设备;1:池塘传感器;)", example = "1")
    private Integer warnDeviceType;

    @Schema(description = "告警对象", example = "张三")
    private String warnName;

    @Schema(description = "告警类型(0:环境告警;1:事故告警;-2病虫告警;)", example = "2")
    private Integer warnType;

    @Schema(description = "告警描述")
    private String warnContent;

    @Schema(description = "负责人", example = "赵六")
    private String userName;

    @Schema(description = "状态(0:待处理;1:已处理;)", example = "1")
    private Integer status;

    @Schema(description = "处理描述")
    private String handleInfo;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "26092")
    private Long userId;

    @Schema(description = "所属部门", example = "24185")
    private Long deptId;
    private Long tenantId;
    private List<Long> deptIds;

    @Schema(description = "文件id", example = "5959")
    private String fid;

    private String metricId; // 设备测量项传感器和控制单元编码
    private String deviceId; // 报警设备id
}
