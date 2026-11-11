package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 群聊成员分页 Request VO")
@Data
public class ZkChatGroupUserPageReqVO extends PageParam {

    @Schema(description = "群组id", example = "24788")
    private Long groupId;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "用户名", example = "王五")
    private String userName;

    @Schema(description = "用户类型(0:成员;1:群主;)", example = "1")
    private Integer userType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "组员", example = "2194")
    private Long userId;

    @Schema(description = "所属部门", example = "11775")
    private Long deptId;
    private String groupName;

}