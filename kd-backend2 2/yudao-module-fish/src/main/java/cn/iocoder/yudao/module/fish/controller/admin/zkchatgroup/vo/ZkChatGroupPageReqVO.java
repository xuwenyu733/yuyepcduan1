package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 群聊组分页 Request VO")
@Data
public class ZkChatGroupPageReqVO extends PageParam {

    @Schema(description = "群组名称", example = "张三")
    private String groupName;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "记录人", example = "3798")
    private Long userId;
    private Long groupUserId;

    @Schema(description = "所属部门", example = "23455")
    private Long deptId;
    private String userNames;
    private List<ZkChatGroupUserDO> userList;
    private String avatar; // 群组图片

}