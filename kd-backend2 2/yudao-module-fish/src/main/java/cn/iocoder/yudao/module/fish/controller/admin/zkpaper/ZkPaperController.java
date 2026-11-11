package cn.iocoder.yudao.module.fish.controller.admin.zkpaper;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyFile;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaper.to.PaperQuestionReader;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import cn.iocoder.yudao.module.fish.service.zkpaperquestion.ZkPaperQuestionService;
import org.springframework.util.CollectionUtils;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpaper.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
// import cn.iocoder.yudao.module.fish.convert.zkpaper.ZkPaperConvert;
import cn.iocoder.yudao.module.fish.service.zkpaper.ZkPaperService;

@Tag(name = "管理后台 - 试卷")
@RestController
@RequestMapping("/fish/zk-paper")
@Validated
public class ZkPaperController {

    @Resource
    private ZkPaperService zkPaperService;

    @Resource
    private ZkPaperQuestionService zkPaperQuestionService;

    @PostMapping("/create")
    @Operation(summary = "创建试卷")
    public CommonResult<Long> createZkPaper(@Valid @RequestBody ZkPaperCreateReqVO createReqVO) {
        // 1. 获取excel里面试题
        String filePath = EasyFile.downloadByUrl(createReqVO.getExamUrl());
        List<ZkPaperQuestionDO> serPaperQuestions = PaperQuestionReader.reade(filePath);
        if(CollectionUtils.isEmpty(serPaperQuestions))
            throw new CommonException(403, "从试题文件中读取的试题为空");
        // 2. 添加试卷
        ZkPaperDO zkPaper = zkPaperService.createZkPaper(createReqVO);
        // 3. 添加试题
        serPaperQuestions.forEach(item -> {
            item.setPaperId(zkPaper.getId());
            item.setDeptId(zkPaper.getDeptId());
            item.setUserId(zkPaper.getUserId());
        });
        zkPaperQuestionService.customSaveBatch(serPaperQuestions);
        return success(zkPaper.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新试卷")
    public CommonResult<Boolean> updateZkPaper(@Valid @RequestBody ZkPaperUpdateReqVO updateReqVO) {
        // 1. 更新试卷
        ZkPaperDO zkPaperDO = zkPaperService.updateZkPaper(updateReqVO);
        // 2. 判断是否需要更新试题
        if(StringUtil.isNotEmptyWith0(updateReqVO.getExamUrl())) {
            updatePaperQuestion(updateReqVO.getExamUrl(), zkPaperDO);
        }
        return success(true);
    }

    public void updatePaperQuestion(String url, ZkPaperDO zkPaperDO) {
        // 1. 获取excel里面试题
        String filePath = EasyFile.downloadByUrl(url);
        List<ZkPaperQuestionDO> serPaperQuestions = PaperQuestionReader.reade(filePath);
        if(CollectionUtils.isEmpty(serPaperQuestions))
            throw new CommonException(403, "从试题文件中读取的试题为空");
        // 2. 删除之前试题
        zkPaperQuestionService.removeByPaperId(zkPaperDO.getId());
        // 3. 添加试题
        serPaperQuestions.forEach(item -> {
            item.setPaperId(zkPaperDO.getId());
            item.setDeptId(zkPaperDO.getDeptId());
            item.setUserId(zkPaperDO.getUserId());
        });
        zkPaperQuestionService.customSaveBatch(serPaperQuestions);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试卷")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPaper(@RequestParam("id") Long id) {
        zkPaperService.deleteZkPaper(id);
        // 2. 删除试题
        zkPaperQuestionService.removeByPaperId(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得试卷")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPaperRespVO> getZkPaper(@RequestParam("id") Long id) {
        ZkPaperDO zkPaper = zkPaperService.getZkPaper(id);
        return success(BeanUtils.toBean(zkPaper, ZkPaperRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得试卷列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPaperRespVO>> getZkPaperList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPaperDO> list = zkPaperService.getZkPaperList(ids);
        return success(BeanUtils.toBean(list, ZkPaperRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得试卷分页")
    public CommonResult<PageResult<ZkPaperRespVO>> getZkPaperPage(@Valid ZkPaperPageReqVO pageVO) {
        PageResult<ZkPaperDO> pageResult = zkPaperService.getZkPaperPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkPaperRespVO.class));
    }

    @GetMapping("/listForNeedExam")
    @Operation(summary = "获得试卷分页")
    public CommonResult<List<ZkPaperRespVO>> pageForNeedExam(@Valid ZkPaperPageReqVO pageVO) {
        // 1. 获取需要考试的记录
        List<ZkPaperDO> pageResult = zkPaperService.listForNeedExam(pageVO);

        return success(BeanUtils.toBean(pageResult, ZkPaperRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出试卷 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPaperExcel(@Valid ZkPaperExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPaperDO> list = zkPaperService.getZkPaperList(exportReqVO);
        // 导出 Excel
        List<ZkPaperExcelVO> datas = BeanUtils.toBean(list, ZkPaperExcelVO.class);
        ExcelUtils.write(response, "试卷.xls", "数据", ZkPaperExcelVO.class, datas);
    }

}
