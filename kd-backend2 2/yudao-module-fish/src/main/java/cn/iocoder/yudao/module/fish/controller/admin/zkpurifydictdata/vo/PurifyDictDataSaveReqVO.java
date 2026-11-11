package cn.iocoder.yudao.module.fish.controller.admin.zkpurifydictdata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 业务字典明细新增/修改 Request VO")
@Data
public class PurifyDictDataSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "7112")
    private Long id;

    @Schema(description = "字典类型(category:品类, source:来源, spec:规格)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "字典类型(category:品类, source:来源, spec:规格)不能为空")
    private String dictType;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字典标签不能为空")
    private String dictLabel;

    @Schema(description = "字典键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "字典键值不能为空")
    private String dictValue;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "所属部门", example = "3469")
    private Long deptId;

}