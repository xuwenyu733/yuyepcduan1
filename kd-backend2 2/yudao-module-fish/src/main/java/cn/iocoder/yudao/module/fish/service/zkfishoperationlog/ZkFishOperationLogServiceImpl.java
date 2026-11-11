package cn.iocoder.yudao.module.fish.service.zkfishoperationlog;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to.FishOperationLogTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfeedinglog.ZkFeedingLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkfishoperationlog.ZkFishOperationLogConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog.ZkFishOperationLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 鱼苗统计 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkFishOperationLogServiceImpl implements ZkFishOperationLogService {

    @Resource
    private ZkFishOperationLogMapper zkFishOperationLogMapper;

    @Autowired
    @Lazy
    private ZkPondService zkPondService;

    @Override
    public Long createZkFishOperationLog(ZkFishOperationLogCreateReqVO createReqVO) {
        // 插入
        ZkFishOperationLogDO zkFishOperationLog = BeanUtils.toBean(createReqVO, ZkFishOperationLogDO.class);
        zkFishOperationLogMapper.insert(zkFishOperationLog);
        // 返回
        return zkFishOperationLog.getId();
    }

    @Override
    public void updateZkFishOperationLog(ZkFishOperationLogUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkFishOperationLogExists(updateReqVO.getId());
        // 更新
        ZkFishOperationLogDO updateObj = BeanUtils.toBean(updateReqVO, ZkFishOperationLogDO.class);
        zkFishOperationLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkFishOperationLog(Long id) {
        // 校验存在
        validateZkFishOperationLogExists(id);
        // 删除
        zkFishOperationLogMapper.deleteById(id);
    }

    private void validateZkFishOperationLogExists(Long id) {
        if (zkFishOperationLogMapper.selectById(id) == null) {
            throw exception(ZK_FISH_OPERATION_LOG_NOT_EXISTS);
        }
    }

    @Override
    public ZkFishOperationLogDO getZkFishOperationLog(Long id) {
        return zkFishOperationLogMapper.selectById(id);
    }

    @Override
    public List<ZkFishOperationLogDO> getZkFishOperationLogList(Collection<Long> ids) {
        return zkFishOperationLogMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkFishOperationLogDO> getZkFishOperationLogPage(ZkFishOperationLogPageReqVO pageReqVO) {
        return zkFishOperationLogMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkFishOperationLogDO> getZkFishOperationLogList(ZkFishOperationLogExportReqVO exportReqVO) {
        return zkFishOperationLogMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkFishOperationLogDO> getByLogDay(String dayStr) {
        return getByLogDaySet(new HashSet<>(Arrays.asList(dayStr)));
    }
    public List<ZkFishOperationLogDO> getByLogDaySet(Set<String> dayStrSet) {
        QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("log_day", dayStrSet);

        return zkFishOperationLogMapper.selectList(queryWrapper);
    }

    @Override
    public void customSaveBatch(List<ZkFishOperationLogDO> res) {
        if(EasyListUtil.isEmpty(res)) return;
        // 1. 之前已经保存就不重复保存
        Set<String> logDaySet = res.stream().map(obj -> obj.clear_fishSpec().getLogDay()).collect(Collectors.toSet());
        List<ZkFishOperationLogDO> has = getByLogDaySet(logDaySet);
        if(EasyListUtil.isEmpty(has)) {
            // 2. 为空直接添加
            zkFishOperationLogMapper.insertBatch(res);
        } else {
            // 2. 不为空，排除重复的
            List<ZkFishOperationLogDO> needAddList = getNeedAddList(res, has);
            if(EasyListUtil.isEmpty(needAddList)) return;
            // 3. 添加新的
            zkFishOperationLogMapper.insertBatch(needAddList);
        }
    }

    @Override
    public List<String> getBatchNumberList() {
        List<ZkFishOperationLogDO> list = zkFishOperationLogMapper.getBatchNumberList();
        if(EasyListUtil.isEmpty(list)) return new ArrayList<>();
        List<String> collect = list.stream().map(obj -> obj.getBatchNumber()).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<ZkFishOperationLogDO> getByPondIds(Set<Long> pondIdSet) {
        if(CollectionUtils.isAnyEmpty(pondIdSet)) return new ArrayList<>();
        List<ZkFishOperationLogDO> resList = new ArrayList<>();
        for(Long pondId : pondIdSet) {
            // 1. 获取 最大的历史天数
            ZkFishOperationLogDO temp = zkFishOperationLogMapper.selectMaxStatisticsDay3(pondId);
            // 2. 获取结果
            QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
            // queryWrapper.in("pond_id", pondIdSet); // 换成eq，之前这里有用户输入 死亡时间 不一致问题
            queryWrapper.eq("pond_id", pondId);
            if(temp != null) {
                queryWrapper.eq("log_day", temp.getLogDay());
            }
            List<ZkFishOperationLogDO> zkFishOperationLogDOS = zkFishOperationLogMapper.selectList(queryWrapper);
            if(EasyListUtil.isEmpty(zkFishOperationLogDOS)) continue;
            resList.add(zkFishOperationLogDOS.get(0));
        }
        return resList;
    }

    @Override
    public List<ZkFishOperationLogDO> getByBatchNumbers(Set<String> batchNumbers, String logType) {
        QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("batch_number", batchNumbers)
                .eq("log_type", logType);
        return zkFishOperationLogMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkFishOperationLogDO> getByLogType(String logType) {
        QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("log_type", logType);
        return zkFishOperationLogMapper.getByLogType(queryWrapper);
    }

    @Override
    public Integer customUpdateBatch(List<ZkFishOperationLogDO> needUpdate) {
        if(EasyListUtil.isEmpty(needUpdate)) return 0;
        zkFishOperationLogMapper.updateBatch(needUpdate, needUpdate.size());
        return needUpdate.size();
    }

    @Override
    public ZkFishOperationLogDO sync_currFeedCount(ZkFeedingLogDO zkFeedingLog) {
        // 1. 获取今天的存塘记录
        String currDay = EasyDateUtil.format(zkFeedingLog.getFeedTime(), "yyyy-MM-dd");
        QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", zkFeedingLog.getPondId())
                .eq("log_day", currDay).orderByDesc("id").last("limit 1");
        ZkFishOperationLogDO logDO = zkFishOperationLogMapper.selectOne(queryWrapper);
        if(logDO == null) return null;
        logDO.build_currFeedCount(zkFeedingLog.getFeedCount(), zkFeedingLog.getFeedingCost());
        zkFishOperationLogMapper.updateById(logDO);
        return logDO;
    }

    @Override
    public List<ZkFishOperationLogDO> selectDaysMortalityRate(List<ZkPondFishDetailDO> list, Integer days) {
        if(EasyListUtil.isEmpty(list)) return null;
        // 1. 获取最近七天 日志 ==> 因为每次投放的日期都不一样，是不能够统一查询的，只能够挨个查询
        List<ZkFishOperationLogDO> logList = new ArrayList<>();
        for(ZkPondFishDetailDO detailItem : list) {
            // 2. 获取 投放后 7日的日期天数
            List<String> sevenDayList = EasyDateUtil.plusDayList(detailItem.getCreateTime(), days, "yyyy-MM-dd");
            // 3. 获取 死亡日志 统计信息
            List<ZkFishOperationLogDO> tempList = selectByPondIdAndLogDays(detailItem.getPondId(), sevenDayList);
            if(EasyListUtil.isEmpty(tempList)) continue;
            ZkFishOperationLogDO temp = tempList.get(0);
            temp.setPondFishDetailId(detailItem.getId());
            logList.add(temp);
        }

        return logList;
    }

    @Override
    public Map<String, List<ZkFishOperationLogDO>> handleFishDetailReverse(ZkPondFishDetailDO zkPondFishDetailDO) {
        // 1. 查找到 需要更新的 值
        String dateStr = zkPondFishDetailDO.build_createStr().getCreateStr();
        List<ZkFishOperationLogDO> needUpdate = selectByPondIdAndLogDays(zkPondFishDetailDO.getPondId(), List.of(dateStr));
        if(EasyListUtil.isEmpty(needUpdate)) return null;
        List<ZkFishOperationLogDO> origin = JSON.parseArray(JSON.toJSONString(needUpdate), ZkFishOperationLogDO.class);
        // 2. 执行更新操作
        for(ZkFishOperationLogDO item : needUpdate) {
            item.add_dayFishNum(-zkPondFishDetailDO.getFishNum());
        }
        zkFishOperationLogMapper.updateBatch(needUpdate);
        return new EasyMap3<List<ZkFishOperationLogDO>>()
                .put("原数据", origin).put("新数据", needUpdate)
                .value();
    }

    @Override
    public void fillInfo() {
        // 1. 获取列表
        QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("pond_id").and(obj -> {
            obj.isNull("pond_detail_num").or().isNull("dept_id");
        });
        List<ZkFishOperationLogDO> zkFishOperationLogDOS = zkFishOperationLogMapper.selectList(queryWrapper);
        fillInfoByList(zkFishOperationLogDOS);
    }

    @Override
    public void fillInfoByList(List<ZkFishOperationLogDO> zkFishOperationLogDOS) {
        if(EasyListUtil.isEmpty(zkFishOperationLogDOS)) return;
        Set<Long> pondIds = zkFishOperationLogDOS.stream().map(ZkFishOperationLogDO::getPondId).collect(Collectors.toSet());
        List<ZkPondDO> zkPondDOS = zkPondService.selectByPondIds(pondIds);
        if(EasyListUtil.isEmpty(zkPondDOS)) return;
        CommonUtil.origins2Targets3(zkPondDOS, "id", List.of("pondDetailNum", "userId", "deptId", "tenantId"), zkFishOperationLogDOS, "pondId",
                List.of("pondDetailNum", "userId", "deptId", "tenantId"));
        customUpdateBatch(zkFishOperationLogDOS);
    }


    public List<ZkFishOperationLogDO> selectByPondIdAndLogDays(Long pondId, List<String> logDays) {
        QueryWrapper<ZkFishOperationLogDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pond_id", pondId)
                .in("log_day", logDays)
                .orderByDesc("id");
        return zkFishOperationLogMapper.selectList(queryWrapper);
    }

    public List<ZkFishOperationLogDO> getNeedAddList(List<ZkFishOperationLogDO> newList, List<ZkFishOperationLogDO> has) {
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        // 2. 需要添加的，以及已有的 进行排除
        List<ZkFishOperationLogDO> needUpdate = new ArrayList<>();
        for(ZkFishOperationLogDO newItem : newList) {
            List<ZkFishOperationLogDO> tempList = has.stream().filter(hasItem -> StringUtils.equals(newItem.getLogDay(), hasItem.getLogDay())
                            && StringUtils.equals(newItem.getLogType(), hasItem.getLogType())).collect(Collectors.toList());

            ZkFishOperationLogDO flag = judgeIsHas(tempList, newItem);
            if(flag == null) res.add(newItem); // 数据库没有是需要新增的
            else { //  if(StringUtils.equals(flag.getLogDay(), currDay))
                // 数据库 已有的是需要 更新的
                CommonUtil.copyPropertiesIgnoreNull(newItem, flag);
                needUpdate.add(flag);
            }
        }
        // 3. 将需要更新的进行更新操作
        if(EasyListUtil.isNotEmpty(needUpdate)) {
            zkFishOperationLogMapper.updateBatch(needUpdate, needUpdate.size());
        }
        return res;
    }

    public ZkFishOperationLogDO judgeIsHas(List<ZkFishOperationLogDO> hasList, ZkFishOperationLogDO curr) {
        if(EasyListUtil.isEmpty(hasList)) return null;
        if(StringUtils.equals(FishOperationLogTypeEnum.BATCH.getLogType(), curr.getLogType())) {
            for(ZkFishOperationLogDO has : hasList) {
                if(StringUtils.equals(has.getBatchNumber(), curr.getBatchNumber())) return has;
            }
        } else if(StringUtils.equals(FishOperationLogTypeEnum.WORKSHOP.getLogType(), curr.getLogType())) {
            for(ZkFishOperationLogDO has : hasList) {
                if(Objects.equals(has.getWorkshopId(), curr.getWorkshopId())) return has;
            }
        } else if(StringUtils.equals(FishOperationLogTypeEnum.SYSTEM.getLogType(), curr.getLogType())) {
            for(ZkFishOperationLogDO has : hasList) {
                if(StringUtils.equals(has.getPondNum(), curr.getPondNum())) return has;
            }
        } else if(StringUtils.equals(FishOperationLogTypeEnum.POND.getLogType(), curr.getLogType())) {
            for(ZkFishOperationLogDO has : hasList) {
                if(Objects.equals(has.getPondId(), curr.getPondId())) return has;
            }
        }
        return null;
    }

}
