/**
 *
 */
package cn.iocoder.yudao.module.bpm.controller.admin.model;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.bpm.controller.admin.model.vo.ModelPageItemRespVO;
import cn.iocoder.yudao.module.bpm.controller.admin.model.vo.ModelPageReqVO;
import cn.iocoder.yudao.module.bpm.service.other.WfModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


/**
 * 流程模型设计
 * @author hushouquan
 *
 */
@Tag(name = "管理后台 - 流程模型")
@RestController
@RequestMapping("/workflow/model")
@Validated
public class ModelController {

    @Resource
    private WfModelService modelService;

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    public CommonResult<PageResult<ModelPageItemRespVO>> getModelPage(ModelPageReqVO pageVO) {
        return success(modelService.getModelPage(pageVO));
    }

}
