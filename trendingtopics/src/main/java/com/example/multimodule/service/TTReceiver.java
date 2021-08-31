package com.example.multimodule.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TTReceiver {

    @Autowired
    private Calculator calculator;

    @RabbitListener(queues = "trending-topics-in")
    public void receiveMessageTT(Message message) {

        //System.out.println("Received <" + message.toString() + ">");
        byte[] bytes = message.getBody();
        String body = new String(bytes);
        this.calculator.parseMessage(body);
    }

}