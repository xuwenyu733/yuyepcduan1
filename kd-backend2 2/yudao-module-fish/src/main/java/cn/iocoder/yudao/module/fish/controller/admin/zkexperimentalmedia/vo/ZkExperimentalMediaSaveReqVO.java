package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 实验多媒体新增/修改 Request VO")
@Data
public class ZkExperimentalMediaSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4032")
    private Long id;

    @Schema(description = "实验id", example = "25959")
    private Long experimentId;

    @Schema(description = "实验编号")
    private String experimentNumber;

    @Schema(description = "文件类型(1:图片;2:视频;)", example = "1")
    private Integer fileType;

    @Schema(description = "体表类别", example = "2")
    private String bodyType;

    @Schema(description = "文件id", example = "10112")
    private String fid;

    @Schema(description = "文件名列表")
    private String fileNames;

    @Schema(description = "文件地址列表")
    private String fileUrls;

    @Schema(description = "所属部门", example = "29497")
    private Long deptId;

}