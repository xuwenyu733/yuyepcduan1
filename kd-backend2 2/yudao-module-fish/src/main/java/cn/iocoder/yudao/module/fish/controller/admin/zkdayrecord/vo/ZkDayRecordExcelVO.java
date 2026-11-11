package cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 时间字典 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkDayRecordExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("时间(格式:yyyy-MM-dd)")
    private String simpleFormat;

    private Integer dateYear;
    private Integer dateMonth;
    private Integer dateValue;
    @ExcelProperty("日期名称(只有节假日有意义)")
    private String holidayName;

    @ExcelProperty("星期名称")
    private String weekName;

    @ExcelProperty("0:工作日;1:周末;2:节假日;")
    private Byte dateType;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
