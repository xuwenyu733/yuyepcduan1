package cn.iocoder.yudao.module.infra.controller.admin.file.vo.file;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "管理后台 - 上传文件 Request VO")
@Data
@NoArgsConstructor
public class FileUploadReqVO {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "文件目录", example = "XXX/YYY")
    private String directory;

    // @AssertTrue(message = "文件目录不正确")
    // @JsonIgnore
    // public boolean isDirectoryValid() {
    //     return !StrUtil.containsAny(directory, "..", "/", "\\");
    // }

    private String fid;
    private String busType;

    public FileUploadReqVO(MultipartFile file, String directory, String fid, String busType) {
        this.file = file;
        this.directory = directory;
        this.fid = fid;
        this.busType = busType;
    }
}
