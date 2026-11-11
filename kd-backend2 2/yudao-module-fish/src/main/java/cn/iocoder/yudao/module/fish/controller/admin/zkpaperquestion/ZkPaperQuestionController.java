package cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkpaperquestion.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
// import cn.iocoder.yudao.module.fish.convert.zkpaperquestion.ZkPaperQuestionConvert;
import cn.iocoder.yudao.module.fish.service.zkpaperquestion.ZkPaperQuestionService;

@Tag(name = "管理后台 - 试题")
@RestController
@RequestMapping("/fish/zk-paper-question")
@Validated
public class ZkPaperQuestionController {

    @Resource
    private ZkPaperQuestionService zkPaperQuestionService;

    @PostMapping("/create")
    @Operation(summary = "创建试题")
    public CommonResult<Long> createZkPaperQuestion(@Valid @RequestBody ZkPaperQuestionCreateReqVO createReqVO) {
        return success(zkPaperQuestionService.createZkPaperQuestion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新试题")
    public CommonResult<Boolean> updateZkPaperQuestion(@Valid @RequestBody ZkPaperQuestionUpdateReqVO updateReqVO) {
        zkPaperQuestionService.updateZkPaperQuestion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试题")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPaperQuestion(@RequestParam("id") Long id) {
        zkPaperQuestionService.deleteZkPaperQuestion(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得试题")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPaperQuestionRespVO> getZkPaperQuestion(@RequestParam("id") Long id) {
        ZkPaperQuestionDO zkPaperQuestion = zkPaperQuestionService.getZkPaperQuestion(id);
        return success(BeanUtils.toBean(zkPaperQuestion, ZkPaperQuestionRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得试题列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPaperQuestionRespVO>> getZkPaperQuestionList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPaperQuestionDO> list = zkPaperQuestionService.getZkPaperQuestionList(ids);
        return success(BeanUtils.toBean(list, ZkPaperQuestionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得试题分页")
    public CommonResult<PageResult<ZkPaperQuestionRespVO>> getZkPaperQuestionPage(@Valid ZkPaperQuestionPageReqVO pageVO) {
        PageResult<ZkPaperQuestionDO> pageResult = zkPaperQuestionService.getZkPaperQuestionPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkPaperQuestionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出试题 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPaperQuestionExcel(@Valid ZkPaperQuestionExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPaperQuestionDO> list = zkPaperQuestionService.getZkPaperQuestionList(exportReqVO);
        // 导出 Excel
        List<ZkPaperQuestionExcelVO> datas = BeanUtils.toBean(list, ZkPaperQuestionExcelVO.class);
        ExcelUtils.write(response, "试题.xls", "数据", ZkPaperQuestionExcelVO.class, datas);
    }

}
