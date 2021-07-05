package com.example.multimodule.application;


import com.example.multimodule.fileupload.FileController;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Controller
public class Receiver {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private List<String> allQueueNames;

    @Autowired
    private FileController fileController;

    @Autowired
    private TopicExchange exchange;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private SimpleMessageListenerContainer listenerContainer;

    public Receiver(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.allQueueNames = new ArrayList<String>();
    }

    @MessageMapping("/notifications")
    public void createNotifications (final JsonMessage jsonMessage) {
        String exchangeName = "notifications";

        rabbitAdmin.deleteExchange(exchangeName);
        FanoutExchange exchange = new FanoutExchange(exchangeName);

        rabbitAdmin.declareExchange(exchange);
        for(int i = 0; i<allQueueNames.size(); i++) {
            Queue queue = new Queue(allQueueNames.get(i), true);
            Binding binding = BindingBuilder.bind(queue).to(exchange); //TODO: No se yo
            rabbitAdmin.declareBinding(binding);
        }
    }

    @MessageMapping("/addToRoom")
    public void addToRoom (final JsonMessage jsonMessage){
        String text = jsonMessage.getText();
        System.out.println(text);
        String[] parts = text.split(":::");
        String room = parts[0];
        String user = parts[1];

        Queue queue = new Queue(user, true);
        FanoutExchange exchange = new FanoutExchange(room);
        Binding binding = BindingBuilder.bind(queue).to(exchange);

        rabbitAdmin.declareBinding(binding);
    }

    @MessageMapping("/createRoom")
    public void createChatRoom (final JsonMessage jsonMessage){
        String queueName = jsonMessage.getFrom();
        String exchangeName = jsonMessage.getText();

        /*
        boolean b = fileController.newGroupInternal(exchangeName);

        if(b){
            System.out.println("ALMACENADO CON EXITO");
        } else {
            System.out.println("ERROR AL ALMACENAR");
        }
        */

        rabbitAdmin.deleteExchange(exchangeName);
        FanoutExchange exchange = new FanoutExchange(exchangeName);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange); //TODO: No se yo

        rabbitAdmin.declareExchange(exchange);
        // rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }

    @MessageMapping("/route")
    public void newBind (final JsonMessage jsonMessage) {
        String queueName = jsonMessage.getText();
        allQueueNames.add(queueName);
        String keyMask = "foo.bar." + jsonMessage.getText();
        org.springframework.amqp.core.Queue queue = new org.springframework.amqp.core.Queue(queueName, true);
        Binding bind = BindingBuilder.bind(queue).to(exchange).with(keyMask);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(bind);

        RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        while(rabbitAdmin.getQueueInfo(queue.getName()).getMessageCount() > 0){
            String result = (String) rabbitTemplate.receiveAndConvert(queue.getName());
            SimpleMessageConverter converter = new SimpleMessageConverter();
            MessageProperties properties = new MessageProperties();
            properties.setConsumerQueue(queue.getName());
            Message msg = converter.toMessage(result, properties);

            try{
                this.receiveMessage(msg);
            } catch (Exception e){
                System.out.println("ERROR: " + e);
            }
        }

        String[] namesArray = allQueueNames.toArray(new String[0]);
        listenerContainer.setQueueNames(namesArray);
    }

    public void receiveMessage(Message message) throws Exception {
        System.out.println("Received <" + message.toString() + ">");
        MessageProperties properties = message.getMessageProperties();
        String consumerQueue = properties.getConsumerQueue();
        // String receivedExchange = properties.getReceivedExchange(); TODO: Anyadir esto

        String destination = "/topic/" +  consumerQueue; // + "/" + receivedExchange;
        byte[] bytes = message.getBody();
        String body = new String(bytes);
        String[] parts = body.split(":::");
        String from = parts[0];
        String text = "";
        for(int i = 1; i< parts.length; i++){
            text += parts[i];
        }
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend(destination,
                new OutputMessage(from, text, time));
    }
}