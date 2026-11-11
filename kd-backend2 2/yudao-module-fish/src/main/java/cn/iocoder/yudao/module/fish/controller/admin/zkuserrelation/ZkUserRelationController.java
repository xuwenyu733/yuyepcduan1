package cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation;

import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkuserrelation.ZkUserRelationDO;
import cn.iocoder.yudao.module.fish.service.zkuserrelation.ZkUserRelationService;

@Tag(name = "管理后台 - 用户信息关联")
@RestController
@RequestMapping("/fish/zk-user-relation")
@Validated
@Slf4j
public class ZkUserRelationController {

    @Resource
    private ZkUserRelationService zkUserRelationService;

    @PostMapping("/create")
    @Operation(summary = "创建用户信息关联")
    // @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:create')")
    public CommonResult<Long> createZkUserRelation(@Valid @RequestBody ZkUserRelationSaveReqVO createReqVO) {
        log.warn("【创建用户信息关联-{}】start,参数:{}", TokenUtil.userInfo(), JSON.toJSONString(createReqVO));
        return success(zkUserRelationService.createZkUserRelation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户信息关联")
    @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:update')")
    public CommonResult<Boolean> updateZkUserRelation(@Valid @RequestBody ZkUserRelationSaveReqVO updateReqVO) {
        zkUserRelationService.updateZkUserRelation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户信息关联")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:delete')")
    public CommonResult<Boolean> deleteZkUserRelation(@RequestParam("id") Long id) {
        zkUserRelationService.deleteZkUserRelation(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用户信息关联")
                @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:delete')")
    public CommonResult<Boolean> deleteZkUserRelationList(@RequestParam("ids") List<Long> ids) {
        zkUserRelationService.deleteZkUserRelationListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户信息关联")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:query')")
    public CommonResult<ZkUserRelationRespVO> getZkUserRelation(@RequestParam("id") Long id) {
        ZkUserRelationDO zkUserRelation = zkUserRelationService.getZkUserRelation(id);
        return success(BeanUtils.toBean(zkUserRelation, ZkUserRelationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户信息关联分页")
    // @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:query')")
    public CommonResult<PageResult<ZkUserRelationRespVO>> getZkUserRelationPage(@Valid ZkUserRelationPageReqVO pageReqVO) {
        PageResult<ZkUserRelationDO> pageResult = zkUserRelationService.getZkUserRelationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ZkUserRelationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户信息关联 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-user-relation:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkUserRelationExcel(@Valid ZkUserRelationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkUserRelationDO> list = zkUserRelationService.getZkUserRelationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户信息关联.xls", "数据", ZkUserRelationRespVO.class,
                        BeanUtils.toBean(list, ZkUserRelationRespVO.class));
    }

}