package cn.iocoder.yudao.module.fish.controller.admin.zkexperimental;

import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia.ZkExperimentalMediaDO;
import cn.iocoder.yudao.module.fish.service.zkexperimentaldata.ZkExperimentalDataService;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpoolmapping.ZkExperimentalPoolMappingDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkexperimentalpoolmapping.ZkExperimentalPoolMappingMapper;
import cn.iocoder.yudao.module.fish.service.zkexperimentalmedia.ZkExperimentalMediaService;
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
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimental.ZkExperimentalDO;
import cn.iocoder.yudao.module.fish.service.zkexperimental.ZkExperimentalService;
import software.amazon.awssdk.utils.CollectionUtils;

@Tag(name = "管理后台 - 实验")
@RestController
@RequestMapping("/fish/zk-experimental")
@Validated
public class ZkExperimentalController {

    @Resource
    private ZkExperimentalService zkExperimentalService;

    @Resource
    private ZkExperimentalDataService zkExperimentalDataService;

    @Resource
    private ZkExperimentalMediaService zkExperimentalMediaService;

    @Resource
    private ZkExperimentalPoolMappingMapper zkExperimentalPoolMappingMapper;

    @PostMapping("/create")
    @Operation(summary = "创建实验")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:create')")
    public CommonResult<Long> createZkExperimental(@Valid @RequestBody ZkExperimentalSaveReqVO createReqVO) {
        return success(zkExperimentalService.createZkExperimental(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实验")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:update')")
    public CommonResult<Boolean> updateZkExperimental(@Valid @RequestBody ZkExperimentalSaveReqVO updateReqVO) {
        zkExperimentalService.updateZkExperimental(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实验")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:delete')")
    public CommonResult<Boolean> deleteZkExperimental(@RequestParam("id") Long id) {
        zkExperimentalService.deleteZkExperimental(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除实验")
                @PreAuthorize("@ss.hasPermission('fish:zk-experimental:delete')")
    public CommonResult<Boolean> deleteZkExperimentalList(@RequestParam("ids") List<Long> ids) {
        zkExperimentalService.deleteZkExperimentalListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实验")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:query')")
    public CommonResult<ZkExperimentalRespVO> getZkExperimental(@RequestParam("id") Long id) {
        ZkExperimentalDO zkExperimental = zkExperimentalService.getZkExperimental(id);
        enhancerList(Collections.singletonList(zkExperimental));
        return success(BeanUtils.toBean(zkExperimental, ZkExperimentalRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实验分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:query')")
    public CommonResult<PageResult<ZkExperimentalRespVO>> getZkExperimentalPage(@Valid ZkExperimentalPageReqVO pageReqVO) {
        PageResult<ZkExperimentalDO> pageResult = zkExperimentalService.getZkExperimentalPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkExperimentalRespVO.class));
    }

    public void enhancerList(List<ZkExperimentalDO> tList) {
        if(EasyListUtil.isEmpty(tList)) return;
        // 1. 获取实验id
        Set<Long> experimentalIdSet = tList.stream().map(ZkExperimentalDO::getId).collect(Collectors.toSet());
        // 2. 获取数据 针对 数据采集和多媒体
        Map<Long, Map<String, ZkExperimentalDataDO>> dataMap = zkExperimentalDataService.selectMapByIds(experimentalIdSet);
        Map<Long, List<ZkExperimentalMediaDO>> mediaMap = zkExperimentalMediaService.selectMapByIds(experimentalIdSet);
        
        // 3. 获取关联池子
        List<ZkExperimentalPoolMappingDO> allMappings = zkExperimentalPoolMappingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ZkExperimentalPoolMappingDO>()
                .in(ZkExperimentalPoolMappingDO::getExperimentalId, experimentalIdSet));
        Map<Long, List<ZkExperimentalPoolMappingDO>> mappingMap = allMappings.stream()
                .collect(Collectors.groupingBy(ZkExperimentalPoolMappingDO::getExperimentalId));

        // 4. 设置值
        tList.forEach(obj -> {
            MapUtils.findAndThen(dataMap, obj.getId(), obj::setDataMap);
            MapUtils.findAndThen(mediaMap, obj.getId(), obj::setFileList);
            
            List<ZkExperimentalPoolMappingDO> mappings = mappingMap.get(obj.getId());
            if (EasyListUtil.isNotEmpty(mappings)) {
                obj.setPoolIds(EasyListUtil.listToIds3(mappings, ZkExperimentalPoolMappingDO::getPoolId));
                obj.setPoolNumbers(EasyListUtil.listToIds3(mappings, ZkExperimentalPoolMappingDO::getPoolNumber));
                obj.setPoolDetails(mappings);
            }
        });
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实验 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkExperimentalExcel(@Valid ZkExperimentalPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalDO> list = zkExperimentalService.getZkExperimentalPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "实验.xls", "数据", ZkExperimentalRespVO.class,
                        BeanUtils.toBean(list, ZkExperimentalRespVO.class));
    }

    @GetMapping("/analysis/summary")
    @Operation(summary = "实验数据分析汇总")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental:query')")
    public CommonResult<ZkExperimentalAnalysisSummaryRespVO> getAnalysisSummary(
            @RequestParam(value = "experimentId", required = false) Long experimentId,
            @RequestParam(value = "experimentalId", required = false) Long experimentalId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        Long resolvedId = experimentId != null ? experimentId : experimentalId;
        return success(zkExperimentalService.getAnalysisSummary(resolvedId, startDate, endDate));
    }

}