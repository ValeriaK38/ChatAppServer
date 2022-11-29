package chatApp.controller;
import chatApp.Entities.ChatMessage;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.SystemMessage;
import chatApp.Entities.RequestMessage;
import chatApp.Entities.User;
import chatApp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/message")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public ChatMessage sendPlainMessage(@RequestBody ChatMessage message) {
        chatService.checkIfMuted(message.getSender());
        return message;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public ChatMessage greeting(SystemMessage message) throws Exception {
            return new ChatMessage("SYSTEM", message.getName() + " has joined the chat");
    }

    @MessageMapping("/bye")
    @SendTo("/topic/mainChat")
    public ChatMessage farewell(SystemMessage message) throws Exception {
        return new ChatMessage("SYSTEM", message.getName() + " has left the chat");
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createMessage(@RequestBody RequestMessage requestMessage){
        return chatService.addMessage(requestMessage).toString();
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<ChatMessage> getAllMessages() {
        return chatService.getAllMessages();
    }


    @RequestMapping(value = "/getLatest", method = RequestMethod.GET)
    public List<ChatMessage> getLatest() {
        final int COUNT_MESSAGES_TO_SHOW=40;
        List<ChatMessage>  messages =  chatService.getAllMessages();
        return messages.subList(messages.size()-COUNT_MESSAGES_TO_SHOW, messages.size());
    }
}