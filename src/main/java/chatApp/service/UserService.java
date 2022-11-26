package chatApp.service;

import chatApp.Entities.User;
import chatApp.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public void muteUnmute(String adminNickName, String userNickName, String status) {
        User tempUser = userRepository.findByNickName(userNickName);
        User tempAdmin = userRepository.findByNickName(adminNickName);

        if(status == "mute"){
            tempAdmin.adminMuteUser(tempUser);
        }else{
            tempAdmin.adminUnmuteUser(tempUser);
        }
        userRepository.save(tempUser);
    }
}
