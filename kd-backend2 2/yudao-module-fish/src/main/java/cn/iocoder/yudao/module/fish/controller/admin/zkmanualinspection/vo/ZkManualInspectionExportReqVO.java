package cn.iocoder.yudao.module.fish.controller.admin.zkmanualinspection.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 人工巡检 Excel 导出 Request VO，参数和 ZkManualInspectionPageReqVO 是一致的")
@Data
public class ZkManualInspectionExportReqVO {

    @Schema(description = "所属车间id", example = "11251")
    private Long workshopId;

    @Schema(description = "车间名称", example = "张三")
    private String workshopName;

    @Schema(description = "巡检名称", example = "张三")
    private String inspectionName;
    private Long pondId;
    private String pondName;
    private String pondNum;

    @Schema(description = "巡检描述")
    private String inspectionContent;

    @Schema(description = "巡检状态(-2:有事故待处理;-1:有事故已处理;0:一切正常;)", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "11395")
    private Long userId;
    private String userName;

    @Schema(description = "所属部门", example = "21719")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "17083")
    private String fid;

}
