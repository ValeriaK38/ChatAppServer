package chatApp.repository;

import chatApp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        User findByEmail(String email);
        User findByNickName(String nickName);
        User save(User user);
        User findByToken(String token);

        Optional<User> findById(Long id);

}
