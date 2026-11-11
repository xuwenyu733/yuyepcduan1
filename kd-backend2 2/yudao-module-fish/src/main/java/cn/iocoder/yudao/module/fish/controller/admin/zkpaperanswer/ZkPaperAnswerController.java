package cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.to.AnswerVo;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaper.ZkPaperDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperquestion.ZkPaperQuestionDO;
import cn.iocoder.yudao.module.fish.service.zkpaper.ZkPaperService;
import cn.iocoder.yudao.module.fish.service.zkpaperquestion.ZkPaperQuestionService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpaperanswer.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpaperanswer.ZkPaperAnswerDO;
// import cn.iocoder.yudao.module.fish.convert.zkpaperanswer.ZkPaperAnswerConvert;
import cn.iocoder.yudao.module.fish.service.zkpaperanswer.ZkPaperAnswerService;

@Tag(name = "管理后台 - 答题记录")
@RestController
@RequestMapping("/fish/zk-paper-answer")
@Validated
@Slf4j
public class ZkPaperAnswerController {

    @Resource
    private ZkPaperAnswerService zkPaperAnswerService;

    @Resource
    private ZkPaperQuestionService zkPaperQuestionService;

    @Resource
    private ZkPaperService zkPaperService;

    @PostMapping("/create")
    @Operation(summary = "创建答题记录")
    public CommonResult<Long> createZkPaperAnswer(@Valid @RequestBody ZkPaperAnswerCreateReqVO obj) {
        log.warn("答题记录====================obj:{}", JSON.toJSONString(obj));
        if(obj == null || StringUtils.isBlank(obj.getAnswer()))
            throw new CommonException(403, "参数不能为空");
        List<AnswerVo> answerVos = JSON.parseArray(obj.getAnswer(), AnswerVo.class);
        List<Long> questionIds = answerVos.stream().map(iii -> iii.getQuestionId()).collect(Collectors.toList());
        // 1. 获取试题并计算分数
        List<ZkPaperQuestionDO> serPaperQuestions = zkPaperQuestionService.listByIds(questionIds);
        Double totalScore = Double.valueOf(0.00);
        for(AnswerVo vo : answerVos) {
            ZkPaperQuestionDO currQuestion = getById(vo.getQuestionId(), serPaperQuestions);
            if(org.apache.commons.lang3.StringUtils.equals(currQuestion.getAnswer(), vo.getAnswer())) {
                totalScore += currQuestion.getScore();
            }
        }
        // 2. 判断是否及格
        ZkPaperDO paper = zkPaperService.getZkPaper(obj.getPaperId());
        if(totalScore >= paper.getPassScore()) {
            obj.setPassed("1");
        } else obj.setPassed("0");
        obj.setScore(totalScore);

        Long zkPaperAnswer = zkPaperAnswerService.createZkPaperAnswer(obj);

        return success(zkPaperAnswer).put("zkPaperAnswer", obj);
    }

    public ZkPaperQuestionDO getById(Long questionId, List<ZkPaperQuestionDO> serPaperQuestions) {
        for(ZkPaperQuestionDO serPaperQuestion : serPaperQuestions) {
            if(Objects.equals(questionId, serPaperQuestion.getId())) {
                return serPaperQuestion;
            }
        }
        return null;
    }

    @PutMapping("/update")
    @Operation(summary = "更新答题记录")
    public CommonResult<Boolean> updateZkPaperAnswer(@Valid @RequestBody ZkPaperAnswerUpdateReqVO updateReqVO) {
        zkPaperAnswerService.updateZkPaperAnswer(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除答题记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPaperAnswer(@RequestParam("id") Long id) {
        zkPaperAnswerService.deleteZkPaperAnswer(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得答题记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPaperAnswerRespVO> getZkPaperAnswer(@RequestParam("id") Long id) {
        ZkPaperAnswerDO zkPaperAnswer = zkPaperAnswerService.getZkPaperAnswer(id);
        // 2. 填充其他信息
        enhancer(Arrays.asList(zkPaperAnswer));
        return success(BeanUtils.toBean(zkPaperAnswer, ZkPaperAnswerRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得答题记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPaperAnswerRespVO>> getZkPaperAnswerList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPaperAnswerDO> list = zkPaperAnswerService.getZkPaperAnswerList(ids);
        // 2. 填充其他信息
        enhancer(list);
        return success(BeanUtils.toBean(list, ZkPaperAnswerRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得答题记录分页")
    public CommonResult<PageResult<ZkPaperAnswerRespVO>> getZkPaperAnswerPage(@Valid ZkPaperAnswerPageReqVO pageVO) {
        // 1. 获取答题记录
        PageResult<ZkPaperAnswerDO> pageResult = zkPaperAnswerService.getZkPaperAnswerPage(pageVO);
        // 2. 填充其他信息
        enhancer(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkPaperAnswerRespVO.class));
    }

    public void enhancer(List<ZkPaperAnswerDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取试卷列表
        Set<Long> paperIds = list.stream().map(obj -> obj.getPaperId()).collect(Collectors.toSet());
        List<ZkPaperDO> zkPaperList = zkPaperService.getZkPaperList(new ArrayList<>(paperIds));
        // 2. 填充信息
        for(ZkPaperDO item : zkPaperList) {
            for(ZkPaperAnswerDO answer : list) {
                if(Objects.equals(answer.getPaperId(), item.getId())) {
                    answer.setPaperName(item.getName());
                }
            }
        }
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出答题记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPaperAnswerExcel(@Valid ZkPaperAnswerExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPaperAnswerDO> list = zkPaperAnswerService.getZkPaperAnswerList(exportReqVO);
        // 导出 Excel
        List<ZkPaperAnswerExcelVO> datas = BeanUtils.toBean(list, ZkPaperAnswerExcelVO.class);
        ExcelUtils.write(response, "答题记录.xls", "数据", ZkPaperAnswerExcelVO.class, datas);
    }

}
