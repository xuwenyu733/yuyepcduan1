package cn.iocoder.yudao.module.fish.controller.admin.zkdevicerecord.schedule;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.easy.CustomMultipartFile;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.EnvInfo;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdeviceproperties.ZkDevicePropertiesDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkintelligentdiagnosis.ZkIntelligentDiagnosisDO;
import cn.iocoder.yudao.module.fish.service.zkdeviceproperties.ZkDevicePropertiesService;
import cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService;
import cn.iocoder.yudao.module.fish.service.zkintelligentdiagnosis.ZkIntelligentDiagnosisService;
import cn.iocoder.yudao.module.infra.controller.admin.file.FileController;
import cn.iocoder.yudao.module.infra.controller.admin.file.vo.file.FileUploadReqVO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 这个任务用于处理，crash 之后的消费者未消费完的消息
 */
@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class SchedulingClearDeviceLog {

    private static final String LOCK_KEY = "redis:stream:scheduling_clear_device_log:lock";

    private static final int EXPIRE_TIME = 5 * 60;

    private final List<AbstractRedisStreamMessageListener<?>> listeners;
    private final RedisMQTemplate redisTemplate;
    private final RedissonClient redissonClient;

    @Resource
    private ZkDeviceRecordService zkDeviceRecordService;

    @Resource
    private ZkDevicePropertiesService zkDevicePropertiesService;

    @Resource
    private FileController fileController;

    /**
     * 入口方法   0 0 1 * * ?
     */
    @Scheduled(cron = "0 0/10 * * * ? ")
    public void messageResend() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                // execute();
            } catch (Exception ex) {
                log.error("[messageResend][执行异常]", ex);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * 执行具体逻辑
     * zk_device_record 、 zk_device_properties 两个表里面的数据会持续增长，只保留最近7天的数据
     */
    private void execute() {
        log.warn("开始执行定时任务==>zk_device_record 、 zk_device_properties start");
        clearDeviceRecord();
        clearDeviceProperties();
        log.warn("结束执行定时任务==>zk_device_record 、 zk_device_properties end");
    }

    public void clearDeviceRecord() {
        try {
            // 1. 获取需要删除的数据
            List<ZkDeviceRecordDO> recordDOS = zkDeviceRecordService.selectNeedDel(3);
            recordDOS = recordDOS.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (CollUtil.isEmpty(recordDOS)) {
                log.warn("【清除zk_device_record中一周前数据】没有需要删除的数据 ZkDeviceRecordDO");
                return;
            }
            // 2. 保存为txt文件
            String filePath = EnvInfo.genPath("zk_device_record.txt");
            FileUtils.writeLines(new File(filePath), recordDOS);
            // 3. 保存到阿里云OSS中
            CustomMultipartFile file = new CustomMultipartFile("zk_device_record.txt", filePath, "text/plain", Files.readAllBytes(Paths.get(filePath)));
            FileUploadReqVO vo = new FileUploadReqVO(file, "zk_device", UUID.randomUUID().toString(), "zk_device_record");
            CommonResult<String> stringCommonResult = fileController.uploadFile(vo);
            log.warn("【清除zk_device_record中一周前数据】保存到阿里云OSS中成功 {}", JSON.toJSONString(stringCommonResult));
            // 4. 删除数据
            zkDeviceRecordService.customeDelete(recordDOS);
        } catch (Exception e) {
            String sub = String.format("【定时删除zk_device_record 数据防止数据过多异常】message:%s", e.getMessage());
            String error_msg = String.format("error: %s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, error_msg);
            log.error(sub, error_msg);
        }
    }


    public void clearDeviceProperties() {
        try {
            // 1. 获取需要删除的数据
            List<ZkDevicePropertiesDO> propertiesDOS = zkDevicePropertiesService.selectNeedDel(3);
            propertiesDOS = propertiesDOS.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (CollUtil.isEmpty(propertiesDOS)) {
                log.warn("【清除zk_device_properties中一周前数据】没有需要删除的数据 ZkDeviceRecordDO");
                return;
            }
            // 2. 保存为txt文件
            String filePath = EnvInfo.genPath("zk_device_properties.txt");
            FileUtils.writeLines(new File(filePath), propertiesDOS);
            // 3. 保存到阿里云OSS中
            CustomMultipartFile file = new CustomMultipartFile("zk_device_properties.txt", filePath, "text/plain", Files.readAllBytes(Paths.get(filePath)));
            FileUploadReqVO vo = new FileUploadReqVO(file, "zk_device", UUID.randomUUID().toString(), "zk_device_properties");
            CommonResult<String> stringCommonResult = fileController.uploadFile(vo);
            log.warn("【清除zk_device_properties中一周前数据】保存到阿里云OSS中成功 {}", JSON.toJSONString(stringCommonResult));
            // 4. 删除数据
            zkDevicePropertiesService.customeDelete(propertiesDOS);
        } catch (Exception e) {
            String sub = String.format("【定时删除zk_device_properties 数据防止数据过多异常】message:%s", e.getMessage());
            String error_msg = String.format("error: %s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, error_msg);
            log.error(sub, error_msg);
        }
    }

}
