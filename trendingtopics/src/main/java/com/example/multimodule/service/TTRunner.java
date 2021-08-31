package com.example.multimodule.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class TTRunner {

    RabbitTemplate rabbitTemplate;

    public TTRunner(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void run(String message) throws Exception {
        // System.out.println("Sending message: <" + message + ">");
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(message, new MessageProperties());
        rabbitTemplate.convertAndSend("notifications", "", msg);
    }

}
