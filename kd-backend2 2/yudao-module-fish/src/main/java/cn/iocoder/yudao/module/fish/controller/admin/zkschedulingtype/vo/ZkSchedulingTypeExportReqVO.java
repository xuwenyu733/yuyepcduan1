package cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 检测配置 Excel 导出 Request VO，参数和 ZkSchedulingTypePageReqVO 是一致的")
@Data
public class ZkSchedulingTypeExportReqVO {

    @Schema(description = "检测名称", example = "王五")
    private String checkName;

    @Schema(description = "类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)", example = "1")
    private String checkType;

    @Schema(description = "备注")
    private String checkContent;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    private String fid;
    private String fileUrl;

}
