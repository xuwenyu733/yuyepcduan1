package cn.iocoder.yudao.module.fish.controller.admin.zkprecisionfeeding.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 精准投喂 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPrecisionFeedingBaseVO {

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

    @Schema(description = "负责人id", example = "19063")
    private Long userId;

    @Schema(description = "所属部门", example = "3670")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "1549")
    private String fid;

}
