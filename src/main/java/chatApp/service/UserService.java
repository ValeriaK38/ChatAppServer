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
    @Autowired
    VerificationEmailController verificationEmailController;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Adds a user to the database if it has a unique email
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws SQLDataException when the provided email already exists
     */
    public User addUser(User user) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }
        if (userRepository.findByNickName(user.getNickName()) != null) {
            throw new SQLDataException(String.format("Nickname %s exists in users table", user.getNickName()));
        }
        verificationEmailController.sendEmail(user);
        return userRepository.save(user);
    }

    public void validateUserAccount(String email, String verificationCode) {
        User user = userRepository.findByEmail(email);
        if(user!= null) {
            if (!user.getVerificationCode().equals(verificationCode)) {
                throw new NoSuchElementException("The verification code doesn't match");
            } else {
                user.setVerified(true);
                userRepository.save(user);
            }
        }
        else{
            throw new NoSuchElementException("User with given email doesn't exists " + email);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
