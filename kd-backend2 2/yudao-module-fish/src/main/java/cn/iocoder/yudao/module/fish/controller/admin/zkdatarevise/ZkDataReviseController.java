package cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.to.DataReviseTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.ZkPondFishDetailController;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkfeedinglog.ZkFeedingLogService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkdatarevise.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdatarevise.ZkDataReviseDO;
// import cn.iocoder.yudao.module.fish.convert.zkdatarevise.ZkDataReviseConvert;
import cn.iocoder.yudao.module.fish.service.zkdatarevise.ZkDataReviseService;

@Tag(name = "管理后台 - 数据修正记录")
@RestController
@RequestMapping("/fish/zk-data-revise")
@Validated
public class ZkDataReviseController {

    @Resource
    private ZkDataReviseService zkDataReviseService;
    @Resource
    private ZkPondFishDetailController zkPondFishDetailController;
    @Resource
    private ZkFeedingLogService zkFeedingLogService;
    @Resource
    private ZkPondService zkPondService;

    @PostMapping("/create")
    @Operation(summary = "创建数据修正记录")
    public CommonResult<Long> createZkDataRevise(@Valid @RequestBody ZkDataReviseCreateReqVO createReqVO) {
        createReqVO.build_reviseTypeName();
        // 1. 判断修正类型
        if(StringUtils.equals(DataReviseTypeEnum.PUSH.getReviseType(), createReqVO.getReviseType())) { // 投放
            handle_push(createReqVO);
        } else if (StringUtils.equals(DataReviseTypeEnum.DEAD.getReviseType(), createReqVO.getReviseType())) { // 死亡
            handle_dead(createReqVO);
        } else if (StringUtils.equals(DataReviseTypeEnum.SPLIT.getReviseType(), createReqVO.getReviseType())) { // 分池
            handle_split(createReqVO);
        } else if (StringUtils.equals(DataReviseTypeEnum.FEED.getReviseType(), createReqVO.getReviseType())) { // 投喂
            handle_feed(createReqVO);
        } else if (StringUtils.equals(DataReviseTypeEnum.POND.getReviseType(), createReqVO.getReviseType())) { // 存塘
            handle_pond(createReqVO);
        } else { // 错误类型
            throw new CommonException(403, "错误修正类型");
        }
        return success(zkDataReviseService.createZkDataRevise(createReqVO));
    }

    public void handle_pond(ZkDataReviseCreateReqVO createReqVO) {
        ZkPondDO pondData = createReqVO.getPondData();
        if(pondData == null)
            throw new CommonException(403, "修正的存塘数据为空");
        Long fishNum = pondData.getFishNum();
        Integer differenceData = Integer.parseInt(createReqVO.getDifferenceData());
        Long newFishNum = fishNum + differenceData;
        pondData.build_reviseLog(fishNum.toString());
        pondData.setFishNum(newFishNum);
        zkPondService.updateById(pondData);
    }

    public void handle_feed(ZkDataReviseCreateReqVO createReqVO) {
        ZkFeedingLogDO feedingLog = createReqVO.getFeedingLog();
        if(feedingLog == null)
            throw new CommonException(403, "修正的投喂数据为空");
        BigDecimal feedCount = feedingLog.getFeedCount();
        BigDecimal differenceData = new BigDecimal(createReqVO.getDifferenceData());
        BigDecimal newFeedCount = EasyBigDecimalUtil.add(feedCount, differenceData);
        feedingLog.build_reviseLog(feedCount.toString());
        feedingLog.setFeedCount(newFeedCount);
        zkFeedingLogService.customUpdate(feedingLog);
    }

    public void handle_push(ZkDataReviseCreateReqVO createReqVO) {
        ZkPondFishDetailDO fishPush = createReqVO.getFishPush();
        if(fishPush == null)
            throw new CommonException(403, "修正的投放数据为空");
        Long fishNum = fishPush.getFishNum();
        Long differenceData = Long.parseLong(createReqVO.getDifferenceData());
        Long newFishNum = fishNum + differenceData;
        fishPush.build_reviseLog(fishNum.toString(), differenceData);
        fishPush.setFishNum(newFishNum);
        zkPondFishDetailController.updateFishDetail(fishPush);
    }
    public void handle_dead(ZkDataReviseCreateReqVO createReqVO) {
        ZkPondFishDetailDO fishPush = createReqVO.getFishDead();
        if(fishPush == null)
            throw new CommonException(403, "修正的死亡数据为空");
        Long fishNum = fishPush.getFishNum();
        Long differenceData = Long.parseLong(createReqVO.getDifferenceData());
        Long newFishNum = fishNum + differenceData;
        fishPush.build_reviseLog(fishNum.toString(), differenceData);
        fishPush.setFishNum(newFishNum);
        zkPondFishDetailController.updateFishDetail(fishPush);
    }
    public void handle_split(ZkDataReviseCreateReqVO createReqVO) {
        ZkPondFishDetailDO fishPush = createReqVO.getFishSplit();
        if(fishPush == null)
            throw new CommonException(403, "修正的分池数据为空");
        Long fishNum = fishPush.getFishNum();
        Long differenceData = Long.parseLong(createReqVO.getDifferenceData());
        Long newFishNum = fishNum + differenceData;
        Long targetNewFishNum = fishPush.getTargetCurrFishNum()-differenceData;
        fishPush.build_reviseLog(fishNum.toString(), differenceData);
        fishPush.setFishNum(newFishNum);
        fishPush.setTargetCurrFishNum(Integer.parseInt(targetNewFishNum.toString()));
        zkPondFishDetailController.updateFishDetail(fishPush);
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据修正记录")
    public CommonResult<Boolean> updateZkDataRevise(@Valid @RequestBody ZkDataReviseUpdateReqVO updateReqVO) {
        updateReqVO.build_reviseTypeName();
        zkDataReviseService.updateZkDataRevise(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据修正记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkDataRevise(@RequestParam("id") Long id) {
        zkDataReviseService.deleteZkDataRevise(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得数据修正记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkDataReviseRespVO> getZkDataRevise(@RequestParam("id") Long id) {
        ZkDataReviseDO zkDataRevise = zkDataReviseService.getZkDataRevise(id);
        return success(BeanUtils.toBean(zkDataRevise, ZkDataReviseRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得数据修正记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkDataReviseRespVO>> getZkDataReviseList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkDataReviseDO> list = zkDataReviseService.getZkDataReviseList(ids);
        return success(BeanUtils.toBean(list, ZkDataReviseRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得数据修正记录分页")
    public CommonResult<PageResult<ZkDataReviseRespVO>> getZkDataRevisePage(@Valid ZkDataRevisePageReqVO pageVO) {
        PageResult<ZkDataReviseDO> pageResult = zkDataReviseService.getZkDataRevisePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkDataReviseRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出数据修正记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkDataReviseExcel(@Valid ZkDataReviseExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkDataReviseDO> list = zkDataReviseService.getZkDataReviseList(exportReqVO);
        // 导出 Excel
        List<ZkDataReviseExcelVO> datas = BeanUtils.toBean(list, ZkDataReviseExcelVO.class);
        ExcelUtils.write(response, "数据修正记录.xls", "数据", ZkDataReviseExcelVO.class, datas);
    }

}
