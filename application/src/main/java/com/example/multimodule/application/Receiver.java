package com.example.multimodule.application;


import com.example.multimodule.fileupload.FileController;
import com.example.multimodule.fileupload.FileDBGrupo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Controller
public class Receiver {

    private static Logger logger = LoggerFactory.getLogger(Receiver.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private List<String> allQueueNames;

    @Autowired
    private FileController fileController;

    @Autowired
    private TopicExchange exchange;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    @Qualifier("Server")
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
            if(!allQueueNames.get(i).equals("trending-topics-in")){
                Queue queue = new Queue(allQueueNames.get(i), true);
                Binding binding = BindingBuilder.bind(queue).to(exchange); //TODO: No se yo
                rabbitAdmin.declareBinding(binding);
            }
        }
        /*
        Binding binding = BindingBuilder.bind("queueInTT").to(exchange);
        rabbitAdmin.declareBinding(binding);
         */
    }

    @MessageMapping("/addToRoom")
    public void addToRoom (final JsonMessage jsonMessage){
        String text = jsonMessage.getText();
        // System.out.println(text);
        String[] parts = text.split(":::");
        String room = parts[0];
        String user = parts[1];

        Queue queue = new Queue(user, true);
        allQueueNames.add(user);
        rabbitAdmin.declareQueue(queue);
        FanoutExchange exchange = new FanoutExchange(room);
        Binding binding = BindingBuilder.bind(queue).to(exchange);

        rabbitAdmin.declareBinding(binding);
    }

    @MessageMapping("/createRoom")
    public void createChatRoom (final JsonMessage jsonMessage){
        String queueName = jsonMessage.getFrom();
        String exchangeName = jsonMessage.getText();

        rabbitAdmin.deleteExchange(exchangeName);
        FanoutExchange exchange = new FanoutExchange(exchangeName);
        Queue queue = new Queue(queueName, true);
        Binding binding = BindingBuilder.bind(queue).to(exchange); //TODO: No se yo

        Queue queueTT = new Queue("trending-topics-in", true);
        Binding bindingTT = BindingBuilder.bind(queueTT).to(exchange);

        rabbitAdmin.declareExchange(exchange);
        // rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
        rabbitAdmin.declareBinding(bindingTT);
    }

    @MessageMapping("/route")
    public void newBind (final JsonMessage jsonMessage) {
        String queueName = jsonMessage.getText();
        // System.out.println("Queue: " + queueName);
        logger.debug("Queue: " + queueName);
        allQueueNames.add(queueName);
        String keyMask = "foo.bar." + jsonMessage.getText();
        Queue queue = new Queue(queueName, true);
        Binding bind = BindingBuilder.bind(queue).to(exchange).with(keyMask);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(bind);

        /* NUEVO */
        Queue queueTT = new Queue("trending-topics-in", true);
        Binding bindTT = BindingBuilder.bind(queueTT).to(exchange).with(keyMask);
        if(!allQueueNames.contains("trending-topics-in")){
            allQueueNames.add("trending-topics-in");
            rabbitAdmin.declareQueue(queueTT);
        }
        rabbitAdmin.declareBinding(bindTT);

        FanoutExchange admin = new FanoutExchange("notifications");
        Binding bindingAdmin = BindingBuilder.bind(queue).to(admin);
        rabbitAdmin.declareBinding(bindingAdmin);

        /* --------- */

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
                // System.out.println("ERROR: " + e);
                logger.error("ERROR", e);
            }
        }

        String[] namesArray = allQueueNames.toArray(new String[0]);
        List<String> list = new ArrayList<String>(Arrays.asList(namesArray));
        list.remove("trending-topics-in");
        namesArray = list.toArray(new String[0]);
        listenerContainer.setQueueNames(namesArray);
    }

    public void receiveMessage(Message message) throws Exception {
        // System.out.println("Received <" + message.toString() + ">");
        MessageProperties properties = message.getMessageProperties();
        String consumerQueue = properties.getConsumerQueue();
        // String receivedExchange = properties.getReceivedExchange(); TODO: Anyadir esto

        String destination = "/topic/" +  consumerQueue; // + "/" + receivedExchange;
        byte[] bytes = message.getBody();
        String body = new String(bytes);
        String[] parts = body.split(":::");
        String from = parts[0];
        String text = "chat:::";
        for(int i = 1; i< parts.length; i++){
            text += parts[i];
        }
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        simpMessagingTemplate.convertAndSend(destination,
                new OutputMessage(from, text, time));
    }

    public void showGroups(String grupos, String consumerQueue) throws Exception{

        String destination = "/topic/" +  consumerQueue;
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        String text = "showGroups:::";
        text = text.concat(grupos);
        text = text.substring(0, text.length() - 1);
        simpMessagingTemplate.convertAndSend(destination,
                new OutputMessage(consumerQueue, text, time));
    }

    public void login(String consumerQueue){
        String destination = "/topic/" +  consumerQueue;
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        String text = "login:::";
        text = text.concat(consumerQueue);
        simpMessagingTemplate.convertAndSend(destination,
                new OutputMessage(consumerQueue, text, time));
    }
}