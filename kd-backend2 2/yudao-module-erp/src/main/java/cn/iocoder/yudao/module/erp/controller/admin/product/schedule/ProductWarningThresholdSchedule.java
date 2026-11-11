package cn.iocoder.yudao.module.erp.controller.admin.product.schedule;

import cn.iocoder.yudao.framework.common.base_api.BaseNotifyService;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.EnvInfo;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.service.product.ErpProductService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存预警提醒
 */
@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class ProductWarningThresholdSchedule {

    private static final String LOCK_KEY = "redis:lock:ProductWarningThresholdSchedule";


    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private PlusRedisService plusRedisService;

    @Autowired
    private ErpProductService erpProductService;
    @Resource
    private BaseNotifyService baseNotifyService;
    // 已经报过警的不能重复提醒
    public static String PRODUCT_WARNING_THRESHOLD_KEY = "erp:product_warning_threshold:";
    public static Long PRODUCT_WARNING_THRESHOLD_TIME = 60*60*24*5L;

    /**
     * 每日凌晨3点1分1秒执行
     * 35 * * * * ? 每分钟的35s执行
     * 1 1 3 * * ?
     * 当前是3小时执行一次
     */
    @Scheduled(cron = "1 1 0/3 * * ?")
    public void scheduleMethod() {
        // if(EnvInfo.isDev()) { //  || true
        //     log.warn(String.format("%s 测试环境无需执行统计定时入库", "ProductWarningThresholdSchedule"));
        //     return;
        // }
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                String execute = execute();
                EmailUtil.send("库存预警提醒-结束", execute);
            } catch (Exception ex) {
                log.error("ProductWarningThresholdSchedule [messageResend][执行异常]", ex);
                EmailUtil.send("库存预警提醒-异常", ExceptionUtil.getStackTrace(ex));
            } finally {
                lock.unlock();
            }
        } else {
            log.warn(String.format("%s 抢锁失败", "ProductWarningThresholdSchedule"));
            String subject = String.format("【定时任务生成统计数据-抢锁失败】,middle,redis锁key:%s", LOCK_KEY);
            String content = String.format("具体异常：%s", "抢锁失败");
            EmailUtil.send(subject, content);
            log.error(subject, content);
        }
    }

    private String execute() {
        try {
            log.warn(String.format("ProductWarningThresholdSchedule========> 抢到锁了 start "));
            // 1. 获取需要通知的商品
            List<ErpProductDO> erpProductDOS = erpProductService.selectNeedWarningThreshold();
            // 2. 执行通知
            if(EasyListUtil.isEmpty(erpProductDOS)) {
                log.warn(String.format("ProductWarningThresholdSchedule========> 没有需要通知的商品"));
                return "没有需要通知的商品";
            }
            List<String> noticeMsgList = new ArrayList<>();
            for(ErpProductDO item : erpProductDOS) {
                // 3. 判断是否5天内通知过，如果通知过不重复通知
                String redisKey = PRODUCT_WARNING_THRESHOLD_KEY+ item.getId();
                if(plusRedisService.exist(redisKey)) continue;
                // 4. 没有通知，执行通知
                String msg = String.format("您好！当前系统%s库存%s,预警阈值为%s，请确认后尽快采购。", item.getProductName()+item.getProductNo(),
                        item.getCount(), item.getWarningThreshold());
                baseNotifyService.insertNotify(item.getUserId(), msg);
                noticeMsgList.add(msg);
                // 5. 添加redis缓存，防止重复提醒
                plusRedisService.insertStr(redisKey, "1", PRODUCT_WARNING_THRESHOLD_TIME);
            }
            log.warn(String.format("ProductWarningThresholdSchedule========> 商品预警阈值信息如下：%s", JSON.toJSONString(noticeMsgList)));
            return JSON.toJSONString(noticeMsgList);
        } catch (Exception e) {
            // e.printStackTrace();
            String subject = String.format("【库存预警提醒-异常】,middle,异常名:%s", e.getMessage());
            String content = String.format("具体异常：%s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
            log.error(subject, content);
            return content;
        }
    }

}
