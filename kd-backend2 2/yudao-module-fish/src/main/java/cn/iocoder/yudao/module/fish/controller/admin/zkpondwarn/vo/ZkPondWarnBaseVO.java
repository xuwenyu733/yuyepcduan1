package cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 告警 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPondWarnBaseVO {

    @Schema(description = "车间名称", example = "赵六")
    private String workshopName;

    @Schema(description = "具体位置")
    private String workshopPosition;

    @Schema(description = "所属车间id", example = "25757")
    private Long workshopId;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;
    private String pondNum; // 存塘编号

    @Schema(description = "所属存塘id", example = "6081")
    private Long pondId;

    private Long warnDeviceId; // 报警设备id
    @Schema(description = "告警对象", example = "芋艿")
    private String warnName;

    @Schema(description = "状态(0:环境告警;1:事故告警;-1:病虫告警;)", example = "1")
    private String warnType;

    @Schema(description = "告警描述")
    private String warnContent;

    @Schema(description = "负责人", example = "王五")
    private String userName;

    @Schema(description = "状态(0:待处理;1:已处理;)", example = "2")
    private String status;

    @Schema(description = "负责人id", example = "9067")
    private Long userId;

    @Schema(description = "所属部门", example = "28185")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "20218")
    @NotNull(message = "文件id不能为空")
    private String fid;
    private String handleInfo; // 处理描述

    private Long pondSystemId; // 存塘系统id
    private String pondSystemName; // 存塘系统名称
    private String pondDetailNum; // 存塘编号

    private String iotMessageId; // iot消息唯一标识
}
