package cn.iocoder.yudao.module.fish.controller.admin.zkpondfishdetail.schedule;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishtype.ZkPondFishTypeDO;
import cn.iocoder.yudao.module.fish.service.zkpondfishdetail.ZkPondFishDetailService;
import cn.iocoder.yudao.module.fish.service.zkpondfishtype.ZkPondFishTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class SyncPondFishTypeSchedule {

    private static final String LOCK_KEY = "redis:lock:SyncPondFishTypeSchedule";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ZkPondFishDetailService zkPondFishDetailService;

    @Autowired
    private ZkPondFishTypeService zkPondFishTypeService;

    @Scheduled(cron = "0 0 0/3 * * ?") // 每3小时执行一次
    public void scheduleMethod() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.tryLock()) {
            try {
                execute();
            } catch (Exception ex) {
                log.error("[SyncPondFishTypeSchedule][执行异常]", ex);
            } finally {
                lock.unlock();
            }
        } else {
            log.warn("SyncPondFishTypeSchedule 抢锁失败");
        }
    }

    private void execute() {
        log.info("SyncPondFishTypeSchedule start");
        
        // 1. 查询所有明细，按创建时间排序，以便从头计算出当前的准确存塘情况
        LambdaQueryWrapper<ZkPondFishDetailDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(ZkPondFishDetailDO::getCreateTime);
        List<ZkPondFishDetailDO> details = zkPondFishDetailService.list(queryWrapper);
        
        if (EasyListUtil.isEmpty(details)) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        // 2. 删除今天的数据（因为要重新统计插入今天的最新数据）
        LambdaQueryWrapper<ZkPondFishTypeDO> deleteTodayWrapper = new LambdaQueryWrapper<>();
        deleteTodayWrapper.eq(ZkPondFishTypeDO::getStaticYear, today.getYear())
                .eq(ZkPondFishTypeDO::getStaticMonth, today.getMonthValue())
                .eq(ZkPondFishTypeDO::getStaticDay, today.getDayOfMonth());
        zkPondFishTypeService.remove(deleteTodayWrapper);

        // 删除7天前及更早的数据，从而保留最近7天的汇总
        LambdaQueryWrapper<ZkPondFishTypeDO> deleteOldWrapper = new LambdaQueryWrapper<>();
        String sevenDaysAgoStr = String.format("%04d-%02d-%02d", sevenDaysAgo.getYear(), sevenDaysAgo.getMonthValue(), sevenDaysAgo.getDayOfMonth());
        deleteOldWrapper.apply("CONCAT(static_year, '-', LPAD(static_month, 2, '0'), '-', LPAD(static_day, 2, '0')) <= {0}", sevenDaysAgoStr);
        zkPondFishTypeService.remove(deleteOldWrapper);

        // pondId -> fishType -> batchNumber -> fishSpec -> fishNum
        Map<Long, Map<String, Map<String, Map<String, Long>>>> pondFishMap = new HashMap<>();

        // 3. 顺序回放所有操作，计算出当前的存塘鱼苗情况
        for (ZkPondFishDetailDO detail : details) {
            processDetail(detail, pondFishMap);
        }

        // 4. 构建今天的数据
        List<ZkPondFishTypeDO> insertList = new ArrayList<>();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        for (Map.Entry<Long, Map<String, Map<String, Map<String, Long>>>> pondEntry : pondFishMap.entrySet()) {
            Long pondId = pondEntry.getKey();
            for (Map.Entry<String, Map<String, Map<String, Long>>> typeEntry : pondEntry.getValue().entrySet()) {
                String fishType = typeEntry.getKey();
                for (Map.Entry<String, Map<String, Long>> batchEntry : typeEntry.getValue().entrySet()) {
                    String batchNumber = batchEntry.getKey();
                    for (Map.Entry<String, Long> specEntry : batchEntry.getValue().entrySet()) {
                        String fishSpec = specEntry.getKey();
                        Long fishNum = specEntry.getValue();

                        if (fishNum > 0) {
                            ZkPondFishTypeDO typeDO = new ZkPondFishTypeDO();
                            typeDO.setPondId(pondId);
                            typeDO.setFishType(fishType);
                            typeDO.setBatchNumber(batchNumber);
                            typeDO.setFishSpec(fishSpec);
                            typeDO.setFishNum(fishNum);
                            // 统计时间使用当前系统执行时间
                            typeDO.setStaticYear(year);
                            typeDO.setStaticMonth(month);
                            typeDO.setStaticDay(day);
                            // typeDO.setTenantId(1L); // 默认租户
                            insertList.add(typeDO);
                        }
                    }
                }
            }
        }

        // 5. 批量插入今天的数据
        if (!insertList.isEmpty()) {
            // 分批插入
            int batchSize = 1000;
            for (int i = 0; i < insertList.size(); i += batchSize) {
                int end = Math.min(i + batchSize, insertList.size());
                zkPondFishTypeService.saveBatch(insertList.subList(i, end));
            }
        }

        log.info("SyncPondFishTypeSchedule end, total insert for today: {}", insertList.size());
    }

    private void processDetail(ZkPondFishDetailDO detail, Map<Long, Map<String, Map<String, Map<String, Long>>>> pondFishMap) {
        String addType = detail.getAddType();
        Long pondId = detail.getPondId();
        String fishType = detail.getFishTypes();
        String batchNumber = detail.getBatchNumber();
        String fishSpec = detail.getFishSpec();
        Long fishNum = detail.getFishNum() == null ? 0L : detail.getFishNum();

        if (pondId == null || fishType == null) return;
        if (batchNumber == null) batchNumber = "";
        if (fishSpec == null) fishSpec = "";

        if ("1".equals(addType)) {
            // 投放
            addFish(pondFishMap, pondId, fishType, batchNumber, fishSpec, fishNum);
        } else if ("-2".equals(addType) || "-1".equals(addType)) {
            // 死亡、销售
            addFish(pondFishMap, pondId, fishType, batchNumber, fishSpec, -fishNum);
        } else if ("-3".equals(addType)) {
            // 分池
            Long targetPondId = detail.getTargetPondId();
            String targetFishSpec = detail.getTargetFishSpec();
            if (targetFishSpec == null) targetFishSpec = "";
            
            if (targetPondId != null && targetPondId.equals(pondId)) {
                // 自己给自己分池，直接设置数量
                setFish(pondFishMap, pondId, fishType, batchNumber, targetFishSpec, fishNum);
            } else {
                // 原塘减少
                addFish(pondFishMap, pondId, fishType, batchNumber, fishSpec, -fishNum);
                // 目标塘增加
                if (targetPondId != null) {
                    addFish(pondFishMap, targetPondId, fishType, batchNumber, targetFishSpec, fishNum);
                }
            }
        }
    }

    private void addFish(Map<Long, Map<String, Map<String, Map<String, Long>>>> pondFishMap, 
                         Long pondId, String fishType, String batchNumber, String fishSpec, Long num) {
        pondFishMap.computeIfAbsent(pondId, k -> new HashMap<>())
                .computeIfAbsent(fishType, k -> new HashMap<>())
                .computeIfAbsent(batchNumber, k -> new HashMap<>())
                .merge(fishSpec, num, Long::sum);
    }

    private void setFish(Map<Long, Map<String, Map<String, Map<String, Long>>>> pondFishMap, 
                         Long pondId, String fishType, String batchNumber, String fishSpec, Long num) {
        pondFishMap.computeIfAbsent(pondId, k -> new HashMap<>())
                .computeIfAbsent(fishType, k -> new HashMap<>())
                .computeIfAbsent(batchNumber, k -> new HashMap<>())
                .put(fishSpec, num);
    }
}
