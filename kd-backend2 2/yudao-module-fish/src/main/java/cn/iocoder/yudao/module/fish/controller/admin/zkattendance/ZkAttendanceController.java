package cn.iocoder.yudao.module.fish.controller.admin.zkattendance;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo.*;
import cn.iocoder.yudao.module.fish.service.zkattendance.ZkAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 考勤管理")
@RestController
@RequestMapping("/fish/zk-attendance")
@Validated
public class ZkAttendanceController {

    @Resource
    private ZkAttendanceService attendanceService;

    @PostMapping("/punch")
    @Operation(summary = "打卡")
    public CommonResult<Long> punch(@Valid @RequestBody ZkAttendanceCreateReqVO createReqVO) {
        return success(attendanceService.punch(createReqVO));
    }

    @GetMapping("/today")
    @Operation(summary = "获得今日考勤信息")
    public CommonResult<ZkAttendanceTodayVO> getTodayAttendance() {
        return success(attendanceService.getTodayAttendance());
    }

    @GetMapping("/page")
    @Operation(summary = "获得考勤分页")
    public CommonResult<PageResult<ZkAttendanceRespVO>> getAttendancePage(@Valid ZkAttendancePageReqVO pageReqVO) {
        PageResult<ZkAttendanceRespVO> pageResult = attendanceService.getAttendancePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得考勤统计")
    @Parameter(name = "month", description = "月份，格式：yyyy-MM，为空则查询当前月", example = "2024-12")
    public CommonResult<ZkAttendanceStatisticsVO> getAttendanceStatistics(@RequestParam(value = "month", required = false) String month) {
        return success(attendanceService.getAttendanceStatistics(month));
    }
}





