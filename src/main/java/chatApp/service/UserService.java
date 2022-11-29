package chatApp.service;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.SystemMessage;
import chatApp.Entities.User;
import chatApp.controller.ChatController;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Autowired
    ChatController chatController;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return the list of all the users in the database.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "UserStatus")
                .and(Sort.by(Sort.Direction.ASC, "UserType"))
        );
    }

    public User getUserByNickname(String nickName) {
        return userRepository.findByNickName(nickName);
    }

    public void muteUnmute(String adminNickName, String userNickName, String status) {
        User tempUser = userRepository.findByNickName(userNickName);
        User tempAdmin = userRepository.findByNickName(adminNickName);

        if (adminNickName == userNickName) {
            throw new IllegalArgumentException("You cant mute/unmute yourself!");
        }
        if (tempUser.getUserType() == UserType.ADMIN) {
            throw new IllegalArgumentException("Cant mute or unmute admins.");
        }
        if (tempAdmin.getUserType() != UserType.ADMIN) {
            throw new IllegalArgumentException("Only admin can mute or unmute users.");
        }

        if (status.equals("mute")) {
            tempAdmin.adminMuteUser(tempUser);
        } else {
            tempAdmin.adminUnmuteUser(tempUser);
        }
        userRepository.save(tempUser);
    }

    public UserStatus awayOnline(String nickName) {
        User tempUser = userRepository.findByNickName(nickName);
        UserStatus nowStatus = tempUser.getUserStatus();
        if (nowStatus == UserStatus.ONLINE) {
            tempUser.setUserStatus(UserStatus.AWAY);
        } else {
            tempUser.setUserStatus(UserStatus.ONLINE);
        }
        userRepository.save(tempUser);
        return (tempUser.getUserStatus());
    }

    public void saveUserInDB(User user) {
        userRepository.save(user);
    }

    public void keepAlive(String userNickname) {
        if(userNickname.startsWith("Guest")){
            userNickname = userNickname.replace("Guest-", "");
        }
        Timestamp now = Timestamp.from(Instant.now());
        User userDB = userRepository.findByNickName(userNickname);
        if (userDB != null) {
            userDB.setLast_Loggedin(now);
            userRepository.save(userDB);
        }
    }

    public void checkOfflineUsers(){
        Timestamp now = Timestamp.from(Instant.now());

        List<User> users = getAllUsers();

        for (User tempUser : users) {
            if ((now.getTime() - tempUser.getLast_Loggedin().getTime()) / (60 * 1000) >= 1) {
                if (tempUser.getUserStatus() != UserStatus.OFFLINE) {
                    authService.logOut(tempUser.getNickName());
                }
            }
        }
    }
}
