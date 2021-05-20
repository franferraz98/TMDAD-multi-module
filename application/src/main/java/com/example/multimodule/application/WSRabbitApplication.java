package com.example.multimodule.application;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WSRabbitApplication {

    static final String topicExchangeName = "spring-boot-exchange";

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }


    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        MessageListenerAdapter listener = new MessageListenerAdapter(receiver, "receiveMessage");
        listener.setMessageConverter(null);
        return listener;
    }



    public static void main(String[] args) throws InterruptedException{
        SpringApplication.run(WSRabbitApplication.class, args);
    }
}
