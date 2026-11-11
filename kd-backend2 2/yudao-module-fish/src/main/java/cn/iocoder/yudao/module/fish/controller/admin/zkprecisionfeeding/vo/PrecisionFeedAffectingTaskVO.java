package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "影响投喂的任务")
@Data
public class PrecisionFeedAffectingTaskVO {

    private Long taskId;
    private String taskName;
    private String taskType;
    private String status;
    private LocalDateTime planTime;
    private String remark;
}
