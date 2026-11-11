package cn.iocoder.yudao.server.thread;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 创建异步线程池，方便异步编排操作
 * @author lr
 * @date 2021/1/18
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Slf4j
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfig){
        log.warn("【ThreadPoolExecutor配置】{}", JSON.toJSONString(poolConfig));
        return new ThreadPoolExecutor(poolConfig.getCoreSize()  , poolConfig.getMaxSize() , poolConfig.getKeepAliveTime() , TimeUnit.SECONDS,
                new LinkedBlockingDeque<>( poolConfig.getBlockQueueSize() ),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy() ) ;
    }
}
