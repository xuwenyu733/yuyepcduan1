package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.schdule;

import cn.iocoder.yudao.framework.common.util.easy.*;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn.WarnMessage4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog.ZkFishOperationLogController;
import cn.iocoder.yudao.module.fish.service.zkpondwarn.ZkPondWarnService;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class GenFishStatisticsLogSchedule {

    private static final String LOCK_KEY = "redis:lock:GenFishStatisticsLogSchedule";

    @Autowired
    private PlusRedisService plusRedisService;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ZkFishOperationLogController zkFishOperationLogController;
    // 环境指标key
    public static String DEVICE_WARN_KEY = "device:event:*";
    public static String DEVICE_WARN_HANDLED_KEY = "device:event_has_handled:";

    /**
     * 每日凌晨3点1分1秒执行
     * 35 * * * * ? 每分钟的35s执行
     * 1 1 3 * * ?
     * 当前是3小时执行一次
     */
    @Scheduled(cron = "1 1 0/3 * * ?")
    public void scheduleMethod() {
         if(EnvInfo.isDev()) { //  || true
             log.warn(String.format("%s 测试环境无需执行统计定时入库", "GenFishStatisticsLogSchedule"));
             return;
         }
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                execute();
                EmailUtil.send("定时任务生成zk_fish_operation_log统计数据-结束", "任务执行完成。");
            } catch (Exception ex) {
                log.error("GenFishStatisticsLogSchedule [messageResend][执行异常]", ex);
                EmailUtil.send("定时任务生成zk_fish_operation_log统计数据-异常", ExceptionUtil.getStackTrace(ex));
            } finally {
                lock.unlock();
            }
        } else {
            log.warn(String.format("%s 抢锁失败", "GenFishStatisticsLogSchedule"));
            String subject = String.format("【定时任务生成统计数据-抢锁失败】,middle,redis锁key:%s", LOCK_KEY);
            String content = String.format("具体异常：%s", "抢锁失败");
            EmailUtil.send(subject, content);
            log.error(subject, content);
        }
    }

    private void execute() {
        try {
            log.warn(String.format("GenFishStatisticsLogSchedule========> 抢到锁了 start "));
            // 1. 开始统计
            // zkFishOperationLogController.genLog(EasyDateUtil.format(new Date(), "yyyy-MM-dd"), false);
            zkFishOperationLogController.genLog();
        } catch (Exception e) {
            // e.printStackTrace();
            String subject = String.format("【定时任务生成zk_fish_operation_log统计数据-异常】,middle,异常名:%s", e.getMessage());
            String content = String.format("具体异常：%s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
            log.error(subject, content);
        }
    }

}
