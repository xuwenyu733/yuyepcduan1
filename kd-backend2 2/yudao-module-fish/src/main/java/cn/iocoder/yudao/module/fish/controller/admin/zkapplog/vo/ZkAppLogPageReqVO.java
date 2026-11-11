package cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - app记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkAppLogPageReqVO extends PageParam {

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

    public ZkAppLogPageReqVO build_deviceMac(String deviceMac) {
        this.setDeviceMac(deviceMac);
        return this;
    }
}
