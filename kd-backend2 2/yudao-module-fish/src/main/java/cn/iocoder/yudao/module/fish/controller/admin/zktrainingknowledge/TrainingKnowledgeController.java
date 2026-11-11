package cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.TextVo;
import cn.iocoder.yudao.module.fish.controller.admin.zktrainingknowledge.vo.*;
// import cn.iocoder.yudao.module.fish.convert.zktrainingknowledge.TrainingKnowledgeConvert;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktrainingknowledge.TrainingKnowledgeDO;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import cn.iocoder.yudao.module.fish.service.zktrainingknowledge.TrainingKnowledgeService;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 培训知识")
@RestController
@RequestMapping("/fish/training-knowledge")
@Validated
@Slf4j
public class TrainingKnowledgeController {

    @Resource
    private TrainingKnowledgeService trainingKnowledgeService;

    @Autowired
    private AiAgentService aiAgentService;

    @PostMapping("/create")
    @Operation(summary = "创建培训知识")
    public CommonResult<Long> createTrainingKnowledge(@Valid @RequestBody TrainingKnowledgeCreateReqVO createReqVO) {
        // 1. 同步到知识库
        if(StringUtils.equals(createReqVO.getStatus(), "1")) {
            String q_id = UUID.randomUUID().toString();
            String text = String.format("知识名称：%s，知识详情:%s，知识备注:%s", createReqVO.getKnowledgeName(),
                    createReqVO.getKnowledgeContent(), createReqVO.getKnowledgeRemark());
            aiAgentService.add_text(new TextVo(q_id, text));
            createReqVO.setAgentId(q_id);
        }
        // 2. 添加数据
        Long id = trainingKnowledgeService.createTrainingKnowledge(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新培训知识")
    public CommonResult<Boolean> updateTrainingKnowledge(@Valid @RequestBody TrainingKnowledgeUpdateReqVO updateReqVO) {
        // 1. 同步到知识库
        if(StringUtils.equals(updateReqVO.getStatus(), "1")) {
            String q_id = StringUtil.isEmptyWith0(updateReqVO.getAgentId())?UUID.randomUUID().toString():updateReqVO.getAgentId();
            String text = String.format("知识名称：%s，知识详情:%s，知识备注:%s", updateReqVO.getKnowledgeName(),
                    updateReqVO.getKnowledgeContent(), updateReqVO.getKnowledgeRemark());
            aiAgentService.add_text(new TextVo(q_id, text));
            updateReqVO.setAgentId(q_id);
        } else if(!StringUtils.equals(updateReqVO.getStatus(), "1") && StringUtils.isNotEmpty(updateReqVO.getAgentId())) {
            aiAgentService.delete_text(new TextVo(updateReqVO.getAgentId(), ""));
            updateReqVO.setAgentId("0");
        }
        trainingKnowledgeService.updateTrainingKnowledge(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除培训知识")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteTrainingKnowledge(@RequestParam("id") Long id) {
        TrainingKnowledgeDO trainingKnowledgeDO = trainingKnowledgeService.deleteTrainingKnowledge(id);
        // 2. 删除知识库
        if(!StringUtil.isEmptyWith0(trainingKnowledgeDO.getAgentId())) {
            aiAgentService.delete_text(new TextVo(trainingKnowledgeDO.getAgentId(), ""));
        }
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得培训知识")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<TrainingKnowledgeRespVO> getTrainingKnowledge(@RequestParam("id") Long id) {
        TrainingKnowledgeDO trainingKnowledge = trainingKnowledgeService.getTrainingKnowledge(id);
        return success(BeanUtils.toBean(trainingKnowledge, TrainingKnowledgeRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得培训知识列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<TrainingKnowledgeRespVO>> getTrainingKnowledgeList(@RequestParam("ids") Collection<Long> ids) {
        List<TrainingKnowledgeDO> list = trainingKnowledgeService.getTrainingKnowledgeList(ids);
        return success(BeanUtils.toBean(list, TrainingKnowledgeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得培训知识分页")
    public CommonResult<PageResult<TrainingKnowledgeRespVO>> getTrainingKnowledgePage(@Valid TrainingKnowledgePageReqVO pageVO) {
        PageResult<TrainingKnowledgeDO> pageResult = trainingKnowledgeService.getTrainingKnowledgePage(pageVO);
        return success(BeanUtils.toBean(pageResult, TrainingKnowledgeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出培训知识 Excel")
    // @OperateLog(type = EXPORT)
    public void exportTrainingKnowledgeExcel(@Valid TrainingKnowledgeExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<TrainingKnowledgeDO> list = trainingKnowledgeService.getTrainingKnowledgeList(exportReqVO);
        // 导出 Excel
        List<TrainingKnowledgeExcelVO> datas = BeanUtils.toBean(list, TrainingKnowledgeExcelVO.class);
        ExcelUtils.write(response, "培训知识.xls", "数据", TrainingKnowledgeExcelVO.class, datas);
    }

    @PostMapping("/judgeParams")
    @Operation(summary = "创建培训知识判断参数是否缺少")
    public CommonResult<String> judgeParams(String params) {
        log.warn("【创建培训知识判断参数是否缺少-{}】middle,接受参数:{}", TokenUtil.userInfo(), params);
        // 槽位0 properties： 属性名， 槽位1 json_serializable： json序列化说明， 槽位2 property_enum： 属性信息枚举， 槽位3 eg： 示例， 槽位4 remark： 参数交互描述
        String t1 = "properties \n json_serializable \n property_enum \n eg \n remark";
        t1 = t1.replace("properties", "添加【培训知识】数据需要收集的属性信息有： 知识名称、知识详情、知识备注 ；");
        t1 = t1.replace("json_serializable", "json序列化过程中，属性名不要用中文，知识名称用knowledgeName替换、知识详情用knowledgeContent替换、知识备注用knowledgeRemark替换 。");
        t1 = t1.replace("property_enum", "");
        t1 = t1.replace("eg", "添加【培训知识】数据输入示例： {\"knowledgeName\":\"鱼烂鳃\",\"knowledgeContent\":\"出现鱼烂鳃一般可以使用碘伏消毒。\",\"knowledgeRemark\":\"注意隔离\"}");
        t1 = t1.replace("remark", "添加【培训知识】数据缺少必要属性数据时，作为助手的你可以向用户索取数据，轻易不要使用示例中数据。");
        if(StringUtils.isEmpty(params)) {
            return CommonResult.error(403, t1);
        }
        try {
            TrainingKnowledgeCreateReqVO vo = JSON.parseObject(params, TrainingKnowledgeCreateReqVO.class);
            if(StringUtils.isEmpty(vo.getKnowledgeName()) && StringUtils.isEmpty(vo.getKnowledgeContent()) && StringUtils.isEmpty(vo.getKnowledgeRemark())
            ) {
                return CommonResult.error(403, t1);
            } else if(StringUtils.isEmpty(vo.getKnowledgeName()) || StringUtils.isEmpty(vo.getKnowledgeContent()) || StringUtils.isEmpty(vo.getKnowledgeRemark())){
                return CommonResult.error(403, String.format(t1));
            }
            return success(String.format("培训知识 模块参数：%s，校验合格", JSON.toJSONString(vo)));
        } catch (Exception e) {
            return CommonResult.error(403, t1);
        }
    }

}
/**

 String eg = "输入示例： '培训知识__\"userId\": \"223\", \"token\": \"Bearer e3434e8955864d0fa5f19f5c4695378e\"__{\"knowledgeName\":\"鱼烂鳃\",\"knowledgeContent\":\"出现鱼烂鳃一般可以使用碘伏消毒。\",\"knowledgeRemark\":\"注意隔离\"}'。\n";
 String notice = String.format("添加培训知识数据缺少参数，下面是参数描述：\n" +
 "输入格式： '模块名__权限参数__{\"knowledgeName\":\"知识名称\",\"knowledgeContent\":\"知识详情\",\"knowledgeRemark\":\"知识备注\"}'，\n" +
 eg +
 "当发现用户想要添加培训知识，缺少参数时，需要跟用户询问其他参数，以下是提示示例：\n" +
 "       您还有知识名称没有告诉我呢！\n" +
 "       您还有知识详情没有告诉我呢！\n" +
 "       您还有知识备注没有告诉我呢！\n" +
 "用户输入后，将用户填写的培训知识信息进行汇总，汇总的参数格式参考\"输入格式和输入示例\"。");

 */