package chatApp.service;

import chatApp.Entities.User;
import chatApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceTemp {
    private final UserRepository userRepository;

    public AuthServiceTemp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static HashMap<String, String> userTokens = new HashMap<>();

    public HashMap<String, String> logIn(String email, String password) {

        int id;

        User tempUser = userRepository.findByEmail(email);
        if (tempUser.getEmail().equals(email) && tempUser.getPassword().equals(password)) {
            id = tempUser.getId();
        } else {
            throw new IllegalArgumentException("the user is not valid");
        }
        if (userTokens.containsKey("" + id)) {
            throw new IllegalArgumentException("the user is logged in ");
        }

        String token = createToken();
        userTokens.put("" + id, token);
        HashMap<String, String> res = new HashMap<>();
        res.put("" + id, token);

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
}
