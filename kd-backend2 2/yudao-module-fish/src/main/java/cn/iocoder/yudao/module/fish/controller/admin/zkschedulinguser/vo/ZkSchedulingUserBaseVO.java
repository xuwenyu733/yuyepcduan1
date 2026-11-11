package cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 排班用户 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkSchedulingUserBaseVO {

    @Schema(description = "排班id", example = "17732")
    private Long schedulingId;

    @Schema(description = "被排班人id", example = "3853")
    private Long userId;

    @Schema(description = "被排班人", example = "李四")
    private String userName;

    @Schema(description = "被排班人所属部门", example = "30245")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "22894")
    private String fid;

}
