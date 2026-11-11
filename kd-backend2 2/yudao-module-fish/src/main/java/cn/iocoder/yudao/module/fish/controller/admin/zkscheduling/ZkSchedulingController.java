package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.to.SchedulingUpdater;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.ZkTaskController;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.service.zktask.ZkTaskService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
// import cn.iocoder.yudao.module.fish.convert.zkscheduling.ZkSchedulingConvert;
import cn.iocoder.yudao.module.fish.service.zkscheduling.ZkSchedulingService;

@Tag(name = "管理后台 - 排班")
@RestController
@RequestMapping("/fish/zk-scheduling")
@Validated
@Slf4j
public class ZkSchedulingController {

    @Resource
    private ZkSchedulingService zkSchedulingService;

    @Resource
    private ZkTaskService zkTaskService;
    @Resource
    private TaskDetailService taskDetailService;
    @Resource
    private SchedulingUpdater schedulingUpdater;

    @Lazy
    @Resource
    private ZkTaskController zkTaskController;

    @PostMapping("/create")
    @Operation(summary = "创建排班")
    public CommonResult<Integer> createZkScheduling(@Valid @RequestBody ZkSchedulingCreateReqVO createReqVO) {
        log.warn("【添加排班】middle,createReqVO:{}", JSON.toJSONString(createReqVO));
        // return null;
        // 1. 添加排班
        ZkSchedulingDO zkScheduling = zkSchedulingService.createZkScheduling(createReqVO.build_userIdStr());
        // 2. 添加任务
        List<ZkTaskDO> taskList = new ArrayList<>();
        createReqVO.getUserList().stream().forEach(obj -> {
            List<ZkTaskDO> tempList = ZkTaskDO.buildXunCha_all(obj, zkScheduling);
            tempList.forEach(ti -> ti.build_pondIdArr(createReqVO.getPondIdArr()));
            taskList.addAll(tempList);
        });
        Integer count = zkTaskService.customSaveBatch(taskList);
        // 3. 更新通知
        createReqVO.getUserList().forEach(obj -> zkTaskController.notifyNeedReadTask(obj.getId()));

        return success(count);
    }

    @PutMapping("/update")
    @Operation(summary = "更新排班(赞不支持联动 zk_task，如果 需要修改可以先删除在新增)")
    public CommonResult<Boolean> updateZkScheduling(@Valid @RequestBody ZkSchedulingUpdateReqVO updateReqVO) {
        // 1. 更新数据
        List<ZkSchedulingDO> zkSchedulingDOS = zkSchedulingService.updateZkScheduling(updateReqVO);
        // 2. 处理 数据
        schedulingUpdater.handleUpdate(zkSchedulingDOS.get(0), zkSchedulingDOS.get(1));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排班")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Integer> deleteZkScheduling(@RequestParam("id") Long id) {
        // 1. 删除排班信息
        zkSchedulingService.deleteZkScheduling(id);
        // 2. 删除关联的任务信息
        Integer count = zkTaskService.customRemoveBySchedulingId(id);
        taskDetailService.customRemoveBySchedulingId(id);
        return success(count);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排班")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkSchedulingRespVO> getZkScheduling(@RequestParam("id") Long id) {
        // 1. 获取排班数据
        ZkSchedulingDO zkScheduling = zkSchedulingService.getZkScheduling(id);
        // 2. 将排班人员信息回显
        if(zkScheduling != null) listEnhancer(Arrays.asList(zkScheduling));
        return success(BeanUtils.toBean(zkScheduling, ZkSchedulingRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得排班列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkSchedulingRespVO>> getZkSchedulingList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkSchedulingDO> list = zkSchedulingService.getZkSchedulingList(ids);
        listEnhancer(list);
        return success(BeanUtils.toBean(list, ZkSchedulingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排班分页")
    public CommonResult<PageResult<ZkSchedulingRespVO>> getZkSchedulingPage(@Valid ZkSchedulingPageReqVO pageVO) {
        PageResult<ZkSchedulingDO> pageResult = zkSchedulingService.getZkSchedulingPage(pageVO);
        listEnhancer(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkSchedulingRespVO.class));
    }

    public void listEnhancer(List<ZkSchedulingDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取任务明细
        Set<Long> scheduleIds = list.stream().map(obj -> obj.getId()).collect(Collectors.toSet());
        List<ZkTaskDO> taskList= zkTaskService.getByScheduleIds(scheduleIds);
        // 2. 获取 人员列表
        if(EasyListUtil.isEmpty(taskList)) return;
        Map<Long, List<ZkTaskDO>> collect = taskList.stream().collect(Collectors.groupingBy(obj -> obj.getSchedulingId()));
        for(Long scheduleId : collect.keySet()) {
            List<ZkTaskDO> tempList = collect.get(scheduleId);
            Set<Long> userIds = tempList.stream().filter(o -> o.getUserId() != null).map(o -> o.getUserId()).collect(Collectors.toSet());
            for(ZkSchedulingDO local : list) {
                if(Objects.equals(local.getId(), scheduleId)) {
                    local.setUserIds(new ArrayList<>(userIds));
                    local.setUserIdStr(userIds.stream().map(o -> o.toString()).collect(Collectors.joining(",")));
                }
            }
        }
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排班 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkSchedulingExcel(@Valid ZkSchedulingExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkSchedulingDO> list = zkSchedulingService.getZkSchedulingList(exportReqVO);
        // 导出 Excel
        List<ZkSchedulingExcelVO> datas = BeanUtils.toBean(list, ZkSchedulingExcelVO.class);
        ExcelUtils.write(response, "排班.xls", "数据", ZkSchedulingExcelVO.class, datas);
    }

}
