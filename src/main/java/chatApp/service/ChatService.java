package chatApp.service;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.RequestMessage;
import chatApp.Entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static chatApp.service.AuthService.userTokens;
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
     * @param sender -  The user who sent the message
     * @return a saved message with it's generated id
     */
    public ChatMessage addMessage(RequestMessage requestMessage, User sender) {

        if(sender == null){
            throw new IllegalArgumentException("Wrong token was sent from client or no such user");
        }

        if (sender.isMuted()) {
            throw new RuntimeException("You are muted! you cant send messages.");
        }

        ChatMessage chatMessage = new ChatMessage(sender.getNickName(), requestMessage.getContent());
        return messageRepository.save(chatMessage);
    }

    /**
     * @return a list of all the messages in the DB
     */
    public List<ChatMessage> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Checks if a user is muted , if a user is muted he cannot send messages in the main chat.
     *
     * @param sender - The user we wish to check if he is muted or not
     */
    public void checkIfMuted(String sender) {
        //Changes the nickname to be just the nickname without the prefix for correct usage in the repo.
        if (sender.startsWith("Guest")) {
            sender = sender.replace("Guest-", "");
        }
        if (userRepository.findByNickName(sender).isMuted()) {
            throw new RuntimeException("You are muted! you cant send messages.");
        }
    }
}