package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to.PondHealthParser;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.TaskDetailCheckTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondhealth.ZkPondHealthDO;
import cn.iocoder.yudao.module.fish.service.zkpondhealth.ZkPondHealthService;

@Tag(name = "管理后台 - 鱼池健康")
@RestController
@RequestMapping("/fish/zk-pond-health")
@Validated
@Slf4j
public class ZkPondHealthController {

    @Resource
    private ZkPondHealthService zkPondHealthService;
    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkDeviceRecordService zkDeviceRecordService;
    @Resource
    private TaskDetailService taskDetailService;
    @Resource
    private PondHealthParser pondHealthParser;

    @PostMapping("/create")
    @Operation(summary = "创建鱼池健康")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:create')")
    public CommonResult<Long> createZkPondHealth(@Valid @RequestBody ZkPondHealthSaveReqVO createReqVO) {
        return success(zkPondHealthService.createZkPondHealth(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼池健康")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:update')")
    public CommonResult<Boolean> updateZkPondHealth(@Valid @RequestBody ZkPondHealthSaveReqVO updateReqVO) {
        zkPondHealthService.updateZkPondHealth(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼池健康")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:delete')")
    public CommonResult<Boolean> deleteZkPondHealth(@RequestParam("id") Long id) {
        zkPondHealthService.deleteZkPondHealth(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除鱼池健康")
                @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:delete')")
    public CommonResult<Boolean> deleteZkPondHealthList(@RequestParam("ids") List<Long> ids) {
        zkPondHealthService.deleteZkPondHealthListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼池健康")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:query')")
    public CommonResult<ZkPondHealthRespVO> getZkPondHealth(@RequestParam("id") Long id) {
        ZkPondHealthDO zkPondHealth = zkPondHealthService.getZkPondHealth(id);
        return success(BeanUtils.toBean(zkPondHealth, ZkPondHealthRespVO.class));
    }


    @GetMapping("/genPondHealthData")
    @Operation(summary = "请求大模型生成存塘健康评估数据")
    public CommonResult<List<ZkPondHealthDO>> genPondHealthData() {
        // 1. 获取所有存塘信息
        List<ZkPondDO> pondList = zkPondService.selectList();
        List<ZkPondHealthDO> healthDOS = new ArrayList<>();
        if(EasyListUtil.isEmpty(pondList)) return success(healthDOS).setMsg("暂无存塘");
        List<String> typeList = List.of(TaskDetailCheckTypeEnum.DAY_AREA1_FISH.getValue(), TaskDetailCheckTypeEnum.REGULAR_AREA1_1.getValue());
        for(ZkPondDO pondDO : pondList) {
            try {
                // 2. 获取 设备统计 信息
                List<ZkDeviceRecordDO> recordDOS = zkDeviceRecordService.selectRecentByQuery(pondDO.getId());
                // 3. 获取 任务信息 日巡检_养殖区_鱼 11 、 定期巡检_养殖区_鱼 31
                List<ZkTaskDO> taskDOS = taskDetailService.getByTypesAndPondId(typeList, pondDO.getId());
                // 4. 将 设备统计、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充健康评估数据库
                ZkPondHealthDO zkPondHealthDO = pondHealthParser.exeParser(pondDO, recordDOS, taskDOS);
                log.warn("将 设备统计、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充健康评估数据库 ： {}", JSON.toJSONString(zkPondHealthDO));
                healthDOS.add(zkPondHealthDO);
                Thread.sleep(3*1000);
            } catch (InterruptedException e) {
                String subject = String.format("【将 设备统计、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充健康评估数据库-异常】,middle,异常名:%s", e.getMessage());
                String content = String.format("具体数据：%s,具体异常：%s", JSON.toJSONString(pondDO), ExceptionUtil.getStackTrace(e));
                EmailUtil.send(subject, content);
                log.error(subject, content);
            }
        }

        return success(healthDOS);
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼池健康分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:query')")
    public CommonResult<PageResult<ZkPondHealthRespVO>> getZkPondHealthPage(@Valid ZkPondHealthPageReqVO pageReqVO) {
        PageResult<ZkPondHealthDO> pageResult = zkPondHealthService.getZkPondHealthPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkPondHealthRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼池健康 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkPondHealthExcel(@Valid ZkPondHealthPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkPondHealthDO> list = zkPondHealthService.getZkPondHealthPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "鱼池健康.xls", "数据", ZkPondHealthRespVO.class,
                        BeanUtils.toBean(list, ZkPondHealthRespVO.class));
    }

    @GetMapping("/health-status-distribution")
    @Operation(summary = "获得健康状态分布")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:query')")
    public CommonResult<Map<String, Object>> getHealthStatusDistribution() {
        return success(zkPondHealthService.getHealthStatusDistribution());
    }

    @GetMapping("/latest-pond-health-list")
    @Operation(summary = "获得各鱼池最新健康评分")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-health:query')")
    public CommonResult<List<ZkPondHealthRespVO>> getLatestPondHealthList() {

        // Long pondId = 331L;
        // ZkPondDO zkPond = zkPondService.getZkPond(pondId);
        // List<ZkDeviceRecordDO> zkDeviceRecords = zkDeviceRecordService.selectRecentByQuery(307L);
        // List<ZkTaskDO> taskList = taskDetailService.getByTypesAndPondId(List.of("11", "31"), 329L);
        // pondHealthParser.exeParser(zkPond, zkDeviceRecords, taskList);

        List<ZkPondHealthDO> list = zkPondHealthService.getLatestPondHealthList();
        return success(BeanUtils.toBean(list, ZkPondHealthRespVO.class));
    }

}