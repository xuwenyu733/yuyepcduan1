package cn.iocoder.yudao.server.thread;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lr
 * @date 2021/1/18
 */
@ConfigurationProperties(prefix = "kbs.ecology.thread")
@Getter
@Setter
public class ThreadPoolConfigProperties {
    // 核心线程大小，设置成服务器核心数最佳
    private Integer coreSize  = 4;
    // 线程池最大容量大小
    private Integer maxSize = 100;
    // 线程空闲时，线程存活的时间,秒
    private Integer keepAliveTime = 0 ;
    // 任务队列数。一个阻塞队列
    private Integer blockQueueSize = 10000;
}
