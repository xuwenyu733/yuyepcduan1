package cn.iocoder.yudao.module.fish.controller.admin.zkexperimental;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimental.vo.ZkExperimentalAnalysisSummaryRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.ZkExperimentalPermissionUserPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo.ZkExperimentalPermissionUserRespVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo.ZkExperimentalTemplatePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaltemplate.vo.ZkExperimentalTemplateRespVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalpermission.ZkExperimentalPermissionUserDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaltemplate.ZkExperimentalTemplateDO;
import cn.iocoder.yudao.module.fish.service.zkexperimental.ZkExperimentalService;
import cn.iocoder.yudao.module.fish.service.zkexperimentalpermission.ZkExperimentalPermissionService;
import cn.iocoder.yudao.module.fish.service.zkexperimentaltemplate.ZkExperimentalTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 实验模块前端/BFF 路径兼容（与 /fish/zk-experimental-* 能力一致）
 */
@Tag(name = "管理后台 - 实验（前端路径兼容）")
@RestController
@RequestMapping("/fish/experiment")
@Validated
public class ZkExperimentFrontCompatController {

    @Resource
    private ZkExperimentalTemplateService zkExperimentalTemplateService;
    @Resource
    private ZkExperimentalPermissionService zkExperimentalPermissionService;
    @Resource
    private ZkExperimentalService zkExperimentalService;

    @GetMapping("/templates")
    @Operation(summary = "实验模板列表（兼容 BFF /api/experiment/templates）")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-template:query') or @ss.hasPermission('fish:zk-experimental:query')")
    public CommonResult<List<ZkExperimentalTemplateRespVO>> getTemplates(@Valid ZkExperimentalTemplatePageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalTemplateDO> list = zkExperimentalTemplateService.getZkExperimentalTemplatePage(pageReqVO).getList();
        return success(BeanUtils.toBean(list, ZkExperimentalTemplateRespVO.class));
    }

    @GetMapping("/permissions/users")
    @Operation(summary = "实验权限用户列表（兼容 BFF /api/experiment/permissions/users）")
    @PreAuthorize("@ss.hasPermission('fish:zk-experimental-permission:query') or @ss.hasPermission('fish:zk-experimental:query')")
    public CommonResult<List<ZkExperimentalPermissionUserRespVO>> getPermissionUsers(
            @Valid ZkExperimentalPermissionUserPageReqVO pageReqVO) {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkExperimentalPermissionUserDO> list = zkExperimentalPermissionService.getUserList(pageReqVO);
        return success(BeanUtils.toBean(list, ZkExperimentalPermissionUserRespVO.class));
    }

    @GetMapping("/analysis/summary")
    @Operation(summary = "实验数据分析汇总（兼容 BFF /api/experiment/analysis/summary）")
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
