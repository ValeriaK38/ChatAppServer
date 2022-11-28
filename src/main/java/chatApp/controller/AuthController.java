package chatApp.controller;

import chatApp.Entities.Enums.PrivacyStatus;
import chatApp.Entities.RequestAddUser;
import chatApp.Entities.User;
import chatApp.service.AuthService;
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
public class AuthController {
    @Autowired
    private AuthService authenticationService;
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
            LocalDate dateTime = null;
            if (request.getDateOfBirth() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                dateTime = LocalDate.parse(request.getDateOfBirth(), formatter);
            }
            String privacyStatusStr = request.getPrivacyStatus();
            PrivacyStatus privacyStatus;
            if (privacyStatusStr.equals("PUBLIC")) {
                privacyStatus = PrivacyStatus.PUBLIC;
            } else {
                privacyStatus = PrivacyStatus.PRIVATE;
            }
            User user = new User.UserBuilder(request.getEmail(), request.getPassword(), request.getNickName()).firstName(request.getFirstName()).description(request.getDescription()).profilePhoto(null).dateOfBirth(dateTime).privacyStatus(privacyStatus).build();
            validateInputUser(user);
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

    /**
     * Does the log in process for a registered user in our database.
     *
     * @param user - The user we want to log in.
     * @return Returns a string which consists of nickName:Token, This helps us in the client to parse the response
     * and save the token and nickname in the session storage for later use.
     * In general the login is supposed to generate a token for the user and set his status to ONLINE.
     */
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

    /**
     * Does the log out process for a logged-in user in our database(When clicking the log out button).
     *
     * @param user - The user we want to log out.
     * @return Returns a string which consists of a successful log-out message
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public String logOut(@RequestBody User user) {
        return authenticationService.logOut(user.getNickName());
    }

    public boolean authUser(String id, String token) {
        if (token.length() != 18) {
            throw new IllegalArgumentException("invalid token");
        }
        return authenticationService.authUser(id, token);
    }

    /**
     * Allows a user to enter a nickName which must be unique and enter the main chatroom.
     *
     * @param guest - The guest we want to add to the chat room.
     * @return Returns a string which consists of nickName:Token, This helps us in the client to parse the response
     * and save the token and nickname in the session storage for later use.
     * The guest will get the Guest- prefix before his nickname and he will be shown with the status ONLINE.
     */
    @RequestMapping(value = "/guest", method = RequestMethod.POST)
    public String createGuest(@RequestBody User guest) {
        User tempGuest = new User.UserBuilder(guest.getNickName()).build();
        return authenticationService.addUGuest(guest);
    }

    public void deleteUserByNickname(String nickName) {
        authenticationService.deleteUserByNickname(nickName);
    }
}
