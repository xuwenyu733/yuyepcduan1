package cn.iocoder.yudao.module.fish.controller.admin.zktask;

import cn.iocoder.yudao.framework.common.base_api.BaseNotifyService;
import cn.iocoder.yudao.framework.common.enums.RedisKeyConstants;
import cn.iocoder.yudao.framework.common.util.easy.*;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatVo;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.Task2StrHandler;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.TaskDetailHandler;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.TaskStatusEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zktask.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
// import cn.iocoder.yudao.module.fish.convert.zktask.ZkTaskConvert;
import cn.iocoder.yudao.module.fish.service.zktask.ZkTaskService;

@Tag(name = "管理后台 - 任务")
@RestController
@RequestMapping("/fish/zk-task")
@Validated
@Slf4j
public class ZkTaskController {

    @Resource
    private ZkTaskService zkTaskService;
    @Resource
    private TaskDetailService taskDetailService;
    @Autowired
    private Task2StrHandler task2StrHandler;
    @Autowired
    private AdminUserApi adminUserApi;
    @Resource
    private TaskDetailHandler taskDetailHandler;
    @Resource
    private AiAgentService aiAgentService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Resource
    private PlusRedisService plusRedisService;
    @Resource
    private BaseNotifyService baseNotifyService;

    @PostMapping("/create")
    @Operation(summary = "创建任务")
    public CommonResult<Long> createZkTask(@Valid @RequestBody ZkTaskCreateReqVO createReqVO) {
        // 1. 初始化 时间 区间
        createReqVO.build_timeInfo();
        return success(zkTaskService.createZkTask(createReqVO).getId());
    }

    @PostMapping("/create4App")
    @Operation(summary = "创建任务")
    public CommonResult<Long> create4App(@Valid @RequestBody ZkTaskCreateReqVO createReqVO, HttpServletRequest request) {
        createReqVO.setToken(EasyAuthUtil.selectAuthorization(request));
        // 1. 初始化 时间 区间
        createReqVO.build_timeInfo();
        ZkTaskDO zkTask = zkTaskService.createZkTask(createReqVO);
        // 2. 创建明细
        List<TaskDetailDO> detailDOS = createReqVO.selectTaskDetailList(zkTask);
        if(EasyListUtil.isNotEmpty(detailDOS)) {
            taskDetailService.customUpdateBatch(detailDOS);
            // 3. 同步数据到打样记录
            taskDetailHandler.syncData(detailDOS);
            // 4. 同步生成 智能体巡检报告
            CompletableFuture.runAsync(() -> genAgentInspectionReport(createReqVO.getToken(), createReqVO.getTaskDetailDOS()), executor);
        }

        return success(zkTask.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新任务")
    public CommonResult<Boolean> updateZkTask(@Valid @RequestBody ZkTaskUpdateReqVO updateReqVO, HttpServletRequest request) {
        updateReqVO.setToken(EasyAuthUtil.selectAuthorization(request));
        // 1. 修改 任务信息
        zkTaskService.updateZkTask(updateReqVO);
        // 2. 修改 完成明细
        List<TaskDetailDO> detailDOS = updateReqVO.selectTaskDetailList();
        if(EasyListUtil.isNotEmpty(detailDOS)) {
            taskDetailService.customUpdateBatch(detailDOS);
            // 3. 同步数据到打样记录、死亡、投喂
            taskDetailHandler.syncData(detailDOS);
            // 4. 同步生成 智能体巡检报告
            CompletableFuture.runAsync(() -> genAgentInspectionReport(updateReqVO.getToken(), updateReqVO.getTaskDetailDOS()), executor);
        }
        return success(true);
    }

    public void genAgentInspectionReport(String token, List<TaskDetailDO> taskDetailDOS) {
        if(EasyListUtil.isEmpty(taskDetailDOS)) return;
        // 1. 生成uid
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        String uid = ChatVo.selectUid(adminUserRespDTO.getUsername(), adminUserRespDTO.getNickname(), adminUserRespDTO.getId());
        // 2. 每隔 3s 生成报告
        for(TaskDetailDO item : taskDetailDOS) {
            try {
                // 3. 获取 巡检 txt
                String detailStr = task2StrHandler.parseTaskDetail(item);
                if(StringUtils.isEmpty(detailStr)) continue;
                String otherParams = new EasyMap3<Long>().put("taskDetailId", item.getId()).toJson();
                // 4. 生成报告
                String time = new Date().getTime()+"";
                ChatVo chatVo = new ChatVo(uid, adminUserRespDTO.getId()+"", detailStr, token,
                        "pc", "deepseek-v3", "inspection_report", "false", time,
                        String.format("%sAAA%s", adminUserRespDTO.getId(), time)).build_otherParams(otherParams);
                ChatRespVo chatRespVo = aiAgentService.chat(chatVo);
                log.warn("【Java请求智能体服务生成巡检报告-{}】middle,操作类型为 巡检明细内容：{},智能体返回内容:{}", TokenUtil.userInfo(), JSON.toJSONString(item), JSON.toJSONString(chatRespVo));
                Thread.sleep(3*1000);
            } catch (Exception e) {
                String title = String.format("【Java请求智能体服务生成巡检报告-异常】e:%s", e.getMessage());
                String content = String.format("【Java请求智能体服务生成巡检报告-异常-%s】middle error,操作类型为 用户信息：%s,巡检明细内容:%s", TokenUtil.userInfo(), JSON.toJSONString(adminUserRespDTO), JSON.toJSONString(item));
                log.error(content);
                EmailUtil.send(title, content);
            }
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除任务")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkTask(@RequestParam("id") Long id) {
        zkTaskService.deleteZkTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkTaskRespVO> getZkTask(@RequestParam("id") Long id) {
        ZkTaskDO zkTask = zkTaskService.getZkTask(id);
        taskDetailEnhancer(Arrays.asList(zkTask));
        return success(BeanUtils.toBean(zkTask, ZkTaskRespVO.class));
    }

    @GetMapping("/getTaskInfo")
    @Operation(summary = "获得任务解析")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<String> getTaskInfo(@RequestParam("id") Long id) {
        // 1. 获取任务信息
        ZkTaskDO zkTask = zkTaskService.getZkTask(id);
        // 2. 获取明细信息
        List<TaskDetailDO> byTaskIds = taskDetailService.getByTaskIds(List.of(id));
        zkTask.setTaskDetailDOS(byTaskIds);
        String taskInfo = task2StrHandler.parseTask(zkTask);
        return success(taskInfo);
    }


    @GetMapping("/list")
    @Operation(summary = "获得任务列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkTaskRespVO>> getZkTaskList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkTaskDO> list = zkTaskService.getZkTaskList(ids);
        taskDetailEnhancer(list);
        return success(BeanUtils.toBean(list, ZkTaskRespVO.class));
    }

    /**
     * @param userId 谁的任务
     * @return 需要 通知 用户 知晓的任务
     */
    public String notifyNeedReadTask(Long userId) {
        // 1. 获取 未读任务
        ZkTaskDO zkTaskDO = new ZkTaskDO().setUserId(userId).setStatus(TaskStatusEnum.NEED_READ.getValue());
        List<ZkTaskDO> list = zkTaskService.selectByQuery(new QueryWrapper<>(zkTaskDO));
        if(EasyListUtil.isEmpty(list)) return null;
        // 2. 执行通知
        List<Long> taskIds = plusRedisService.selectArr(RedisKeyConstants.HAD_NOTIFY_FISH_TASK_KEY, Long.class);
        if(taskIds == null) taskIds = new ArrayList<>();
        String msg = "";
        ZkTaskDO curr = null;
        for(ZkTaskDO currTask : list) {
            if(taskIds.contains(currTask.getId())) continue;
            AdminUserRespDTO user = adminUserApi.getUser(Long.parseLong(currTask.getCreator()));
            if(user == null) continue;
            msg = String.format("%s于%s下发任务：%s，请尽快完成并在App端填写任务反馈！", user.getNickname(),
                    EasyDateUtil.format(currTask.getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
                    currTask.getTaskName());
            curr = currTask;
            break;
        }
        if(StringUtil.isNotEmptyWith0(msg) && curr != null) {
            baseNotifyService.insertNotify(userId, msg);
            // 3. 缓存 该任务 已经通知
            taskIds.add(curr.getId());
            plusRedisService.insertStr(RedisKeyConstants.HAD_NOTIFY_FISH_TASK_KEY, JSON.toJSONString(taskIds), RedisKeyConstants.NOTIFY_EXPIRE);
        }

        return msg;
    }

    @GetMapping("/page")
    @Operation(summary = "获得任务分页")
    public CommonResult<PageResult<ZkTaskRespVO>> getZkTaskPage(@Valid ZkTaskPageReqVO pageVO) {
        // 1. 获取列表数据
        PageResult<ZkTaskDO> pageResult = zkTaskService.getZkTaskPage(pageVO);
        // 2. 获取明细内容
        taskDetailEnhancer(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkTaskRespVO.class));
    }

    /**
     * 给zk_task list 填充 zk_task_detail, 如果是巡检类型，都会有巡检明细数据
     * @param list
     */
    public void taskDetailEnhancer(List<ZkTaskDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取明细数据
        List<Long> taskIds = list.stream().map(ZkTaskDO::getId).collect(Collectors.toList());
        List<TaskDetailDO> detailDOS = taskDetailService.getByTaskIds(taskIds);
        if(EasyListUtil.isEmpty(detailDOS)) return;
        // 2. 将对应数据填充到zk_task中
        Map<Long, List<TaskDetailDO>> collect = detailDOS.stream().collect(Collectors.groupingBy(TaskDetailDO::getTaskId));
        for(ZkTaskDO item : list) {
            List<TaskDetailDO> tempDetailList = collect.get(item.getId());
            if(EasyListUtil.isNotEmpty(tempDetailList)) {
                tempDetailList.forEach(TaskDetailDO::build_checkIsOk);
            }
            item.setTaskDetailDOS(tempDetailList);
        }
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出任务 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkTaskExcel(@Valid ZkTaskExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkTaskDO> list = zkTaskService.getZkTaskList(exportReqVO);
        // 导出 Excel
        List<ZkTaskExcelVO> datas = BeanUtils.toBean(list, ZkTaskExcelVO.class);
        ExcelUtils.write(response, "任务.xls", "数据", ZkTaskExcelVO.class, datas);
    }

}
