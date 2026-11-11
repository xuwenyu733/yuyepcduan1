package cn.iocoder.yudao.module.fish.controller.admin.daping.schedule;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.EnvInfo;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn.WarnMessage4IOT;
import cn.iocoder.yudao.module.fish.service.zkpondwarn.ZkPondWarnService;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class GetDeviceWarn4IOTSchedule {

    private static final String LOCK_KEY = "redis:lock:GetDeviceWarn4IOTSchedule";

    @Autowired
    private PlusRedisService plusRedisService;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ZkPondWarnService zkPondWarnService;
    // 环境指标key
    public static String DEVICE_WARN_KEY = "device:event:*";
    public static String DEVICE_WARN_HANDLED_KEY = "device:event_has_handled:";

    /**
     * 每5小时执行一次，是为了避免整点任务过多的问题
     * 35 * * * * ?
     */
    @Scheduled(cron = "1 1 0/5 * * ?")
    public void scheduleMethod() {
        if(EnvInfo.isDev() || true) {
            log.warn(String.format("%s 测试环境无需执行警告定时入库", "GetDeviceWarn4IOTSchedule"));
            return;
        }
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                execute();
            } catch (Exception ex) {
                log.error("[messageResend][执行异常]", ex);
            } finally {
                lock.unlock();
            }
        } else {
            log.warn(String.format("%s 抢锁失败", "GetDeviceWarn4IOTSchedule"));
        }
    }

    private void execute() {
        try {
            log.warn(String.format("GetDeviceWarn4IOTSchedule========> 抢到锁了 start "));
            // 1. 获取警告
            List<WarnMessage4IOT> warnMessage4IOTS = selectWarn();
            if(EasyListUtil.isEmpty(warnMessage4IOTS)) {
                log.warn(String.format("GetDeviceWarn4IOTSchedule========> 没有系统警告"));
                return;
            }
            for(WarnMessage4IOT warnMessage4IOT : warnMessage4IOTS) {
                // 2. 判断是否已经处理过
                String key = String.format("%s%s", DEVICE_WARN_HANDLED_KEY, warnMessage4IOT.getTimestamp());
                if(plusRedisService.exist(key)) continue;
                // 3. 添加到数据库,数据库添加了唯一标识，且推送上来数据不多，可以一条一条的添加
                Integer addRes = zkPondWarnService.customInsert(warnMessage4IOT);
                log.warn(String.format("GetDeviceWarn4IOTSchedule========> 抢到锁了 end,添加告警信息:%s", addRes));
                // 4. 警告添加到redis 表示已经处理过了
                plusRedisService.insertStr(key, "1", 60*24*30L); // 缓存30天
            }
        } catch (Exception e) {
            log.warn("[messageResend][执行异常]" + e);
            String subject = String.format("【产品填充仓库信息-异常】,middle,异常名:%s", e.getMessage());
            String content = String.format("具体异常：%s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(subject, content);
            log.error(subject, content);
        }
    }

    /**
     * 从redis获取iot推送过来的系统警报
     * @return
     */
    public List<WarnMessage4IOT> selectWarn() {
        List<WarnMessage4IOT> res = new ArrayList<>();
        List<String> childrenByParent = plusRedisService.getChildrenByParent(DEVICE_WARN_KEY);
        for(String item : childrenByParent) {
            if(StringUtil.isEmpty(item)) continue;
            try {
                // log.warn(String.format("item 1==========>%s", item));
                WarnMessage4IOT warnMessage4IOT = JSON.parseObject(item, WarnMessage4IOT.class);
                if(warnMessage4IOT == null) continue;

                res.add(warnMessage4IOT);
                // log.warn(String.format("item 2==========>%s", JSON.toJSONString(warnMessage4IOT)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // log.warn(String.format("item==========>%s", item));
        }
        return res;
    }
}
