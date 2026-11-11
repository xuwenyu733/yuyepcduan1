package cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 车间分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkWorkshopPageReqVO extends PageParam {

    @Schema(description = "车间名称", example = "张三")
    private String workshopName;

    @Schema(description = "具体位置")
    private String workshopPosition;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "维度")
    private String dimension;

    @Schema(description = "负责人", example = "赵六")
    private String userName;

    @Schema(description = "状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "16495")
    private Long userId;

    @Schema(description = "所属部门", example = "10584")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "16178")
    private String fid;
    private String tenantName; // 租户名称
    private String deptName; // 部门名称
    private List<Long> deptIds; // 部门ids

}
