package chatApp.service;

import chatApp.Entities.ChatMessage;
import chatApp.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.sql.SQLDataException;

@Service
public class ChatService {

    private final MessageRepository messageRepository;

    public ChatService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Adds a message to the database - content paired with sender
     * @param chatMessage - the message's data
     * @return a saved message with it's generated id
     */
    public ChatMessage addMessage(ChatMessage chatMessage){
        return messageRepository.save(chatMessage);
    }
}