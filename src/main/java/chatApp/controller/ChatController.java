package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.HelloMessage;
import chatApp.Entities.RequestMessage;
import chatApp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/message")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public ChatMessage sendPlainMessage(@RequestBody ChatMessage message) {
        return message;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public ChatMessage greeting(HelloMessage message) throws Exception {
        return new ChatMessage("SYSTEM", message.getName() + " has joined the chat");
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createMessage(@RequestBody RequestMessage requestMessage){
        return chatService.addMessage(requestMessage).toString();
    }
}