package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 精准投喂 Excel 导出 Request VO，参数和 ZkPrecisionFeedingPageReqVO 是一致的")
@Data
public class ZkPrecisionFeedingExportReqVO {

    @Schema(description = "所属车间id", example = "15322")
    private Long workshopId;

    @Schema(description = "车间名称", example = "张三")
    private String workshopName;

    @Schema(description = "所属系统id", example = "30307")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "张三")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "15390")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "投喂重量")
    private Object feedingWeight;

    @Schema(description = "投喂次数")
    private Integer feedingFrequency;

    @Schema(description = "摄食量")
    private Object foodIntake;

    @Schema(description = "投喂量")
    private Object feedingAmount;

    @Schema(description = "饵料系数")
    private Object feedConversionRatio;

    @Schema(description = "转化率")
    private Object conversionRate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "19063")
    private Long userId;

    @Schema(description = "所属部门", example = "3670")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "1549")
    private String fid;

}
