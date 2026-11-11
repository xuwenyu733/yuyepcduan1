package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalmedia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 实验多媒体 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkExperimentalMediaRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4032")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "实验id", example = "25959")
    @ExcelProperty("实验id")
    private Long experimentId;

    @Schema(description = "实验编号")
    @ExcelProperty("实验编号")
    private String experimentNumber;

    @Schema(description = "文件类型(1:图片;2:视频;)", example = "1")
    @ExcelProperty("文件类型(1:图片;2:视频;)")
    private Integer fileType;

    @Schema(description = "体表类别", example = "2")
    @ExcelProperty("体表类别")
    private String bodyType;

    @Schema(description = "文件id", example = "10112")
    @ExcelProperty("文件id")
    private String fid;

    @Schema(description = "文件名列表")
    @ExcelProperty("文件名列表")
    private String fileNames;

    @Schema(description = "文件地址列表")
    @ExcelProperty("文件地址列表")
    private String fileUrls;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "29497")
    @ExcelProperty("所属部门")
    private Long deptId;

}