package cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.controller.admin.oaattendancesetting.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.oaattendancesetting.OaAttendanceSettingDO;
import cn.iocoder.yudao.module.fish.service.oaattendancesetting.OaAttendanceSettingService;

@Tag(name = "管理后台 - OA考勤设置")
@RestController
@RequestMapping("/fish/oa-attendance-setting")
@Validated
public class OaAttendanceSettingController {

    @Resource
    private OaAttendanceSettingService oaAttendanceSettingService;

    @PostMapping("/create")
    @Operation(summary = "创建OA考勤设置")
    public CommonResult<Long> createOaAttendanceSetting(@Valid @RequestBody OaAttendanceSettingCreateReqVO createReqVO) {
        return success(oaAttendanceSettingService.createOaAttendanceSetting(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新OA考勤设置")
    public CommonResult<Boolean> updateOaAttendanceSetting(@Valid @RequestBody OaAttendanceSettingUpdateReqVO updateReqVO) {
        oaAttendanceSettingService.updateOaAttendanceSetting(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除OA考勤设置")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteOaAttendanceSetting(@RequestParam("id") Long id) {
        oaAttendanceSettingService.deleteOaAttendanceSetting(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得OA考勤设置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<OaAttendanceSettingRespVO> getOaAttendanceSetting(@RequestParam("id") Long id) {
        OaAttendanceSettingDO setting = oaAttendanceSettingService.getOaAttendanceSetting(id);
        return success(BeanUtils.toBean(setting, OaAttendanceSettingRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得OA考勤设置列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<OaAttendanceSettingRespVO>> getOaAttendanceSettingList(@RequestParam("ids") Collection<Long> ids) {
        List<OaAttendanceSettingDO> list = oaAttendanceSettingService.getOaAttendanceSettingList(ids);
        return success(BeanUtils.toBean(list, OaAttendanceSettingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得OA考勤设置分页")
    public CommonResult<PageResult<OaAttendanceSettingRespVO>> getOaAttendanceSettingPage(@Valid OaAttendanceSettingPageReqVO pageVO) {
        PageResult<OaAttendanceSettingDO> pageResult = oaAttendanceSettingService.getOaAttendanceSettingPage(pageVO);
        return success(BeanUtils.toBean(pageResult, OaAttendanceSettingRespVO.class));
    }

    @GetMapping("/enabled-list")
    @Operation(summary = "获得启用的OA考勤设置列表")
    public CommonResult<List<OaAttendanceSettingRespVO>> getEnabledOaAttendanceSettingList() {
        List<OaAttendanceSettingDO> list = oaAttendanceSettingService.getEnabledOaAttendanceSettingList();
        return success(BeanUtils.toBean(list, OaAttendanceSettingRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出OA考勤设置 Excel")
    public void exportOaAttendanceSettingExcel(@Valid OaAttendanceSettingExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<OaAttendanceSettingDO> list = oaAttendanceSettingService.getOaAttendanceSettingList(exportReqVO);
        // 导出 Excel
        // List<OaAttendanceSettingExcelVO> datas = BeanUtils.toBean(list, OaAttendanceSettingExcelVO.class);
        // ExcelUtils.write(response, "OA考勤设置.xls", "数据", OaAttendanceSettingExcelVO.class, datas);
    }
}



