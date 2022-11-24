package chatApp.controller;

import chatApp.Entities.User;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static chatApp.utilities.Utilities.createRandomString;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthControllerTempTest {

    @Autowired
    AuthController authControllerTemp;
    @Autowired
    private AuthService authServiceTemp;

    @Autowired
    private static UserRepository userRepository;
    private static User testUser;
    private static User testGuest;

    @BeforeEach
    public void setup() {
        testUser = new User.UserBuilder("leon@test.com", "leon1234", "LeonTest").firstName("leon").build();
        testGuest = new User.UserBuilder("testGuest123").build();
    }

    @AfterEach
    public void cleanup() {
        testUser = null;
        testGuest = null;
        authServiceTemp.userTokens.clear();
    }

    @Test
    public void User_Logs_In_Successfully() {
        System.out.println("-------- Test user logs in successfully --------");

        //Given there is a registered user.

        String loggedInUser = authControllerTemp.logIn(testUser); // When he logs in
        String[] result = loggedInUser.split(":");

        assertEquals(result[0], testUser.getNickName()); // Then he gets a token
        assertNotEquals(result[1], null);

        System.out.println(String.format("The user %s has logged in successfully and his token is: %s", result[0], result[1]));
    }

    @Test
    public void Logged_In_User_Fails_To_Log_In_Again() {
        System.out.println("-------- Test logged in user fails to login again --------");

        //Given there is a logged in user.
        authControllerTemp.logIn(testUser);

        // When he tries to log in again Then he fails
        assertThrows(Exception.class, () -> authControllerTemp.logIn(testUser));

        System.out.println("The user was already logged in, failed to log in again.");
    }

    @Test
    public void Test_Log_In_When_Not_Registered() {
        System.out.println("-------- Test logging in when not registered --------");

        //Given a user who's not registered
        testUser.setEmail("Invalidemail@test.com");

        //When He tries to login THen he fails
        assertThrows(Exception.class, () -> authControllerTemp.logIn(testUser));

        System.out.println("The user is not registered, failed to log in.");
    }

    @Test
    public void Test_Try_To_Login_With_Invalid_Email() {
        System.out.println("-------- Test try logging with invalid email --------");

        //Given a user with an invalid email (should not be possible to register , checking just in case)
        testUser.setEmail("Invalidemail");

        //When He tries to login THen he fails
        assertThrows(Exception.class, () -> authControllerTemp.logIn(testUser));

        System.out.println("The email is in an invalid format");
    }

    @Test
    public void Test_Try_To_Login_With_Invalid_Password() {
        System.out.println("-------- Test try logging with invalid password --------");

        //Given a user with an invalid password (should not be possible to register , checking just in case)
        testUser.setPassword("InvalidPassword");

        //When He tries to login THen he fails
        assertThrows(Exception.class, () -> authControllerTemp.logIn(testUser));

        System.out.println("The password is in an invalid format");
    }

    @Test
    public void Guest_Logs_In_Successfully() {
        System.out.println("-------- Test guest logs in successfully --------");

        //Given a user enters a unique nickname
        testGuest.setNickName(createRandomString(10));

        //When he tries to enter the main chat
        String createdGuest = authControllerTemp.createGuest(testGuest);
        String[] result = createdGuest.split(":");

        //Then he gets a token and enters the chat.
        assertEquals(result[0], "Guest-" + testGuest.getNickName());
        assertNotEquals(result[1], null); // Then

        System.out.println(String.format("The guest %s has logged in successfully and his token is: %s", result[0], result[1]));
    }

    @Test
    public void Guest_Tries_To_Use_Existing_Nickname() {
        System.out.println("-------- Test guest tries to use existing nickname --------");

        //Given there is a guest in the Database

        //When someone tries to enter as a guest with the same nickName Then he fails to login
        assertThrows(Exception.class, () -> authControllerTemp.createGuest(testGuest));

        System.out.println("The nickname is already taken, please choose a different one");
    }
}