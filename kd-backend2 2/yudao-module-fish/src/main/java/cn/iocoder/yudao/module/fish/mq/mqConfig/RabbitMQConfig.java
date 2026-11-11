package cn.iocoder.yudao.module.fish.mq.mqConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@Configuration
public class RabbitMQConfig {

    // 队列名称 - 使用与另一个服务相同的队列名称，实现消息广播
    public static final String ALERT_CALL_QUEUE2 = "alertCallMessage2";
    public static final String ALERT_CALL_QUEUE1 = "alertCallMessage1";
    
    // 交换机名称 - 使用与另一个服务相同的交换机名称
    public static final String ALERT_EXCHANGE = "massageHandler_to_alert1";
    
    // 路由键 - 使用与另一个服务相同的路由键模式
    public static final String ALERT_ROUTING_KEY = "alertCallMessage.#";

    /**
     * 声明队列
     */
    @Bean
    public Queue alertCallQueue2() {
        // 使用与另一个服务相同的队列名称，避免队列名称冲突
        // 不强制设置队列参数，避免与已存在的队列冲突
        return new Queue(ALERT_CALL_QUEUE2, true, false, false);
    }

    @Bean
    public Queue alertCallQueue1() {
        // 使用与另一个服务相同的队列名称，避免队列名称冲突
        // 不强制设置队列参数，避免与已存在的队列冲突
        return new Queue(ALERT_CALL_QUEUE1, true, false, false);
    }

    /**
     * 声明交换机
     */
    @Bean
    public FanoutExchange alertExchange() {
        return new FanoutExchange(ALERT_EXCHANGE, true, false);
    }

    /**
     * 队列绑定到交换机
     */
//    @Bean
//    public Binding bindingAlertCallQueue(Queue alertCallQueue, FanoutExchange alertExchange) {
//        return BindingBuilder.bind(alertCallQueue).to(alertExchange);
//    }

    @Bean
    public Binding bindingAlertCallQueue2(Queue alertCallQueue2, FanoutExchange alertExchange) {
        return BindingBuilder.bind(alertCallQueue2).to(alertExchange);
    }
    @Bean
    public Binding bindingAlertCallQueue1(Queue alertCallQueue1, FanoutExchange alertExchange) {
        return BindingBuilder.bind(alertCallQueue1).to(alertExchange);
    }


    // @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper());
    }
    
    @Bean
    @ConditionalOnBean(ConnectionFactory.class)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        
        // 设置消息确认模式
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功");
            } else {
                System.out.println("消息发送失败: " + cause);
            }
        });
        
        // 设置返回回调
        template.setReturnsCallback(returned -> {
            System.out.println("消息被退回: " + returned);
        });
        
        // 添加连接恢复监听器
        template.setRecoveryCallback(recoveryContext -> {
            System.out.println("RabbitMQ连接恢复");
            return null;
        });
        
        // 设置重试机制
        template.setRetryTemplate(null); // 使用外部配置的重试模板
        
        return template;
    }
}