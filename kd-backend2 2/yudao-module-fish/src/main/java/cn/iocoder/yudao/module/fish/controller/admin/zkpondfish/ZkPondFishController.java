package cn.iocoder.yudao.module.fish.controller.admin.zkpondfish;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfish.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
// import cn.iocoder.yudao.module.fish.convert.zkpondfish.ZkPondFishConvert;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;

@Tag(name = "管理后台 - 存塘鱼苗")
@RestController
@RequestMapping("/fish/zk-pond-fish")
@Validated
public class ZkPondFishController {

    @Resource
    private ZkPondFishService zkPondFishService;

    @PostMapping("/create")
    @Operation(summary = "创建存塘鱼苗")
    public CommonResult<Long> createZkPondFish(@Valid @RequestBody ZkPondFishCreateReqVO createReqVO) {
        return success(zkPondFishService.createZkPondFish(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新存塘鱼苗")
    public CommonResult<Boolean> updateZkPondFish(@Valid @RequestBody ZkPondFishUpdateReqVO updateReqVO) {
        zkPondFishService.updateZkPondFish(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除存塘鱼苗")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPondFish(@RequestParam("id") Long id) {
        zkPondFishService.deleteZkPondFish(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得存塘鱼苗")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPondFishRespVO> getZkPondFish(@RequestParam("id") Long id) {
        ZkPondFishDO zkPondFish = zkPondFishService.getZkPondFish(id);
        flush_flshInfo(Collections.singletonList(zkPondFish));
        return success(BeanUtils.toBean(zkPondFish, ZkPondFishRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得存塘鱼苗列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPondFishRespVO>> getZkPondFishList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPondFishDO> list = zkPondFishService.getZkPondFishList(ids);
        flush_flshInfo(list);
        return success(BeanUtils.toBean(list, ZkPondFishRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得存塘鱼苗分页")
    public CommonResult<PageResult<ZkPondFishRespVO>> getZkPondFishPage(@Valid ZkPondFishPageReqVO pageVO) {
        PageResult<ZkPondFishDO> pageResult = zkPondFishService.getZkPondFishPage(pageVO);
        flush_flshInfo(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkPondFishRespVO.class));
    }


    public void flush_flshInfo(List<ZkPondFishDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        list.forEach(obj -> obj.build_flushInfo());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出存塘鱼苗 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPondFishExcel(@Valid ZkPondFishExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPondFishDO> list = zkPondFishService.getZkPondFishList(exportReqVO);
        flush_flshInfo(list);
        // 导出 Excel
        List<ZkPondFishExcelVO> datas = BeanUtils.toBean(list, ZkPondFishExcelVO.class);
        ExcelUtils.write(response, "存塘鱼苗.xls", "数据", ZkPondFishExcelVO.class, datas);
    }

}
