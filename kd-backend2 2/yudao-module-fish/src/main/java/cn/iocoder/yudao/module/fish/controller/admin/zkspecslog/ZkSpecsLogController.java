package cn.iocoder.yudao.module.fish.controller.admin.zkspecslog;

import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.to.CalculateFishRadioTool;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
// import cn.iocoder.yudao.module.fish.convert.zkspecslog.ZkSpecsLogConvert;
import cn.iocoder.yudao.module.fish.service.zkspecslog.ZkSpecsLogService;

@Tag(name = "管理后台 - 规格记录")
@RestController
@RequestMapping("/fish/zk-specs-log")
@Validated
public class ZkSpecsLogController {

    @Resource
    private ZkSpecsLogService zkSpecsLogService;
    @Resource
    private ZkPondFishService zkPondFishService;
    @Resource
    private CalculateFishRadioTool calculateFishRadioTool;

    @PostMapping("/create")
    @Operation(summary = "创建规格记录")
    public CommonResult<Long> createZkSpecsLog(@Valid @RequestBody ZkSpecsLogCreateReqVO createReqVO) {
        return success(zkSpecsLogService.createZkSpecsLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新规格记录")
    public CommonResult<Boolean> updateZkSpecsLog(@Valid @RequestBody ZkSpecsLogUpdateReqVO updateReqVO) {
        zkSpecsLogService.updateZkSpecsLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除规格记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkSpecsLog(@RequestParam("id") Long id) {
        zkSpecsLogService.deleteZkSpecsLog(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得规格记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkSpecsLogRespVO> getZkSpecsLog(@RequestParam("id") Long id) {
        // 1. 同步最新数据
        sync_newData();
        ZkSpecsLogDO zkSpecsLog = zkSpecsLogService.getZkSpecsLog(id);
        return success(BeanUtils.toBean(zkSpecsLog, ZkSpecsLogRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得规格记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkSpecsLogRespVO>> getZkSpecsLogList(@RequestParam("ids") Collection<Long> ids) {
        // 1. 同步最新数据
        sync_newData();
        List<ZkSpecsLogDO> list = zkSpecsLogService.getZkSpecsLogList(ids);
        return success(BeanUtils.toBean(list, ZkSpecsLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得规格记录分页")
    public CommonResult<PageResult<ZkSpecsLogRespVO>> getZkSpecsLogPage(@Valid ZkSpecsLogPageReqVO pageVO) {
        // 1. 同步最新数据
        sync_newData();
        // 2. 进行 搜索
        PageResult<ZkSpecsLogDO> pageResult = zkSpecsLogService.getZkSpecsLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkSpecsLogRespVO.class));
    }

    /**
     * 同步 关联 最新数据，
     * 接口位置
     */
    public synchronized List<ZkSpecsLogDO> sync_newData() {
        // 1. 获取 存塘 鱼信息
        List<ZkPondFishDO> list = zkPondFishService.getListWithSpec();
        if(EasyListUtil.isEmpty(list)) return null;
        // 2. 从当前目录 获取数据
        List<ZkSpecsLogDO> logs = zkSpecsLogService.getList();
        List<ZkSpecsLogDO> needAdd = new ArrayList<>();
        List<ZkSpecsLogDO> needUpdate = new ArrayList<>();
        if(EasyListUtil.isEmpty(logs)) {
            needAdd = list.stream().map(obj -> {
                return new ZkSpecsLogDO(obj.getWorkshopId(), obj.getWorkshopName(), obj.getPondSystemName(), null,
                        obj.getPondNum(), obj.getPondName(), obj.getPondId(), obj.getPondDetailNum(), obj.getInitFishSpec(), obj.getPushFishNum(), obj.getFishNum()).build_feedCount(obj.getFeedCount())
                        .setTenantId(obj.getTenantId()).setDeptId(obj.getDeptId()).setUserId(obj.getUserId());
            }).collect(Collectors.toList());
        } else {
            // 部分需要更新，部分需要新增
            for(ZkPondFishDO remote : list) {
                ZkSpecsLogDO curr = null;
                for(ZkSpecsLogDO local : logs) {
                    if(Objects.equals(remote.getPondId(), local.getPondId())) {
                        curr = local;
                        break;
                    }
                }
                if(curr != null) {
                    // 2.2 需要修改
                    curr.build_num(remote.getInitFishSpec(), remote.getPushFishNum(), remote.getFishNum()).build_feedCount(remote.getFeedCount())
                            .build_pondInfo(remote.getWorkshopId(), remote.getWorkshopName(), remote.getPondSystemName(), null,
                                    remote.getPondNum(), remote.getPondName(), remote.getPondId(), remote.getPondDetailNum())
                            .setActuralSpecs(remote.getCurrFishSpec())
                            .setTenantId(remote.getTenantId()).setDeptId(remote.getDeptId()).setUserId(remote.getUserId());
                    needUpdate.add(curr);
                } else {
                    // 2.1 需要新增
                    ZkSpecsLogDO addItem = new ZkSpecsLogDO(remote.getWorkshopId(), remote.getWorkshopName(), remote.getPondSystemName(), null,
                            remote.getPondNum(), remote.getPondName(), remote.getPondId(), remote.getPondDetailNum(), remote.getInitFishSpec(), remote.getPushFishNum(),
                            remote.getFishNum()).build_feedCount(remote.getFeedCount())
                            .setActuralSpecs(remote.getCurrFishSpec())
                            .setTenantId(remote.getTenantId()).setDeptId(remote.getDeptId()).setUserId(remote.getUserId());
                    needAdd.add(addItem);
                }
            }
        }
        // 3. 执行 同步数据
        zkSpecsLogService.customBatchInsert(needAdd);
        zkSpecsLogService.customBatchUpdate(needUpdate);
        // 4. 最终做一个更新计算鱼苗 饵料系数, 从巡检打样记录获取 ==》 zk_fish_proofing.avg_specs
        needUpdate.addAll(needAdd);
        List<ZkSpecsLogDO> updateAfterRadioList = calculateFishRadioTool.calculateRadio(needUpdate);
        zkSpecsLogService.customBatchUpdate(updateAfterRadioList);
        // 5. 更新 zk_pond_fish
        List<ZkPondFishDO> needUpdateFish = enhancerList(updateAfterRadioList, list);
        if(EasyListUtil.isNotEmpty(needUpdateFish)) zkPondFishService.customBatchUpdate(needUpdateFish);
        System.out.println(String.format("zkSpecsLog model 9=====================>zkSpecsLog:%s", JSON.toJSONString(updateAfterRadioList)));
        return updateAfterRadioList;
    }

    public List<ZkPondFishDO> enhancerList(List<ZkSpecsLogDO> updateAfterRadioList, List<ZkPondFishDO> list) {
        if(EasyListUtil.isEmpty(updateAfterRadioList) || EasyListUtil.isEmpty(list)) return new ArrayList<>();
        Map<Long, List<ZkSpecsLogDO>> collect = updateAfterRadioList.stream().collect(Collectors.groupingBy(ZkSpecsLogDO::getPondId));
        for(ZkPondFishDO item : list) {
            MapUtils.findAndThen(collect, item.getPondId(), currList -> {
                if(EasyListUtil.isEmpty(currList)) return;
                ZkSpecsLogDO curr = currList.get(0);
                if(!EasyBigDecimalUtil.gt0(curr.getFeedCount())) return;
                BigDecimal subtract = EasyBigDecimalUtil.subtract(item.getAvgFishSpec(), curr.select_fishSpecs());
                if(EasyBigDecimalUtil.ge0(subtract)) return;
                item.setBiologicalAssets(curr.getBiologicalAssets()).setSumBiologicalAssets(curr.getSumBiologicalAssets())
                        .setAvgFishSpec(curr.select_fishSpecs())
                        .setAvgBaitCoefficient(curr.getAvgBaitCoefficient()).setAccumulatedWeightGain(curr.select_accumulatedWeightGain());
            });
        }
        System.out.println(String.format("zkSpecsLog model 8=====================>ZkPondFishDO:%s", JSON.toJSONString(list)));
        return list;
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出规格记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkSpecsLogExcel(@Valid ZkSpecsLogExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkSpecsLogDO> list = zkSpecsLogService.getZkSpecsLogList(exportReqVO);
        // 导出 Excel
        List<ZkSpecsLogExcelVO> datas = BeanUtils.toBean(list, ZkSpecsLogExcelVO.class);
        ExcelUtils.write(response, "规格记录.xls", "数据", ZkSpecsLogExcelVO.class, datas);
    }

    @GetMapping("/fcr-model-list")
    @Operation(summary = "获得饵料系数模型列表")
    @Parameter(name = "deptId", description = "归属部门（本部门+子部门）；不传则取登录用户部门", required = false)
    public CommonResult<List<ZkFcrModelRespVO>> getFcrModelList(
            @RequestParam(value = "deptId", required = false) Long deptId) {
        return success(zkSpecsLogService.getFcrModelList(deptId));
    }

}
