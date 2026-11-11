package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 聊天消息接受 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkChatMsgReceiveRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19342")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "聊天消息id", requiredMode = Schema.RequiredMode.REQUIRED, example = "6564")
    @ExcelProperty("聊天消息id")
    private Long chatMsgId;

    @Schema(description = "消息所属人", example = "9068")
    @ExcelProperty("消息所属人")
    private Long userId;

    @Schema(description = "消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)", example = "1")
    @ExcelProperty("消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)")
    private Integer handleType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "21844")
    @ExcelProperty("所属部门")
    private Long deptId;
    private ZkChatMsgDO zkChatMsgDO;

}