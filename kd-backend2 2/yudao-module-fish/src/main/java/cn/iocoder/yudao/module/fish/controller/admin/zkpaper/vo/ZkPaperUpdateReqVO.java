package cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 试卷更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPaperUpdateReqVO extends ZkPaperBaseVO {

    @Schema(description = "编号", required = true, example = "14508")
    @NotNull(message = "编号不能为空")
    private Long id;

    // 是否更新文件，如果更新文件，需要重新添加试题，如果考试时间过了，则不能更新试题
    private Boolean isUpdateFile;

    @Schema(description = "试题文件url", required = true, example = "20285")
    @NotNull(message = "文件不能为空")
    private String examUrl;

}
