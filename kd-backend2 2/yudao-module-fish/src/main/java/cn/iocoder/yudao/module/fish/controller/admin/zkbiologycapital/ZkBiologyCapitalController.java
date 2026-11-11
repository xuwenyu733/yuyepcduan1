package cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbiologycapital.ZkBiologyCapitalDO;
// import cn.iocoder.yudao.module.fish.convert.zkbiologycapital.ZkBiologyCapitalConvert;
import cn.iocoder.yudao.module.fish.service.zkbiologycapital.ZkBiologyCapitalService;

@Tag(name = "管理后台 - 生物性资产")
@RestController
@RequestMapping("/fish/zk-biology-capital")
@Validated
@Slf4j
public class ZkBiologyCapitalController {

    @Resource
    private ZkBiologyCapitalService zkBiologyCapitalService;

    @Resource
    private ZkPondFishService zkPondFishService;

    @PostMapping("/create")
    @Operation(summary = "创建生物性资产")
    public CommonResult<Long> createZkBiologyCapital(@Valid @RequestBody ZkBiologyCapitalCreateReqVO createReqVO) {
        return success(zkBiologyCapitalService.createZkBiologyCapital(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新生物性资产")
    public CommonResult<Boolean> updateZkBiologyCapital(@Valid @RequestBody ZkBiologyCapitalUpdateReqVO updateReqVO) {
        zkBiologyCapitalService.updateZkBiologyCapital(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除生物性资产")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkBiologyCapital(@RequestParam("id") Long id) {
        zkBiologyCapitalService.deleteZkBiologyCapital(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得生物性资产")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkBiologyCapitalRespVO> getZkBiologyCapital(@RequestParam("id") Long id) {
        ZkBiologyCapitalDO zkBiologyCapital = zkBiologyCapitalService.getZkBiologyCapital(id);
        return success(BeanUtils.toBean(zkBiologyCapital, ZkBiologyCapitalRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得生物性资产列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkBiologyCapitalRespVO>> getZkBiologyCapitalList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkBiologyCapitalDO> list = zkBiologyCapitalService.getZkBiologyCapitalList(ids);
        return success(BeanUtils.toBean(list, ZkBiologyCapitalRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得生物性资产分页")
    public CommonResult<PageResult<ZkBiologyCapitalRespVO>> getZkBiologyCapitalPage(@Valid ZkBiologyCapitalPageReqVO pageVO) {
        // 1. 统计数据（失败不影响成本分页查询）
        try {
            genLog();
        } catch (Exception e) {
            log.warn("【生物资产统计】genLog 失败，跳过同步: {}", e.getMessage());
        }
        // 2. 查询记录
        PageResult<ZkBiologyCapitalDO> pageResult = zkBiologyCapitalService.getZkBiologyCapitalPage(pageVO);
        // 这里面的统计值有可能为空，从数据库里面统计一份兜底
        ZkBiologyCapitalDO sqlStatic = zkBiologyCapitalService.sqlStatic();
        // 空分页可能是不可变 List，需拷贝后再追加兜底统计行
        java.util.ArrayList<ZkBiologyCapitalDO> mutableList = new java.util.ArrayList<>(
                pageResult.getList() == null ? java.util.Collections.emptyList() : pageResult.getList());
        mutableList.add(sqlStatic);
        pageResult.setList(mutableList);
        return success(BeanUtils.toBean(pageResult, ZkBiologyCapitalRespVO.class));
    }

    public synchronized Integer genLog() {
        // 1. 从 zk_pond_fish 中获取 数据
        List<ZkPondFishDO> fishList = zkPondFishService.getList();
        if(EasyListUtil.isEmpty(fishList)) return 0;
        fishList = fishList.stream().filter(obj -> obj.getPondId()!=null).collect(Collectors.toList());
        // 2. 获取 zk_biology_capital 数据
        List<ZkBiologyCapitalDO> list = zkBiologyCapitalService.getList();
        // 3. 合并并回写数据
        Integer res = enhancer_list(fishList, list);
        log.warn("【生物资产统计】middle,统计条数:{}，成功入库", res);
        return res;
    }

    public Integer enhancer_list(List<ZkPondFishDO> fishList, List<ZkBiologyCapitalDO> list) {
        List<ZkBiologyCapitalDO> needAdd = new ArrayList<>();
        if(EasyListUtil.isEmpty(list)) {
            needAdd = fishList.stream().map(ZkPondFishDO::transformerToCapital).collect(Collectors.toList());
        } else {
            for(ZkPondFishDO remote : fishList) {
                Boolean flag = false;
                for(ZkBiologyCapitalDO local : list) {
                    if(Objects.equals(local.getPondId(), remote.getPondId())) {
                        flag = true;
                        local.build_update(remote.getFishNum()).setSpecifications(remote.getCurrFishSpec()+"").setTenantId(remote.getTenantId())
                                .setDeptId(remote.getDeptId()).setUserId(remote.getUserId());
                    }
                }
                if(!flag) {
                    needAdd.add(remote.transformerToCapital());
                }
            }
        }
        // 2. 回显到数据库
        Integer res = zkBiologyCapitalService.saveBatch(needAdd);
        res += zkBiologyCapitalService.updateBatch(list);
        return res;
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出生物性资产 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkBiologyCapitalExcel(@Valid ZkBiologyCapitalExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkBiologyCapitalDO> list = zkBiologyCapitalService.getZkBiologyCapitalList(exportReqVO);
        // 导出 Excel
        List<ZkBiologyCapitalExcelVO> datas = BeanUtils.toBean(list, ZkBiologyCapitalExcelVO.class);
        ExcelUtils.write(response, "生物性资产.xls", "数据", ZkBiologyCapitalExcelVO.class, datas);
    }

    @GetMapping("/main-home")
    @Operation(summary = "获得首页统计数据")
    public CommonResult<ZkBiologyCapitalMainHomeRespVO> getMainHomeData() {
        return success(zkBiologyCapitalService.getMainHomeData());
    }

    @GetMapping("/precision-feeding-stats")
    @Operation(summary = "精准投喂-顶部统计")
    public CommonResult<PrecisionFeedingStatsRespVO> getPrecisionFeedingStats() {
        return success(zkBiologyCapitalService.getPrecisionFeedingStats());
    }

    @GetMapping("/precision-feeding-ponds")
    @Operation(summary = "精准投喂-鱼池列表")
    public CommonResult<List<PrecisionFeedingPondRespVO>> getPrecisionFeedingPonds() {
        return success(zkBiologyCapitalService.getPrecisionFeedingPonds());
    }

    @GetMapping("/precision-feeding-today-logs")
    @Operation(summary = "精准投喂-今日投喂记录")
    public CommonResult<List<PrecisionFeedingTodayLogRespVO>> getPrecisionFeedingTodayLogs() {
        return success(zkBiologyCapitalService.getPrecisionFeedingTodayLogs());
    }

}
