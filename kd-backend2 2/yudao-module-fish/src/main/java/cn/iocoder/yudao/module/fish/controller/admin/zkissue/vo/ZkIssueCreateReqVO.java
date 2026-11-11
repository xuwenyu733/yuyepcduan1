package cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 问题总结创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkIssueCreateReqVO extends ZkIssueBaseVO {

}
