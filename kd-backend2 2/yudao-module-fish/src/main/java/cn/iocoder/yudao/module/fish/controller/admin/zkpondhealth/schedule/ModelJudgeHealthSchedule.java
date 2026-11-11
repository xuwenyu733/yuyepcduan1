package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.schedule;

import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.EnvInfo;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.ZkPondHealthController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class ModelJudgeHealthSchedule {


    private static final String LOCK_KEY = "redis:lock:ModelJudgeHealthSchedule";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ZkPondHealthController zkPondHealthController;

    /**
     * 每日凌晨3点3分1秒执行
     * 35 * * * * ? 每分钟的35s执行
     */
    @Scheduled(cron = "1 3 3 * * ?")
    public void scheduleMethod() {
        if(EnvInfo.isDev()) { //  || true
            log.warn(String.format("%s 将 设备统计、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充健康评估数据库", "ModelJudgeHealthSchedule"));
            return;
        }
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                execute();
            } catch (Exception ex) {
                log.error("ModelJudgeHealthSchedule [messageResend][执行异常]", ex);
            } finally {
                lock.unlock();
            }
        } else {
            log.warn(String.format("%s 抢锁失败", "ModelJudgeHealthSchedule"));
        }
    }

    private void execute() {
        try {
            log.warn(String.format("ModelJudgeHealthSchedule========> 抢到锁了 start "));
            // 1. 开始统计
            zkPondHealthController.genPondHealthData();
        } catch (Exception e) {
            String subject = String.format("【将 设备统计、任务信息 ==》中文描述==》请求大模型==》获取json结果==》填充健康评估数据库-异常】,middle,异常名:%s", e.getMessage());
            String content = String.format("具体异常：%s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
            log.error(subject, content);
        }
    }
    
}
