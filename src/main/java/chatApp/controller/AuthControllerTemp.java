package chatApp.controller;

import chatApp.Entities.User;
import chatApp.service.AuthServiceTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthControllerTemp {

    @Autowired
    private AuthServiceTemp authenticationService;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{8,20}$");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");


    @RequestMapping(value = "login" , method = RequestMethod.POST)
    public HashMap<String, String> logIn(@RequestBody User user) {

        Matcher matchMail = emailPattern.matcher(user.getEmail());
        Matcher matchPassword = PASSWORD_PATTERN.matcher(user.getPassword());

        boolean emailMatchFound = matchMail.matches();
        boolean passwordMatchFound = matchPassword.matches();

        if (!emailMatchFound) {
            throw new IllegalArgumentException("write email properly example@ex.com");
        }
        if (!passwordMatchFound) {
            throw new IllegalArgumentException("password isn't proper password");
        }
        if (authenticationService.checkIfEmailExists(user.getEmail())) {
            return authenticationService.logIn(user.getEmail(), user.getPassword());
        }
        throw new IllegalArgumentException("user is not registered");
    }

    public boolean authUser(String id, String token) {
        if (token.length() != 18) {
            throw new IllegalArgumentException("the token is not valid");
        }
        return authenticationService.authUser(id, token);
    }
}
