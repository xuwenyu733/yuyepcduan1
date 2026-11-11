package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据修正记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkDataRevisePageReqVO extends PageParam {

    @Schema(description = "车间id", example = "854")
    private Long workshopId;

    @Schema(description = "车间名称", example = "芋艿")
    private String workshopName;

    @Schema(description = "系统名称", example = "赵六")
    private String pondSystemName;

    @Schema(description = "系统id", example = "21670")
    private Long pondSystemId;

    @Schema(description = "系统编号")
    private String pondNum;

    @Schema(description = "存塘名称", example = "王五")
    private String pondName;

    @Schema(description = "存塘id", example = "8247")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "操作类型(1:投放;2:死亡;3:分池;4:投喂;5:存塘;)", example = "1")
    private String reviseType;
    private String reviseTypeName; // 操作类型名称

    @Schema(description = "原数据")
    private String originData;

    @Schema(description = "差值")
    private String differenceData;

    @Schema(description = "变化比例")
    private String changeRatio;

    @Schema(description = "原因", example = "不香")
    private String reason;

    @Schema(description = "技术总监意见")
    private String technicalDirectorsOpinion;

    @Schema(description = "厂长意见")
    private String directorsOpinion;

    @Schema(description = "业务id", example = "2197")
    private Long busId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "9777")
    private Long userId;

    @Schema(description = "所属部门", example = "1813")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "6872")
    private String fid;

    private String status; // 状态(0:已申请;1:已批准;2:技术总监已审批;3:厂长已审批;4:已完成;-1:已拒绝;)
    private String processInstanceId; // 流程实例
}
