package com.n11.bootcamp.ecommerce.stock_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${stock.rabbit.exchange}")
    private String exchangeName;

    @Value("${stock.rabbit.reserveRequestedQueue}")
    private String reserveRequestedQueueName;

    @Value("${stock.rabbit.reserveRequestedRoutingKey}")
    private String reserveRequestedRoutingKey;


//    @Value("${stock.rabbit.dlx:}")
//    private String dlxName;
//
//    @Value("${stock.rabbit.dlq:}")
//    private String dlqName;

    @Bean
    public TopicExchange stockExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue reserveRequestedQueue() {
//        if (dlxName != null && !dlxName.isBlank()) {
//            return QueueBuilder.durable(reserveRequestedQueueName)
//                    .withArgument("x-dead-letter-exchange", dlxName)
//                    .withArgument("x-dead-letter-routing-key", dlqName)
//                    .build();
//        }
        return QueueBuilder.durable(reserveRequestedQueueName).build();
    }

    @Bean
    public Binding reserveRequestedBinding(Queue reserveRequestedQueue, TopicExchange stockExchange) {
        // Order -> (stock.events.exchange, "order.stock.reserve.requested") -> Stock queue
        //konum park, evden gidicen, 56 lar üzerinden gidicen
        return BindingBuilder.bind(reserveRequestedQueue)
                .to(stockExchange)
                .with(reserveRequestedRoutingKey);
    }


//    @Bean
//    public TopicExchange stockDlx() {
//        if (dlxName == null || dlxName.isBlank()) return null;
//        return new TopicExchange(dlxName, true, false);
//    }
//
//    @Bean
//    public Queue stockDlq() {
//        if (dlqName == null || dlqName.isBlank()) return null;
//        return QueueBuilder.durable(dlqName).build();
//    }
//
//    @Bean
//    public Binding stockDlqBinding(Queue stockDlq, TopicExchange stockDlx) {
//        if (stockDlq == null || stockDlx == null) return null;
//        return BindingBuilder.bind(stockDlq).to(stockDlx).with(dlqName);
//    }


    @Bean
    public MessageConverter stockJacksonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter stockJacksonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(stockJacksonMessageConverter);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter stockJacksonMessageConverter) {
        if (connectionFactory instanceof CachingConnectionFactory ccf) {
            ccf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
            ccf.setPublisherReturns(true);
        }

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(stockJacksonMessageConverter);
        template.setMandatory(true);
        return template;
    }

}
