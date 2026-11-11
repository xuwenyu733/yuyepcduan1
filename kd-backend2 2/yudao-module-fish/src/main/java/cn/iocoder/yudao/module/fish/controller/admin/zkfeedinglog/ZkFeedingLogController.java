package cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.erp.controller.admin.stock.ErpStockRecordController;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.dataobject.stock.ErpStockDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockRecordService;
import cn.iocoder.yudao.module.erp.service.stock.ErpStockService;
import cn.iocoder.yudao.module.erp.service.stock.bo.ErpStockRecordCreateReqBO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.tools.FishSystemLogTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.ZkFishOperationLogController;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.service.zkbiologycapital.ZkBiologyCapitalService;
import cn.iocoder.yudao.module.fish.service.zkdatarevise.ZkDataReviseService;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
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
import java.math.BigDecimal;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkfeedinglog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
// import cn.iocoder.yudao.module.fish.convert.zkfeedinglog.ZkFeedingLogConvert;
import cn.iocoder.yudao.module.fish.service.zkfeedinglog.ZkFeedingLogService;

@Tag(name = "管理后台 - 投喂记录")
@RestController
@RequestMapping("/fish/zk-feeding-log")
@Validated
@Slf4j
public class ZkFeedingLogController {

    @Resource
    private ZkFeedingLogService zkFeedingLogService;
    @Resource
    private ZkBiologyCapitalService zkBiologyCapitalService;
    @Resource
    private ZkDataReviseService zkDataReviseService;
    @Autowired
    @Lazy
    private ZkFishOperationLogController zkFishOperationLogController;
    @Resource
    private ZkFishOperationLogService zkFishOperationLogService;
    @Autowired
    @Lazy
    private ZkPondFishService zkPondFishService;

    @Autowired
    @Lazy
    private ZkPondService zkPondService;

    @Autowired
    @Lazy
    private ErpProductService erpProductService;

    @Autowired
    @Lazy
    private ErpStockRecordController erpStockRecordController;

    @Autowired
    @Lazy
    private ErpStockRecordService erpStockRecordService;

    @Autowired
    @Lazy
    private ErpStockService erpStockService;

    @PostMapping("/create")
    @Operation(summary = "创建投喂记录")
    public CommonResult<Long> createZkFeedingLog(@Valid @RequestBody ZkFeedingLogCreateReqVO createReqVO) {
        judgeAddStock(createReqVO);
        // 0. 投放之前触发一次计算
        zkFishOperationLogController.genLog();
        // 1. 获取前5天平均值
        BigDecimal avg = zkFeedingLogService.getAvg(createReqVO.getPondId());
        createReqVO.build_differenceBefore(avg);
        ZkFeedingLogDO zkFeedingLog = zkFeedingLogService.createZkFeedingLog(createReqVO);
        // 2. 同步数据到 成本统计 zk_biology_capital
        sync_biologyCapital(zkFeedingLog);
        // 5. 操作完成 以后 同步 统计日志
        zkFishOperationLogController.genLog();
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(zkFeedingLog.getPondDetailNum(), "创建投喂记录", JSON.toJSONString(zkFeedingLog), "", "", "")
                .setRemark(""));

        return success(zkFeedingLog.getId());
    }

    public void judgeAddStock(ZkFeedingLogCreateReqVO createReqVO) {
        // 1. 获取 库存
        ErpStockDO stock = erpStockService.getStock(createReqVO.getProductId(), createReqVO.getWarehouseId());
        if(stock == null) throw new CommonException(403, "仓库id和商品id错误");
        BigDecimal subtract = EasyBigDecimalUtil.subtract(stock.getCount(), createReqVO.getFeedCount());
        if(EasyBigDecimalUtil.compare0(subtract, "LT")) {
            throw new CommonException(403, String.format("仓库(有%s，需要%s)库存不够了", stock.getCount(), createReqVO.getFeedCount()));
        }
    }

    public void sync_biologyCapital(ZkFeedingLogDO zkFeedingLog) {
        // 0. 添加出库
        addProductStock(zkFeedingLog);
        // 1. 成本为空 则 无需同步
        if(!EasyBigDecimalUtil.gt0(zkFeedingLog.getFeedingCost())) {
            return;
        }
        // 2. 执行同步
        ZkBiologyCapitalDO capitalDO = zkBiologyCapitalService.feedingCostUpdate(zkFeedingLog);
        log.warn("【添加投喂记录-{}】middle,同步数据到zk_biology_capital,ZkBiologyCapitalDO:{}，成功入库", TokenUtil.userInfo(), JSON.toJSONString(capitalDO));
        // 3. zk_biology_capital 统计的是累计投喂量，而 zk_fish_operation_log 是统计每天的指标量(颗粒度更细)， 这里同步 每天的投喂量 到zk_fish_operation_log
        ZkFishOperationLogDO logDO = zkFishOperationLogService.sync_currFeedCount(zkFeedingLog);
        log.warn("【添加投喂记录-{}】middle,同步数据到zk_fish_operation_log,ZkFishOperationLogDO:{}，成功入库", TokenUtil.userInfo(), JSON.toJSONString(logDO));
        // 4. 将指标 值 统计 到 zk_pond_fish 中
        ZkPondFishDO byPondId = zkPondFishService.getByPondId(zkFeedingLog.getPondId());
        if(byPondId == null) return;
        byPondId.build_accumulatedFeedingAmount(zkFeedingLog.getFeedCount());
        zkPondFishService.customUpdate(byPondId);
    }

    public void addProductStock(ZkFeedingLogDO zkFeedingLog) {
        try {
            ErpStockRecordCreateReqBO createReqBO = new ErpStockRecordCreateReqBO(
                    zkFeedingLog.getProductId(), zkFeedingLog.getWarehouseId(),
                    EasyBigDecimalUtil.subtract(new BigDecimal(0), zkFeedingLog.getFeedCount()), 20, 0L, 0L, "手动出库(投喂)"
            ).setTenantId(zkFeedingLog.getTenantId()).setDeptId(zkFeedingLog.getDeptId()).setUserId(zkFeedingLog.getUserId()).setBusId(zkFeedingLog.getId());
            erpStockRecordController.createStockRecord(createReqBO);
        } catch (Exception e) {
            String sub = String.format("【投喂扣减库存】e:%s", e.getMessage());
            String error_msg = String.format("zkFeedingLog:%s, error: %s", JSON.toJSONString(zkFeedingLog), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, error_msg);
            log.error(sub, error_msg);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "更新投喂记录")
    public CommonResult<Boolean> updateZkFeedingLog(@Valid @RequestBody ZkFeedingLogUpdateReqVO updateReqVO) {
        judgeUpdateStock(updateReqVO);
        // 1. 修改记录
        List<ZkFeedingLogDO> updateLog = zkFeedingLogService.updateZkFeedingLog(updateReqVO);
        ZkFeedingLogDO before = updateLog.get(0);
        // 2. 同步数据到修正记录
        zkDataReviseService.customInsertFeedingLog(updateReqVO);
        // 3. 操作完成 以后 同步 统计日志
        zkFishOperationLogController.genLog();
        // 4. 同步库存
        removeAndUpdate(updateLog.get(1));
        // 5. 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(before.getPondDetailNum(), "创建投喂记录", JSON.toJSONString(before), JSON.toJSONString(updateLog.get(1)), "", "")
                .setRemark(""));

        return success(true);
    }

    public void judgeUpdateStock(ZkFeedingLogUpdateReqVO createReqVO) {
        // 1. 获取 库存
        ZkFeedingLogDO oldFeed = zkFeedingLogService.getById(createReqVO.getId());
        // 2. 计算差值 新投喂 - 原来投喂 = 新增 > 0, 此时需要判断库存
        BigDecimal updateAdd = EasyBigDecimalUtil.subtract(createReqVO.getFeedCount(), oldFeed.getFeedCount());
        if(EasyBigDecimalUtil.compare0(updateAdd, "LT")) {
            return; // 减少投喂不用管
        }
        ErpStockDO stock = erpStockService.getStock(createReqVO.getProductId(), createReqVO.getWarehouseId());
        if(stock == null) throw new CommonException(403, "仓库id和商品id错误");
        BigDecimal subtract = EasyBigDecimalUtil.subtract(stock.getCount(), updateAdd);
        if(EasyBigDecimalUtil.compare0(subtract, "LT")) {
            throw new CommonException(403, String.format("仓库(有%s，除了之前扣除的还需要%s)库存不够了", stock.getCount(), updateAdd));
        }
    }

    public void removeAndUpdate(ZkFeedingLogDO updateReqVO) {
        // 1. 删除旧的，新增新的
        erpStockRecordService.removeByBizTypeAndBusId(20, updateReqVO.getId());
        // 2. 重新新增
        addProductStock(updateReqVO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除投喂记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkFeedingLog(@RequestParam("id") Long id) {
        // 1. 执行删除操作
        ZkFeedingLogDO before = zkFeedingLogService.deleteZkFeedingLog(id);
        // 2. 执行 数据同步 zk_biology_capital 总共的、zk_fish_operation_log 每天的
        ZkFeedingLogDO curr = new ZkFeedingLogDO().setFeedCount(new BigDecimal(0)).setFeedingPrice(before.getFeedingPrice())
                .setFeedTime(before.getFeedTime());
        zkFeedingLogService.sync_biologyCapital(curr, before);
        // 3. 执行数据同步 到库存记录
        erpStockRecordService.removeByBizTypeAndBusId(20, id);

        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(before.getPondDetailNum(), "创建投喂记录", JSON.toJSONString(before), "", "", "")
                .setRemark(""));

        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得投喂记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkFeedingLogRespVO> getZkFeedingLog(@RequestParam("id") Long id) {
        ZkFeedingLogDO zkFeedingLog = zkFeedingLogService.getZkFeedingLog(id);
        return success(BeanUtils.toBean(zkFeedingLog, ZkFeedingLogRespVO.class));
    }

    @GetMapping("/various-fry-stages-cost")
    @Operation(summary = "获得各鱼苗期成本")
    public CommonResult<List<VariousFryStagesCostRespVO>> getVariousFryStagesCost() {
        return success(zkFeedingLogService.getVariousFryStagesCost());
    }

    @GetMapping("/list")
    @Operation(summary = "获得投喂记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkFeedingLogRespVO>> getZkFeedingLogList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkFeedingLogDO> list = zkFeedingLogService.getZkFeedingLogList(ids);
        enhancerList(list);
        return success(BeanUtils.toBean(list, ZkFeedingLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得投喂记录分页")
    public CommonResult<PageResult<ZkFeedingLogRespVO>> getZkFeedingLogPage(@Valid ZkFeedingLogPageReqVO pageVO) {
        // 1. 获取记录
        PageResult<ZkFeedingLogDO> pageResult = zkFeedingLogService.getZkFeedingLogPage(pageVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkFeedingLogRespVO.class));
    }

    public void enhancerList(List<ZkFeedingLogDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        // 1. 找 对应数据
        Set<Long> pondIdSet = tList.stream().filter(obj -> {
            return StringUtil.isEmptyWith0(obj.getPondDetailNum()) ||
                    StringUtil.isEmptyWith0(obj.getPondSystemName());
        }).map(ZkFeedingLogDO::getPondId).collect(Collectors.toSet());
        List<ZkPondDO> oList = zkPondService.getZkPondList(pondIdSet);
        if(EasyListUtil.isEmpty(oList)) return;
        // 2. 赋值
        List<ZkFeedingLogDO> finalNeedUpdate = new ArrayList<>();
        CommonUtil.origins2Targets4(oList, ZkPondDO::getId, tList, ZkFeedingLogDO::getPondId, (ZkPondDO o, ZkFeedingLogDO t) -> {
            t.setPondDetailNum(o.getPondDetailNum()).setPondSystemName(t.getPondSystemName());
            finalNeedUpdate.add(t);
        });
        if(EasyListUtil.isNotEmpty(finalNeedUpdate)) zkFeedingLogService.customUpdateBatch(finalNeedUpdate);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出投喂记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkFeedingLogExcel(@Valid ZkFeedingLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkFeedingLogDO> list = zkFeedingLogService.getZkFeedingLogList(exportReqVO);
        // 导出 Excel
        List<ZkFeedingLogExcelVO> datas = BeanUtils.toBean(list, ZkFeedingLogExcelVO.class);
        ExcelUtils.write(response, "投喂记录.xls", "数据", ZkFeedingLogExcelVO.class, datas);
    }

}
