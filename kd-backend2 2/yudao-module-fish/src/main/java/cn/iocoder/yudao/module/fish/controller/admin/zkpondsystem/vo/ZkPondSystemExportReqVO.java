package cn.iocoder.yudao.module.fish.controller.admin.zkpondsystem.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 存塘系统 Excel 导出 Request VO，参数和 ZkPondSystemPageReqVO 是一致的")
@Data
public class ZkPondSystemExportReqVO {

    @Schema(description = "系统编号")
    private String systemNum;

    @Schema(description = "系统名称", example = "赵六")
    private String systemName;

    @Schema(description = "所在车间", example = "芋艿")
    private String workshopName;

    @Schema(description = "所在车间id", example = "8085")
    private Long workshopId;

    @Schema(description = "负责人", example = "李四")
    private String userName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "28230")
    private Long userId;

    @Schema(description = "所属部门", example = "22698")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "26295")
    private String fid;
    private String tenantName; // 租户名称
    private String deptName; // 部门名称

}
