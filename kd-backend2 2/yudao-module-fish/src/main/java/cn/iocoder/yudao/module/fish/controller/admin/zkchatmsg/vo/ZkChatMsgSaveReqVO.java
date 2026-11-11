package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 聊天消息新增/修改 Request VO")
@Data
public class ZkChatMsgSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "14158")
    private Long id;

    @Schema(description = "群组id(群聊消息时有效)", requiredMode = Schema.RequiredMode.REQUIRED, example = "19853")
    // @NotNull(message = "群组id(群聊消息时有效)不能为空")
    private Long groupId;

    @Schema(description = "接收人id(1对1时有效)", requiredMode = Schema.RequiredMode.REQUIRED, example = "27119")
    // @NotNull(message = "接收人id(1对1时有效)不能为空")
    private Long targetUserId;

    @Schema(description = "消息(文件消息时是url)")
    private String msg;
    /**
     * 接受消息的设备
     * cn.iocoder.yudao.framework.common.enums.DeviceTypeEnum
     */
    private String deviceType = "app"; // pc app

    @Schema(description = "文件id", example = "17029")
    private String fid;

    @Schema(description = "消息类型(0:文本;1:图片;2:视频;3:文档;99:其他;)", example = "1")
    private Integer msgType;

    @Schema(description = "发送人", example = "11208")
    private Long userId;

    @Schema(description = "所属部门", example = "1567")
    private Long deptId;
    private String groupName;
    private String targetUserName;
    private String userName;
    private String targetDeptId;

}