package cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.TaskDetail2FishProofing;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.params.ProofingParam;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkfishproofing.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishproofing.ZkFishProofingDO;
// import cn.iocoder.yudao.module.fish.convert.zkfishproofing.ZkFishProofingConvert;
import cn.iocoder.yudao.module.fish.service.zkfishproofing.ZkFishProofingService;

@Tag(name = "管理后台 - 打样记录")
@RestController
@RequestMapping("/fish/zk-fish-proofing")
@Validated
public class ZkFishProofingController {

    @Resource
    private ZkFishProofingService zkFishProofingService;
    @Resource
    private TaskDetailService taskDetailService;

    @PostMapping("/create")
    @Operation(summary = "创建打样记录")
    public CommonResult<Long> createZkFishProofing(@Valid @RequestBody ZkFishProofingCreateReqVO createReqVO) {
        return success(zkFishProofingService.createZkFishProofing(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新打样记录")
    public CommonResult<Boolean> updateZkFishProofing(@Valid @RequestBody ZkFishProofingUpdateReqVO updateReqVO) {
        zkFishProofingService.updateZkFishProofing(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除打样记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkFishProofing(@RequestParam("id") Long id) {
        zkFishProofingService.deleteZkFishProofing(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得打样记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkFishProofingRespVO> getZkFishProofing(@RequestParam("id") Long id) {
        ZkFishProofingDO zkFishProofing = zkFishProofingService.getZkFishProofing(id);
        if(zkFishProofing != null) listEnhancer(List.of(zkFishProofing));
        return success(BeanUtils.toBean(zkFishProofing, ZkFishProofingRespVO.class));
    }
    @GetMapping("/getLastProofing")
    @Operation(summary = "获得最新存塘鱼苗规格")
    @Parameter(name = "pondId", description = "存塘编号", required = true, example = "1024")
    public CommonResult<ZkFishProofingRespVO> getLastProofing(@RequestParam("pondId") Long pondId) {
        if(pondId == null) throw new CommonException(403, "pondId参数不能为空");
        ZkFishProofingDO zkFishProofing = zkFishProofingService.getLastProofing(pondId);
        return success(BeanUtils.toBean(zkFishProofing, ZkFishProofingRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得打样记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkFishProofingRespVO>> getZkFishProofingList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkFishProofingDO> list = zkFishProofingService.getZkFishProofingList(ids);
        return success(BeanUtils.toBean(list, ZkFishProofingRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得打样记录分页")
    public CommonResult<PageResult<ZkFishProofingRespVO>> getZkFishProofingPage(@Valid ZkFishProofingPageReqVO pageVO) {
        PageResult<ZkFishProofingDO> pageResult = zkFishProofingService.getZkFishProofingPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkFishProofingRespVO.class));
    }

    /**
     * 打样记录 设置 任务明细信息
     * @param list
     */
    public void listEnhancer(List<ZkFishProofingDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 任务明细
        Set<Long> taskDetailIds = list.stream().map(ZkFishProofingDO::getTaskDetailId).collect(Collectors.toSet());
        List<TaskDetailDO> taskDetailList = taskDetailService.getByIds(taskDetailIds);
        if(EasyListUtil.isEmpty(taskDetailList)) return;
        // 2. 任务明细 转换成 list
        taskDetailList.forEach(obj -> {
            List<ProofingParam> proofingParams = TaskDetail2FishProofing.transformer2proofing(obj);
            obj.setProofingParamList(proofingParams);
        });
        // 3. 赋值 给到 zk_fish_proofing
        for(ZkFishProofingDO curr : list) {
            TaskDetailDO temp = null;
            for(TaskDetailDO remote : taskDetailList) {
                if(Objects.equals(curr.getTaskDetailId(), remote.getId())) {
                    temp = remote;
                    break;
                }
            }
            if(temp != null) {
                curr.setTaskDetailDO(temp);
            }
        }
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出打样记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkFishProofingExcel(@Valid ZkFishProofingExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkFishProofingDO> list = zkFishProofingService.getZkFishProofingList(exportReqVO);
        // 导出 Excel
        List<ZkFishProofingExcelVO> datas = BeanUtils.toBean(list, ZkFishProofingExcelVO.class);
        ExcelUtils.write(response, "打样记录.xls", "数据", ZkFishProofingExcelVO.class, datas);
    }

}
