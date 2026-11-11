package cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 排班用户 Excel 导出 Request VO，参数和 ZkSchedulingUserPageReqVO 是一致的")
@Data
public class ZkSchedulingUserExportReqVO {

    @Schema(description = "排班id", example = "17732")
    private Long schedulingId;

    @Schema(description = "被排班人id", example = "3853")
    private Long userId;

    @Schema(description = "被排班人", example = "李四")
    private String userName;

    @Schema(description = "被排班人所属部门", example = "30245")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "文件id", example = "22894")
    private String fid;

}
