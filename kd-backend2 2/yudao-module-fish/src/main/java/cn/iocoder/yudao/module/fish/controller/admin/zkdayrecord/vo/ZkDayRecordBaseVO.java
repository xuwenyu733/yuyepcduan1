package cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 时间字典 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkDayRecordBaseVO {

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

}
