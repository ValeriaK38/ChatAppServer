package chatApp.service;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.RequestMessage;
import chatApp.Entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

        if(userRepository.findByNickName(sender).isMuted()){
            throw new RuntimeException("You are muted! you cant send messages.");
        }

        ChatMessage chatMessage = new ChatMessage(sender, requestMessage.getContent());

        return messageRepository.save(chatMessage);
    }

    public List<ChatMessage> getAllMessages() {
        return messageRepository.findAll();
    }

    public void checkIfMuted(String sender) {
        //Changes the nickname to be just the nickname without the prefix for correct usage in the repo.
        if(sender.startsWith("Guest")){
            sender = sender.replace("Guest-", "");
        }
        if(userRepository.findByNickName(sender).isMuted()){
            throw new RuntimeException("You are muted! you cant send messages.");
        }
    }


}