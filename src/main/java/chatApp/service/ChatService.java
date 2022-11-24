package chatApp.service;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.RequestMessage;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a message to the database - content paired with sender
     *
     * @param requestMessage - the message's data with the token of the sender.
     * @return a saved message with it's generated id
     */
    public ChatMessage addMessage(RequestMessage requestMessage) {
        String sender = userRepository.findByToken(requestMessage.getToken()).getNickName();

        ChatMessage chatMessage = new ChatMessage(sender, requestMessage.getContent());

        return messageRepository.save(chatMessage);
    }
}