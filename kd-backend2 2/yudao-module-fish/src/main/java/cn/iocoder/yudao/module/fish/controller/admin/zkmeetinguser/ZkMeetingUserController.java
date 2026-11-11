package cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser;

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

import cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
// import cn.iocoder.yudao.module.fish.convert.zkmeetinguser.ZkMeetingUserConvert;
import cn.iocoder.yudao.module.fish.service.zkmeetinguser.ZkMeetingUserService;

@Tag(name = "管理后台 - 会议用户")
@RestController
@RequestMapping("/fish/zk-meeting-user")
@Validated
public class ZkMeetingUserController {

    @Resource
    private ZkMeetingUserService zkMeetingUserService;

    @PostMapping("/create")
    @Operation(summary = "创建会议用户")
    public CommonResult<Long> createZkMeetingUser(@Valid @RequestBody ZkMeetingUserCreateReqVO createReqVO) {
        return success(zkMeetingUserService.createZkMeetingUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会议用户")
    public CommonResult<Boolean> updateZkMeetingUser(@Valid @RequestBody ZkMeetingUserUpdateReqVO updateReqVO) {
        zkMeetingUserService.updateZkMeetingUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会议用户")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkMeetingUser(@RequestParam("id") Long id) {
        zkMeetingUserService.deleteZkMeetingUser(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会议用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkMeetingUserRespVO> getZkMeetingUser(@RequestParam("id") Long id) {
        ZkMeetingUserDO zkMeetingUser = zkMeetingUserService.getZkMeetingUser(id);
        return success(BeanUtils.toBean(zkMeetingUser, ZkMeetingUserRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得会议用户列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkMeetingUserRespVO>> getZkMeetingUserList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkMeetingUserDO> list = zkMeetingUserService.getZkMeetingUserList(ids);
        return success(BeanUtils.toBean(list, ZkMeetingUserRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得会议用户分页")
    public CommonResult<PageResult<ZkMeetingUserRespVO>> getZkMeetingUserPage(@Valid ZkMeetingUserPageReqVO pageVO) {
        PageResult<ZkMeetingUserDO> pageResult = zkMeetingUserService.getZkMeetingUserPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkMeetingUserRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会议用户 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkMeetingUserExcel(@Valid ZkMeetingUserExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkMeetingUserDO> list = zkMeetingUserService.getZkMeetingUserList(exportReqVO);
        // 导出 Excel
        List<ZkMeetingUserExcelVO> datas = BeanUtils.toBean(list, ZkMeetingUserExcelVO.class);
        ExcelUtils.write(response, "会议用户.xls", "数据", ZkMeetingUserExcelVO.class, datas);
    }

}
