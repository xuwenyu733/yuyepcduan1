package cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 时间字典 Excel 导出 Request VO，参数和 ZkDayRecordPageReqVO 是一致的")
@Data
public class ZkDayRecordExportReqVO {

    @Schema(description = "时间(格式:yyyy-MM-dd)")
    private String simpleFormat;

    private Integer dateYear;
    private Integer dateMonth;
    private Integer dateValue;
    @Schema(description = "日期名称(只有节假日有意义)", example = "李四")
    private String holidayName;

    @Schema(description = "星期名称", example = "张三")
    private String weekName;

    @Schema(description = "0:工作日;1:周末;2:节假日;", example = "2")
    private Byte dateType;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
