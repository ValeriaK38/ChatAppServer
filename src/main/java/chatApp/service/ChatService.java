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


    public ChatMessage addMessage(ChatMessage chatMessage) throws SQLDataException {
        if(messageRepository.findById(chatMessage.getId())!=null){
            throw new SQLDataException(String.format("123123"));
        }
        return messageRepository.save(chatMessage);
    }
}