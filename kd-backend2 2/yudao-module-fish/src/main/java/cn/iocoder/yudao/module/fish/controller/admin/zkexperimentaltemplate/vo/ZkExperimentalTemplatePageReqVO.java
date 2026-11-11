package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实验模板分页 Request VO")
@Data
public class ZkExperimentalTemplatePageReqVO extends PageParam {

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "品种")
    private String variety;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "所属部门")
    private Long deptId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
