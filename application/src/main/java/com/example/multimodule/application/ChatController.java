package com.example.multimodule.application;

import com.example.multimodule.filesupload.MessageDB;
import com.example.multimodule.filesupload.MessageDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import com.example.multimodule.filesupload.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    @Autowired
    private Runner runner;

    @Autowired
    private MessageDBRepository messageDBRepository;

    @MessageMapping("/chat")
    public OutputMessage send(final JsonMessage jsonMessage) throws Exception {
        System.out.println("Received: " + jsonMessage.getFrom() + " -> " + jsonMessage.getText());
        messageDBRepository.save(new MessageDB(jsonMessage.getFrom(), jsonMessage.getText(),new Date().toString()));
        runner.run(jsonMessage.getFrom() + ":::" + jsonMessage.getText());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(jsonMessage.getFrom(), jsonMessage.getText(), time);
    }

    @MessageMapping("/chatRoom")
    public OutputMessage outputMessage(final JsonMessage jsonMessage) throws Exception{
        System.out.println("Received: " + jsonMessage.getFrom() + " -> " + jsonMessage.getText());
        messageDBRepository.save(new MessageDB(jsonMessage.getFrom(), jsonMessage.getText(),new Date().toString()));
        runner.toChatRoom(jsonMessage.getFrom() + ":::" + jsonMessage.getText());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(jsonMessage.getFrom(), jsonMessage.getText(), time);
    }

    @MessageMapping("/notify")
    public OutputMessage notifyAll(final JsonMessage jsonMessage) throws Exception{
        System.out.println("Received: " + jsonMessage.getFrom() + " -> " + jsonMessage.getText());
        runner.notify(jsonMessage.getFrom() + ":::" + jsonMessage.getText());
        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(jsonMessage.getFrom(), jsonMessage.getText(), time);
    }

}