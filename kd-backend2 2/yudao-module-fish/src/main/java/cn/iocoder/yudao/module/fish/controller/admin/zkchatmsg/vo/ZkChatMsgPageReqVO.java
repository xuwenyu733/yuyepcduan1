package cn.iocoder.yudao.module.fish.controller.admin.zkchatmsg.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 聊天消息分页 Request VO")
@Data
public class ZkChatMsgPageReqVO extends PageParam {

    @Schema(description = "群组id(群聊消息时有效)", example = "19853")
    private Long groupId;

    @Schema(description = "接收人id(1对1时有效)", example = "27119")
    private Long targetUserId;

    @Schema(description = "消息(文件消息时是url)")
    private String msg;

    @Schema(description = "文件id", example = "17029")
    private String fid;

    @Schema(description = "消息类型(0:文本;1:图片;2:视频;3:文档;99:其他;)", example = "1")
    private Integer msgType;
    /**
     * 接受消息的设备
     * cn.iocoder.yudao.framework.common.enums.DeviceTypeEnum
     */
    private String deviceType = "app"; // pc app

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "发送人", example = "11208")
    private Long userId;

    @Schema(description = "所属部门", example = "1567")
    private Long deptId;
    private String groupName;
    private String targetUserName;
    private String userName;
    private String targetDeptId;

}