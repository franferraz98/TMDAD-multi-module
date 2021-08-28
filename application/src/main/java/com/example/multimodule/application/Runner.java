package com.example.multimodule.application;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;

@Component
public class Runner {

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
        this.receiver = receiver;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void run(String message) throws Exception {
        System.out.println("Sending message: <" + message + ">");
        String[] parts = message.split(":::");
        String destination = "foo.bar." + parts[parts.length - 1];
        String text = parts[0] + ":::";
        for(int i = 1; i< parts.length-1; i++){
            text += parts[i];
        }
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(text, new MessageProperties());
        rabbitTemplate.convertAndSend("spring-boot-exchange", destination, msg);
    }

    public void toChatRoom(String message) throws Exception {
        System.out.println("Sending message: <" + message + "> to a room");
        String[] parts = message.split(":::");
        String exchange = parts[parts.length - 1];
        String text = parts[0] + ":::";
        for(int i = 1; i< parts.length - 1; i++){
            text += parts[i];
        }
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(text, new MessageProperties());
        rabbitTemplate.convertAndSend(exchange, "", msg);
    }

    public void notify(String message) throws Exception {
        System.out.println("Sending message: <" + message + "> to everyone");
        String[] parts = message.split(":::");
        String exchange = "notifications";
        String text = parts[0] + ":::";
        for(int i = 1; i < parts.length; i++){
            text += parts[i];
        }
        SimpleMessageConverter converter = new SimpleMessageConverter();
        Message msg = converter.toMessage(text, new MessageProperties());
        rabbitTemplate.convertAndSend(exchange, "", msg);
    }

}
