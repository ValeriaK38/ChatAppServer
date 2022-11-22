package chatApp.controller;

import chatApp.Entities.Enums.PrivacyStatus;
import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/register") //user
public class UserController {

    @Autowired
    private UserService userService;

    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{8,20}$");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
    private static final Pattern nicknamePattern = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{4,32}$");
    private static final Pattern stringNamePattern = Pattern.compile("[A-Za-z]{1,32}");

    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public String createUser(@RequestBody User user) {
        try {

//            validateInputUser(user);

            //User tempUser = new User.UserBuilder(user.getEmail(),user.getPassword(), user.getNickName()).build();
            // adding to the user default fields
            user.setFirstName(user.getFirstName());
            user.setLastName(user.getLastName());
            user.setProfilePhoto(user.getProfilePhoto());
            user.setDateOfBirth(user.getDateOfBirth());
            user.setDescription(user.getDescription());
            user.setUserType(UserType.REGISTERED);
            user.setUserStatus(UserStatus.OFFLINE);
            user.setPrivacyStatus(PrivacyStatus.PUBLIC);
            user.setPrefix(null);
            user.setMuted(false);
            user.setVerified(false);
            user.setVerificationCode();
//            System.out.println("=================================== > USER FROM POST ========================> " + user);
//            System.out.println("23 UserController ---------------------------> " + user.getVerificationCode());
            return userService.addUser(user).toString();
        } catch (SQLDataException e) {
            System.out.println(e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.toString(), e);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.toString(), e);
        }
    }

    @RequestMapping(value = "/validateUser", method = RequestMethod.POST)
    public void confirmUserAccount(@RequestParam String email, String verificationCode) {
        try {
//            System.out.println("========================================="+email);
//            System.out.println("========================================="+verificationCode);
            userService.validateUserAccount(email, verificationCode);
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }
    private void validateInputUser(User user) {
        Matcher matchMail = emailPattern.matcher(user.getEmail());
        Matcher matchPassword = passwordPattern.matcher(user.getPassword());
        Matcher matchNickname = nicknamePattern.matcher(user.getNickName());

        boolean emailMatchFound = matchMail.matches();
        boolean passwordMatchFound = matchPassword.matches();
        boolean nicknameMatchFound = matchNickname.matches();

        if (!emailMatchFound) {
            throw new IllegalArgumentException("write email properly example@ex.com");
        }
        if (!passwordMatchFound) {
            throw new IllegalArgumentException("password isn't proper password");
        }

        if (!nicknameMatchFound) {
            throw new IllegalArgumentException("illegal expression for nickname: use A-z/a-z/0-9 chars only");
        }

        if (user.getFirstName() != null) {
            Matcher matchFirstName = stringNamePattern.matcher(user.getFirstName());
            boolean firstNameMatchFound = matchFirstName.matches();
            if (!firstNameMatchFound) {
                throw new IllegalArgumentException("not valid first name: start with capital letter like \" David\" ");
            }

        }
        if (user.getLastName() != null) {
            Matcher matchLastName = stringNamePattern.matcher(user.getLastName());
            boolean lastNameMatchFound = matchLastName.matches();
            if (!lastNameMatchFound) {
                throw new IllegalArgumentException("not valid last name: start with capital letter like \" Cohen\" ");
            }
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

    @RequestMapping( value ="/getAll" ,method = RequestMethod.GET)
    public List<User> getAllUsers(){
        return (List<User>) userService.getAllUsers();
    }


}
