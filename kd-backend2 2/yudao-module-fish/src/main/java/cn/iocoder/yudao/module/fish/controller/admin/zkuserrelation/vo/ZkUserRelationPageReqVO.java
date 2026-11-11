package cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户信息关联分页 Request VO")
@Data
public class ZkUserRelationPageReqVO extends PageParam {

    @Schema(description = "用户id", example = "14073")
    private Long userId;

    @Schema(description = "关联数据类型(1:个推;)", example = "1")
    private Integer relationType;

    @Schema(description = "关联值")
    private String relationValue;

    @Schema(description = "备注(部分信息可能需要json)", example = "随便")
    private String relationRemark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "3081")
    private Long deptId;

}