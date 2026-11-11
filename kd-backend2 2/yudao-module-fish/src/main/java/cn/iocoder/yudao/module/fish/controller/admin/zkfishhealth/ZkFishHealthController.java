package cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth;

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

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkfishhealth.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishhealth.ZkFishHealthDO;
// import cn.iocoder.yudao.module.fish.convert.zkfishhealth.ZkFishHealthConvert;
import cn.iocoder.yudao.module.fish.service.zkfishhealth.ZkFishHealthService;

@Tag(name = "管理后台 - 鱼类健康评估")
@RestController
@RequestMapping("/fish/zk-fish-health")
@Validated
public class ZkFishHealthController {

    @Resource
    private ZkFishHealthService zkFishHealthService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼类健康评估")
    public CommonResult<Long> createZkFishHealth(@Valid @RequestBody ZkFishHealthCreateReqVO createReqVO) {
        return success(zkFishHealthService.createZkFishHealth(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼类健康评估")
    public CommonResult<Boolean> updateZkFishHealth(@Valid @RequestBody ZkFishHealthUpdateReqVO updateReqVO) {
        zkFishHealthService.updateZkFishHealth(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼类健康评估")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkFishHealth(@RequestParam("id") Long id) {
        zkFishHealthService.deleteZkFishHealth(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼类健康评估")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkFishHealthRespVO> getZkFishHealth(@RequestParam("id") Long id) {
        ZkFishHealthDO zkFishHealth = zkFishHealthService.getZkFishHealth(id);
        return success(BeanUtils.toBean(zkFishHealth, ZkFishHealthRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得鱼类健康评估列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkFishHealthRespVO>> getZkFishHealthList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkFishHealthDO> list = zkFishHealthService.getZkFishHealthList(ids);
        return success(BeanUtils.toBean(list, ZkFishHealthRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼类健康评估分页")
    public CommonResult<PageResult<ZkFishHealthRespVO>> getZkFishHealthPage(@Valid ZkFishHealthPageReqVO pageVO) {
        PageResult<ZkFishHealthDO> pageResult = zkFishHealthService.getZkFishHealthPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkFishHealthRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼类健康评估 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkFishHealthExcel(@Valid ZkFishHealthExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkFishHealthDO> list = zkFishHealthService.getZkFishHealthList(exportReqVO);
        // 导出 Excel
        List<ZkFishHealthExcelVO> datas = BeanUtils.toBean(list, ZkFishHealthExcelVO.class);
        ExcelUtils.write(response, "鱼类健康评估.xls", "数据", ZkFishHealthExcelVO.class, datas);
    }

}
