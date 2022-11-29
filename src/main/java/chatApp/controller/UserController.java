package chatApp.controller;


import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
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

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        List<User>  users =  userService.getAllUsers();
        return users.stream().filter( user -> user.isVerified() || user.getUserType() == UserType.GUEST).collect(Collectors.toList());
    }

    @RequestMapping(value = "/muteUnmute", method = RequestMethod.PATCH)
    public String muteUnmute(@RequestParam String adminNickName ,@RequestParam String userNickName, @RequestParam String status){
        userService.muteUnmute(adminNickName,userNickName,status);
        return String.format("%s is now %sd!",userNickName,status);
    }

    @RequestMapping(value = "/awayOnline", method = RequestMethod.PATCH)
    public String awayOnline(@RequestParam String nickName){

        UserStatus status = userService.awayOnline(nickName);
        return String.format("%s changed to %s!",nickName, status.toString());
    }

    @RequestMapping(value = "/keepAlive", method = RequestMethod.POST)
    public void keepAlive(@RequestBody String userNickname) {
        userService.keepAlive(userNickname);
    }

    @RequestMapping(value = "/checkOfflineUsers", method = RequestMethod.GET)
    public void checkOfflineUsers(){
        userService.checkOfflineUsers();
    }


    public void saveUserInDB(User user){
        userService.saveUserInDB(user);
    }

    public User getUserByNickname(String nickName){
        return userService.getUserByNickname(nickName);
    }
}
