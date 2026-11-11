package cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkschedulinguser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulinguser.ZkSchedulingUserDO;
// import cn.iocoder.yudao.module.fish.convert.zkschedulinguser.ZkSchedulingUserConvert;
import cn.iocoder.yudao.module.fish.service.zkschedulinguser.ZkSchedulingUserService;

@Tag(name = "管理后台 - 排班用户")
@RestController
@RequestMapping("/fish/zk-scheduling-user")
@Validated
public class ZkSchedulingUserController {

    @Resource
    private ZkSchedulingUserService zkSchedulingUserService;

    @PostMapping("/create")
    @Operation(summary = "创建排班用户")
    public CommonResult<Long> createZkSchedulingUser(@Valid @RequestBody ZkSchedulingUserCreateReqVO createReqVO) {
        return success(zkSchedulingUserService.createZkSchedulingUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新排班用户")
    public CommonResult<Boolean> updateZkSchedulingUser(@Valid @RequestBody ZkSchedulingUserUpdateReqVO updateReqVO) {
        zkSchedulingUserService.updateZkSchedulingUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除排班用户")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkSchedulingUser(@RequestParam("id") Long id) {
        zkSchedulingUserService.deleteZkSchedulingUser(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得排班用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkSchedulingUserRespVO> getZkSchedulingUser(@RequestParam("id") Long id) {
        ZkSchedulingUserDO zkSchedulingUser = zkSchedulingUserService.getZkSchedulingUser(id);
        return success(BeanUtils.toBean(zkSchedulingUser, ZkSchedulingUserRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得排班用户列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkSchedulingUserRespVO>> getZkSchedulingUserList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkSchedulingUserDO> list = zkSchedulingUserService.getZkSchedulingUserList(ids);
        return success(BeanUtils.toBean(list, ZkSchedulingUserRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得排班用户分页")
    public CommonResult<PageResult<ZkSchedulingUserRespVO>> getZkSchedulingUserPage(@Valid ZkSchedulingUserPageReqVO pageVO) {
        PageResult<ZkSchedulingUserDO> pageResult = zkSchedulingUserService.getZkSchedulingUserPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkSchedulingUserRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出排班用户 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkSchedulingUserExcel(@Valid ZkSchedulingUserExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkSchedulingUserDO> list = zkSchedulingUserService.getZkSchedulingUserList(exportReqVO);
        // 导出 Excel
        List<ZkSchedulingUserExcelVO> datas = BeanUtils.toBean(list, ZkSchedulingUserExcelVO.class);
        ExcelUtils.write(response, "排班用户.xls", "数据", ZkSchedulingUserExcelVO.class, datas);
    }

}
