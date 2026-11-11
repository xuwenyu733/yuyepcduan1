package cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 网盘资源分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkNetBoxPageReqVO extends PageParam {

    @Schema(description = "资源类型(1:入职培训;2:运营手册;3:规章制度;4:工作任务及流程;)", example = "1")
    private String fileType;

    @Schema(description = "资源类型名称", example = "芋艿")
    private String fileTypeName;

    @Schema(description = "资源前缀")
    private String prefixPath;

    @Schema(description = "备份", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "14049")
    private Long userId;

    @Schema(description = "所属部门", example = "5910")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "1790")
    private String fid;

}
