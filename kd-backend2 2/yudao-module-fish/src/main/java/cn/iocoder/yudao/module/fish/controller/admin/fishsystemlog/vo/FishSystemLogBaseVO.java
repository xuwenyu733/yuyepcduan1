package cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* fish系统日志 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class FishSystemLogBaseVO {

    @Schema(description = "存塘编号")
    private String pondDetailNum;

    @Schema(description = "日志标题")
    private String logTitle;

    @Schema(description = "日志类型(0:fish;1:其他;)", example = "2")
    private String logType;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "原始数据")
    private String originData;

    @Schema(description = "目标数据")
    private String targetData;

    @Schema(description = "原始结果数据")
    private String originRes;

    @Schema(description = "目标结果数据")
    private String targetRes;

    @Schema(description = "记录人", example = "16725")
    private Long userId;

    @Schema(description = "所属部门", example = "23676")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "7828")
    @NotNull(message = "文件id不能为空")
    private String fid;

}
