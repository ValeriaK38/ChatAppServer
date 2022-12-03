package chatApp.controller;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.NicknameTokenPair;
import chatApp.Entities.RequestAddUser;
import chatApp.Entities.User;
import chatApp.Entities.UserToPresent;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

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
    private static NicknameTokenPair logoutTokenPair;
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

        logoutTokenPair = authController.logIn(testLogoutUser);

        requestTest = new RequestAddUser("Valeria11", "lera38@gmail11.com",
                "080393Lera", "Valeria", "Krahmalev", "2000-03-02",
                "description", "url", "PUBLIC");
    }

    @AfterEach
    public void cleanup() {
        //Deletes the users from the DB so when you run the tests again they won't exist.

        authController.deleteUserByNickname(testUser.getNickName());
        authController.deleteUserByNickname(testGuest.getNickName());
        authController.deleteUserByNickname(testLogoutUser.getNickName());
        testUser = null;
        testGuest = null;
        logoutTokenPair = null;
        authService.userTokens.clear();

        requestTest = null;
    }

    @Test
    public void User_Logs_In_Successfully() {
        System.out.println("-------- Test user logs in successfully --------");

        //Given there is a registered user.

        NicknameTokenPair loggedInUser = authController.logIn(testUser); // When he logs in

        assertEquals(loggedInUser.getNickName(), testUser.getNickName()); // Then he gets a token
        assertNotEquals(loggedInUser.getToken(), null);

        System.out.println(String.format("The user %s has logged in successfully and his token is: %s",
                loggedInUser.getNickName(), loggedInUser.getToken()));
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
        NicknameTokenPair createdGuest = authController.createGuest(testGuest);

        //Then he gets a token and enters the chat.
        assertEquals(createdGuest.getNickName(), "Guest-" + testGuest.getNickName());
        assertNotEquals(createdGuest.getToken(), null); // Then

        System.out.println(String.format("The guest %s has logged in successfully and his token is: %s",
                createdGuest.getNickName(), createdGuest.getToken()));
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
        authController.logOut(logoutTokenPair);

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
        authController.logOut(logoutTokenPair);

        //When he tries to logout Then he fails
        assertThrows(Exception.class, () -> authController.logOut(logoutTokenPair));

        System.out.println("The user was already logged out , cant log out again.");
    }

    @Test
    public void Not_Registered_User_Tries_To_Log_Out() {
        System.out.println("-------- Test not registered user tries to log out--------");
        //This should not be possible , only someone who entered the chat can see the logout button but checking just in case.

        //Given there is a user we dont have in our DB.
        User testUserNotExists = new User.UserBuilder("bad@user.com", "leon1234", "Nope").build();
        NicknameTokenPair tempPair = new NicknameTokenPair(testUserNotExists.getNickName(), testUserNotExists.getToken());

        //When he tries to logout Then he fails
        assertThrows(Exception.class, () -> authController.logOut(tempPair));

        System.out.println("The user was not registered , cant log out.");
    }

    @Test
    public void Guest_Logs_Out_Successfully() {
        System.out.println("-------- Test guest logs out successfully --------");

        //Given there is a logged in guest.
        User testGuestLogOut = new User.UserBuilder("testGuestLogOut").build();
        authController.createGuest(testGuestLogOut);
        NicknameTokenPair tempPair = new NicknameTokenPair(testGuestLogOut.getNickName(), testGuestLogOut.getToken());

        //When he tries to logout
        authController.logOut(tempPair);

        //Then he is deleted from the DB to free up the nickname.
        assertNull(userController.getUserByNickname(testGuestLogOut.getNickName()));

        System.out.println("The guest was deleted from the database!");
    }


    @Test
    public void User_AddUser_Successfully() {
        System.out.println("-------- Test user register successfully --------");

        //Given there is a user who wants to register

        //When he tries to register
        authController.createUser(requestTest);

        //Then the user is created and added to the DB.
        UserToPresent resUser = userController.getUserByNicknameToPresent(requestTest.getNickName());
        assertEquals(resUser.getNickName(), requestTest.getNickName());

        authController.deleteUserByNickname(requestTest.getNickName());

        System.out.println("The user is now registered!" + resUser);
    }

    @Test
    public void User_AddUser_alreadyRegisteredEmail() {
        System.out.println("-------- Test user already registered with email --------");
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //Given there is a user who wants to register with the same email as registered testUser
                    RequestAddUser request = new RequestAddUser("test11", testUser.getEmail(), testUser.getPassword(), "Valeria", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");
                    //When the user try to register - exception is thrown
                    authController.createUser(request);
                }
        );
        assertTrue(thrown.getMessage().contains("Email " + testUser.getEmail() + " exists in user table"));
    }

    @Test
    public void User_AddUser_alreadyRegisteredNickname() {
        System.out.println("-------- Test user already registered with nickname --------");
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //Given there is a user who wants to register with the same nickname as registered testUser
                    RequestAddUser request = new RequestAddUser(testUser.getNickName(), "lera777@walla1.com", testUser.getPassword(), "Valeria", "Krahmalev", "2000-03-02", "description", "url", "PUBLIC");
                    //When the user try to register - exception is thrown
                    authController.createUser(request);
                }
        );
        assertTrue(thrown.getMessage().contains("Nickname " + testUser.getNickName() + " exists in user table"));
    }

    @Test
    public void User_AddUser_invalidNickName() {
        System.out.println("-------- Test user registration with invalid nickname --------");
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //When trying to register with wrong nickname
                    requestTest.setNickName("Valeria@@@");
                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid nickname: use A-z/a-z/0-9"));
    }

    @Test
    public void User_AddUser_invalidPassword() {
        System.out.println("-------- Test user registration with invalid email --------");
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //When trying to register with wrong password
                    requestTest.setPassword("111");

                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid password: need to be at lest 8 characters long, and contain numbers and letters"));
    }

    @Test
    public void User_AddUser_invalidFirstName() {
        System.out.println("-------- Test user registration with invalid first name --------");
        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //When trying to register with invalid first name
                    requestTest.setFirstName("Valeria2");
                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid first name: use A-Za-Z"));
    }

    @Test
    public void User_AddUser_invalidLstName() {
        System.out.println("-------- Test user registration with invalid last name --------");

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //When trying to register with invalid last name
                    requestTest.setLastName("Krahmalev11");
                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid last name: use A-Za-Z"));
    }

    @Test
    public void User_AddUser_invalidDateOfBirthYear() {
        System.out.println("-------- Test user registration with invalid year in date of birth --------");

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //When trying to register with invalid year
                    requestTest.setDateOfBirth("999-12-12");
                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid year value:  should be in range of 1900-2022"));
    }

    @Test
    public void User_AddUser_invalidDateOfBirthMonth() {
        System.out.println("-------- Test user registration with invalid month in date of birth --------");

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {
                    //When trying to register with invalid month
                    requestTest.setDateOfBirth("2000-15-12");
                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid month value:  should be in range of 1-12"));
    }

    @Test
    public void User_AddUser_invalidDateOfBirthDay() {
        System.out.println("-------- Test user registration with invalid day in date of birth --------");

        ResponseStatusException thrown = assertThrows(
                ResponseStatusException.class,
                () -> {

                    //When trying to register with invalid day
                    requestTest.setDateOfBirth("2000-12-40");

                    //Then the registration fails
                    authController.createUser(requestTest);
                }
        );
        assertTrue(thrown.getMessage().contains("invalid day value:  should be in range of 1-31"));
    }

    @Test
    public void User_ConfirmUserAccount_Successfully() {
        System.out.println("-------- Test user account confirmation success --------");


        //Given there is a registered unverified user.
        testUser.setVerified(false);
        //When verifying we need to send user
        UserToPresent resUser = userController.getUserByNicknameToPresent(testUser.getNickName());
        authController.confirmUserAccount(resUser.getId());
        UserToPresent updatedUser = userController.getUserByNicknameToPresent(testUser.getNickName());
        //Then the user is now verified
        assertEquals(true, updatedUser.isVerified());
    }

    @Test
    public void User_ConfirmUserAccount_NoSuchUser() {
        System.out.println("-------- Test user account confirmation fails - no such user id --------");
        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> authController.confirmUserAccount(-1l)
        );
        assertTrue(thrown.getMessage().contains("No value present"));
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
    public void TesT_Try_To_DeleteUserByNickname_That_Does_Not_Exist() {
        System.out.println("-------- Test trying to delete a user who is not in the DB --------");
        InvalidDataAccessApiUsageException thrown = assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> {
                    //When I try to delete user that doesn't exist in the database - Then it fails
                    authController.deleteUserByNickname("-1"); // no such nickname - illegal nickname
                }
        );
        assertTrue(thrown.getMessage().contains("Entity must not be null!"));
    }
}