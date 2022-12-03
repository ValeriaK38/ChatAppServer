package chatApp.service;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.NicknameTokenPair;
import chatApp.Entities.User;
import chatApp.Entities.UserToPresent;
import chatApp.controller.ChatController;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static chatApp.service.AuthService.userTokens;

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
    public List<UserToPresent> getAllUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "UserStatus")
                .and(Sort.by(Sort.Direction.ASC, "UserType")));

        List<UserToPresent> userToPresent = new ArrayList<>();

        for (User user : users) {
            UserToPresent tempUser = new UserToPresent(user);
            userToPresent.add(tempUser);
        }

        return userToPresent;
    }

    /**
     * @param nickName - The nickname of the user we wish to retrieve.
     * @return the user we wanted to get from the DB but as a UserToPresent Class
     */
    public UserToPresent getUserToPresentByNickname(String nickName) {
        User tempUser = userRepository.findByNickName(nickName);
        UserToPresent userToPresent = null;
        if (tempUser != null) {
            userToPresent = new UserToPresent(tempUser);
        }
        return userToPresent;
    }

    /**
     * @param nickName - The nickname of the user we wish to retrieve.
     * @return the user we wanted to get from the DB
     */
    public User getUserByNickname(String nickName) {
        return userRepository.findByNickName(nickName);
    }

    /**
     * The function mutes or unmutes a user , Only an admin can mute a user , the muted/unmuted user must not be
     * an admin himself.
     *
     * @param admin         - A NicknameTokenPair of the admin who clicked the button.
     * @param userNickName- The nickname of the user the admin wishes to mute.
     * @param status        - Can be either "mute" or "unmute" to know which action to take.
     */
    public void muteUnmute(NicknameTokenPair admin, String userNickName, String status) {
        User tempUser = userRepository.findByNickName(userNickName);
        User tempAdmin = userRepository.findByNickName(admin.getNickName());

        if (!userTokens.get(admin.getNickName()).equals(tempAdmin.getToken())) {
            throw new IllegalArgumentException("The wrong token was sent!");
        }

        if (admin.getNickName() == userNickName) {
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

    /**
     * Switches the status of a user from online to away or from away to online
     *
     * @param user - A NicknameTokenPair of the user we wish to switch his status
     * @return a message of the successful switch.
     */
    public UserStatus awayOnline(NicknameTokenPair user) {
        User tempUser = userRepository.findByNickName(user.getNickName());

        if (!userTokens.get(user.getNickName()).equals(tempUser.getToken())) {
            throw new IllegalArgumentException("The wrong token was sent!");
        }

        UserStatus nowStatus = tempUser.getUserStatus();
        if (nowStatus == UserStatus.ONLINE) {
            tempUser.setUserStatus(UserStatus.AWAY);
        } else {
            tempUser.setUserStatus(UserStatus.ONLINE);
        }
        userRepository.save(tempUser);
        return (tempUser.getUserStatus());
    }

    /**
     * Saves one user in the DB
     *
     * @param user - The user we wish to save in the DB
     */
    public void saveUserInDB(User user) {
        userRepository.save(user);
    }

    /**
     * A method which updates a timestamp of users who are still in our chat to make sure they did not leave without
     * pressing the logout button
     *
     * @param userNickname - The nickname of the user who's still in the chat
     */
    public void keepAlive(String userNickname) {
        if (userNickname.startsWith("Guest")) {
            userNickname = userNickname.replace("Guest-", "");
        }
        Timestamp now = Timestamp.from(Instant.now());
        User userDB = userRepository.findByNickName(userNickname);
        if (userDB != null) {
            userDB.setLast_Loggedin(now);
            userRepository.save(userDB);
        }
    }

    /**
     * Goes over the list of users in the DB and logs off users who did not pass a keepalive check in the past minute
     */
    public void checkOfflineUsers() {
        Timestamp now = Timestamp.from(Instant.now());

        List<User> users = getAllOnlineUsers();

        for (User tempUser : users) {
            NicknameTokenPair tempPair = new NicknameTokenPair(tempUser.getNickName(), tempUser.getToken());
            if ((now.getTime() - tempUser.getLast_Loggedin().getTime()) > 60000) {
                if (tempUser.getUserStatus() != UserStatus.OFFLINE) {
                    authService.logOut(tempPair);
                }
            }
        }
    }

    /**
     * @return the list of all the online users in the database.
     */
    public List<User> getAllOnlineUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().filter(user -> user.getUserStatus() == UserStatus.ONLINE ||
                user.getUserStatus() == UserStatus.AWAY).collect(Collectors.toList());
    }
}
