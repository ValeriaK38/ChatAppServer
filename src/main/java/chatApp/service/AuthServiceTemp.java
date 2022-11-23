package chatApp.service;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.User;
import chatApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceTemp {
    private final UserRepository userRepository;
    public AuthServiceTemp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public static HashMap<String, String> userTokens = new HashMap<>();

    public String logIn(String email, String password) {

        int id;
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
        String res = tempUser.getNickName() +":" +token;

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
        int id = guest.getId();

        if (userRepository.findByNickName(guest.getNickName()) != null) {
            throw new IllegalArgumentException(String.format("Nickname %s already exists in the user table", guest.getNickName()));
        }
        if (userTokens.containsKey("" + id)) {
            throw new IllegalArgumentException("the guest is already logged in ");
        }

        String token = createToken();
        userTokens.put("" + id, token);

        String res = "Guest-" + guest.getNickName() +":" +token;

        guest.setToken(token);
        guest.switchUserStatus(UserStatus.ONLINE);
        userRepository.save(guest);

        return res;
    }

    private static String getSaltString(int stringLength) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        while (salt.length() < stringLength) {
            int index = (int) (ThreadLocalRandom.current().nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public static String createToken() {
        return getSaltString(18);
    }

    public boolean checkIfEmailExists(String email) {
        return (userRepository.findByEmail(email) != null);
    }


    public User addUGuest(User user) throws SQLDataException {
        if (userRepository.findByNickName(user.getNickName()) != null) {
            throw new SQLDataException(String.format("Nickname %s exists in users table", user.getNickName()));
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
