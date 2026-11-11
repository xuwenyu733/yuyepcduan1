package cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail;

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

import cn.iocoder.yudao.module.fish.controller.admin.zktaskdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
// import cn.iocoder.yudao.module.fish.convert.zktaskdetail.TaskDetailConvert;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;

@Tag(name = "管理后台 - 任务明细")
@RestController
@RequestMapping("/fish/task-detail")
@Validated
public class TaskDetailController {

    @Resource
    private TaskDetailService taskDetailService;

    @PostMapping("/create")
    @Operation(summary = "创建任务明细")
    public CommonResult<Long> createTaskDetail(@Valid @RequestBody TaskDetailCreateReqVO createReqVO) {
        return success(taskDetailService.createTaskDetail(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新任务明细")
    public CommonResult<Boolean> updateTaskDetail(@Valid @RequestBody TaskDetailUpdateReqVO updateReqVO) {
        taskDetailService.updateTaskDetail(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除任务明细")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteTaskDetail(@RequestParam("id") Long id) {
        taskDetailService.deleteTaskDetail(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得任务明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<TaskDetailRespVO> getTaskDetail(@RequestParam("id") Long id) {
        TaskDetailDO taskDetail = taskDetailService.getTaskDetail(id);
        return success(BeanUtils.toBean(taskDetail, TaskDetailRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得任务明细列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<TaskDetailRespVO>> getTaskDetailList(@RequestParam("ids") Collection<Long> ids) {
        List<TaskDetailDO> list = taskDetailService.getTaskDetailList(ids);
        return success(BeanUtils.toBean(list, TaskDetailRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得任务明细分页")
    public CommonResult<PageResult<TaskDetailRespVO>> getTaskDetailPage(@Valid TaskDetailPageReqVO pageVO) {
        PageResult<TaskDetailDO> pageResult = taskDetailService.getTaskDetailPage(pageVO);
        return success(BeanUtils.toBean(pageResult, TaskDetailRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出任务明细 Excel")
    // @OperateLog(type = EXPORT)
    public void exportTaskDetailExcel(@Valid TaskDetailExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<TaskDetailDO> list = taskDetailService.getTaskDetailList(exportReqVO);
        // 导出 Excel
        List<TaskDetailExcelVO> datas = BeanUtils.toBean(list, TaskDetailExcelVO.class);
        ExcelUtils.write(response, "任务明细.xls", "数据", TaskDetailExcelVO.class, datas);
    }

}
