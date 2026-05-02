package com.n11.bootcamp.ecommerce.order_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConfig.class);


    @Value("${rabbitmq.exchange}")
    private String ordersExchangeName;

    @Value("${rabbitmq.queue}")
    private String orderCreatedQueueName;

    @Value("${rabbitmq.routingkey}")
    private String orderCreatedRoutingKey;


    @Value("${stock.rabbit.exchange}")
    private String stockExchangeName;

    @Value("${stock.rabbit.reservedRoutingKey}")
    private String stockReservedRoutingKey;

    @Value("${stock.rabbit.rejectedRoutingKey}")
    private String stockRejectedRoutingKey;

    @Value("${order.rabbit.stockReservedQueue}")
    private String orderStockReservedQueueName;

    @Value("${order.rabbit.stockRejectedQueue}")
    private String orderStockRejectedQueueName;



    // Main exchange (orders.exchange)
    @Bean
    public TopicExchange ordersExchange() {
        return ExchangeBuilder.topicExchange(ordersExchangeName)
                .durable(true)
                .build();
    }



    @Bean
    public TopicExchange stockEventsExchange() {
        return new TopicExchange(stockExchangeName, true, false);
    }


    @Bean
    public Queue orderStockReservedQueue() {
        return QueueBuilder.durable(orderStockReservedQueueName).build();
    }


    @Bean
    public Queue orderStockRejectedQueue() {
        return QueueBuilder.durable(orderStockRejectedQueueName).build();
    }


    @Bean
    public Binding orderStockReservedBinding(Queue orderStockReservedQueue,
                                             TopicExchange stockEventsExchange) {
        return BindingBuilder.bind(orderStockReservedQueue)
                .to(stockEventsExchange)
                .with(stockReservedRoutingKey);
    }


    @Bean
    public Binding orderStockRejectedBinding(Queue orderStockRejectedQueue,
                                             TopicExchange stockEventsExchange) {
        return BindingBuilder.bind(orderStockRejectedQueue)
                .to(stockEventsExchange)
                .with(stockRejectedRoutingKey);
    }


    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jacksonMessageConverter) {
        if (connectionFactory instanceof CachingConnectionFactory ccf) {
            ccf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
            ccf.setPublisherReturns(true);
        }

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);
        template.setMessageConverter(jacksonMessageConverter);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                LOGGER.info("Message confirmed. correlationData: {}", correlationData);
            } else {
                LOGGER.warn("Message not approved: cause: {}", cause);
            }
        });

        template.setReturnsCallback(returned -> {
            LOGGER.warn(
                    "Message returned. replyCode: {}, replyText: {}, exchange: {}, routingKey: {}, messageProperties: {}",
                    returned.getReplyCode(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getMessage().getMessageProperties()
            );
        });

        return template;
    }


    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jacksonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonMessageConverter);
        return factory;
    }
}
