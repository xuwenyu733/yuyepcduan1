package cn.iocoder.yudao.module.fish.controller.admin.zkpond;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.daping.to.DeviceInfo;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.tools.FishSystemLogTool;
import cn.iocoder.yudao.module.fish.controller.admin.zkbiologycapital.ZkBiologyCapitalController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.ZkPondFishDetailController;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.DetailJson;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.dictdata.DictDataService;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;
import cn.iocoder.yudao.module.fish.service.zkdatarevise.ZkDataReviseService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
// import cn.iocoder.yudao.module.fish.convert.zkpond.ZkPondConvert;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;

@Tag(name = "管理后台 - 存塘")
@RestController
@RequestMapping("/fish/zk-pond")
@Validated
public class ZkPondController {

    @Resource
    private ZkPondService zkPondService;

    @Resource
    private ZkPondFishService zkPondFishService;
    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;
    @Resource
    private DictDataService dictDataService;

    @Autowired
    @Lazy
    private ZkPondFishDetailController zkPondFishDetailController;
    @Resource
    private ZkDataReviseService zkDataReviseService;
    @Resource
    private ZkBiologyCapitalController zkBiologyCapitalController;

    @Resource
    private BaseDeptService baseDeptService;


    @PostMapping("/create")
    @Operation(summary = "创建存塘")
    public CommonResult<Long> createZkPond(@Valid @RequestBody ZkPondCreateReqVO createReqVO) {
         volidate_pondNum(createReqVO.getPondDetailNum(), 1, null); // 现在是一个系统(zk_pond_system)对应多个存塘(zk_pond),系统编号可以重复
        // 1. 创建 存塘
        ZkPondDO zkPond = zkPondService.createZkPond(createReqVO);
        Long zkPondId = zkPond.getId();
        // 2. 更新zk_pond_fish信息 String workshopName, Long workshopId, String pondName, Long pondId, String fishTypes, Long fishNum, String detailJson
        if(EasyListUtil.isNotEmpty(createReqVO.getDetailJson())) {
            AtomicReference<Long> fishNum = new AtomicReference<>(0l);
            String flushTypes = createReqVO.getDetailJson().stream().map(obj -> {
                fishNum.updateAndGet(v -> v + obj.getFishNum());
                return obj.getFishType();
            }).collect(Collectors.joining(","));
            ZkPondFishDO zkPondFishDO = new ZkPondFishDO(createReqVO.getWorkshopName(), createReqVO.getWorkshopId(), createReqVO.getPondName(), zkPondId,
                    flushTypes, fishNum.get(), JSON.toJSONString(new DetailJson(createReqVO.getDetailJson())))
                    .setTenantId(zkPond.getTenantId()).setDeptId(zkPond.getDeptId()).setUserId(zkPond.getUserId());
            zkPondFishService.customInsert(zkPondFishDO); // 获取 存塘鱼统计数据
            // 3. 更新 存塘日志记录
            zkBiologyCapitalController.genLog();
        }
        // 3. 如果 存塘记录 为空，则新增 zk_pond_fish
        sync_pondFish();
        // 同步操作日志，方便追踪问题
        FishSystemLogTool.gen(createReqVO.getPondDetailNum(), "创建存塘", JSON.toJSONString(zkPond), null, null, null);
        return success(zkPondId);
    }


    public void sync_pondFish() {
        // 1. 获取没有添加 zk_pond_fish 的存塘数据
        List<ZkPondDO> pondList = zkPondFishService.getByEmptyPondFish();
        if(EasyListUtil.isEmpty(pondList)) return;
        // 2. 新增空白数据
        List<ZkPondFishDO> pondFishList = pondList.stream().map(obj -> {
            ZkPondFishDO zkPondFishDO = new ZkPondFishDO(obj.getWorkshopName(), obj.getWorkshopId(), obj.getPondName(), obj.getId(),
                    obj.getFishTypes(), Long.parseLong(obj.getFishNum() + ""), null)
                    .build_pondInfo(obj.getWorkshopId(), obj.getWorkshopName(), obj.getPondSystemName(), null, obj.getPondNum(), obj.getPondName(), obj.getId(), obj.getPondDetailNum())
                    .setTenantId(obj.getTenantId()).setDeptId(obj.getDeptId()).setUserId(obj.getUserId());
            return zkPondFishDO;
        }).collect(Collectors.toList());
        zkPondFishService.customBatchInsert(pondFishList);
    }


    /**
     * 判断 存塘的 系统编号 是否重复
     * @param pondDetailNum 存池编号
     * @param type  1:添加;2:修改;
     */
    // 现在是一个系统(zk_pond_system)对应多个存塘(zk_pond),系统编号可以重复
    public void volidate_pondNum(String pondDetailNum, Integer type, Long currId) {
        // 1. 判断存塘的系统编号是否为空
        List<ZkPondDO> list = zkPondService.getByPondDetailNum(pondDetailNum);
        if(EasyListUtil.isNotEmpty(list)) {
            ZkPondDO zkPondDO = list.get(0);
            if(Objects.equals(1, type)) // 执行添加，只要有就不行
                throw new CommonException(403, String.format("参数pondDetailNum重复，存塘%s的系统编号也为%s", zkPondDO.getPondName(), zkPondDO.getPondDetailNum()));
            if(Objects.equals(2, type))
                judge_isSelf(list, currId); // 执行修改，判断不是自己的 是否有当前 系统编号，因为自己和自己一样不需要报错
        }
    }

    public void judge_isSelf(List<ZkPondDO> list, Long currId) {
        for(ZkPondDO item : list) {
            if(!Objects.equals(currId, item.getId()))
                throw new CommonException(403, String.format("参数pondDetailNum重复，存塘%s的系统编号也为%s", item.getPondName(), item.getPondDetailNum()));
        }
    }

    @PutMapping("/update")
    @Operation(summary = "更新存塘")
    public CommonResult<Boolean> updateZkPond(@Valid @RequestBody ZkPondUpdateReqVO updateReqVO) {
         volidate_pondNum(updateReqVO.getPondDetailNum(), 2, updateReqVO.getId());
        // 1. 执行更新操作
        List<ZkPondDO> zkPondDOS = zkPondService.updateZkPond(updateReqVO);
        // 2. 执行 zk_pond_fish 数据更新, TODO 更新操作为节约时间复用前面代码，后续这里可以 合并优化性能
        ZkPondFishDO byPondId = zkPondFishService.getByPondId(updateReqVO.getId());
        if(byPondId == null) {
            ZkPondFishDO zkPondFishDO = new ZkPondFishDO(updateReqVO.getWorkshopName(), updateReqVO.getWorkshopId(), updateReqVO.getPondName(), updateReqVO.getId(),
                    "", updateReqVO.getFishNum(), JSON.toJSONString(new DetailJson(updateReqVO.getDetailJson())));
            zkPondFishService.customInsert(zkPondFishDO); // 获取 存塘鱼统计数据
        } else {
            zkPondFishService.customUpdate(byPondId.build_info("", updateReqVO.getFishNum(), JSON.toJSONString(new DetailJson(updateReqVO.getDetailJson()))));
        }
        // 3.1 更新 存塘日志记录
        zkBiologyCapitalController.genLog();
        // 3.2 同步到修正记录
        zkDataReviseService.customInsertPond(updateReqVO);
        // 4. 如果 存塘记录 为空，则新增 zk_pond_fish
        sync_pondFish();
        // 同步操作日志，方便追踪问题
        FishSystemLogTool.gen(updateReqVO.getPondDetailNum(), "更新存塘", JSON.toJSONString(zkPondDOS.get(0)), JSON.toJSONString(zkPondDOS.get(1)), null, null);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除存塘")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPond(@RequestParam("id") Long id) {
        // 1. 从 zk_pond_fish 判断数据是否可以删除
        ZkPondFishDO byPondId = zkPondFishService.getByPondId(id);
        if(byPondId != null && byPondId.getFishNum() > 0) {
            throw new CommonException(403, String.format("当前存塘还有鱼苗数:%s，不能删除！", byPondId.getFishNum()));
        }
        zkPondService.deleteZkPond(id);
        // 同步操作日志，方便追踪问题
        FishSystemLogTool.gen(byPondId.getPondDetailNum(), "删除存塘", JSON.toJSONString(byPondId), null, null, null);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得存塘")
    // @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPondRespVO> getZkPond(@RequestParam("id") Long id, @RequestParam(value = "addType", defaultValue = "0") String addType) {
        ZkPondDO zkPond = zkPondService.getZkPond(id);
        enhancer(Arrays.asList(zkPond), addType);
        return success(BeanUtils.toBean(zkPond, ZkPondRespVO.class));
    }


    @GetMapping("/getSummary")
    @Operation(summary = "获得存塘统计信息")
    public CommonResult<PondSummaryVo> getSummary() {
        // 1. 获取鱼苗种类
        List<DictDataVo> dictDataList = dictDataService.getByDictTypes(Arrays.asList("zk_pond_fish_types"));
        // 2. 获取存塘信息
        PondSummaryVo pondSummaryVo = zkPondService.getSummaryInfo();
        pondSummaryVo.setFishTypeCount(Long.parseLong(dictDataList.size()+""));
        return success(pondSummaryVo);
    }


    @GetMapping("/getDeviceInfo")
    @Operation(summary = "获得存塘设备信息")
    public CommonResult<DeviceInfo> getDeviceInfo(@RequestParam("pondId") Long pondId) {
        // 1. 获取设备信息 TODO 真实应该从 数据库查询
        DeviceInfo info = new DeviceInfo(pondId).gen();

        return success(info);
    }

    @GetMapping("/getFishLog")
    @Operation(summary = "获得存塘投放鱼苗记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<PondFishLogVO> getFishLog(@RequestParam("id") Long id, @RequestParam(value = "addType", defaultValue = "0") String addType) {
        // 1. 获取当前存塘鱼数量
        ZkPondDO zkPond = zkPondService.getZkPond(id);
        // 2. 获取鱼的种类信息
        ZkPondFishDO byPondId = zkPondFishService.getByPondId(id);
        // 3. 获取鱼的投放记录信息
        List<ZkPondFishDetailDO>  detailDOS = new ArrayList<>();
        if(byPondId != null) {
            detailDOS = zkPondFishDetailService.getByPondId(id, addType);
        }
        // 4. 汇总信息
        PondFishLogVO vo = new PondFishLogVO().build(zkPond, byPondId, detailDOS);
        return success(vo);
    }

    @GetMapping("/list")
    @Operation(summary = "获得存塘列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPondRespVO>> getZkPondList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPondDO> list = zkPondService.getZkPondList(ids);
        enhancer(list, "0");
        return success(BeanUtils.toBean(list, ZkPondRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得存塘分页")
    public CommonResult<PageResult<ZkPondRespVO>> getZkPondPage(@Valid ZkPondPageReqVO pageVO) {
        PageResult<ZkPondDO> pageResult = zkPondService.getZkPondPage(pageVO);
        enhancer(pageResult.getList(), "0");
        // 补充部门名称
        baseDeptService.listEnhancerDeptName(pageResult.getList(), Optional.of("deptId"), Optional.of("deptName"));
        return success(BeanUtils.toBean(pageResult, ZkPondRespVO.class));
    }

    public void enhancer(List<ZkPondDO> list, String addType) {
        if(EasyListUtil.isEmpty(list)) return;
        Set<Long> pondIds = list.stream().map(obj -> obj.build_totalWeight().getId()).collect(Collectors.toSet());
        // 获取 zk_pond_fish 列表
        List<ZkPondFishDO> fishList = zkPondFishService.getListByPondIds(pondIds);
        if(EasyListUtil.isEmpty(fishList)) return;
        // 获取 zk_pond_fish_detail 列表
        List<ZkPondFishDetailDO> fishDetails = zkPondFishDetailService.getListByPondIds(pondIds, addType);
        if(EasyListUtil.isEmpty(fishDetails)) return;
        Map<Long, List<ZkPondFishDetailDO>> collect = fishDetails.stream().collect(Collectors.groupingBy(obj -> obj.getPondId()));
        for(ZkPondDO item : list) {
            for(ZkPondFishDO fish : fishList) {
                if(Objects.equals(item.getId(), fish.getPondId())) {
                    PondFishLogVO pondFishLogVO = new PondFishLogVO().build(item, fish, collect.get(fish.getPondId()));
                    item.setPondFishLogVO(pondFishLogVO);
                    break;
                }
            }
        }
        // 更新 数据
        zkPondService.batchUpdate(list);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出存塘 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPondExcel(@Valid ZkPondExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPondDO> list = zkPondService.getZkPondList(exportReqVO);
        // 导出 Excel
        List<ZkPondExcelVO> datas = BeanUtils.toBean(list, ZkPondExcelVO.class);
        ExcelUtils.write(response, "存塘.xls", "数据", ZkPondExcelVO.class, datas);
    }

}
