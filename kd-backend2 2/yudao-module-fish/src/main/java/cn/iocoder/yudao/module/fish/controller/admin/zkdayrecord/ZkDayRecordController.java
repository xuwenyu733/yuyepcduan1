package cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord;

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

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdayrecord.ZkDayRecordDO;
// import cn.iocoder.yudao.module.fish.convert.zkdayrecord.ZkDayRecordConvert;
import cn.iocoder.yudao.module.fish.service.zkdayrecord.ZkDayRecordService;

@Tag(name = "管理后台 - 时间字典")
@RestController
@RequestMapping("/fish/zk-day-record")
@Validated
public class ZkDayRecordController {

    @Resource
    private ZkDayRecordService zkDayRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建时间字典")
    public CommonResult<Long> createZkDayRecord(@Valid @RequestBody ZkDayRecordCreateReqVO createReqVO) {
        return success(zkDayRecordService.createZkDayRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新时间字典")
    public CommonResult<Boolean> updateZkDayRecord(@Valid @RequestBody ZkDayRecordUpdateReqVO updateReqVO) {
        zkDayRecordService.updateZkDayRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除时间字典")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDayRecord(@RequestParam("id") Long id) {
        zkDayRecordService.deleteZkDayRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得时间字典")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDayRecordRespVO> getZkDayRecord(@RequestParam("id") Long id) {
        ZkDayRecordDO zkDayRecord = zkDayRecordService.getZkDayRecord(id);
        return success(BeanUtils.toBean(zkDayRecord, ZkDayRecordRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得时间字典列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDayRecordRespVO>> getZkDayRecordList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDayRecordDO> list = zkDayRecordService.getZkDayRecordList(ids);
        return success(BeanUtils.toBean(list, ZkDayRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得时间字典分页")
    public CommonResult<PageResult<ZkDayRecordRespVO>> getZkDayRecordPage(@Valid ZkDayRecordPageReqVO pageVO) {
        PageResult<ZkDayRecordDO> pageResult = zkDayRecordService.myGetZkDayRecordPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDayRecordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出时间字典 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDayRecordExcel(@Valid ZkDayRecordExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDayRecordDO> list = zkDayRecordService.getZkDayRecordList(exportReqVO);
        // 导出 Excel
        List<ZkDayRecordExcelVO> datas = BeanUtils.toBean(list, ZkDayRecordExcelVO.class);
        ExcelUtils.write(response, "时间字典.xls", "数据", ZkDayRecordExcelVO.class, datas);
    }

}
