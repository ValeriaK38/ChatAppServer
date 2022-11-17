package chatApp.repository;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findById(int id);
}
