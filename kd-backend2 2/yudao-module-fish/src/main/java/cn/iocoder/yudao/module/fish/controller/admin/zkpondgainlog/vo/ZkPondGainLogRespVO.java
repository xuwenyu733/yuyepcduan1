package cn.iocoder.yudao.module.fish.controller.admin.zkpondgainlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 鱼生长 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ZkPondGainLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4537")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "车间id", example = "16632")
    @ExcelProperty("车间id")
    private Long workshopId;

    @Schema(description = "系统id", example = "854")
    @ExcelProperty("系统id")
    private Long systemId;

    @Schema(description = "存塘id", example = "6795")
    @ExcelProperty("存塘id")
    private Long pondId;

    @Schema(description = "批次编号")
    @ExcelProperty("批次编号")
    private String batchNumber;

    @Schema(description = "统计日期")
    @ExcelProperty("统计日期")
    private String staticDay;

    @Schema(description = "鱼苗数量")
    @ExcelProperty("鱼苗数量")
    private Long fishNum;

    @Schema(description = "鱼苗规格")
    @ExcelProperty("鱼苗规格")
    private String fishSpec;

    @Schema(description = "今日鱼增加重量", example = "10407")
    @ExcelProperty("今日鱼增加重量")
    private BigDecimal fishGainCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门", example = "32733")
    @ExcelProperty("所属部门")
    private Long deptId;

}