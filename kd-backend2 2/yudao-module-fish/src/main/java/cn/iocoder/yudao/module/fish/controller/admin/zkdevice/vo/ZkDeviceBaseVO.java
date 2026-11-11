package cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 设备 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkDeviceBaseVO {

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

    @Schema(description = "所在车间id", required = true, example = "3782")
    @NotNull(message = "所在车间id不能为空")
    private Long workshopId;

    @Schema(description = "负责人", example = "赵六")
    private String userName;

    @Schema(description = "状态(0:关闭中;1:开启中;-1:已废弃;)", example = "1")
    private String status;

    @Schema(description = "负责人id", example = "2238")
    private Long userId;

    @Schema(description = "所属部门", example = "31352")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "28742")
    @NotNull(message = "文件id不能为空")
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
