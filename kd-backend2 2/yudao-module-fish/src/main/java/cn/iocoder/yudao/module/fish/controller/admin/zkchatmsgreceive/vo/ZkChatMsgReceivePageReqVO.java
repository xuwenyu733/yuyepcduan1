package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsgreceive.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatmsg.ZkChatMsgDO;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 聊天消息接受分页 Request VO")
@Data
public class ZkChatMsgReceivePageReqVO extends PageParam {

    @Schema(description = "聊天消息id", example = "6564")
    private Long chatMsgId;

    @Schema(description = "消息所属人", example = "9068")
    private Long userId;

    @Schema(description = "消息处理类型(0:待通知;1:已通知;2:已确认;3:已查看;4:已回复;)", example = "1")
    private Integer handleType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "所属部门", example = "21844")
    private Long deptId;
    private ZkChatMsgDO zkChatMsgDO;

}