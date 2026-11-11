package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 试卷创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPaperCreateReqVO extends ZkPaperBaseVO {

    @Schema(description = "试题文件url", required = true, example = "20285")
    @NotNull(message = "文件不能为空")
    private String examUrl;
}
