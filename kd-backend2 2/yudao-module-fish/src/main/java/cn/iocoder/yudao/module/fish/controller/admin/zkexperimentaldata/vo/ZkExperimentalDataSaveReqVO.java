package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 实验数据采集新增/修改 Request VO")
@Data
public class ZkExperimentalDataSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29718")
    private Long id;

    @Schema(description = "实验id", example = "21349")
    private Long experimentId;

    @Schema(description = "实验id（前端别名 experimentalId）", example = "21349")
    private Long experimentalId;

    public Long resolveExperimentId() {
        return experimentId != null ? experimentId : experimentalId;
    }

    @Schema(description = "实验编号")
    private String experimentNumber;

    @Schema(description = "采集类型", example = "1")
    private String collectionType;

    @Schema(description = "采集数据")
    private String collectData;

    @Schema(description = "所属部门", example = "21551")
    private Long deptId;

    @Schema(description = "鱼池ID", example = "1024")
    private Long poolId;

    @Schema(description = "池号")
    private String poolNumber;

}