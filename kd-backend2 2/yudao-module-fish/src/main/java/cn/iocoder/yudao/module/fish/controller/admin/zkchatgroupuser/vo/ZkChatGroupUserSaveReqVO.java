package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 群聊成员新增/修改 Request VO")
@Data
public class ZkChatGroupUserSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "358")
    private Long id;

    @Schema(description = "群组id", requiredMode = Schema.RequiredMode.REQUIRED, example = "24788")
    @NotNull(message = "群组id不能为空")
    private Long groupId;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "用户名", example = "王五")
    private String userName;

    @Schema(description = "用户类型(0:成员;1:群主;)", example = "1")
    private Integer userType;

    @Schema(description = "组员", example = "2194")
    private Long userId;

    @Schema(description = "所属部门", example = "11775")
    private Long deptId;
    private String groupName;

}