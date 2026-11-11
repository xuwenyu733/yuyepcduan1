package cn.iocoder.yudao.module.fish.controller.admin.evaluation.schedule;

import cn.iocoder.yudao.framework.common.util.easy.EnvInfo;
import cn.iocoder.yudao.module.fish.service.evaluation.OaEvaluationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * OA考核评分定时任务
 * 每月最后一天执行考核评分计算
 *
 * @author 系统管理员
 */
@Slf4j
@Component
@EnableScheduling
public class OaEvaluationSchedule {

    private static final String LOCK_KEY = "redis:lock:OaEvaluationSchedule";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OaEvaluationService oaEvaluationService;

    /**
     * 每月最后一天23:59:59执行
     * Cron表达式说明：
     * 秒 分 时 日 月 周
     * 59 59 23 L * ?  - 每月最后一天23:59:59
     * 注意：Spring的@Scheduled不支持L（最后一天），所以使用每天检查的方式
     * 或者使用：0 0 1 1 * ? - 每月1号凌晨1点执行（计算上个月的考核）
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void scheduleMethod() {
        if (EnvInfo.isDev()) {
            log.warn("{} 测试环境无需执行考核评分计算", "OaEvaluationSchedule");
            return;
        }

        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                execute();
            } catch (Exception ex) {
                log.error("OaEvaluationSchedule [执行异常]", ex);
            } finally {
                lock.unlock();
            }
        } else {
            log.warn("{} 抢锁失败", "OaEvaluationSchedule");
        }
    }

    private void execute() {
        try {
            log.info("OaEvaluationSchedule========> 开始执行考核评分计算");
            
            // 计算上个月的考核评分
            LocalDate now = LocalDate.now();
            YearMonth lastMonth = YearMonth.from(now.minusMonths(1));
            String evaluationPeriod = lastMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            oaEvaluationService.calculateEvaluation(evaluationPeriod);
            
            log.info("OaEvaluationSchedule========> 考核评分计算完成，考核期：{}", evaluationPeriod);
        } catch (Exception ex) {
            log.error("[OaEvaluationSchedule][执行异常]", ex);
        }
    }
}



