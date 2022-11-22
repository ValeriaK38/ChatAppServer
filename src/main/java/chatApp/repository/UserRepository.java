package chatApp.repository;

import chatApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
        User findByNickName(String nickName);
        User saveAndFlush(User user);
        User findByToken(String token);

}
