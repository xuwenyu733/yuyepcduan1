package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 群聊组新增/修改 Request VO")
@Data
public class ZkChatGroupSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16665")
    private Long id;

    @Schema(description = "群组名称", example = "张三")
    private String groupName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "记录人", example = "3798")
    private Long userId;

    @Schema(description = "所属部门", example = "23455")
    private Long deptId;
    private String userNames;

    private List<ZkChatGroupUserDO> userList;
    private String avatar; // 群组图片

}