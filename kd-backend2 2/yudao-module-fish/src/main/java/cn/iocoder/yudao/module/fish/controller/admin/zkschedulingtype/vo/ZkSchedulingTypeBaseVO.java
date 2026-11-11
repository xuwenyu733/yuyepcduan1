package cn.iocoder.yudao.module.fish.controller.admin.zkschedulingtype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 检测配置 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkSchedulingTypeBaseVO {

    @Schema(description = "检测名称", example = "王五")
    private String checkName;

    @Schema(description = "类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)", example = "1")
    private String checkType;

    @Schema(description = "备注")
    private String checkContent;
    private String fid;
    private String fileUrl;

}
