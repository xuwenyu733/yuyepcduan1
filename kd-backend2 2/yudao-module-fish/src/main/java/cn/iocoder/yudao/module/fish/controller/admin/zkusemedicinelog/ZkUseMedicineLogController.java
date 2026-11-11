package cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog;

import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.tools.FishSystemLogTool;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.service.zkbiologycapital.ZkBiologyCapitalService;
import cn.iocoder.yudao.module.fish.service.zkmedicinelogpond.ZkMedicineLogPondService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.alibaba.fastjson.JSON;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicinelog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
// import cn.iocoder.yudao.module.fish.convert.zkusemedicinelog.ZkUseMedicineLogConvert;
import cn.iocoder.yudao.module.fish.service.zkusemedicinelog.ZkUseMedicineLogService;

@Tag(name = "管理后台 - 用药明细")
@RestController
@RequestMapping("/fish/zk-use-medicine-log")
@Validated
public class ZkUseMedicineLogController {

    @Resource
    private ZkUseMedicineLogService zkUseMedicineLogService;
    @Resource
    private ZkBiologyCapitalService zkBiologyCapitalService;
    @Resource
    private ZkMedicineLogPondService zkMedicineLogPondService;
    @Resource
    private ZkPondService zkPondService;

    @PostMapping("/create")
    @Operation(summary = "创建用药明细")
    public CommonResult<Long> createZkUseMedicineLog(@Valid @RequestBody ZkUseMedicineLogCreateReqVO createReqVO) {
        // 1. 添加数据
        ZkUseMedicineLogDO zkUseMedicineLog = zkUseMedicineLogService.createZkUseMedicineLog(createReqVO);
        // 2. 添加关联
        if(EasyListUtil.isNotEmpty(createReqVO.getPondIdArr())) {
            add_medicineLogPond(createReqVO.getPondIdArr(), zkUseMedicineLog);
        }
        // 3. 执行 同步数据
        sync_biologyCapital(zkUseMedicineLog);
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(zkUseMedicineLog.getPondDetailNum(), "创建用药明细", JSON.toJSONString(zkUseMedicineLog), null, null, null)
                .setRemark(null));
        return success(zkUseMedicineLog.getId());
    }

    /**
     * 新增 zk_medicine_log_pond ， 存储 zk_use_medicine_log 和 zk_pond 关联关系
     * @param pondIds 存塘id
     * @param zkUseMedicineLog 用药日志信息
     */
    public void add_medicineLogPond(List<Long> pondIds, ZkUseMedicineLogDO zkUseMedicineLog) {
        List<ZkMedicineLogPondDO> collect = pondIds.stream().map(i -> new ZkMedicineLogPondDO(i, zkUseMedicineLog.getId())).toList();
        zkMedicineLogPondService.customSaveBatch(zkUseMedicineLog.getId(), collect);
    }


    public void sync_biologyCapital(ZkUseMedicineLogDO item) {
        // 1. 成本为空 则 无需同步
        if(!EasyBigDecimalUtil.gt0(item.getMedicineCost())) {
            return;
        }
        // 2. 执行同步
        zkBiologyCapitalService.medicineCostUpdate(item.getPondId(), item.getMedicineCost());
    }

    @PutMapping("/update")
    @Operation(summary = "更新用药明细")
    public CommonResult<Boolean> updateZkUseMedicineLog(@Valid @RequestBody ZkUseMedicineLogUpdateReqVO updateReqVO) {
        List<ZkUseMedicineLogDO> updateLog = zkUseMedicineLogService.updateZkUseMedicineLog(updateReqVO);
        ZkUseMedicineLogDO before = updateLog.get(0);
        // 2. 添加关联
        if(EasyListUtil.isNotEmpty(updateReqVO.getPondIdArr())) {
            add_medicineLogPond(updateReqVO.getPondIdArr(), updateLog.get(1));
        }
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(before.getPondDetailNum(), "更新用药明细", JSON.toJSONString(before), JSON.toJSONString(updateLog.get(1)), null, null)
                .setRemark(null));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用药明细")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkUseMedicineLog(@RequestParam("id") Long id) {
        ZkUseMedicineLogDO before = zkUseMedicineLogService.deleteZkUseMedicineLog(id);
        zkMedicineLogPondService.deleteByMedicineLogId(id);
        // 同步操作日志，方便追踪问题，后续可注释
        FishSystemLogTool.gen(new FishSystemLogDO(before.getPondDetailNum(), "更新用药明细", JSON.toJSONString(before), null, null, null)
                .setRemark(null));
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用药明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkUseMedicineLogRespVO> getZkUseMedicineLog(@RequestParam("id") Long id) {
        ZkUseMedicineLogDO zkUseMedicineLog = zkUseMedicineLogService.getZkUseMedicineLog(id);
        enhancerList(List.of(zkUseMedicineLog));
        return success(BeanUtils.toBean(zkUseMedicineLog, ZkUseMedicineLogRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得用药明细列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkUseMedicineLogRespVO>> getZkUseMedicineLogList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkUseMedicineLogDO> list = zkUseMedicineLogService.getZkUseMedicineLogList(ids);
        enhancerList(list);
        return success(BeanUtils.toBean(list, ZkUseMedicineLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用药明细分页")
    public CommonResult<PageResult<ZkUseMedicineLogRespVO>> getZkUseMedicineLogPage(@Valid ZkUseMedicineLogPageReqVO pageVO) {
        PageResult<ZkUseMedicineLogDO> pageResult = zkUseMedicineLogService.getZkUseMedicineLogPage(pageVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkUseMedicineLogRespVO.class));
    }

    public void enhancerList(List<ZkUseMedicineLogDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        // 1. 获取列表
        Set<Long> collect = tList.stream().map(ZkUseMedicineLogDO::getId).collect(Collectors.toSet());
        List<ZkMedicineLogPondDO> oList = zkMedicineLogPondService.selectByMedicineLogIds(collect);
        if(EasyListUtil.isEmpty(oList)) return;
        // 2. 分组 设置
        Map<Long, List<ZkMedicineLogPondDO>> oMap = oList.stream().collect(Collectors.groupingBy(ZkMedicineLogPondDO::getMedicineLogId));
        for(ZkUseMedicineLogDO item : tList) {
            List<ZkMedicineLogPondDO> tempList = oMap.get(item.getId());
            if(EasyListUtil.isEmpty(tempList)) continue;
            List<Long> pondIdArr = tempList.stream().map(ZkMedicineLogPondDO::getPondId).toList();
            item.setPondIdArr(pondIdArr);
        }
        // 3. 设置存塘名称
        Set<Long> pondIdSet = new HashSet<>();
        for(ZkUseMedicineLogDO item : tList) pondIdSet.addAll(item.getPondIdArr());
        List<ZkPondDO> zkPondDOS = zkPondService.selectByPondIds(pondIdSet);
        if(EasyListUtil.isEmpty(zkPondDOS)) return;
        for(ZkUseMedicineLogDO item : tList) {
            List<ZkMedicineLogPondDO> tempList = oMap.get(item.getId());
            if(EasyListUtil.isEmpty(tempList)) continue;
            String pondDetailNumStr = tempList.stream().map(obj -> getPondName(obj.getPondId(), zkPondDOS)).collect(Collectors.joining(","));
            item.setPondDetailNumStr(pondDetailNumStr);
        }
    }

    public String getPondName(Long pondId, List<ZkPondDO> pondDOS) {
        for(ZkPondDO item : pondDOS) {
            if(Objects.equals(item.getId(), pondId)) return item.getPondDetailNum();
        }
        return "";
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用药明细 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkUseMedicineLogExcel(@Valid ZkUseMedicineLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkUseMedicineLogDO> list = zkUseMedicineLogService.getZkUseMedicineLogList(exportReqVO);
        // 导出 Excel
        List<ZkUseMedicineLogExcelVO> datas = BeanUtils.toBean(list, ZkUseMedicineLogExcelVO.class);
        ExcelUtils.write(response, "用药明细.xls", "数据", ZkUseMedicineLogExcelVO.class, datas);
    }

}
