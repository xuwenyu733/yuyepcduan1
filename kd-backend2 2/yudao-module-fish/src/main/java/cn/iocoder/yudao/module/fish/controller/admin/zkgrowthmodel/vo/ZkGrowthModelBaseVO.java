package cn.iocoder.yudao.module.fish.controller.admin.zkgrowthmodel.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 生长模型 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkGrowthModelBaseVO {

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

    @Schema(description = "负责人id", example = "12102")
    private Long userId;

    @Schema(description = "所属部门", example = "991")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "21147")
    private String fid;

}
