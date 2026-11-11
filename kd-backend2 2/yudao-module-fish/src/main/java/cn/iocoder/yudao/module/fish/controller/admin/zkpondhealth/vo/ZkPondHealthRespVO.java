package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 鱼池健康 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkPondHealthRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "17856")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "存塘id", example = "16889")
    @ExcelProperty("存塘id")
    private Long pondId;

    @Schema(description = "环境层评分")
    @ExcelProperty("环境层评分")
    private BigDecimal environmentalLayerRating;

    @Schema(description = "行为层评分")
    @ExcelProperty("行为层评分")
    private BigDecimal behavioralLayerRating;

    @Schema(description = "机体层评分")
    @ExcelProperty("机体层评分")
    private BigDecimal bodyLayerRating;

    @Schema(description = "综合评分")
    @ExcelProperty("综合评分")
    private BigDecimal overallRating;

    @Schema(description = "环境层信息")
    @ExcelProperty("环境层信息")
    private String environmentalLayerInformation;

    @Schema(description = "行为层信息")
    @ExcelProperty("行为层信息")
    private String behavioralLayerInformation;

    @Schema(description = "机体层信息")
    @ExcelProperty("机体层信息")
    private String bodyLayerInformation;

    @Schema(description = "异常信息")
    @ExcelProperty("异常信息")
    private String exceptionMessage;

    @Schema(description = "异常个数")
    @ExcelProperty("异常个数")
    private Integer numberOfExceptions;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "31107")
    @ExcelProperty("所属部门")
    private Long deptId;
    private String pondDetailNum;
    private String workshopName;
    private String pondSystemName;
    private String pondName;

}