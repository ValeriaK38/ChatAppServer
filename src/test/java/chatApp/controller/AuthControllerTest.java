package chatApp.controller;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.RequestAddUser;
import chatApp.Entities.User;
import chatApp.Entities.UserToPresent;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import chatApp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLDataException;
import java.util.HashMap;

import static chatApp.utilities.Utilities.createRandomString;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthController authController;
    @Autowired
    private AuthService authService;
    @Autowired
    UserController userController;
    private static User testUser;
    private static User testGuest;
    private static User testLogoutUser;
    RequestAddUser requestTest;

    @BeforeEach
    public void setup() {
        //Creates users for the tests and saves them in the database so anyone can run the tests at any time.

        testUser = new User.UserBuilder("test@test1.com", "leon1234", "test1").build();
        testGuest = new User.UserBuilder("testGuest1").build();
        testLogoutUser = new User.UserBuilder("testLogout@test.com", "leon1234", "TestLogout").build();
        testUser.setVerified(true);
        testLogoutUser.setVerified(true);

        userController.saveUserInDB(testUser);
        userController.saveUserInDB(testGuest);
        userController.saveUserInDB(testLogoutUser);
        authController.logIn(testLogoutUser);

        requestTest = new RequestAddUser("Valeria11", "lera38@gmail11.com", "080393Lera", "Valeria", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");
    }

    @AfterEach
    public void cleanup() {
        //Deletes the users from the DB so when you run the tests again they won't exist.

        authController.deleteUserByNickname(testUser.getNickName());
        authController.deleteUserByNickname(testGuest.getNickName());
        authController.deleteUserByNickname(testLogoutUser.getNickName());
        testUser = null;
        testGuest = null;
        authService.userTokens.clear();

        requestTest = null;
    }

    @Test
    public void User_Logs_In_Successfully() {
        System.out.println("-------- Test user logs in successfully --------");

        //Given there is a registered user.

        String loggedInUser = authController.logIn(testUser); // When he logs in
        String[] result = loggedInUser.split(":");

        assertEquals(result[0], testUser.getNickName()); // Then he gets a token
        assertNotEquals(result[1], null);

        System.out.println(String.format("The user %s has logged in successfully and his token is: %s", result[0], result[1]));
    }

    @Test
    public void Logged_In_User_Fails_To_Log_In_Again() {
        System.out.println("-------- Test logged in user fails to login again --------");

        //Given there is a logged in user.
        authController.logIn(testUser);

        // When he tries to log in again Then he fails
        assertThrows(Exception.class, () -> authController.logIn(testUser));

        System.out.println("The user was already logged in, failed to log in again.");
    }

    @Test
    public void Test_Log_In_When_Not_Registered() {
        System.out.println("-------- Test logging in when not registered --------");

        //Given a user who's not registered
        testUser.setEmail("Invalidemail@test.com");

        //When He tries to login THen he fails
        assertThrows(Exception.class, () -> authController.logIn(testUser));

        System.out.println("The user is not registered, failed to log in.");
    }

    @Test
    public void Test_Try_To_Login_With_Invalid_Email() {
        System.out.println("-------- Test try logging with invalid email --------");

        //Given a user with an invalid email (should not be possible to register , checking just in case)
        testUser.setEmail("Invalidemail");

        //When He tries to login THen he fails
        assertThrows(Exception.class, () -> authController.logIn(testUser));

        System.out.println("The email is in an invalid format");
    }

    @Test
    public void Test_Try_To_Login_With_Invalid_Password() {
        System.out.println("-------- Test try logging with invalid password --------");

        //Given a user with an invalid password (should not be possible to register , checking just in case)
        testUser.setPassword("InvalidPassword");

        //When He tries to login THen he fails
        assertThrows(Exception.class, () -> authController.logIn(testUser));

        System.out.println("The password is in an invalid format");
    }

    @Test
    public void Guest_Logs_In_Successfully() {
        System.out.println("-------- Test guest logs in successfully --------");

        //Given a user enters a unique nickname
        testGuest.setNickName(createRandomString(10));

        //When he tries to enter the main chat
        String createdGuest = authController.createGuest(testGuest);
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
        assertThrows(Exception.class, () -> authController.createGuest(testGuest));

        System.out.println("The nickname is already taken, please choose a different one");
    }

    @Test
    public void User_Logs_Out_Successfully() {
        System.out.println("-------- Test User logs out successfully --------");

        //Given there is a logged in user.

        //When he tries to logout
        authController.logOut(testLogoutUser);

        //Then his status changes to OFFLINE and his token becomes null
        assertNull(testLogoutUser.getToken());
        assertEquals(testLogoutUser.getUserStatus(), UserStatus.OFFLINE);

        System.out.println("The user is now: " + testLogoutUser.getUserStatus());
    }

    @Test
    public void Logged_Out_User_Tries_To_Log_Out() {
        System.out.println("-------- Test logged out user tries to log out--------");
        //This should not be possible , only someone who entered the chat can see the logout button but checking just in case.

        //Given there is a logged out user.
        authController.logOut(testLogoutUser);

        //When he tries to logout Then he fails
        assertThrows(Exception.class, () -> authController.logOut(testLogoutUser));

        System.out.println("The user was already logged out , cant log out again.");
    }

    @Test
    public void Not_Registered_User_Tries_To_Log_Out() {
        System.out.println("-------- Test not registered user tries to log out--------");
        //This should not be possible , only someone who entered the chat can see the logout button but checking just in case.

        //Given there is a user we dont have in our DB.
        User testUserNotExists = new User.UserBuilder("bad@user.com", "leon1234", "Nope").build();

        //When he tries to logout Then he fails
        assertThrows(Exception.class, () -> authController.logOut(testUserNotExists));

        System.out.println("The user was not registered , cant log out.");
    }

    @Test
    public void Guest_Logs_Out_Successfully() {
        System.out.println("-------- Test guest logs out successfully --------");

        //Given there is a logged in guest.
        User testGuestLogOut = new User.UserBuilder("testGuestLogOut").build();
        authController.createGuest(testGuestLogOut);

        //When he tries to logout
        authController.logOut(testGuestLogOut);

        //Then he is deleted from the DB to free up the nickname.
        assertNull(userController.getUserByNickname(testGuestLogOut.getNickName()));

        System.out.println("The guest was deleted from the database!");
    }


    @Test
    public void User_AddUser_Successfully() {
        System.out.println("-------- Test user register successfully --------");

        //Given there is a user who wants to register

        //When he tries to register
        String str = authController.createUser(requestTest);

        //Then the user is created and added to the DB.
        UserToPresent resUser = userController.getUserByNickname("Valeria11");
        assertEquals(str, resUser.toString());

        authController.deleteUserByNickname(requestTest.getNickName());

        System.out.println("The user is now registered!" + resUser);
    }

    @Test
    public void User_AddUser_alreadyRegistered() {
        System.out.println("-------- Test user already registered --------");

        //Given there is a registered user.

        //When trying to register the same user.
        RequestAddUser request2 = new RequestAddUser("Valeria11", "lera38@gmail11.com", "080393Lera", "Valeria", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");
        authController.createUser(requestTest);

        //Then he fails to register
        assertThrows(Exception.class, () -> authController.createUser(request2));

        authController.deleteUserByNickname(requestTest.getNickName());

        System.out.println("Cant register with the same details again!");
    }

    @Test
    public void User_AddUser_invalidNickName() {
        System.out.println("-------- Test user registration with invalid nickname --------");

        //When trying to register with wrong nickname
        RequestAddUser request1 = new RequestAddUser("Valeria@@@", "lera38@gmail11.com", "080393Lera", "Valeria", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid nickname");
    }

    @Test
    public void User_AddUser_invalidPassword() {
        System.out.println("-------- Test user registration with invalid email --------");

        //When trying to register with wrong password
        RequestAddUser request1 = new RequestAddUser("Valeria11", "lera38", "111", "Valeria", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid password");
    }

    @Test
    public void User_AddUser_invalidFirstName() {
        System.out.println("-------- Test user registration with invalid first name --------");

        //When trying to register with invalid first name
        RequestAddUser request1 = new RequestAddUser("Valeria11", "lera38", "080393Lera", "Valeria2", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid first name");
    }

    @Test
    public void User_AddUser_invalidLstName() {
        System.out.println("-------- Test user registration with invalid last name --------");

        //When trying to register with invalid last name
        RequestAddUser request1 = new RequestAddUser("Valeria11", "lera38", "080393Lera", "Valeria", "Krahmalev2", "2000-03-02", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid last name");
    }

    @Test
    public void User_AddUser_invalidDateOfBirthYear() {
        System.out.println("-------- Test user registration with invalid year in date of birth --------");

        //When trying to register with invalid year
        RequestAddUser request1 = new RequestAddUser("Valeria11", "lera38", "080393Lera", "Valeria", "Krahmalev2", "2000-12-12", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid year value in date of birth");
    }

    @Test
    public void User_AddUser_invalidDateOfBirthMonth() {
        System.out.println("-------- Test user registration with invalid month in date of birth --------");

        //When trying to register with invalid year
        RequestAddUser request1 = new RequestAddUser("Valeria15", "lera38", "080393Lera", "Valeria", "Krahmalev2", "2000-15-12", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid month value in date of birth");
    }

    @Test
    public void User_AddUser_invalidDateOfBirthDay() {
        System.out.println("-------- Test user registration with invalid day in date of birth --------");

        //When trying to register with invalid year
        RequestAddUser request1 = new RequestAddUser("Valeria11", "lera38", "080393Lera", "Valeria", "Krahmalev2", "2000-12-40", "description", "url", "PUBLIC");

        //Then the registration fails
        assertThrows(Exception.class, () -> authController.createUser(request1));

        System.out.println("Cant register with an invalid day value in date of birth");
    }

    @Test
    public void User_ConfirmUserAccount_Successfully() {
        System.out.println("-------- Test user account confirmation success --------");

        //Given there is a registered user.
        RequestAddUser request = new RequestAddUser("Valeria1", "lera38@gmail1.com", "080393Lera", "Valeria", "Krahmalev", "2000-12-03", "description", "url", "PUBLIC");
        authController.createUser(request);

        //When verifying
        UserToPresent resUser = userController.getUserByNickname("Valeria1");
        System.out.println(resUser.isVerified());
        authController.confirmUserAccount(resUser.getId());
        UserToPresent updatedUser = userController.getUserByNickname("Valeria1");

        //Then the user is now verified
        assertEquals(true, updatedUser.isVerified());

        authController.deleteUserByNickname("Valeria1");

        System.out.println("The user is now verified!");
    }

    @Test
    public void User_ConfirmUserAccount_NoSuchUser() {
        System.out.println("-------- Test user account confirmation fails - no such user id --------");

        //When trying to verify a none existent user Then it fails
        assertThrows(Exception.class, () -> authController.confirmUserAccount(-1l));

        System.out.println("Cant verify a none existent user");
    }

    @Test
    public void Delete_User_Successfully() {
        System.out.println("-------- Test delete user successfully  --------");

        //Given there is a user in the DB.
        User testDeleteUser = new User.UserBuilder("test@test5.com", "leon1234", "test5").build();
        userController.saveUserInDB(testDeleteUser);

        //When I try to delete him
        authController.deleteUserByNickname(testDeleteUser.getNickName());

        //Then the user is no longer in the DW
        assertNull(userController.getUserByNickname(testDeleteUser.getNickName()));

        System.out.println("The user was deleted successfully");
    }

    @Test
    public void TesT_Try_To_Delete_User_That_Does_Not_Exist() {
        System.out.println("-------- Test trying to delete a user who is not in the DB --------");

        //Given the user we want to delete does not exist in the DB.

        User testDeleteUser = new User.UserBuilder("test@test6.com", "leon1234", "test6").build();

        //When I try to delete him Then it fails
        assertThrows(Exception.class, () -> authController.deleteUserByNickname(testDeleteUser.getNickName()));

        System.out.println("The user was not found in the DB, cant delete someone who does not exist.");
    }
}