package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.to;

import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyBigDecimalUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog.StaticDeadInfoVo3;
import cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog.ZkFishOperationLogMapper;
import cn.iocoder.yudao.module.fish.service.zkfeedinglog.ZkFeedingLogService;
import cn.iocoder.yudao.module.fish.service.zkfishoperationlog.ZkFishOperationLogService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zkpondfish.ZkPondFishService;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 根据 死亡记录 得到 汇总结果
 * @创建人 lr
 * @创建时间 2025-07-17
 * @描述
 */
@Component
@Slf4j
public class ListToAnalysisTool {

    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkFishOperationLogService zkFishOperationLogService;
    @Resource
    private ZkPondFishService zkPondFishService;

    @Resource
    private ZkFishOperationLogMapper zkFishOperationLogMapper;

    @Resource
    private ZkFeedingLogService zkFeedingLogService;

    @Resource
    private ZkPondFishDetailService zkPondFishDetailService;

    /**
     * 根据 死亡记录 从 批次、车间、系统、存塘 几个维度得到汇总结果
     * @param list
     * @return
     */
    public List<ZkFishOperationLogDO> execute(List<ZkPondFishDetailDO> list) {
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        // 1. 按时间维度 分开
        Map<String, List<ZkPondFishDetailDO>> day2list = list.stream()
                .map(ZkPondFishDetailDO::build_createStr)
                .filter(obj -> obj.getCreateStr() != null)
                .collect(Collectors.groupingBy(ZkPondFishDetailDO::getCreateStr));
        // 2. 执行统计
        for(String dayStr : day2list.keySet()) {
            // 2.1 分析
            List<ZkPondFishDetailDO> tempList = day2list.get(dayStr);
            List<ZkFishOperationLogDO> batchRes = batchAnalysisExecute(dayStr, tempList);
            List<ZkFishOperationLogDO> workshopRes = workshopAnalysisExecute(dayStr, tempList);
            List<ZkFishOperationLogDO> systemRes = systemAnalysisExecute(dayStr, tempList);
            List<ZkFishOperationLogDO> pondRes = pondAnalysisExecute(dayStr, tempList);
            // 2.2 汇总
            res.addAll(batchRes);
            res.addAll(workshopRes);
            res.addAll(systemRes);
            res.addAll(pondRes);
        }
        // 3. 填充 权限信息
        zkFishOperationLogService.fillInfoByList(res);

        return res;
    }

    public List<ZkFishOperationLogDO> batchAnalysisExecute(String dayStr, List<ZkPondFishDetailDO> list) {
        // 1. 批次维度
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        Map<String, List<ZkPondFishDetailDO>> collect = list.stream().filter(obj -> StringUtil.isNotEmptyWith0(obj.getBatchNumber()))
                .collect(Collectors.groupingBy(obj -> obj.getBatchNumber()));
        for(String item : collect.keySet()) {
            List<ZkPondFishDetailDO> tempList = collect.get(item);
            ZkFishOperationLogDO temp = new ZkFishOperationLogDO(item, dayStr);
            for(ZkPondFishDetailDO detail : tempList) {
                temp.add_dayFishNum(detail.getFishNum());
            }
            ZkPondFishDetailDO first = tempList.get(0);
            temp.build_info(first.getProductNo(), first.getFishTypes(), first.getFishSpec(), FishOperationLogTypeEnum.BATCH.getLogType())
                    .setTenantId(first.getTenantId()).setDeptId(first.getDeptId()).setUserId(first.getUserId());
            res.add(temp);
        }

        return res;
    }

    public List<ZkFishOperationLogDO> workshopAnalysisExecute(String dayStr, List<ZkPondFishDetailDO> list) {
        // 1. 车间维度
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        // 2. 时间已经分好，按存塘分组
        Map<Long, List<ZkPondFishDetailDO>> collect = list.stream()
                .filter(obj -> obj.getWorkshopId() != null)
                .collect(Collectors.groupingBy(ZkPondFishDetailDO::getWorkshopId));
        for(Long item : collect.keySet()) {
            // 2.1 构建基础汇总信息对象
            List<ZkPondFishDetailDO> tempList = collect.get(item);
            ZkPondFishDetailDO first = tempList.get(0);
            // Long workshopId, Long pondSystemId, String pondNum, Long pondId, String workshopName, String pondSystemName, String pondName, String logDay, String logType
            ZkFishOperationLogDO temp = new ZkFishOperationLogDO(first.getWorkshopId(), null, null, null, first.getWorkshopName(), null, null, dayStr,
                    FishOperationLogTypeEnum.WORKSHOP.getLogType());
            // 2.2 汇总 死亡数量
            for(ZkPondFishDetailDO detail : tempList) {
                temp.add_dayFishNum(detail.getFishNum());
            }
            temp.build_info(first.getProductNo(), first.getFishTypes(), first.getFishSpec(), FishOperationLogTypeEnum.WORKSHOP.getLogType())
                    .setTenantId(first.getTenantId()).setDeptId(first.getDeptId()).setUserId(first.getUserId());
            res.add(temp);
        }

        return res;
    }

    public List<ZkFishOperationLogDO> systemAnalysisExecute(String dayStr, List<ZkPondFishDetailDO> list) {
        // 1. 系统维度
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        // 2. 时间已经分好，按存塘分组
        Map<String, List<ZkPondFishDetailDO>> collect = list.stream()
                .filter(obj -> StringUtil.isNotEmptyWith0(obj.getPondNum()))
                .collect(Collectors.groupingBy(ZkPondFishDetailDO::getPondNum));
        for(String item : collect.keySet()) {
            // 2.1 构建基础汇总信息对象
            List<ZkPondFishDetailDO> tempList = collect.get(item);
            ZkPondFishDetailDO first = tempList.get(0);
            // Long workshopId, Long pondSystemId, String pondNum, Long pondId, String workshopName, String pondSystemName, String pondName, String logDay, String logType
            ZkFishOperationLogDO temp = new ZkFishOperationLogDO(first.getWorkshopId(), null, first.getPondNum(), null, first.getWorkshopName(), first.getPondSystemName(), null, dayStr,
                    FishOperationLogTypeEnum.SYSTEM.getLogType()).build_pondDetailNum(first.getPondDetailNum());
            // 2.2 汇总 死亡数量
            for(ZkPondFishDetailDO detail : tempList) {
                temp.add_dayFishNum(detail.getFishNum());
            }
            temp.build_info(first.getProductNo(), first.getFishTypes(), first.getFishSpec(), FishOperationLogTypeEnum.SYSTEM.getLogType())
                    .setTenantId(first.getTenantId()).setDeptId(first.getDeptId()).setUserId(first.getUserId());
            res.add(temp);
        }

        return res;
    }

    public List<ZkFishOperationLogDO> pondAnalysisExecute(String dayStr, List<ZkPondFishDetailDO> list) {
        // 1. 存塘维度
        List<ZkFishOperationLogDO> res = new ArrayList<>();
        // 2. 时间已经分好，按存塘分组
        Map<String, List<ZkPondFishDetailDO>> collect = list.stream().filter(obj -> StringUtil.isNotEmptyWith0(obj.getPondDetailNum())).collect(Collectors.groupingBy(ZkPondFishDetailDO::getPondDetailNum));
        for(String item : collect.keySet()) {
            // 2.1 构建基础汇总信息对象
            List<ZkPondFishDetailDO> tempList = collect.get(item);
            ZkPondFishDetailDO first = tempList.get(0);
            // Long workshopId, Long pondSystemId, String pondNum, Long pondId, String workshopName, String pondSystemName, String pondName, String logDay, String logType
            ZkFishOperationLogDO temp = new ZkFishOperationLogDO(first.getWorkshopId(), null, first.getPondNum(), first.getPondId(), first.getWorkshopName(), first.getPondSystemName(), first.getPondName(), dayStr,
                    FishOperationLogTypeEnum.POND.getLogType()).build_pondDetailNum(first.getPondDetailNum());
            // 2.2 汇总 死亡数量
            for(ZkPondFishDetailDO detail : tempList) {
                temp.add_dayFishNum(detail.getFishNum());
            }
            temp.build_info(first.getProductNo(), first.getFishTypes(), first.getFishSpec(), FishOperationLogTypeEnum.POND.getLogType())
                    .setTenantId(first.getTenantId()).setDeptId(first.getDeptId()).setUserId(first.getUserId());
            res.add(temp);
        }
        // 3. 汇总分析

        return res;
    }

    public Integer syncNewData(List<ZkFishOperationLogDO> res) {
        if(EasyListUtil.isEmpty(res)) return 0;
        // 1.1 从 zk_pond、zk_specs_log、zk_biology_capital 中获取实际饵料系数、预估饵料系数、累计投喂、当前投喂、当前成本、生物资产
        Set<Long> pondSet = res.stream().map(ZkFishOperationLogDO::getPondId).collect(Collectors.toSet());
        Map<Long, ZkPondDO> pondMap = zkPondService.selectMapByPondIds(pondSet);
        // System.out.println(String.format("syncNewData 1=====================>ZkPondDO:%s", JSON.toJSONString(pondMap)));
        // 1.2. 从 zk_pond_fish 获取 信息
        Map<Long, ZkPondFishDO> fishMap = zkPondFishService.selectMapByPondIdSet(pondSet);
        // System.out.println(String.format("syncNewData 2=====================>ZkPondFishDO:%s", JSON.toJSONString(fishMap)));
        // 1.3 获取鱼苗成本信息
        List<Map<String, Object>> mapList = zkFishOperationLogMapper.staticCost();
        List<ZkFishOperationLogDO> needUpdate = new ArrayList<>();
        for(ZkFishOperationLogDO detail : res) {
            AtomicReference<Boolean> flag = new AtomicReference<>(false);
            // 1.3 存塘 信息 补充
            MapUtils.findAndThen(pondMap, detail.getPondId(), curr -> {
                detail.build_fishDensity(curr.getPondScale(), Long.parseLong(curr.getFishNum()+""), curr.getFishSpec()).setFishSpec(curr.getFishSpec()+"");
                detail.build_other3(curr.getAccumulateFeedCount(), curr.selectRadio(), curr.getBiologyCost()).setPondDetailNum(curr.getPondDetailNum());
                flag.set(true);
            });
            MapUtils.findAndThen(fishMap, detail.getPondId(), fish -> {
                detail.build_pondFish(fish);
                flag.set(true);
            });
            if(!flag.get()) continue;
            needUpdate.add(detail);
        }
        CommonUtil.originList2Targets3(mapList, "pondId", needUpdate, "pondId", (Map<String, Object> o, ZkFishOperationLogDO t) -> {
            t.build_map(o);
        });
        // 2. 更新 使用 sql统计的信息，部分数据因为 反复错误操作 导致 zk_fish_operation_log 统计数据不正确，使用SQL统计更加准确，添加自动修复功能
        useSqlStaticValue(needUpdate);

        // 3. 执行更新
        zkFishOperationLogService.customUpdateBatch(needUpdate);

        return res.size();
    }

    /**
     * 更新 使用 sql统计的信息，部分数据因为 反复错误操作 导致 zk_fish_operation_log 统计数据不正确，使用SQL统计更加准确，添加自动修复功能
     * @param needUpdate 需要更新的 统计值
     */
    public void useSqlStaticValue(List<ZkFishOperationLogDO> needUpdate) {
        if(EasyListUtil.isEmpty(needUpdate)) return;
        // 1. 查询当前的投喂统计(当日、累计)
        handFeed(needUpdate);
        // 2. 查询 当前 死亡统计(当前、累计)
        handleDead(needUpdate);
    }

    private void handFeed(List<ZkFishOperationLogDO> needUpdate) {
        try {
            List<Map<String, Object>> feedMapList = zkFeedingLogService.staticFeedInfo();
            for(Map<String, Object> o : feedMapList) {
                Long pondId = (Long) o.get("pond_id");
                ZkFishOperationLogDO curr = null;
                for(ZkFishOperationLogDO item : needUpdate) {
                    if(Objects.equals(item.getPondId(), pondId)) curr = item;
                }
                if(curr == null) continue;
                // 2. 设置 当前值
                BigDecimal curr_feed_count = (BigDecimal) o.get("curr_feed_count");
                BigDecimal accumulate_feed_count = (BigDecimal) o.get("accumulate_feed_count");
                curr.setCurrFeedCount(curr_feed_count)
                        .setAccumulateFeedCount(accumulate_feed_count);
            }
        } catch (Exception e) {
            String subject = String.format("【统计异常】", e.getMessage());
            String content = String.format("更新 使用 sql统计的信息，部分数据因为 反复错误操作 导致 zk_fish_operation_log 统计数据不正确，使用SQL统计更加准确，添加投喂自动修复功能错误，数据：%s,error：%s", JSON.toJSONString(needUpdate), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
            log.error(content);
        }
    }

    private void handleDead(List<ZkFishOperationLogDO> needUpdate) {
        try {
            // List<Map<String, Object>> deadMapList = zkPondFishDetailService.staticDeadInfo();
            List<StaticDeadInfoVo3> deadMapList = zkPondFishDetailService.staticDeadInfo3();
            for(StaticDeadInfoVo3 o : deadMapList) {
                Long pondId = o.getPondId();
                ZkFishOperationLogDO curr = null;
                for(ZkFishOperationLogDO item : needUpdate) {
                    if(Objects.equals(item.getPondId(), pondId)) curr = item;
                }
                if(curr == null) continue;
                // 2. 设置 当前值
                long fish_num = o.getPondFishNum();
                BigDecimal day_rate = EasyBigDecimalUtil.divide(new BigDecimal(o.getTodayDeath()), EasyBigDecimalUtil.add(new BigDecimal(o.getTodayDeath()), new BigDecimal(fish_num)));
                BigDecimal recentDayMortalityRatePer = EasyBigDecimalUtil.divide(new BigDecimal(o.getSevenDayDeath()), EasyBigDecimalUtil.add(new BigDecimal(o.getSevenDayDeath()), new BigDecimal(fish_num)));
                BigDecimal accumulate_rate = EasyBigDecimalUtil.divide(new BigDecimal(o.getTotalDeath()), EasyBigDecimalUtil.add(new BigDecimal(o.getTotalDeath()), new BigDecimal(fish_num)));
                curr.setDayFishNum(o.getTodayDeath()).setAccumulateFishNum(o.getTotalDeath())
                        .setRecentDayMortalityRate(o.getSevenDayDeath()).setRecentDayMortalityRatePer(recentDayMortalityRatePer)
                        .setSurvivalRate(EasyBigDecimalUtil.subtract(new BigDecimal(1), accumulate_rate))
                        .setDayRate(day_rate).setAccumulateRate(accumulate_rate);
            }
        } catch (NumberFormatException e) {
            String subject = String.format("【统计异常】", e.getMessage());
            String content = String.format("更新 使用 sql统计的信息，部分数据因为 反复错误操作 导致 zk_fish_operation_log 统计数据不正确，使用SQL统计更加准确，添加死亡自动修复功能错误，数据：%s,error：%s", JSON.toJSONString(needUpdate), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
            log.error(content);
        }
    }

}
