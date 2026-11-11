package cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 用户信息关联新增/修改 Request VO")
@Data
public class ZkUserRelationSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "31891")
    private Long id;

    @Schema(description = "用户id", example = "14073")
    private Long userId;

    @Schema(description = "关联数据类型(1:个推;)", example = "1")
    private Integer relationType;

    @Schema(description = "关联值")
    private String relationValue;

    @Schema(description = "备注(部分信息可能需要json)", example = "随便")
    private String relationRemark;

    @Schema(description = "所属部门", example = "3081")
    private Long deptId;

}