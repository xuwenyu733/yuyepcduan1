package cn.iocoder.yudao.module.fish.controller.admin.zkdevicealarm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 设备报警 Dashboard Response VO")
@Data
public class ZkDeviceAlarmDashboardRespVO {
    @Schema(description = "编号", example = "1024")
    private Long id;
    @Schema(description = "告警设备类型(0设备 1水 2鱼 3人)", example = "0")
    private Integer warnDeviceType;
    @Schema(description = "告警类型", example = "1")
    private Integer warnType;
    @Schema(description = "处理状态(0未处理 1已处理)", example = "0")
    private Integer status;
    @Schema(description = "告警对象", example = "设备异常")
    private String warnName;
    @Schema(description = "告警内容")
    private String warnContent;
    @Schema(description = "处理信息")
    private String handleInfo;
    @Schema(description = "车间名称")
    private String workshopName;
    @Schema(description = "系统名称")
    private String pondSystemName;
    @Schema(description = "鱼池编号")
    private String pondDetailNum;
    @Schema(description = "鱼池名称")
    private String pondName;
    @Schema(description = "处理人编号")
    private Long userId;
    @Schema(description = "处理人名称")
    private String userName;
    @Schema(description = "部门编号")
    private Long deptId;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "类别 (fish, water, equipment, human)", example = "fish")
    private String category;
    @Schema(description = "标题", example = "【鱼】摄食率异常")
    private String title;
    @Schema(description = "消息内容", example = "1号车间 C池 - 摄食率下降至 75%")
    private String message;
    @Schema(description = "建议", example = "建议：检查鱼体健康状况")
    private String suggestion;
    @Schema(description = "时间友好显示", example = "1分钟前")
    private String time;
}
