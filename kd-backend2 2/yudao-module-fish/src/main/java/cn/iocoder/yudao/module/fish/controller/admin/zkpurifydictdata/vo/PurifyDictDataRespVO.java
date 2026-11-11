package cn.iocoder.yudao.module.fish.controller.admin.zkpurifydictdata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 业务字典明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurifyDictDataRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "7112")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "字典类型(category:品类, source:来源, spec:规格)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("字典类型(category:品类, source:来源, spec:规格)")
    private String dictType;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("字典标签")
    private String dictLabel;

    @Schema(description = "字典键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("字典键值")
    private String dictValue;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "3469")
    @ExcelProperty("所属部门")
    private Long deptId;

}