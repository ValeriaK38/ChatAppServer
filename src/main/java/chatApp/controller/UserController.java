package chatApp.controller;

import chatApp.Entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return (List<User>) userService.getAllUsers();
    }

    public User getUserByNickname(String nickName){
        return userService.getUserByNickname(nickName);
    }

    @RequestMapping(value = "/muteUnmute", method = RequestMethod.PATCH)
    public String muteUnmute(@RequestParam String adminNickName ,@RequestParam String userNickName, @RequestParam String status){
        userService.muteUnmute(adminNickName,userNickName,status);
        return String.format("%s is now %sd!",userNickName,status);
    }
}
