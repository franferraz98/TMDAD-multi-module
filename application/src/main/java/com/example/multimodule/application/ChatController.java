package com.example.multimodule.application;

import com.example.multimodule.fileupload.FileController;
import com.example.multimodule.fileupload.FileDBGrupo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public Logger logger;

    @Autowired
    private Runner runner;

    @Autowired
    private Receiver receiver;

    @Autowired
    private FileController fileController;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/client")
    public OutputMessage interpret(final JsonMessage jsonMessage) throws Exception {
        System.out.println("Received: " + jsonMessage.getFrom() + " -> " + jsonMessage.getText());

        String body = jsonMessage.getText();
        String[] parts = body.split("---");
        String type = parts[0];
        int b;

        String text = "";
        for(int i = 1; i< parts.length; i++){
            text += parts[i];
        }
        String textpre = text;

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
                text = text.replace(":::", "&");
                text = text + "&" + jsonMessage.getFrom();
                b = fileController.newUserinGroupSpring(text);
                switch (b){
                    case 1:
                        System.err.println("Only the administrator can add users");
                        break;
                    case 2:
                        System.err.println("Group does already exist");
                        break;
                    case 3:
                        System.err.println("User does not exist");
                        break;
                    case 4:
                        System.err.println("Exception on DB");
                        break;
                    default:
                        receiver.addToRoom(new JsonMessage(jsonMessage.getFrom(), textpre));
                }
                break;
            case "createRoom":
                b = fileController.newGroupSpring(jsonMessage.getFrom() + "&" + text);
                switch (b) {
                    case 1:
                        System.err.println("Group does already exist");
                        break;
                    case 2:
                        System.err.println("User does not exist");
                        break;
                    case 3:
                        System.err.println("Exception in DB");
                        break;
                    default:
                        receiver.createChatRoom(new JsonMessage(jsonMessage.getFrom(), text));
                }
                break;
            case "route":
                receiver.newBind(new JsonMessage(jsonMessage.getFrom(), text));
                break;
            case "showGroups":
                String grupos = fileController.getGroupsSpring(jsonMessage.getFrom());
                receiver.showGroups(grupos, jsonMessage.getFrom());
                break;
            case "signup":
                text = text.replace(":::", "&");
                b = fileController.newUserSpring(text);
                switch (b){
                    case 1:
                        System.err.println("User already exists");
                        break;
                    case 2:
                        System.err.println("Error in DB");
                        break;
                    default:
                        System.out.println("User created");
                }
                break;
            case "login":
                b = fileController.loginSpring(text);
                switch (b) {
                    case 1:
                        System.err.println("Password not correct");
                        break;
                    case 2:
                        System.err.println("Doesn't exist");
                        break;
                    case 3:
                        System.err.println("Error in DB");
                        break;
                    default:
                        receiver.newBind(new JsonMessage(jsonMessage.getFrom(), jsonMessage.getFrom()));
                        receiver.login(jsonMessage.getFrom());
                }
            default:
                // Error
                text = "No functionality found";
        }
        return new OutputMessage(jsonMessage.getFrom(), text, time);
    }
}