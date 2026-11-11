package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent;

import cn.iocoder.yudao.framework.common.util.easy.EasyDataMapper;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailRespVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpond.ZkPondMapper;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
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

import java.math.BigDecimal;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishparent.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishparent.ZkPondFishParentDO;
import cn.iocoder.yudao.module.fish.service.zkpondfishparent.ZkPondFishParentService;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.ZkPondFishDetailCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.ZkPondFishDetailController;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import org.springframework.transaction.annotation.Transactional;

@Tag(name = "管理后台 - 鱼池操作父")
@RestController
@RequestMapping("/fish/zk-pond-fish-parent")
@Validated
public class ZkPondFishParentController {

    @Resource
    private ZkPondFishParentService zkPondFishParentService;

    @Resource
    private ZkPondFishDetailController zkPondFishDetailController;

    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;

    @Resource
    private ZkPondMapper zkPondMapper;

    @Resource
    private ZkPondService zkPondService;

    @PostMapping("/create")
    @Operation(summary = "创建鱼池操作父")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:create')")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Long> createZkPondFishParent(@Valid @RequestBody ZkPondFishParentSaveReqVO createReqVO) {
        // 1. 创建父表记录
        Long parentId = zkPondFishParentService.createZkPondFishParent(createReqVO);

        createDetails(createReqVO.getDetails(), parentId);
        return success(parentId);
    }

    private void createDetails(List<ZkPondFishDetailCreateReqVO> details, Long parentId) {
        // 2. 如果有明细，则创建明细
        if (details != null && !details.isEmpty()) {
            ZkPondFishDetailCreateReqVO saleReq = null;
            for (ZkPondFishDetailCreateReqVO detailReqVO : details) {
                detailReqVO.setParentId(parentId);
                // 如果是自己给自己分池，直接设置
                if(Objects.equals(detailReqVO.getPondId(), detailReqVO.getTargetPondId())) {
                    saleReq = detailReqVO;
                    continue;
                }
                // 复用之前的分池逻辑
                zkPondFishDetailController.createZkPondFishDetail(detailReqVO);
                // 更新avg fishSpec 到 zk_pond和zk_pond_fish
                zkPondService.updateFishSpecByPondId(detailReqVO.getTargetPondId(), detailReqVO.getSplitAvgFishSpec());
            }
            if(saleReq != null) {
                zkPondFishDetailController.createZkPondFishDetail(saleReq);
                // 更新avg fishSpec 到 zk_pond和zk_pond_fish
                BigDecimal fishSpec = Objects.equals(saleReq.getFishNum(), 0)?new BigDecimal(0):saleReq.getSplitAvgFishSpec();
                zkPondService.updateFishSpecByPondId(saleReq.getTargetPondId(), fishSpec);
            }
        }
    }

    @PutMapping("/update")
    @Operation(summary = "更新鱼池操作父")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:update')")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateZkPondFishParent(@Valid @RequestBody ZkPondFishParentSaveReqVO updateReqVO) {
        // 1. 更新父表
        zkPondFishParentService.updateZkPondFishParent(updateReqVO);

        // 2. 处理明细 (先删除旧的，再添加新的，或者根据ID更新)
        // 这里简单处理：删除原有关联的明细，重新添加
        List<ZkPondFishDetailDO> oldDetails = zkPondFishDetailService.getZkPondFishDetailListByParentId(updateReqVO.getId());
        if (oldDetails != null && !oldDetails.isEmpty()) {
            for (ZkPondFishDetailDO oldDetail : oldDetails) {
                zkPondFishDetailController.deleteZkPondFishDetail(oldDetail.getId());
            }
        }

        createDetails(updateReqVO.getDetails(), updateReqVO.getId());

        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除鱼池操作父")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:delete')")
    public CommonResult<Boolean> deleteZkPondFishParent(@RequestParam("id") Long id) {
        zkPondFishParentService.deleteZkPondFishParent(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除鱼池操作父")
                @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:delete')")
    public CommonResult<Boolean> deleteZkPondFishParentList(@RequestParam("ids") List<Long> ids) {
        zkPondFishParentService.deleteZkPondFishParentListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得鱼池操作父")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:query')")
    public CommonResult<ZkPondFishParentRespVO> getZkPondFishParent(@RequestParam("id") Long id) {
        ZkPondFishParentDO zkPondFishParent = zkPondFishParentService.getZkPondFishParent(id);
        enhancerList(List.of(zkPondFishParent));
        ZkPondFishParentRespVO respVO = BeanUtils.toBean(zkPondFishParent, ZkPondFishParentRespVO.class);
        // 填充明细
        List<ZkPondFishDetailDO> details = zkPondFishDetailService.getZkPondFishDetailListByParentId(id);
        if (details != null && !details.isEmpty()) {
            respVO.setDetails(BeanUtils.toBean(details, ZkPondFishDetailRespVO.class));
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得鱼池操作父分页")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:query')")
    public CommonResult<PageResult<ZkPondFishParentRespVO>> getZkPondFishParentPage(@Valid ZkPondFishParentPageReqVO pageReqVO) {
        PageResult<ZkPondFishParentDO> pageResult = zkPondFishParentService.getZkPondFishParentPage(pageReqVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkPondFishParentRespVO.class));
    }

    @SuppressWarnings("unchecked")
    public void enhancerList(List<ZkPondFishParentDO> tList) {
        // 1. 获取 关联 值
        Set<Long> oIds = EasyListUtil.listToIds(tList, ZkPondFishParentDO::getOriginPondId);
        // 2. 利用 关联值 查询
        List<ZkPondDO> oList = EasyDataMapper.listByValueList(zkPondMapper, ZkPondDO::getId, oIds);
        // 3. 赋值
        CommonUtil.origins2Targets4(oList, ZkPondDO::getId, EasyListUtil.getList(ZkPondDO::getPondDetailNum),
                tList, ZkPondFishParentDO::getOriginPondId, EasyListUtil.setList(ZkPondFishParentDO::setOriginPondDetailNum));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出鱼池操作父 Excel")
    @PreAuthorize("@ss.hasPermission('fish:zk-pond-fish-parent:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportZkPondFishParentExcel(@Valid ZkPondFishParentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ZkPondFishParentDO> list = zkPondFishParentService.getZkPondFishParentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "鱼池操作父.xls", "数据", ZkPondFishParentRespVO.class,
                        BeanUtils.toBean(list, ZkPondFishParentRespVO.class));
    }

}