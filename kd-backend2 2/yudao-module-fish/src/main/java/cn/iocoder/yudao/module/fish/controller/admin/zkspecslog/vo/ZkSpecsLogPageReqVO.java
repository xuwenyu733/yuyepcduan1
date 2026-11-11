package cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 规格记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkSpecsLogPageReqVO extends PageParam {

    @Schema(description = "车间id", example = "28090")
    private Long workshopId;

    @Schema(description = "车间名称", example = "王五")
    private String workshopName;

    @Schema(description = "系统名称", example = "李四")
    private String pondSystemName;

    @Schema(description = "系统id", example = "19618")
    private Long pondSystemId;

    @Schema(description = "系统编号")
    private String pondNum;

    @Schema(description = "存塘名称", example = "张三")
    private String pondName;

    @Schema(description = "存塘id", example = "12429")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "初始规格")
    private BigDecimal initSpecs;

    @Schema(description = "初始数量")
    private Long initNum;

    @Schema(description = "投喂总量", example = "12320")
    private BigDecimal feedCount;
    private BigDecimal currFeedCount; // 当日投喂量(Kg)

    @Schema(description = "当前存塘数量")
    private Long currNum;

    @Schema(description = "预估饵料系数")
    private BigDecimal predictRadio;

    @Schema(description = "预估规格")
    private BigDecimal predictSpecs;

    @Schema(description = "实际打样规格")
    private BigDecimal acturalSpecs;

    @Schema(description = "实际饵料系数")
    private BigDecimal acturalRadio;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "27326")
    private Long userId;

    @Schema(description = "所属部门", example = "9949")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "30368")
    private String fid;
    private BigDecimal predictAccumulatedWeightGain; // 预估累计增重
    private BigDecimal acturalAccumulatedWeightGain; // 实际累计增重
    private BigDecimal biologicalAssets; // 存塘生物资产
    private BigDecimal sumBiologicalAssets; // 总生物资产
    private BigDecimal avgBaitCoefficient; // 平均饵料系数

}
