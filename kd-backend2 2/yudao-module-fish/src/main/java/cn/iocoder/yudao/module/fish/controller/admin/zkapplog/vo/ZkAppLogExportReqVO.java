package cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - app记录 Excel 导出 Request VO，参数和 ZkAppLogPageReqVO 是一致的")
@Data
public class ZkAppLogExportReqVO {

    @Schema(description = "app版本")
    private Integer appVersion;
    private String appName;

    @Schema(description = "app地址", example = "https://www.iocoder.cn")
    private String fileUrl;
    private String fileId;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    private String baseWebUrl;
    private String deviceMac;

}
