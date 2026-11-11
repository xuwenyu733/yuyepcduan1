package cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 问题总结更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIssueUpdateReqVO extends ZkIssueBaseVO {

    @Schema(description = "编号", required = true, example = "12190")
    @NotNull(message = "编号不能为空")
    private Long id;

}
