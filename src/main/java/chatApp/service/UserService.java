package chatApp.service;

import chatApp.Entities.User;
import chatApp.controller.VerificationEmailController;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.NoSuchElementException;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return the list of all the users in the database.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByNickname(String nickName) {
        return userRepository.findByNickName(nickName);
    }
}
