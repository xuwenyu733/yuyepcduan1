package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 聊天消息接受新增/修改 Request VO")
@Data
public class ZkChatMsgReceiveSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19342")
    private Long id;

    @Schema(description = "聊天消息id", requiredMode = Schema.RequiredMode.REQUIRED, example = "6564")
    @NotNull(message = "聊天消息id不能为空")
    private Long chatMsgId;

    @Schema(description = "消息所属人", example = "9068")
    private Long userId;

    @Schema(description = "消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)", example = "1")
    private Integer handleType;

    @Schema(description = "所属部门", example = "21844")
    private Long deptId;
    private ZkChatMsgDO zkChatMsgDO;

}