package cn.iocoder.yudao.module.fish.controller.admin.zknetbox;

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

import cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zknetbox.ZkNetBoxDO;
// import cn.iocoder.yudao.module.fish.convert.zknetbox.ZkNetBoxConvert;
import cn.iocoder.yudao.module.fish.service.zknetbox.ZkNetBoxService;

@Tag(name = "管理后台 - 网盘资源")
@RestController
@RequestMapping("/fish/zk-net-box")
@Validated
public class ZkNetBoxController {

    @Resource
    private ZkNetBoxService zkNetBoxService;

    @PostMapping("/create")
    @Operation(summary = "创建网盘资源")
    public CommonResult<Long> createZkNetBox(@Valid @RequestBody ZkNetBoxCreateReqVO createReqVO) {
        return success(zkNetBoxService.createZkNetBox(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新网盘资源")
    public CommonResult<Boolean> updateZkNetBox(@Valid @RequestBody ZkNetBoxUpdateReqVO updateReqVO) {
        zkNetBoxService.updateZkNetBox(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除网盘资源")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkNetBox(@RequestParam("id") Long id) {
        zkNetBoxService.deleteZkNetBox(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得网盘资源")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkNetBoxRespVO> getZkNetBox(@RequestParam("id") Long id) {
        ZkNetBoxDO zkNetBox = zkNetBoxService.getZkNetBox(id);
        return success(BeanUtils.toBean(zkNetBox, ZkNetBoxRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得网盘资源列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkNetBoxRespVO>> getZkNetBoxList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkNetBoxDO> list = zkNetBoxService.getZkNetBoxList(ids);
        return success(BeanUtils.toBean(list, ZkNetBoxRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得网盘资源分页")
    public CommonResult<PageResult<ZkNetBoxRespVO>> getZkNetBoxPage(@Valid ZkNetBoxPageReqVO pageVO) {
        PageResult<ZkNetBoxDO> pageResult = zkNetBoxService.getZkNetBoxPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkNetBoxRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出网盘资源 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkNetBoxExcel(@Valid ZkNetBoxExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkNetBoxDO> list = zkNetBoxService.getZkNetBoxList(exportReqVO);
        // 导出 Excel
        List<ZkNetBoxExcelVO> datas = BeanUtils.toBean(list, ZkNetBoxExcelVO.class);
        ExcelUtils.write(response, "网盘资源.xls", "数据", ZkNetBoxExcelVO.class, datas);
    }

}
