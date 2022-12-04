package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.RequestMessage;
import chatApp.Entities.SystemMessage;
import chatApp.Entities.User;
import chatApp.service.AuthService;
import chatApp.service.ChatService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/message")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private AuthService authService;
    @Autowired
    @Lazy
    private UserService userService;

    /**
     * A method that sends a message to the chat room.
     *
     * @param message - The message we wish to send
     * @return The message we sent.
     */
    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public ChatMessage sendPlainMessage(@RequestBody ChatMessage message) {
        chatService.checkIfMuted(message.getSender());
        return message;
    }

    /**
     * A method that sends a system message that says the user has joined the chat
     *
     * @param message - The message which will hold the sender.
     * @return the message itself.
     */
    @MessageMapping("/hello")
    @SendTo("/topic/mainChat")
    public ChatMessage greeting(SystemMessage message) throws Exception {
        return new ChatMessage("SYSTEM", message.getName() + " has joined the chat");
    }

    /**
     * A system message that says the user has left the chat
     *
     * @param message - The message which will hold the sender.
     * @return the message itself.
     */
    @MessageMapping("/bye")
    @SendTo("/topic/mainChat")
    public ChatMessage farewell(SystemMessage message) throws Exception {
        return new ChatMessage("SYSTEM", message.getName() + " has left the chat");
    }

    /**
     * Adds a message to the database - content paired with sender
     *
     * @param requestMessage - the message's data with the token of the sender.
     * @return a saved message with it's generated id
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createMessage(@RequestBody RequestMessage requestMessage) {

        User sender = userService.getUserByToken(requestMessage.getToken());

        if (authService.authenticateUser(sender.getNickName(), sender.getToken())) {
            return chatService.addMessage(requestMessage, sender).toString();
        } else {
            throw new IllegalArgumentException("Invalid token was sent");
        }
    }

    /**
     * @return a list of all the messages in the DB
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<ChatMessage> getAllMessages() {
        return chatService.getAllMessages();
    }

    /**
     * @return a list of the X latest messages in the DB
     */
    @RequestMapping(value = "/getLatest", method = RequestMethod.GET)
    public List<ChatMessage> getLatest() {
        int numberOfMessagesToShow = 20;
        List<ChatMessage> messages = chatService.getAllMessages();
        if (messages.size() > numberOfMessagesToShow) {
            return messages.subList(messages.size() - numberOfMessagesToShow, messages.size());
        } else {
            return messages;
        }
    }

    /**
     * returns list of messages by given amount
     *
     * @param chunks number of massages
     * @return list of messages
     */
    @RequestMapping(value = "/getLatestChunks", method = RequestMethod.GET)
    public List<ChatMessage> getLatestChunks(@RequestParam int chunks) {
        List<ChatMessage> messages = chatService.getAllMessages();
        int numberOfMessagesToShow = 20 * chunks;
        if (messages.size() > 20 && numberOfMessagesToShow < messages.size()) {
            return messages.subList(messages.size() - numberOfMessagesToShow, messages.size());
        } else if (messages.size() < 20 && messages.size() > 0) {
            return messages;
        } else {
            return null;
        }
    }
}