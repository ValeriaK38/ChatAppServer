package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.HelloMessage;
import chatApp.Entities.User;
import chatApp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
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
        return new ChatMessage("SYSTEM", message.getName() + "joined the chat");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String createMessage(@RequestBody ChatMessage chatMessage){
        try {
            return chatService.addMessage(chatMessage).toString();
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }
}