package chatApp.controller;

import chatApp.Entities.RequestAddUser;
import chatApp.Entities.User;
import chatApp.service.AuthServiceTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthControllerTemp {

    @Autowired
    private AuthServiceTemp authenticationService;

    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{8,20}$");
    private static final Pattern nicknamePattern = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{4,20}$");
    private static final Pattern stringNamePattern = Pattern.compile("[A-Za-z]{2,20}");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");

    /**
     * The method sends to controller the user's id to validate it
     *
     * @param id user's id that need to be validated
     *           catches SQLDataException when the provided id doesn't exist in DB
     */
    @RequestMapping(value = "/validateUser", method = RequestMethod.POST)
    public void confirmUserAccount(@RequestBody Long id) {
        try {
            authenticationService.validateUserAccount(id);
        } catch (SQLDataException e) {
            System.out.println(e);
        }
    }

    /**
     * The controller creates user based on input from the registration client page
     *
     * @param request - user's data to add to the DB and url : sends verification email with the url
     *                catches SQLDataException when the provided user already exists by unique fields
     * @return server POST response.
     */
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String createUser(@RequestBody RequestAddUser request) {
        try {
            User user = new User.UserBuilder(request.getEmail(), request.getPassword(), request.getNickName()).firstName(request.getFirstName()).profilePhoto(null).build();
            validateInputUser(user);
            if (request.getDateOfBirth() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateTime = LocalDate.parse(request.getDateOfBirth(), formatter);
                user.setDateOfBirth(dateTime);
            }
            return authenticationService.addUser(user, request.getUrl()).toString();
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


    /**
     * The method validates the user input data for user parameters
     *
     * @param user
     * @throws IllegalArgumentException for invalid input
     */
    private void validateInputUser(User user) {
        Matcher matchMail = emailPattern.matcher(user.getEmail());
        Matcher matchPassword = passwordPattern.matcher(user.getPassword());
        Matcher matchNickname = nicknamePattern.matcher(user.getNickName());

        boolean emailMatchFound = matchMail.matches();
        boolean passwordMatchFound = matchPassword.matches();
        boolean nicknameMatchFound = matchNickname.matches();

        if (!emailMatchFound) {
            throw new IllegalArgumentException("invalid email, use pattern: example@gmail.com");
        }
        if (!passwordMatchFound) {
            throw new IllegalArgumentException("invalid password: need to be at lest 8 characters long, and contain numbers and letters");
        }

        if (!nicknameMatchFound) {
            throw new IllegalArgumentException("invalid nickname: use A-z/a-z/0-9");
        }

        if (user.getFirstName() != null) {
            Matcher matchFirstName = stringNamePattern.matcher(user.getFirstName());
            boolean firstNameMatchFound = matchFirstName.matches();
            if (!firstNameMatchFound) {
                throw new IllegalArgumentException("invalid first name: use A-Za-Z");
            }

        }
        if (user.getLastName() != null) {
            Matcher matchLastName = stringNamePattern.matcher(user.getLastName());
            boolean lastNameMatchFound = matchLastName.matches();
            if (!lastNameMatchFound) {
                throw new IllegalArgumentException("invalid last name: use A-Za-Z");
            }
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String logIn(@RequestBody User user) {

        Matcher matchMail = emailPattern.matcher(user.getEmail());
        Matcher matchPassword = passwordPattern.matcher(user.getPassword());

        boolean emailMatchFound = matchMail.matches();
        boolean passwordMatchFound = matchPassword.matches();

        if (!emailMatchFound) {
            throw new IllegalArgumentException("invalid email, use pattern: example@gmail.com");
        }
        if (!passwordMatchFound) {
            throw new IllegalArgumentException("invalid password: need to be at lest 8 characters long, and contain numbers and letters");
        }
        if (authenticationService.checkIfEmailExists(user.getEmail())) {
            return authenticationService.logIn(user.getEmail(), user.getPassword());
        }
        throw new IllegalArgumentException("user is not registered");
    }

    public boolean authUser(String id, String token) {
        if (token.length() != 18) {
            throw new IllegalArgumentException("invalid token");
        }
        return authenticationService.authUser(id, token);
    }

}
