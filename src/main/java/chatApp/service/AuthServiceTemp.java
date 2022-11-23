package chatApp.service;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.User;
import chatApp.controller.VerificationEmailController;
import chatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.List;

import static chatApp.utilities.Utilities.createRandomString;

@Service
public class AuthServiceTemp {
    private final UserRepository userRepository;
    @Autowired
    VerificationEmailController verificationEmailController;

    public AuthServiceTemp(UserRepository userRepository) {
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
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }
        if (userRepository.findByNickName(user.getNickName()) != null) {
            throw new SQLDataException(String.format("Nickname %s exists in users table", user.getNickName()));
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

    public String logIn(String email, String password) {

        Long id;
        User tempUser = userRepository.findByEmail(email);

        if (tempUser.getEmail().equals(email) && tempUser.getPassword().equals(password)) {
            id = tempUser.getId();
        } else {
            throw new IllegalArgumentException("the user is not valid");
        }
        if (userTokens.containsKey("" + id)) {
            throw new IllegalArgumentException("the user is already logged in ");
        }

        String token = createToken();
        userTokens.put("" + id, token);
        String res = tempUser.getNickName() + ":" + token;

        tempUser.setToken(token);
        tempUser.switchUserStatus(UserStatus.ONLINE);
        userRepository.save(tempUser);
        return res;
    }

    public boolean authUser(String id, String token) {
        for (HashMap.Entry<String, String> entry : userTokens.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue().equals(token);
            }
        }
        return false;
    }

    public String addUGuest(User guest) {
        Long id = guest.getId();

        if (userRepository.findByNickName(guest.getNickName()) != null) {
            throw new IllegalArgumentException(String.format("Nickname %s already exists in the user table", guest.getNickName()));
        }
        if (userTokens.containsKey("" + id)) {
            throw new IllegalArgumentException("the guest is already logged in ");
        }

        String token = createToken();
        userTokens.put("" + id, token);

        String res = "Guest-" + guest.getNickName() + ":" + token;

        guest.setToken(token);
        guest.switchUserStatus(UserStatus.ONLINE);
        userRepository.save(guest);

        return res;
    }


    public static String createToken() {
        return createRandomString(18);
    }

    public boolean checkIfEmailExists(String email) {
        return (userRepository.findByEmail(email) != null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
