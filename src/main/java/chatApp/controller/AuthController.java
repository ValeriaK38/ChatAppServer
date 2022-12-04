package chatApp.controller;

import chatApp.Entities.Enums.PrivacyStatus;
import chatApp.Entities.RequestActivation;
import chatApp.Entities.NicknameTokenPair;
import chatApp.Entities.RequestAddUser;
import chatApp.Entities.User;
import chatApp.Entities.UserToPresent;
import chatApp.service.AuthService;
import chatApp.service.EmailSenderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private EmailSenderService emailSenderService;
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z]).{8,20}$");
    private static final Pattern nicknamePattern = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{4,20}$");
    private static final Pattern stringNamePattern = Pattern.compile("[A-Za-z]{2,20}");
    private static final Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");

    /**
     * The method sends to controller the user's id to validate it
     *
     * @param activationRequest has user's id that need to be validated and verification code
     *                          catches SQLDataException when the provided id doesn't exist in DB
     */
    @RequestMapping(value = "/validateUser", method = RequestMethod.POST)
    public void confirmUserAccount(@RequestBody RequestActivation activationRequest) {
        try {
            Long id = Long.parseLong(activationRequest.getId());
            authenticationService.validateUserAccount(id, activationRequest.getActivationCode());
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    /**
     * The controller creates user based on input from the registration client page
     * @param request - user's data to add to the DB and url : sends verification email with the url
     *                catches SQLDataException when the provided user already exists by unique fields
     * @return server POST response.
     */
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String createUser(@RequestBody RequestAddUser request) {
        try {
            LocalDate dateTime = null;
            if (request.getDateOfBirth() != null) {
                validateInputStringDateDate(request.getDateOfBirth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                dateTime = LocalDate.parse(request.getDateOfBirth(), formatter);
                if (dateTime.isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException("invalid date:  you are not from the future!");
                }
            }
            String privacyStatusStr = request.getPrivacyStatus();
            PrivacyStatus privacyStatus;
            if (privacyStatusStr.equals("PUBLIC")) {
                privacyStatus = PrivacyStatus.PUBLIC;
            } else {
                privacyStatus = PrivacyStatus.PRIVATE;
            }

            User user = new User.UserBuilder(request.getEmail(), request.getPassword(), request.getNickName()).firstName(request.getFirstName()).lastName(request.getLastName()).description(request.getDescription()).profilePhoto(null).dateOfBirth(dateTime).privacyStatus(privacyStatus).build();
            validateInputUser(user);
            User userRes = authenticationService.addUser(user);
            emailSenderService.sendVerificationEmail(user, request.getUrl());
            UserToPresent userToPresent = new UserToPresent(userRes);
            return userToPresent.toString();

        } catch (IllegalArgumentException e) {
            System.out.println(e);
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * The function validates the input of date string
     *
     * @param dateOfBirth
     */
    private void validateInputStringDateDate(String dateOfBirth) {
        String yearStr = StringUtils.substringBefore(dateOfBirth, "-");
        int year = Integer.valueOf(yearStr);
        if (year > 2022 || year < 1900)
            throw new IllegalArgumentException("invalid year value:  should be in range of 1900-2022");

        String monthStr = StringUtils.substringAfter(dateOfBirth, "-");
        monthStr = StringUtils.substringBefore(monthStr, "-");
        int month = Integer.valueOf(monthStr);
        if (month > 12 || month < 1)
            throw new IllegalArgumentException("invalid month value:  should be in range of 1-12");

        String dayStr = StringUtils.substringAfterLast(dateOfBirth, "-");
        int day = Integer.valueOf(dayStr);
        if (day > 31 || day < 1)
            throw new IllegalArgumentException("invalid day value:  should be in range of 1-31");
    }

    /**
     * The method validates the user input data for user parameters
     *
     * @param user
     * @throws IllegalArgumentException for invalid input
     */
    private void validateInputUser(User user) {

        if (user.getNickName() != null) {
            Matcher matchNickname = nicknamePattern.matcher(user.getNickName());
            boolean nicknameMatchFound = matchNickname.matches();
            if (!nicknameMatchFound) {
                throw new IllegalArgumentException("invalid nickname: use A-z/a-z/0-9");
            }
        }

        if (user.getPassword() != null) {
            Matcher matchPassword = passwordPattern.matcher(user.getPassword());
            boolean passwordMatchFound = matchPassword.matches();
            if (!passwordMatchFound) {
                throw new IllegalArgumentException("invalid password: need to be at lest 8 characters long, and contain numbers and letters");
            }
        }

        if (user.getEmail() != null) {
            Matcher matchMail = emailPattern.matcher(user.getEmail());
            boolean emailMatchFound = matchMail.matches();
            if (!emailMatchFound) {
                throw new IllegalArgumentException("invalid email, use pattern: example@gmail.com");
            }
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
    public NicknameTokenPair logIn(@RequestBody User user) {

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
    public String logOut(@RequestBody NicknameTokenPair user) {
        return authenticationService.logOut(user);
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
    public NicknameTokenPair createGuest(@RequestBody User guest) {
        return authenticationService.addUGuest(guest);
    }

    /**
     * Deletes the user from database by nickname
     *
     * @param nickName - The nickname of the user we want to delete
     */
    public void deleteUserByNickname(String nickName) {
        authenticationService.deleteUserByNickname(nickName);
    }
}
