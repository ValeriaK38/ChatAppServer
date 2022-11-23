package chatApp.controller;

import chatApp.Entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/register") //user
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/guest", method = RequestMethod.POST)
    public String createGuest(@RequestBody User user) {
        try {
            User guest = new User.UserBuilder(user.getNickName()).build();
            return userService.addUGuest(guest).toString();
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "nickName already exists", e);
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return (List<User>) userService.getAllUsers();
    }


}
