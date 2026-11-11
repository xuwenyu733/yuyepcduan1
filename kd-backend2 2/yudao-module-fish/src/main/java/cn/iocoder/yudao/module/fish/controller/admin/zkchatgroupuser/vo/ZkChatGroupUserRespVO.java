package cn.iocoder.yudao.module.fish.controller.admin.zkchatgroupuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 群聊成员 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkChatGroupUserRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "358")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "群组id", requiredMode = Schema.RequiredMode.REQUIRED, example = "24788")
    @ExcelProperty("群组id")
    private Long groupId;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "用户名", example = "王五")
    @ExcelProperty("用户名")
    private String userName;

    @Schema(description = "用户类型(0:成员;1:群主;)", example = "1")
    @ExcelProperty("用户类型(0:成员;1:群主;)")
    private Integer userType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "组员", example = "2194")
    @ExcelProperty("组员")
    private Long userId;

    @Schema(description = "所属部门", example = "11775")
    @ExcelProperty("所属部门")
    private Long deptId;
    private String groupName;

}