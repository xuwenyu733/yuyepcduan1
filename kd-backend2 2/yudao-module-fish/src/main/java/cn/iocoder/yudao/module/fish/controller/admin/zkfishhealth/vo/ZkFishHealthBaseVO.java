package cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 鱼类健康评估 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkFishHealthBaseVO {

    @Schema(description = "所属车间id", example = "17027")
    private Long workshopId;

    @Schema(description = "车间名称", example = "李四")
    private String workshopName;

    @Schema(description = "所属系统id", example = "3482")
    private Long pondSystemId;

    @Schema(description = "存塘系统编号")
    private String pondNum;

    @Schema(description = "存塘系统名称", example = "张三")
    private String pondSystemName;

    @Schema(description = "所属存塘id", example = "26253")
    private Long pondId;

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "存塘名称", example = "芋艿")
    private String pondName;

    @Schema(description = "批次编号")
    private String batchNumber;

    @Schema(description = "呼吸频率")
    private Integer breathingRate;

    @Schema(description = "摄食状态", example = "1")
    private Integer feedingStatus;

    @Schema(description = "健康数量", example = "5300")
    private Integer healthyCount;

    @Schema(description = "亚健康数量", example = "26696")
    private Integer subhealthyCount;

    @Schema(description = "发病数量", example = "10949")
    private Integer diseasedCount;

    @Schema(description = "健康评分")
    private Integer healthScore;

    @Schema(description = "负责人id", example = "2002")
    private Long userId;

    @Schema(description = "所属部门", example = "15268")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "20500")
    private String fid;

}
