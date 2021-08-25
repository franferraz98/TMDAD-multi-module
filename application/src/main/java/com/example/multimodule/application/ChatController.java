package com.example.multimodule.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public Logger logger;

    @Autowired
    private Runner runner;

    @Autowired
    private Receiver receiver;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/client")
    public OutputMessage interpret(final JsonMessage jsonMessage) throws Exception {
        System.out.println("Received: " + jsonMessage.getFrom() + " -> " + jsonMessage.getText());

        String body = jsonMessage.getText();
        String[] parts = body.split("---");
        String type = parts[0];

        String text = "";
        for(int i = 1; i< parts.length; i++){
            text += parts[i];
        }

        final String time = new SimpleDateFormat("HH:mm").format(new Date());

        switch (type) {
            case "chat":
                runner.run(jsonMessage.getFrom() + ":::" + text);
                break;
            case "chatRoom":
                runner.toChatRoom(jsonMessage.getFrom() + ":::" + text);
                break;
            case "notify":
                runner.notify(jsonMessage.getFrom() + ":::" + text);
                break;
            case "notifications":
                receiver.createNotifications(new JsonMessage(jsonMessage.getFrom(), text));
                break;
            case "addToRoom":
                receiver.addToRoom(new JsonMessage(jsonMessage.getFrom(), text));
                break;
            case "createRoom":
                receiver.createChatRoom(new JsonMessage(jsonMessage.getFrom(), text));
                break;
            case "route":
                receiver.newBind(new JsonMessage(jsonMessage.getFrom(), text));
                break;
            default:
                // Error
                text = "No functionality found";
        }
        return new OutputMessage(jsonMessage.getFrom(), text, time);
    }
}