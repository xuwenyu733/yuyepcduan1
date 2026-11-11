package cn.iocoder.yudao.module.fish.controller.admin.zktask.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 任务 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkTaskBaseVO {

    @Schema(description = "业务id", example = "20049")
    private Long busId;
    private Long schedulingId;

    @Schema(description = "任务名称", example = "李四")
    private String taskName;

    @Schema(description = "任务描述")
    private String taskContent;

    @Schema(description = "任务反馈")
    private String taskPoint;

    @Schema(description = "任务结果")
    private String taskSummary;

    @Schema(description = "执行人id", example = "19925")
    private Long userId;

    @Schema(description = "执行人", example = "赵六")
    private String userName;

    @Schema(description = "执行人所属部门", example = "17315")
    private Long deptId;
    private Long tenantId;
    /**
     * 任务类型 参考字典配置：   zk_scheduling_type
     */
    private String taskType;

    @Schema(description = "状态(0:已下发;1:已通知;2:待完成;3:已完成;4:已评估;-1:已删除;)", example = "2")
    private String status;

    @Schema(description = "文件id", example = "25335")
    private String fid;
    private String fileUrl; // 详情url

    @Schema(description = "流程实例", example = "8760")
    private String processInstanceId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime finishTime; // 任务完成时间
    private String taskConfigIds; // 巡检配置ids
    private String checkTypes; // 巡检类型(多个英文,号分隔)
    private List<TaskDetailDO> taskDetailDOS; // 巡检明细
    private List<String> tempTimeArr; // 任务时间

    public ZkTaskBaseVO build_timeInfo() {
        if(EasyListUtil.isEmpty(tempTimeArr)) {
            return this;
        }
        if(StringUtils.isNotBlank(tempTimeArr.get(0))) {
            String startStr = tempTimeArr.get(0);
            LocalDateTime startTime = EasyDateUtil.parse(startStr);
            this.startTime = startTime;
        }
        if(tempTimeArr.size()==2 && StringUtils.isNotEmpty(tempTimeArr.get(1))) {
            String endStr = tempTimeArr.get(1);
            this.endTime = EasyDateUtil.parse(endStr);
        }
        return this;
    }
}
