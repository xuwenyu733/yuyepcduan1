package cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 用户信息关联 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkUserRelationRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "31891")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "用户id", example = "14073")
    @ExcelProperty("用户id")
    private Long userId;

    @Schema(description = "关联数据类型(1:个推;)", example = "1")
    @ExcelProperty("关联数据类型(1:个推;)")
    private Integer relationType;

    @Schema(description = "关联值")
    @ExcelProperty("关联值")
    private String relationValue;

    @Schema(description = "备注(部分信息可能需要json)", example = "随便")
    @ExcelProperty("备注(部分信息可能需要json)")
    private String relationRemark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "3081")
    @ExcelProperty("所属部门")
    private Long deptId;

}