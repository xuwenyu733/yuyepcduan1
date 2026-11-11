package cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 生长模型分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkGrowthModelPageReqVO extends PageParam {

    @Schema(description = "所属车间id", example = "27502")
    private Long workshopId;

    @Schema(description = "车间名称", example = "赵六")
    private String workshopName;

    @Schema(description = "所属系统id", example = "6394")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "张三")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "10341")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "平均长度")
    private Integer averageLength;

    @Schema(description = "平均体重")
    private Integer averageWeight;

    @Schema(description = "增长速度")
    private Integer growthRate;

    @Schema(description = "规格")
    private Integer specification;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "12102")
    private Long userId;

    @Schema(description = "所属部门", example = "991")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "21147")
    private String fid;

}
