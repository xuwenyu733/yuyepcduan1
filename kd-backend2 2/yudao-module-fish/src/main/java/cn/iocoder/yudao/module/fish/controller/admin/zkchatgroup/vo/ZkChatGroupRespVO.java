package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroup.vo;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkchatgroupuser.ZkChatGroupUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 群聊组 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkChatGroupRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16665")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "群组名称", example = "张三")
    @ExcelProperty("群组名称")
    private String groupName;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "记录人", example = "3798")
    @ExcelProperty("记录人")
    private Long userId;

    @Schema(description = "所属部门", example = "23455")
    @ExcelProperty("所属部门")
    private Long deptId;
    private String userNames;
    private List<ZkChatGroupUserDO> userList;
    private String avatar; // 群组图片

}