package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import jakarta.validation.constraints.*;

/**
* 排班 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkSchedulingBaseVO {

    @Schema(description = "排班名称", example = "王五")
    private String schedulingName;

    @Schema(description = "排班描述")
    private String schedulingContent;

    @Schema(description = "排班人id", example = "19601")
    private Long userId;
    // 被安排人
    private List<Long> userIds;

    @Schema(description = "排班人", example = "赵六")
    private String userName;

    @Schema(description = "排班人所属部门", example = "15136")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)", example = "1")
    private String status;
    /**
     * 任务类型(1:打卡;2:巡查;3:其他;)
     */
    private String schedulingType;

    @Schema(description = "排班人id")
    private String userIdStr;

    @Schema(description = "文件id", example = "11720")
    private String fid;
    private String schedulingTime;
    private List<String> selectDays;
    private Integer selectDayType;
    private String taskConfigIds; // 巡检配置ids
    private String pondIds; // 存塘ids(多个英文逗号隔开)
}
