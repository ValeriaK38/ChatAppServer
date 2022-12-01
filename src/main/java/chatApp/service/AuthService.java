package chatApp.service;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.NicknameTokenPair;
import chatApp.Entities.User;
import chatApp.controller.VerificationEmailController;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

import static chatApp.utilities.Utilities.createRandomString;

@Service
public class AuthService {
    private final UserRepository userRepository;
    @Autowired
    VerificationEmailController verificationEmailController;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static HashMap<String, String> userTokens = new HashMap<>();

    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user with it's generated id
     * @throws SQLDataException when the provided email already exists
     */
    public User addUser(User user, String url) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLDataException(String.format("Email %s exists in user table", user.getEmail()));
        }
        if (userRepository.findByNickName(user.getNickName()) != null) {
            throw new SQLDataException(String.format("Nickname %s exists in user table", user.getNickName()));
        }
        userRepository.save(user);
        verificationEmailController.sendEmail(user, url);
        return userRepository.findByEmail(user.getEmail());
    }

    /**
     * Changes user's isVerified(is_verified) filed to "true" and activates user's account
     *
     * @param id - the user's id
     * @throws SQLDataException when the provided id doesn't exist
     */
    public void validateUserAccount(Long id) throws SQLDataException {
        User user = userRepository.findById(id).get();
        if (user != null) {
            user.setVerified(true);
            userRepository.save(user);
        } else {
            throw new SQLDataException("This user is not registered");
        }
    }

    /**
     * Does the log in process for a registered user in our database.
     *
     * @param email    - The email of the user we want to log in.
     * @param password - The password of the user we want to log in.
     * @return Returns a string which consists of nickName:Token, This helps us in the client to parse the response
     * and save the token and nickname in the session storage for later use.
     * In general the login is supposed to generate a token for the user and set his status to ONLINE.
     */
    public NicknameTokenPair logIn(String email, String password) {

        User tempUser = userRepository.findByEmail(email);

        if (tempUser == null) {
            throw new RuntimeException("The user is not registered in the database");
        }
        if (!tempUser.isVerified()) {
            throw new IllegalStateException("The user is not verified. please check your email and activate your account");
        }
        if (!tempUser.getEmail().equals(email)) {
            throw new IllegalArgumentException("You have entered an incorrect email");
        } else if (!tempUser.getPassword().equals(password)) {
            throw new IllegalArgumentException("You have entered an incorrect password, please try again");
        }
        if (userTokens.containsKey(tempUser.getNickName())) {
            throw new IllegalArgumentException("The user is already logged in ");
        }

        String token = createToken();
        userTokens.put(tempUser.getNickName(), token);

        NicknameTokenPair res = new NicknameTokenPair(tempUser.getNickName(),token);

        tempUser.setToken(token);
        tempUser.switchUserStatus(UserStatus.ONLINE);
        tempUser.setLast_Loggedin(Timestamp.from(Instant.now()));
        userRepository.save(tempUser);

        return res;
    }

    /**
     * Does the log out process for a logged-in user in our database(When clicking the log out button).
     *
     * @param nickName - The nickname of the user we want to log out.
     * @return Returns a string which consists of a successful log-out message
     */
    public String logOut(String nickName) {

        //Changes the nickname to be just the nickname without the prefix for correct usage in the repo.
        if (nickName.startsWith("Guest")) {
            nickName = nickName.replace("Guest-", "");
        }

        User tempUser = userRepository.findByNickName(nickName);
        userTokens.remove(nickName);

        if (tempUser.getUserStatus() == UserStatus.OFFLINE) {
            throw new IllegalStateException("The user is already offline! cant log out again");
        }

        if (!(tempUser.getUserType() == UserType.GUEST)) {
            tempUser.setUserStatus(UserStatus.OFFLINE);
            tempUser.setToken(null);
            userRepository.save(tempUser);
        } else {
            userRepository.delete(tempUser);
        }
        return "The user logged out successfully";
    }

    /**
     * Allows a user to enter a nickName which must be unique and enter the main chatroom.
     *
     * @param guest - The guest we want to add to the chat room.
     * @return Returns a string which consists of nickName:Token, This helps us in the client to parse the response
     * and save the token and nickname in the session storage for later use.
     * The guest will get the Guest- prefix before his nickname and he will be shown with the status ONLINE.
     */
    public NicknameTokenPair addUGuest(User guest) {
        if (userRepository.findByNickName(guest.getNickName()) != null) {
            throw new IllegalArgumentException(String.format("Nickname %s already exists in the user table", guest.getNickName()));
        }

        if (userTokens.containsKey(guest.getNickName())) {
            throw new IllegalArgumentException("the guest is already logged in ");
        }

        String token = createToken();
        userTokens.put(guest.getNickName(), token);


        String guestNickName = "Guest-" + guest.getNickName();
        guest.setToken(token);
        guest.switchUserStatus(UserStatus.ONLINE);
        guest.setUserType(UserType.GUEST);
        guest.setMuted(false);
        guest.setLast_Loggedin(Timestamp.from(Instant.now()));
        userRepository.save(guest);

        NicknameTokenPair res = new NicknameTokenPair(guestNickName, token);
        return res;
    }

    /**
     * Creates a token to be given to users during the login process
     *
     * @return a String which represents a unique token
     */
    public static String createToken() {
        return createRandomString(18);
    }

    /**
     * Checks if an email exists in our database (meaning someone has registered with said email).
     *
     * @param email - The email we want to check
     * @return True or False if it exists or not
     */
    public boolean checkIfEmailExists(String email) {
        return (userRepository.findByEmail(email) != null);
    }

    /**
     * Deletes the user from database by nickname
     *
     * @param nickName - The nickname of the user we want to delete
     */
    public void deleteUserByNickname(String nickName) {
        User user = userRepository.findByNickName(nickName);
        userRepository.delete(user);
    }
}
