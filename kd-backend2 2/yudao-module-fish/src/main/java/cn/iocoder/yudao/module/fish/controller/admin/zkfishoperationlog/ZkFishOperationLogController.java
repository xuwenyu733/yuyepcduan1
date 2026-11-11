package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.ZkBiologyCapitalController;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.schdule.StatisticsComponent;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.schdule.StatisticsParam;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to.FishOperationLogTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to.ListToAnalysisTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to.OperationLogEnhancer;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.ZkPondFishDetailController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.FishStatisticsTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.ZkSpecsLogController;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.enums.LogRecordConstants;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
// import cn.iocoder.yudao.module.fish.convert.zkfishoperationlog.ZkFishOperationLogConvert;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;

@Tag(name = "管理后台 - 鱼苗统计")
@RestController
@RequestMapping("/fish/zk-fish-operation-log")
@Validated
@Slf4j
public class ZkFishOperationLogController {

    @Autowired
    @Lazy
    private ZkFishOperationLogService zkFishOperationLogService;
    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;
    @Resource
    private OperationLogEnhancer operationLogEnhancer;
    @Resource
    private ListToAnalysisTool listToAnalysisTool;
    @Resource
    private ZkSpecsLogController zkSpecsLogController;
    @Resource
    private ZkBiologyCapitalController zkBiologyCapitalController;
    @Resource
    private StatisticsComponent statisticsComponent;
    @Autowired
    @Lazy
    private ZkPondFishDetailController zkPondFishDetailController;

    @Autowired
    @Lazy
    private ZkPondService zkPondService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼苗统计")
    public CommonResult<Long> createZkFishOperationLog(@Valid @RequestBody ZkFishOperationLogCreateReqVO createReqVO) {
        return success(zkFishOperationLogService.createZkFishOperationLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼苗统计")
    public CommonResult<Boolean> updateZkFishOperationLog(@Valid @RequestBody ZkFishOperationLogUpdateReqVO updateReqVO) {
        zkFishOperationLogService.updateZkFishOperationLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼苗统计")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkFishOperationLog(@RequestParam("id") Long id) {
        zkFishOperationLogService.deleteZkFishOperationLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼苗统计")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkFishOperationLogRespVO> getZkFishOperationLog(@RequestParam("id") Long id) {
        ZkFishOperationLogDO zkFishOperationLog = zkFishOperationLogService.getZkFishOperationLog(id);
        return success(BeanUtils.toBean(zkFishOperationLog, ZkFishOperationLogRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得鱼苗统计列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkFishOperationLogRespVO>> getZkFishOperationLogList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkFishOperationLogDO> list = zkFishOperationLogService.getZkFishOperationLogList(ids);
        return success(BeanUtils.toBean(list, ZkFishOperationLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼苗统计分页")
    public CommonResult<PageResult<ZkFishOperationLogRespVO>> getZkFishOperationLogPage(@Valid ZkFishOperationLogPageReqVO pageVO) {
        // 1. 重新刷新统计值（失败不阻断分页查询，避免首页「系统异常」）
        try {
            genLog();
        } catch (Exception e) {
            log.warn("【鱼苗统计】刷新 genLog 失败，继续返回分页数据", e);
        }
        // 2. 获取分页信息
        PageResult<ZkFishOperationLogDO> pageResult = zkFishOperationLogService.getZkFishOperationLogPage(pageVO);
        // 3. 获取批次编号列表
        List<String> batchNumberList = getBatchNumberList();
        // 4. 弥补 空数据
        enhancerOperationLogList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkFishOperationLogRespVO.class))
                .put("batchNumberList", batchNumberList);
    }

    private void enhancerOperationLogList(List<ZkFishOperationLogDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        List<ZkFishOperationLogDO> zkFishOperationLogDOS = list.stream().filter(obj -> StringUtil.isEmpty(obj.getPondDetailNum()) && obj.getPondId() != null).toList();
        if(EasyListUtil.isEmpty(zkFishOperationLogDOS)) return;
        Set<Long> pondIds = zkFishOperationLogDOS.stream().map(ZkFishOperationLogDO::getPondId).collect(Collectors.toSet());
        List<ZkPondDO> zkPondDOS = zkPondService.selectByPondIds(pondIds);
        if(EasyListUtil.isEmpty(zkPondDOS)) return;
        CommonUtil.origins2Targets3(zkPondDOS, "id", List.of("pondDetailNum", "userId", "deptId", "tenantId"), zkFishOperationLogDOS, "pondId",
                List.of("pondDetailNum", "userId", "deptId", "tenantId"));
        zkFishOperationLogService.customUpdateBatch(zkFishOperationLogDOS);
    }


    public List<String> getBatchNumberList() {
        List<String> list = zkFishOperationLogService.getBatchNumberList();
        return list;
    }

    /**
     * 生成 死鱼统计值
     */
    public synchronized List<ZkFishOperationLogDO> genLog(String dayStr, Boolean isFirst) {
        if(StringUtil.isEmpty(dayStr)) {
            dayStr = EasyDateUtil.format(new Date(), "yyyy-MM-dd");
        }
        // 0. 统计生物性资产
        zkBiologyCapitalController.genLog();
        // 1. 规格记录(预估饵料、打样) 统计
        zkSpecsLogController.sync_newData();
        // 2. 开始 鱼死亡、累计投喂、累计死亡 统计
        List<ZkFishOperationLogDO> res = genLog_realLogic(dayStr, isFirst);
        return res;
    }

    /**
     * 统计 鱼操作 信息
     * @return
     */
    public synchronized List<ZkFishOperationLogDO> genLog() {
        // 0. 统计生物性资产
        zkBiologyCapitalController.genLog();
        // 1. 规格记录 统计
        zkSpecsLogController.sync_newData();
        // 2. 开始 鱼死亡、累计投喂、累计死亡 统计
        List<ZkPondFishDetailDO> list = statisticsComponent.startStatistics(new StatisticsParam());
        List<ZkFishOperationLogDO> res = genLog_realLogicByList(list);
        log.warn("【{}】middle end, 生成的结束参数是：{}", LogRecordConstants.GEN_FISH_OPERATION_LOG, JSON.toJSONString(res));
        // 3. 统计 7日或3日 死亡量
        zkPondFishDetailController.genLog();
        return res;
    }
    public List<ZkFishOperationLogDO> genLog_realLogic(String dayStr, Boolean isFirst) {
        // 1. 获取 死亡鱼苗信息
        List<ZkPondFishDetailDO> list = zkPondFishDetailService.getRecentDayList(PondFishDetailAddTypeEnum.DEAD.getAddType(), dayStr);
        List<ZkFishOperationLogDO> res = genLog_realLogicByList(list);
        return res;
    }
    public List<ZkFishOperationLogDO> genLog_realLogicByList(List<ZkPondFishDetailDO> list) {
        String handleKey = TokenUtil.selectKey();
        // 1. 获取 死亡鱼苗信息
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        if(EasyListUtil.isNotEmpty(list)) {
            // 1.1 得到 统计 记录
            res = listToAnalysisTool.execute(list);
            // 1.2 获取累计死亡量
            operationLogEnhancer.enhancer_accumulate(res);
            // 1.3. 获取现有统计 ，计算出来需要新增的统计值
            // operationLogEnhancer.enhancer(res); // 在 2.2enhancer_accumulate步骤已经统计出来了
            // 1.4. 添加到数据库中
            zkFishOperationLogService.customSaveBatch(res);
            log.warn("【{}-{}】middle 1,得到 统计 记录:{},成功入库", LogRecordConstants.GEN_FISH_OPERATION_LOG, handleKey, res);
        }
        // 2. 没有死亡的鱼池添加默认统计值
        List<ZkFishOperationLogDO> emptyLogs = zkPondFishDetailService.genByEmptyOperationLog();
        if(EasyListUtil.isNotEmpty(emptyLogs)) {
            zkFishOperationLogService.customSaveBatch(emptyLogs);
            log.warn("【{}-{}】middle 2,没有死亡的鱼池添加默认统计值:{},成功入库", LogRecordConstants.GEN_FISH_OPERATION_LOG, handleKey, emptyLogs);
            res.addAll(emptyLogs);
        }

        // 5. 同步其他数据 zk_fish_operation_log
        List<ZkFishOperationLogDO> logList = zkFishOperationLogService.getByLogType(FishOperationLogTypeEnum.POND.getLogType());
        Integer fishOperationLogUpdate = listToAnalysisTool.syncNewData(logList);
        log.warn("【{}-{}】middle 5,同步其他数据:{},成功入库", LogRecordConstants.GEN_FISH_OPERATION_LOG, handleKey, fishOperationLogUpdate);
        // 6. 已经统计的数据不要重复统计
        if(EasyListUtil.isNotEmpty(list)) {
            list.forEach(obj -> obj.build_statisticsType(FishStatisticsTypeEnum.HAS_STATISTICS.getStatisticsType()));
            Integer hasStatistics = zkPondFishDetailService.customBatchUpdate(list);
            log.warn("【{}-{}】middle 6,更新鱼操作记录:{},具体明细信息:{}，成功入库", LogRecordConstants.GEN_FISH_OPERATION_LOG, handleKey, hasStatistics, JSON.toJSONString(list));
        }
        return res;
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼苗统计 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkFishOperationLogExcel(@Valid ZkFishOperationLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkFishOperationLogDO> list = zkFishOperationLogService.getZkFishOperationLogList(exportReqVO);
        // 导出 Excel
        List<ZkFishOperationLogExcelVO> datas = BeanUtils.toBean(list, ZkFishOperationLogExcelVO.class);
        ExcelUtils.write(response, "鱼苗统计.xls", "数据", ZkFishOperationLogExcelVO.class, datas);
    }

}
