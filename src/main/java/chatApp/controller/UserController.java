package chatApp.controller;

import chatApp.Entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public String createUser(@RequestBody User user){
        try {
            return userService.addUser(user).toString();
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }

    @RequestMapping( value ="/guest" ,method = RequestMethod.POST)
    public String createGuest(@RequestBody User user){
        try {
            User guest = new User.UserBuilder(user.getNickName()).build();
            return userService.addUGuest(guest).toString();
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "nickName already exists", e);
        }
    }


}
