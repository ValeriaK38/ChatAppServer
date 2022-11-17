package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public ChatMessage sendPlainMessage(ChatMessage message) {
        return message;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public ChatMessage greeting(HelloMessage message) throws Exception {
        return new ChatMessage("SYSTEM", message.getName() + "joined the chat");
    }
}