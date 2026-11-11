package cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 任务明细 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class TaskDetailBaseVO {

    @Schema(description = "任务id(zk_task)", required = true, example = "20531")
    @NotNull(message = "任务id(zk_task)不能为空")
    private Long taskId;
    private Long schedulingId; // 排班表id

    @Schema(description = "检测名称", example = "赵六")
    private String checkName;

    @Schema(description = "检测类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)", example = "1")
    private String checkType;

    @Schema(description = "检测config")
    private String checkContent;

    @Schema(description = "检测res")
    private String checkRes;

    private Long workshopId; // 车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 系统名称
    private Long pondSystemId; // 系统id
    private String pondNum; // 系统编号
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String pondDetailNum; // 存塘编号
    private String batchNumber; // 批次编号

    private Long userId; // 用户id
    private Long deptId; // 部门id
    private Long tenantId; // 租户id
}
