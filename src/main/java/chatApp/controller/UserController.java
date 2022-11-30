package chatApp.controller;


import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
import chatApp.Entities.UserToPresent;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * @return the list of all the users in the database.
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<UserToPresent> getAllUsers() {
        List<UserToPresent> users = userService.getAllUsers();
        return users.stream().filter(user -> user.isVerified() || user.getUserType() == UserType.GUEST).collect(Collectors.toList());
    }

    /**
     * The function mutes or unmutes a user , Only an admin can mute a user , the muted/unmuted user must not be
     * an admin himself.
     *
     * @param adminNickName - The nickname of the admin that wishes to mute someone.
     * @param userNickName- The nickname of the user the admin wishes to mute.
     * @param status        - Can be either "mute" or "unmute" to know which action to take.
     */
    @RequestMapping(value = "/muteUnmute", method = RequestMethod.PATCH)
    public String muteUnmute(@RequestParam String adminNickName, @RequestParam String userNickName, @RequestParam String status) {
        userService.muteUnmute(adminNickName, userNickName, status);
        return String.format("%s is now %sd!", userNickName, status);
    }

    /**
     * Switches the status of a user from online to away or from away to online
     *
     * @param nickName - The nickname of the user we wish to switch his status
     * @return a message of the successful switch.
     */
    @RequestMapping(value = "/awayOnline", method = RequestMethod.PATCH)
    public String awayOnline(@RequestParam String nickName) {

        UserStatus status = userService.awayOnline(nickName);
        return String.format("%s changed to %s!", nickName, status.toString());
    }

    /**
     * A method which updates a timestamp of users who are still in our chat to make sure they did not leave without
     * pressing the logout button
     *
     * @param userNickname - The nickname of the user who's still in the chat
     */
    @RequestMapping(value = "/keepAlive", method = RequestMethod.POST)
    public void keepAlive(@RequestBody String userNickname) {
        userService.keepAlive(userNickname);
    }

    /**
     * Goes over the list of users in the DB and logs off users who did not pass a keepalive check in the past minute
     */
    @RequestMapping(value = "/checkOfflineUsers", method = RequestMethod.GET)
    public void checkOfflineUsers() {
        userService.checkOfflineUsers();
    }

    /**
     * Saves one user in the DB
     *
     * @param user - The user we wish to save in the DB
     */
    public void saveUserInDB(User user){
        userService.saveUserInDB(user);
    }

    /**
     * @param nickName - The nickname of the user we wish to retrieve.
     * @return the user we wanted to get from the DB
     */
    @RequestMapping(value = "/getUserByNickname", method = RequestMethod.POST)
    public UserToPresent getUserByNickname(@RequestBody String userNickName) {
        return userService.getUserByNickname(userNickName);
    }
}
