package cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 考勤分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkAttendancePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "张三")
    private String userName;

    @Schema(description = "考勤日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] attendanceDate;

    @Schema(description = "状态(0:正常;1:迟到;2:早退;3:缺勤;4:迟到+早退)", example = "0")
    private String status;
}





