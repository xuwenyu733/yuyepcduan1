package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验多媒体分页 Request VO")
@Data
public class ZkExperimentalMediaPageReqVO extends PageParam {

    @Schema(description = "实验id", example = "25959")
    private Long experimentId;

    @Schema(description = "实验编号")
    private String experimentNumber;

    @Schema(description = "文件类型(1:图片;2:视频;)", example = "1")
    private Integer fileType;

    @Schema(description = "体表类别", example = "2")
    private String bodyType;

    @Schema(description = "文件id", example = "10112")
    private String fid;

    @Schema(description = "文件名列表")
    private String fileNames;

    @Schema(description = "文件地址列表")
    private String fileUrls;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "29497")
    private Long deptId;

}