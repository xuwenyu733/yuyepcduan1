package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail;

import cn.iocoder.yudao.framework.common.base_api.fish.ZkPondFishDetailControllerApi;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.tools.FishSystemLogTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.ZkFishOperationLogController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.schedule.SyncPondFishTypeSchedule;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.FishStatisticsTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.enums.LogRecordConstants;
import cn.iocoder.yudao.module.fish.service.custom.OutOrderProductService;
import cn.iocoder.yudao.module.fish.service.custom.ProductService;
import cn.iocoder.yudao.module.infra.dal.mysql.dict.CommonDictDataMapper;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.FishDetailToBatchTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.OperationForFishTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import cn.iocoder.yudao.module.fish.service.dictdata.DictDataService;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;
import cn.iocoder.yudao.module.fish.service.zkdatarevise.ZkDataReviseService;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import cn.iocoder.yudao.module.fish.service.zkspecslog.ZkSpecsLogService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
// import cn.iocoder.yudao.module.fish.convert.zkpondfishdetail.ZkPondFishDetailConvert;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;

@Tag(name = "管理后台 - 存塘鱼苗明细")
@RestController
@RequestMapping("/fish/zk-pond-fish-detail")
@Validated
@Slf4j
public class ZkPondFishDetailController implements ZkPondFishDetailControllerApi {

    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;

    @Resource
    private ZkPondFishService zkPondFishService;

    @Resource
    private CommonDictDataMapper commonDictDataMapper;

    @Resource
    private ZkPondService zkPondService;
    @Resource
    private DictDataService dictDataService;
    @Resource
    private OutOrderProductService outOrderProductService;
    @Resource
    private ProductService productService;
    @Resource
    private FishDetailToBatchTool fishDetailToBatchTool;
    @Autowired
    @Lazy
    private ZkFishOperationLogController zkFishOperationLogController;
    @Resource
    private ZkDataReviseService zkDataReviseService;
    @Autowired
    @Lazy
    private ZkFishOperationLogService zkFishOperationLogService;
    @Autowired
    @Lazy
    private ZkSpecsLogService zkSpecsLogService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private SyncPondFishTypeSchedule syncPondFishTypeSchedule;

    @PostMapping("/create")
    @Operation(summary = "创建鱼苗操作明细zk_pond_fish_detail")
    public CommonResult<Long> createZkPondFishDetail(@Valid @RequestBody ZkPondFishDetailCreateReqVO createReqVO) {
        // 1. 添加数据
        ZkPondFishDetailDO zkPondFishDetail = zkPondFishDetailService.createZkPondFishDetail(createReqVO);
        CommonUtil.copyPropertiesIgnoreNull(createReqVO, zkPondFishDetail);
        zkPondFishDetail.setFishName(createReqVO.getFishName());
        // 2. 同步数据到 存塘鱼苗统计
        ZkPondFishDO zkPondFishDO = syncData_ZkPondFishDO( zkPondFishDetail);
        // 3. 同步 数据到明细
        zkPondFishDetailService.customUpdate(zkPondFishDetail.build_zkPondFishId(zkPondFishDO.getId()));
        // 4. 同步批次数据
        fishDetailToBatchTool.syncDataToBatchNumber(zkPondFishDetail);
        // 5. 操作完成 以后 同步 统计日志
        zkFishOperationLogController.genLog();
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(createReqVO.getPondDetailNum(), "创建鱼苗操作明细zk_pond_fish_detail", JSON.toJSONString(zkPondFishDetail), null, null, null)
                .setRemark(zkPondFishDetail.selectOperationName()));
        syncStatic();
        return success(zkPondFishDetail.getId());
    }

    /**
     * 开启异步 同步处理 投放、销售、分池、死亡的 数据统计
     */
    public void syncStatic() {
        CompletableFuture.runAsync(() -> syncPondFishTypeSchedule.scheduleMethod(), executor);
    }

    // 添加投喂鱼苗明细，同时同步数据到 存塘鱼苗统计
    private ZkPondFishDO syncData_ZkPondFishDO(ZkPondFishDetailDO zkPondFishDetail) {
        // 1. 添加 zk_pond_fish 数据
        ZkPondFishDO zkPondFishDO = zkPondFishService.getByPondId(zkPondFishDetail.getPondId());
        if(zkPondFishDO == null) {
            // 为空则需要添加
            zkPondFishDO = OperationForFishTool.parseAdd(zkPondFishDetail);
            zkPondFishService.customInsert(zkPondFishDO.build_detailJson(zkPondFishDetail).build_batchNumber(zkPondFishDetail.getBatchNumber()));
        } else {
            // 不为空，直接更新就好了
            OperationForFishTool.parseUpdate(zkPondFishDetail, zkPondFishDO);
            zkPondFishService.customUpdate(zkPondFishDO.update_detailJson(zkPondFishDetail).build_batchNumber(zkPondFishDetail.getBatchNumber()));
        }
        // 2. 更新 zk_pond 数据
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDO.getPondId(), zkPondFishDO.getFishNum(),
                zkPondFishDetail.getBatchNumber()).build_fishTypes(zkPondFishDO.getFishTypes())
                .build_fishSpec(zkPondFishDO.getCurrFishSpec()));
        // 3. 如果是分池， 目标塘需要更新数据
        if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), zkPondFishDetail.getAddType())) {
            syncData_to_targetPond(zkPondFishDetail); // 如果是分池，则目标塘需要添加 鱼苗数
            List<ZkSpecsLogDO> logDOs = zkSpecsLogService.updateLogBySpecs(zkPondFishDetail);
            log.warn("【执行分池操作-{}】middle,分池规格同步到zk_specs_log,更新数据:{}，成功入库", TokenUtil.userInfo(), JSON.toJSONString(logDOs));
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), zkPondFishDetail.getAddType())) { // 投放需要 减少当前批次鱼苗数量
            // 1:投放; 同步鱼苗数据到 erp_purchase_in_items(通过 zk_pond_fish_detail.batch_number=erp_purchase_in_items.relation_order_no)
            outOrderProductService.update_remainingInventory(zkPondFishDetail.getBatchNumber(), zkPondFishDetail.getRemainingInventory(), zkPondFishDetail.getFishNum());
        } else { // -1:销售;-2:死亡; zk_pond_fish_detail_add_type 需要更新pl_product库存数量
            productService.updateStoreByProductNo(zkPondFishDetail.getProductNo(), zkPondFishDetail.getFishNum());
            if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), zkPondFishDetail.getAddType())) { // 死亡
            } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), zkPondFishDetail.getAddType())) { // 销售
            }
        }

        return zkPondFishDO;
    }

    // 分池，同步数据到 目标存塘鱼苗统计
    public ZkPondFishDO syncData_to_targetPond(ZkPondFishDetailDO zkPondFishDetail) {
        // 0. 判断是否是自己给自己分池,如果是就不需要后面的逻辑了
        ZkPondFishDO safeDO = syncData_to_targetPond_safe(zkPondFishDetail);
        if(safeDO != null) return safeDO;
        // 1. 添加 zk_pond_fish 数据
        ZkPondFishDO zkPondFishDO = zkPondFishService.getByPondId(zkPondFishDetail.getTargetPondId());
        if(zkPondFishDO == null) {
            // 为空则需要添加, 因为为空只可能是 直接更新数据，前台计算了基本信息
            zkPondFishDO = OperationForFishTool.parseSplit(zkPondFishDetail);
            zkPondFishService.customInsert(zkPondFishDO.build_for_targetDetailJson(zkPondFishDetail).build_batchNumber(null)); // 获取 存塘鱼统计数据
        } else {
            // 不为空，则需要根据 投放类型 来更新鱼数量
            OperationForFishTool.parseUpdateForSplit(zkPondFishDetail, zkPondFishDO);
            zkPondFishService.customUpdate(zkPondFishDO.update_detailJson(zkPondFishDetail).build_batchNumber(null));
        }
        // 2. 更新 zk_pond 数据
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDetail.getTargetPondId(), zkPondFishDO.getFishNum(), zkPondFishDetail.getBatchNumber())
                .setFishSpec(zkPondFishDO.getCurrFishSpec()));
        return zkPondFishDO;
    }

    // 新增分池类型，就是自己给自己分池，实际就是 直接更新数据，同步数据到 目标存塘鱼苗统计
    public ZkPondFishDO syncData_to_targetPond_safe(ZkPondFishDetailDO zkPondFishDetail) {
        if(!Objects.equals(zkPondFishDetail.getPondId(), zkPondFishDetail.getTargetPondId())) return null;
        // 1. 添加 zk_pond_fish 数据
        ZkPondFishDO zkPondFishDO = zkPondFishService.getByPondId(zkPondFishDetail.getTargetPondId());
        if(zkPondFishDO == null) {
            // 为空则需要添加, 因为为空只可能是 直接更新数据，前台计算了基本信息
            zkPondFishDO = OperationForFishTool.parseSplit4Safe(zkPondFishDetail);
            zkPondFishService.customInsert(zkPondFishDO.build_for_targetDetailJson(zkPondFishDetail).build_batchNumber(null)); // 获取 存塘鱼统计数据
        } else {
            // 不为空，则需要根据 投放类型 来更新鱼数量
            OperationForFishTool.parseUpdateForSplit4Safe(zkPondFishDetail, zkPondFishDO);
            zkPondFishService.customUpdate(zkPondFishDO.update_detailJson(zkPondFishDetail).build_batchNumber(null));
        }
        // 2. 更新 zk_pond 数据
        zkPondService.customUpdateById(new ZkPondDO(zkPondFishDetail.getTargetPondId(), zkPondFishDO.getFishNum(), zkPondFishDetail.getBatchNumber())
                .setFishSpec(zkPondFishDO.getCurrFishSpec()));
        return zkPondFishDO;
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼苗操作明细zk_pond_fish_detail")
    public CommonResult<Boolean> updateZkPondFishDetail(@Valid @RequestBody ZkPondFishDetailUpdateReqVO updateReqVO) {
        // 0. 获取原值
        ZkPondFishDetailDO origin = zkPondFishDetailService.getById(updateReqVO.getId());
        // 1. 修改 投放、分池、死亡、销售 记录，需要将 zk_pond_fish_detail.statisticsType 改为 未统计，方便 统计得到zk_fish_operation_log
        // zkPondFishDetailService.updateZkPondFishDetail(updateReqVO);
        ZkPondFishDetailDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondFishDetailDO.class);
        Long differenceData = origin.getFishNum() - updateObj.getFishNum();
        updateObj.setDifferenceData(differenceData).setStatisticsType(FishStatisticsTypeEnum.NOT_STATISTICS.getStatisticsType());
        updateFishDetail(updateObj.build_addType(updateReqVO.getAddType()));
        // 2. 同步到修正记录
        zkDataReviseService.customInsert(updateReqVO);
        // 3. 操作完成 以后 同步 统计日志
        zkFishOperationLogController.genLog();
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(updateReqVO.getPondDetailNum(), "更新鱼苗操作明细zk_pond_fish_detail", JSON.toJSONString(origin), JSON.toJSONString(updateObj), null, null)
                .setRemark(updateObj.selectOperationName()));
        syncStatic();

        return success(true);
    }

    public CommonResult<Boolean> updateFishDetail(ZkPondFishDetailDO zkPondFishDetailDO) {
        // 1. 直接更新数据
        zkPondFishDetailService.customUpdate(zkPondFishDetailDO);
        // 2. 同步数据 zk_pond_fish
        Long differenceData = zkPondFishDetailDO.getDifferenceData();
        if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), zkPondFishDetailDO.getAddType())) {
            zkPondFishService.updatePush(zkPondFishDetailDO, differenceData);
            // 2.1:投放; 同步鱼苗数据到 erp_purchase_in_items(通过 zk_pond_fish_detail.batch_number=erp_purchase_in_items.relation_order_no)
            outOrderProductService.update_remainingInventory(zkPondFishDetailDO.getBatchNumber(), null, -differenceData);
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), zkPondFishDetailDO.getAddType())) {
            zkPondFishService.updateDead(zkPondFishDetailDO, differenceData);
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), zkPondFishDetailDO.getAddType())) {
            zkPondFishService.updateSplit(zkPondFishDetailDO, differenceData);
            List<ZkSpecsLogDO> logDOs = zkSpecsLogService.updateLogBySpecs(zkPondFishDetailDO);
            log.warn("【执行分池操作-{}】middle,分池规格同步到zk_specs_log,更新数据:{}，成功入库", TokenUtil.userInfo(), JSON.toJSONString(logDOs));
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), zkPondFishDetailDO.getAddType())) {

        } else {

        }
        return success(true);
    }

    @Override
    public CommonResult<Long> createZkPondFishDetail(String param) {
        ZkPondFishDetailCreateReqVO createReqVO = JSON.parseObject(param, ZkPondFishDetailCreateReqVO.class);
        return createZkPondFishDetail(createReqVO);
    }

    @Override
    public CommonResult<Boolean> updateZkPondFishDetail(String param) {
        ZkPondFishDetailUpdateReqVO updateReqVO = JSON.parseObject(param, ZkPondFishDetailUpdateReqVO.class);
        return updateZkPondFishDetail(updateReqVO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼苗操作明细zk_pond_fish_detail")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPondFishDetail(@RequestParam("id") Long id) {
        // 1. 删除数据
        ZkPondFishDetailDO zkPondFishDetailDO = zkPondFishDetailService.deleteZkPondFishDetail(id);
        String currAddType = zkPondFishDetailDO.getAddType();
        // 2. 同步数据到 存塘鱼苗数据统计
        ZkPondFishDO zkPondFishDO = zkPondFishService.getByPondId(zkPondFishDetailDO.getPondId());
        zkPondFishDO.update_detailJson_forDel(zkPondFishDetailDO);
        zkPondFishService.customUpdate(zkPondFishDO);
        zkPondService.updateById(new ZkPondDO(zkPondFishDO.getPondId(), zkPondFishDO.getFishNum()));
        // 3. 如果是分池，则目标塘也需要还原数据
        if(StringUtils.equals(PondFishDetailAddTypeEnum.SPLIT.getAddType(), currAddType)) {
            ZkPondFishDO target_zkPondFishDO = zkPondFishService.getByPondId(zkPondFishDetailDO.getTargetPondId());
            target_zkPondFishDO.update_detailJson_forDel(zkPondFishDetailDO);
            zkPondFishService.customUpdate(target_zkPondFishDO);
            zkPondService.updateById(new ZkPondDO(target_zkPondFishDO.getPondId(), target_zkPondFishDO.getFishNum()));
        } else if(StringUtils.equals(PondFishDetailAddTypeEnum.ADD.getAddType(), currAddType)) { // 投放需要 减少当前批次鱼苗数量
            // 1:投放; 同步鱼苗数据到 erp_purchase_in_items(通过 zk_pond_fish_detail.batch_number=erp_purchase_in_items.relation_order_no)
            outOrderProductService.update_remainingInventory(zkPondFishDetailDO.getBatchNumber(), zkPondFishDetailDO.getRemainingInventory(), -zkPondFishDetailDO.getFishNum());
        } else { // -1:销售;-2:死亡; zk_pond_fish_detail_add_type 需要更新pl_product库存数量
            productService.updateStoreByProductNo(zkPondFishDetailDO.getProductNo(), -zkPondFishDetailDO.getFishNum());
            if(StringUtils.equals(PondFishDetailAddTypeEnum.DEAD.getAddType(), currAddType)) { // 死亡 需要 还原 zk_fish_operation_log 中统计值
                Map<String, List<ZkFishOperationLogDO>> updateRes = zkFishOperationLogService.handleFishDetailReverse(zkPondFishDetailDO);
                log.warn("【{}】middle , 删除数据后，因为是死亡，需要同步zk_fish_operation_log，同步结果：{}", LogRecordConstants.DELETE_POND_FISH_DETAIL, JSON.toJSONString(updateRes));
            } else if(StringUtils.equals(PondFishDetailAddTypeEnum.SALE.getAddType(), currAddType)) { // 销售
            }
        }
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(zkPondFishDetailDO.getPondDetailNum(), LogRecordConstants.DELETE_POND_FISH_DETAIL, JSON.toJSONString(zkPondFishDetailDO), null, null, null)
                .setRemark(zkPondFishDetailDO.selectOperationName()));
        syncStatic();
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得存塘鱼苗明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPondFishDetailRespVO> getZkPondFishDetail(@RequestParam("id") Long id) {
        ZkPondFishDetailDO zkPondFishDetail = zkPondFishDetailService.getZkPondFishDetail(id);
        enhancer_fishInfo(Arrays.asList(zkPondFishDetail));
        return success(BeanUtils.toBean(zkPondFishDetail, ZkPondFishDetailRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得存塘鱼苗明细列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPondFishDetailRespVO>> getZkPondFishDetailList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPondFishDetailDO> list = zkPondFishDetailService.getZkPondFishDetailList(ids);
        enhancer_fishInfo(list);
        return success(BeanUtils.toBean(list, ZkPondFishDetailRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得存塘鱼苗明细分页")
    public CommonResult<PageResult<ZkPondFishDetailRespVO>> getZkPondFishDetailPage(@Valid ZkPondFishDetailPageReqVO pageVO) {
        PageResult<ZkPondFishDetailDO> pageResult = zkPondFishDetailService.getZkPondFishDetailPage(pageVO);
        enhancer_fishInfo(pageResult.getList());
        if(StringUtils.equals(pageVO.getAddType(), PondFishDetailAddTypeEnum.ADD.getAddType())) {
            enhancer_sevenDayMortalityRate(pageResult.getList(), 7, PondFishDetailAddTypeEnum.ADD.getAddType());
        } else if(StringUtils.equals(pageVO.getAddType(), PondFishDetailAddTypeEnum.SPLIT.getAddType())) {
            enhancer_sevenDayMortalityRate(pageResult.getList(), 3, PondFishDetailAddTypeEnum.SPLIT.getAddType());
        }
        return success(BeanUtils.toBean(pageResult, ZkPondFishDetailRespVO.class));
    }

    /**
     * 统计 zk_pond_fish 的 投放7日死亡量和分池3日死亡量
     */
    public void genLog() {
        // 1. 获取所有pond
        List<ZkPondDO> zkPondDOS = zkPondService.selectList();
        if(EasyListUtil.isEmpty(zkPondDOS)) return;
        Set<Long> pondIdSet = zkPondDOS.stream().map(ZkPondDO::getId).collect(Collectors.toSet());
        List<ZkPondFishDO> zkPondFishDOS = zkPondFishService.selectByPondIdSet(pondIdSet);
        if(EasyListUtil.isEmpty(zkPondFishDOS)) return;
        // 2. 获取所有投放记录
        updatePushDeadInfo(pondIdSet, zkPondFishDOS);
        // 3. 获取所有 分池记录
        updateSplitDeadInfo(pondIdSet, zkPondFishDOS);
        // 4. 将 所有 分池的 投放7日死亡量和分池3日死亡量 获取后，执行更新操作
        zkPondFishService.customBatchUpdate(zkPondFishDOS);
    }
    private void updateSplitDeadInfo(Set<Long> pondIdSet, List<ZkPondFishDO> zkPondFishDOS) {
        List<ZkPondFishDetailDO> splitList = zkPondFishDetailService.getListByPondIds(pondIdSet, PondFishDetailAddTypeEnum.SPLIT.getAddType());
        List<ZkPondFishDetailDO> splitResList = null;
        if(EasyListUtil.isNotEmpty(splitList)) {
            splitResList = enhancer_sevenDayMortalityRate(splitList, 3, PondFishDetailAddTypeEnum.SPLIT.getAddType());
            System.out.println(String.format("updateSplitDeadInfo 1=================>splitResList:%s", JSON.toJSONString(splitResList)));
        }
        if(EasyListUtil.isNotEmpty(splitResList)) {
            Map<Long, List<ZkPondFishDetailDO>> collect = splitResList.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getTargetPondId));
            for(ZkPondFishDO pondFish : zkPondFishDOS) {
                List<ZkPondFishDetailDO> tempList = collect.get(pondFish.getPondId());
                if(EasyListUtil.isEmpty(tempList)) continue;
                Long sumFishDeadNum = 0L;
                for(ZkPondFishDetailDO item : tempList) {
                    sumFishDeadNum += item.getSplitAfterDead();
                }
                pondFish.build_splitDayMortalityRate(sumFishDeadNum);
            }
        }
    }
    private void updatePushDeadInfo(Set<Long> pondIdSet, List<ZkPondFishDO> zkPondFishDOS) {
        List<ZkPondFishDetailDO> pushList = zkPondFishDetailService.getListByPondIds(pondIdSet, PondFishDetailAddTypeEnum.ADD.getAddType());
        List<ZkPondFishDetailDO> pushResList = null;
        if(EasyListUtil.isNotEmpty(pushList)) {
            pushResList = enhancer_sevenDayMortalityRate(pushList, 7, PondFishDetailAddTypeEnum.ADD.getAddType());
        }
        if(EasyListUtil.isNotEmpty(pushResList)) {
            Map<Long, List<ZkPondFishDetailDO>> collect = pushResList.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getPondId));
            for(ZkPondFishDO pondFish : zkPondFishDOS) {
                List<ZkPondFishDetailDO> tempList = collect.get(pondFish.getPondId());
                if(EasyListUtil.isEmpty(tempList)) continue;
                Long sumFishDeadNum = 0L;
                for(ZkPondFishDetailDO item : tempList) {
                    sumFishDeadNum += item.getSevenDayMortalityRate();
                }
                pondFish.build_recentDayMortalityRate(sumFishDeadNum);
            }
        }
    }

    /**
     * 统计 分池3日、投放7日的 死亡信息
     * @param list
     * @param days
     * @param addType
     * @return
     */
    public List<ZkPondFishDetailDO> enhancer_sevenDayMortalityRate(List<ZkPondFishDetailDO> list, Integer days, String addType) {
        if(EasyListUtil.isEmpty(list)) return null;
        list = list.stream().filter(Objects::nonNull).toList();
        // 1. 投放记录 填充 7日内死亡信息
        Map<Long, ZkPondFishDetailDO> detailDOMap = zkPondFishDetailService.selectMapByDaysMortalityRate(list, days, addType);
        List<ZkPondFishDetailDO> needUpdate = new ArrayList<>();
        for(ZkPondFishDetailDO detailItem : list) {
            Long history_sevenDayMortalityRate = detailItem.getSevenDayMortalityRate();
            Long history_splitAfterDead = detailItem.getSplitAfterDead();

            MapUtils.findAndThen(detailDOMap, detailItem.getId(), currLog -> {
                if(StringUtils.equals(addType, PondFishDetailAddTypeEnum.ADD.getAddType())
                        && !Objects.equals(history_sevenDayMortalityRate, currLog.getFishNum())) {
                    detailItem.setSevenDayMortalityRate(currLog.getFishNum());
                    needUpdate.add(detailItem);
                } else if(StringUtils.equals(addType, PondFishDetailAddTypeEnum.SPLIT.getAddType()) &&
                        !Objects.equals(history_splitAfterDead, currLog.getFishNum())) {
                    detailItem.setSplitAfterDead(currLog.getFishNum());
                    needUpdate.add(detailItem);
                }
            });
        }
        // 2. 有需要更新执行更新逻辑
        if(EasyListUtil.isNotEmpty(needUpdate)) {
            zkPondFishDetailService.customBatchUpdate(needUpdate);
        }
        return list;
    }
    public void enhancer_fishInfo(List<ZkPondFishDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        list = list.stream().filter(Objects::nonNull).toList();
        // 1. 获取鱼type信息
        List<DictDataVo> dictDataList = dictDataService.getByDictTypes(List.of("zk_pond_fish_types"));
        // 2. 给鱼填充明细信息
        for(ZkPondFishDetailDO item : list) {
            if(item == null) continue;
            for(DictDataVo dict : dictDataList) {
                if(StringUtils.equals(item.getFishTypes(), dict.getValue())) {
                    item.setFishName(dict.getLabel());
                    break;
                }
            }
        }
        // 3. 找到 这个存塘信息，填充当前鱼塘还有多少批次
        Set<Long> pondIdSet = list.stream().map(ZkPondFishDetailDO::getPondId).collect(Collectors.toSet());
        List<ZkPondFishDO> oList = zkPondFishService.selectByPondIdSet(pondIdSet);
        CommonUtil.origins2Targets4(oList, ZkPondFishDO::getPondId, list, ZkPondFishDetailDO::getPondId, (ZkPondFishDO o, ZkPondFishDetailDO t) -> {
            t.setZkPondFishDO(o);
        });
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出存塘鱼苗明细 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPondFishDetailExcel(@Valid ZkPondFishDetailExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPondFishDetailDO> list = zkPondFishDetailService.getZkPondFishDetailList(exportReqVO);
        enhancer_fishInfo(list);
        // 导出 Excel
        List<ZkPondFishDetailExcelVO> datas = BeanUtils.toBean(list, ZkPondFishDetailExcelVO.class);
        ExcelUtils.write(response, "存塘鱼苗明细.xls", "数据", ZkPondFishDetailExcelVO.class, datas);
    }

}
