package cn.iocoder.yudao.module.fish.service.zkpondfishdetail;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to.FishOperationLogTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.to.PondFishDetailAddTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog.StaticDeadInfoVo3;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpond.ZkPondMapper;
import cn.iocoder.yudao.module.fish.service.dictdata.DictDataService;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;
import cn.iocoder.yudao.module.fish.service.zkspecslog.ZkSpecsLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpondfishdetail.ZkPondFishDetailConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfishdetail.ZkPondFishDetailMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 存塘鱼苗明细 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkPondFishDetailServiceImpl implements ZkPondFishDetailService {

    @Resource
    private ZkPondFishDetailMapper zkPondFishDetailMapper;
    @Resource
    private DictDataService dictDataService;
    @Resource
    private ZkSpecsLogService zkSpecsLogService;
    @Resource
    private ZkPondMapper zkPondMapper;

    @Override
    public ZkPondFishDetailDO createZkPondFishDetail(ZkPondFishDetailCreateReqVO createReqVO) {
        // 1. 添加
        ZkPondFishDetailDO zkPondFishDetail = BeanUtils.toBean(createReqVO, ZkPondFishDetailDO.class);
        updateSplitRadio(zkPondFishDetail);
        zkPondFishDetailMapper.insert(zkPondFishDetail);
        // 返回
        return zkPondFishDetail;
    }

    @Override
    public void updateZkPondFishDetail(ZkPondFishDetailUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPondFishDetailExists(updateReqVO.getId());
        // 更新
        ZkPondFishDetailDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondFishDetailDO.class);
        updateSplitRadio(updateObj);
        zkPondFishDetailMapper.updateById(updateObj);
    }

    @Override
    public ZkPondFishDetailDO deleteZkPondFishDetail(Long id) {
        // 校验存在
        ZkPondFishDetailDO zkPondFishDetailDO = validateZkPondFishDetailExists(id);
        // 删除
        zkPondFishDetailMapper.deleteById(id);
        return zkPondFishDetailDO;
    }

    private ZkPondFishDetailDO validateZkPondFishDetailExists(Long id) {
        ZkPondFishDetailDO zkPondFishDetailDO = zkPondFishDetailMapper.selectById(id);
        if (zkPondFishDetailDO == null) {
            throw exception(ZK_POND_FISH_DETAIL_NOT_EXISTS);
        }
        return zkPondFishDetailDO;
    }

    @Override
    public ZkPondFishDetailDO getZkPondFishDetail(Long id) {
        return zkPondFishDetailMapper.selectById(id);
    }

    @Override
    public List<ZkPondFishDetailDO> getZkPondFishDetailList(Collection<Long> ids) {
        return zkPondFishDetailMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPondFishDetailDO> getZkPondFishDetailPage(ZkPondFishDetailPageReqVO pageReqVO) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(pageReqVO.getBatchNumber()), "zpfd.batch_number", pageReqVO.getBatchNumber())
                .like(StringUtils.isNotBlank(pageReqVO.getSupplier()), "zpfd.supplier", pageReqVO.getSupplier())
        ;
        if(pageReqVO.getCreateTime()!=null&&pageReqVO.getCreateTime().length>1) {
            queryWrapper.between("zpfd.create_time", pageReqVO.getCreateTime()[0], pageReqVO.getCreateTime()[1]);
        }
        if(pageReqVO.getAddType() != null) queryWrapper.eq("zpfd.add_type", pageReqVO.getAddType());
        // 存塘类型 1:单一;2:混合;3:全部;
        if("1".equals(pageReqVO.getPondType())) { // 单一
            queryWrapper.notLike("zp.batch_number", "%,%");
        } else if("2".equals(pageReqVO.getPondType())) { // 混合
            queryWrapper.like("zp.batch_number", "%,%");
        }
        Page<ZkPondFishDetailDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        IPage<ZkPondFishDetailDO> zkPondFishDetailDOIPage = zkPondFishDetailMapper.customPage(page, queryWrapper);
        // return zkPondFishDetailMapper.selectPage(pageReqVO);
        return new PageResult<>(zkPondFishDetailDOIPage.getRecords(), zkPondFishDetailDOIPage.getTotal());
    }

    @Override
    public List<ZkPondFishDetailDO> getZkPondFishDetailList(ZkPondFishDetailExportReqVO exportReqVO) {
        return zkPondFishDetailMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkPondFishDetailDO> list(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ZkPondFishDetailDO> queryWrapper) {
        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    @Override
    public void customUpdate(ZkPondFishDetailDO build_zkPondFishId) {
        zkPondFishDetailMapper.updateById(build_zkPondFishId);
    }

    @Override
    public List<ZkPondFishDetailDO> getByPondId(Long pondId) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(obj -> {
            obj.eq("pond_id", pondId).or().eq("target_pond_id", pondId);
        });
        List<ZkPondFishDetailDO> detailDOS = zkPondFishDetailMapper.selectList(queryWrapper);
        enhancer_fishName(detailDOS);
        return detailDOS;
    }

    @Override
    public List<ZkPondFishDetailDO> getByPondId(Long pondId, String addType) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.equals("0", addType), "add_type", addType);
        queryWrapper.and(obj -> {
            obj.eq("pond_id", pondId).or().eq("target_pond_id", pondId);
        });
        List<ZkPondFishDetailDO> detailDOS = zkPondFishDetailMapper.selectList(queryWrapper);
        enhancer_fishName(detailDOS);
        return detailDOS;
    }

    @Override
    public List<ZkPondFishDetailDO> getListByPondIdsAndStatisticsType(Set<Long> pondIds, String addType, String statisticsType) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pond_id", pondIds).eq("statistics_type", statisticsType)
            .eq(!StringUtils.equals("0", addType), "add_type", addType);
        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> getListByPondIds(Set<Long> pondIds, String addType) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pond_id", pondIds).eq(!StringUtils.equals("0", addType), "add_type", addType);
        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> byPondIdsExcludeTargetPondIds(Set<Long> pondIds, String addType, Set<Long> excludePondIds) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(!CollectionUtils.isAnyEmpty(pondIds), "pond_id", pondIds).eq(!StringUtils.equals("0", addType), "add_type", addType)
                .notIn(!CollectionUtils.isAnyEmpty(excludePondIds), "target_pond_id", excludePondIds);
        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> getRecentDayList(String addType, String dayStr) {
        List<Date> during = EasyDateUtil.dayStartAndEnd(dayStr);
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType)
                .between("create_time", during.get(0), during.get(1));

        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> getRecentDayList(String addType) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType).orderByAsc("pond_id");

        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkFishOperationLogDO> genByEmptyOperationLog() {
        // 1. 获取没有统计过的存塘列表
        String logDay = EasyDateUtil.format(LocalDateTime.now(), "yyyy-MM-dd");
        List<ZkPondDO> pondList = zkPondMapper.getPondListByEmptyOperationLog(logDay);
        if(EasyListUtil.isEmpty(pondList)) return new ArrayList<>();
        // 2. 如果有 则需要 添加统计记录
        List<ZkFishOperationLogDO> collect = pondList.stream().map(obj -> {
            ZkFishOperationLogDO logDO = new ZkFishOperationLogDO(obj.getWorkshopId(), obj.getPondSystemId(), obj.getPondNum(), obj.getId(),
                    obj.getWorkshopName(), obj.getPondSystemName(), obj.getPondName(), logDay, FishOperationLogTypeEnum.POND.getLogType())
                    .build_base(obj.getFishSpec()+"", obj.getFishTypes(), null, new BigDecimal(0), Long.parseLong(obj.getFishNum()+""),
                            new BigDecimal(0), obj.getTotalWeight(), null)
                    .build_other(obj.getCurrFeedCount(), obj.getAccumulateFeedCount(), obj.getActuralRadio(), obj.getCurrCost(), obj.getBiologyCost())
                    .setUserId(obj.getUserId()).setDeptId(obj.getDeptId()).setTenantId(obj.getTenantId());
            return logDO;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<ZkPondFishDetailDO> countByWorkshopIdSet(String addType, Set<Long> batchNumberSet) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType)
                .in("workshop_id", batchNumberSet);

        return zkPondFishDetailMapper.countByWorkshopIdSet(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> countByPondIdSet(String addType, Set<Long> pondIdSet) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType)
                .in("pond_id", pondIdSet);

        return zkPondFishDetailMapper.countByPondIdSet(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> countByTargetPondIdSet(String addType, Set<Long> targetPondIdSet) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType)
                .in("target_pond_id", targetPondIdSet);

        return zkPondFishDetailMapper.countByPondIdSet(queryWrapper);
    }

    @Override
    public List<ZkPondFishDetailDO> countByPondNumSet(String addType, Set<String> pondNumSet) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType)
                .in("pond_num", pondNumSet);

        return zkPondFishDetailMapper.countByPondNumSet(queryWrapper);
    }

    @Override
    public ZkPondFishDetailDO getById(Long id) {
        return zkPondFishDetailMapper.selectById(id);
    }

    @Override
    public ZkPondFishDetailDO updateSplitRadio(ZkPondFishDetailDO zkPondFishDetailDO) {
        if(zkPondFishDetailDO == null) return null;
        ZkSpecsLogDO zkSpecsLogDO = zkSpecsLogService.getByPondId(zkPondFishDetailDO.getPondId());
        if(zkSpecsLogDO != null) zkPondFishDetailDO.setSplitRadio(zkSpecsLogDO.getActuralRadio());
        return zkPondFishDetailDO;
    }

    @Override
    public Integer customBatchUpdate(List<ZkPondFishDetailDO> list) {
        if(EasyListUtil.isEmpty(list)) return 0;
        zkPondFishDetailMapper.updateBatch(list, list.size());
        return list.size();
    }

    @Override
    public List<ZkPondFishDetailDO> selectByCreateStrSet(Set<String> createStrSet, String addType) {
        List<ZkPondFishDetailDO> res = new ArrayList<>();
        // 1. 时间区间 只能一次范围，所有需要挨次查询
        for(String dayStr : createStrSet) {
            List<Date> dates = EasyDateUtil.dayStartAndEnd(dayStr);
            QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("add_type", addType)
                    .between("create_time", dates.get(0), dates.get(1));
            List<ZkPondFishDetailDO> tempList = zkPondFishDetailMapper.selectList(queryWrapper);
            if(EasyListUtil.isNotEmpty(tempList)) res.addAll(tempList);
        }
        return res;
    }

    @Override
    public List<ZkPondFishDetailDO> selectDaysMortalityRate(List<ZkPondFishDetailDO> list, Integer days, String addType) {
        if(EasyListUtil.isEmpty(list)) return null;
        // 1. 获取最近七天 日志 ==> 因为每次投放的日期都不一样，是不能够统一查询的，只能够挨个查询
        List<ZkPondFishDetailDO> logList = new ArrayList<>();
        for(ZkPondFishDetailDO detailItem : list) {
            // 2. 获取 投放后 7日的日期天数
            List<Date> sevenDayList = EasyDateUtil.plusDaysDuring(detailItem.getCreateTime(), days);
            // 3. 获取 死亡日志 统计信息
            Long pondId = detailItem.getPondId();
            if(StringUtils.equals(addType, PondFishDetailAddTypeEnum.SPLIT.getAddType())) pondId = detailItem.getTargetPondId();
            List<ZkPondFishDetailDO> tempList = selectByPondIdAndLogDays(pondId, sevenDayList, PondFishDetailAddTypeEnum.DEAD.getAddType());
            if(EasyListUtil.isEmpty(tempList)) continue;
            ZkPondFishDetailDO temp = new ZkPondFishDetailDO();
            CommonUtil.copyPropertiesIgnoreNull(tempList.get(0), temp);
            temp.setFishNum(0L);
            for(ZkPondFishDetailDO i : tempList) {
                temp.build_fishNum(i.getFishNum());
            }
            temp.setId(detailItem.getId());
            logList.add(temp);
        }

        return logList;
    }

    @Override
    public Map<Long, List<ZkPondFishDetailDO>> selectMapByTypeAndTargetPondId(String addType, Set<Long> pondIdSet) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType).in("target_pond_id", pondIdSet);
        List<ZkPondFishDetailDO> tempList = zkPondFishDetailMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(tempList)) return new HashMap<>();
        Map<Long, List<ZkPondFishDetailDO>> collect = tempList.stream().collect(Collectors.groupingBy(ZkPondFishDetailDO::getTargetPondId));
        return collect;
    }

    @Override
    public List<ZkPondFishDetailDO> getByTaskDetailIds(Set<Long> taskDetailIds) {
        if(CollectionUtils.isAnyEmpty(taskDetailIds)) return new ArrayList<>();
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("task_detail_id", taskDetailIds);
        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    private List<ZkPondFishDetailDO> selectByPondIdAndLogDays(Long pondId, List<Date> sevenDayList, String addType) {
        QueryWrapper<ZkPondFishDetailDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("add_type", addType).eq("pond_id", pondId)
                .between("create_time", sevenDayList.get(0), sevenDayList.get(1));
        return zkPondFishDetailMapper.selectList(queryWrapper);
    }

    public void enhancer_fishName(List<ZkPondFishDetailDO> detailDOS) {
        if(EasyListUtil.isEmpty(detailDOS)) return;
        // 2. 填充 鱼名称信息
        List<DictDataVo> dictDataList = dictDataService.getByDictTypes(Arrays.asList("zk_pond_fish_types"));
        for(ZkPondFishDetailDO item : detailDOS) {
            String fishName = dictDataService.getLabel(dictDataList, item.getFishTypes());
            item.setFishName(fishName);
        }
    }


    @Override
    public List<Map<String, Object>> staticDeadInfo() {
         return zkPondFishDetailMapper.staticDeadInfo();
    }

    @Override
    public List<StaticDeadInfoVo3> staticDeadInfo3() {
        return zkPondFishDetailMapper.staticDeadInfo3();
    }



    @Override
    public List<ZkPondFishDetailDO> getZkPondFishDetailListByParentId(Long parentId) {
        return zkPondFishDetailMapper.selectList(new QueryWrapper<ZkPondFishDetailDO>().eq("parent_id", parentId));
    }

}
