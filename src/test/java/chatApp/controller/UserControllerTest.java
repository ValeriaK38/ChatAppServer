package chatApp.controller;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    UserController userController;
    @Autowired
    AuthController authController;

    private static User mutedUser;
    private static User unmutedUser;
    private static User adminUser;
    private static User adminUser2;
    List<UserToPresent> users;

    @BeforeEach
    public void setup() {
        //Creates users for the tests and saves them in the database so anyone can run the tests at any time.

        users = new ArrayList<>();

        mutedUser = new User.UserBuilder("test@test2.com", "leon1234", "test2").build();
        unmutedUser = new User.UserBuilder("test@test3.com", "leon1234", "test3").build();
        adminUser = new User.UserBuilder("admin@test.com", "1234", "AdminTest").build();
        adminUser2 = new User.UserBuilder("admin2@test.com", "1234", "AdminTest2").build();

        adminUser.setUserType(UserType.ADMIN);
        adminUser2.setUserType(UserType.ADMIN);

        userController.saveUserInDB(mutedUser);
        userController.saveUserInDB(unmutedUser);
        userController.saveUserInDB(adminUser);
        userController.saveUserInDB(adminUser2);

        userController.muteUnmute(adminUser.getNickName(),unmutedUser.getNickName(),"unmute");
        userController.muteUnmute(adminUser.getNickName(),mutedUser.getNickName(),"mute");
    }

    @AfterEach
    public void cleanup() {
        //Deletes the users from the DB so when you run the tests again they won't exist.

        userController.muteUnmute(adminUser.getNickName(),unmutedUser.getNickName(),"unmute");
        userController.muteUnmute(adminUser.getNickName(),mutedUser.getNickName(),"mute");

        authController.deleteUserByNickname(mutedUser.getNickName());
        authController.deleteUserByNickname(unmutedUser.getNickName());
        authController.deleteUserByNickname(adminUser.getNickName());
        authController.deleteUserByNickname(adminUser2.getNickName());

        users = null;
        mutedUser = null;
        unmutedUser = null;
        adminUser = null;
        adminUser2 = null;
    }


    @Test
    void Get_All_Users_Successfully() {
        System.out.println("-------- Test pulling all users from database --------");

        //Given there are users in the database

        //When i try to get the list of all the users
        users = userController.getAllUsers();

        //Then I now have a list of all the users in the database
        assertFalse(users.isEmpty());

        System.out.println("The first user in the database is:");
        System.out.println(users.get(0));
    }

    @Test
    public void Admin_Mutes_User_Successfully() {
        System.out.println("-------- Test admin mutes user successfully --------");

        //Given an admin and unmuted registered user exist

        //When the admin mutes the unmuted user.
        userController.muteUnmute(adminUser.getNickName(),unmutedUser.getNickName(),"mute");

        //Then the user is now muted
        UserToPresent tempUser = userController.getUserByNicknameToPresent(unmutedUser.getNickName());
        assertEquals(tempUser.isMuted(), true);

        System.out.println("The user is muted");
    }

    @Test
    public void Admin_Unmutes_User_Successfully() {
        System.out.println("-------- Test admin unmutes user successfully --------");

        //Given an admin and a muted registered user exist

        //When the admin unmutes the muted user.
        userController.muteUnmute(adminUser.getNickName(),mutedUser.getNickName(),"unmute");

        //Then the user is unmuted
        UserToPresent tempUser = userController.getUserByNicknameToPresent(mutedUser.getNickName());
        assertEquals(tempUser.isMuted(), false);

        System.out.println("The user is unmuted:");
    }

    @Test
    public void Admin_Tries_To_Mute_Other_Admin() {
        System.out.println("-------- Test admin tries to mute other admin --------");

        //Give there are two admins in the DB

        //When an admin tries to mute another admin Then he fails
        assertThrows(Exception.class, () -> userController.muteUnmute(adminUser.getNickName(),adminUser2.getNickName()
        ,"mute"));

        System.out.println("Admin cant mute another admin");
    }

    @Test
    public void User_Tries_To_Mute_Other_User() {
        System.out.println("-------- Test user tries to mute other user --------");

        //Given two users exist


        //When a user tries to mute another user Then he fails
        assertThrows(Exception.class, () -> userController.muteUnmute(mutedUser.getNickName(),unmutedUser.getNickName()
                ,"mute"));

        System.out.println("User cant mute another user");
    }

    @Test
    public void User_Tries_To_Unmute_Other_User() {
        System.out.println("-------- Test user tries to unmute other user --------");

        //Given two users exist one of them is muted


        //When a user tries to unmute another user Then he fails
        assertThrows(Exception.class, () -> userController.muteUnmute(mutedUser.getNickName(),unmutedUser.getNickName()
                ,"unmute"));

        System.out.println("User cant unmute another user");
    }

    @Test
    public void Admin_Tries_To_Unmute_Other_Admin() {
        System.out.println("-------- Test admin tries to unmute other admin --------");

        //Given two admins exist

        //When a admin tries to unmute another admin Then he fails
        assertThrows(Exception.class, () -> userController.muteUnmute(adminUser.getNickName(),adminUser2.getNickName()
                ,"unmute"));

        System.out.println("Admin cant unmute another admin");
    }

    @Test
    public void User_Tries_To_Mute_Himself() {
        System.out.println("-------- Test user tries to mute himself --------");

        //Given a user exist

        //When a user tries to mute himself
        assertThrows(Exception.class, () -> userController.muteUnmute(unmutedUser.getNickName(),unmutedUser.getNickName()
                ,"mute"));

        System.out.println("User cant mute himself");
    }

    @Test
    public void Admin_Tries_To_Mute_Himself() {
        System.out.println("-------- Test admin tries to mute himself --------");

        //Given a admin exist

        //When a admin tries to mute himself
        assertThrows(Exception.class, () -> userController.muteUnmute(adminUser.getNickName(),adminUser.getNickName()
                ,"mute"));

        System.out.println("Admin cant mute himself");
    }

    @Test
    public void User_Tries_To_Unmute_Himself() {
        System.out.println("-------- Test user tries to unmute himself --------");

        //Given  a muted user exist

        //When a user tries to unmute himself
        assertThrows(Exception.class, () -> userController.muteUnmute(mutedUser.getNickName(),mutedUser.getNickName()
                ,"unmute"));

        System.out.println("User cant unmute himself");
    }

    @Test
    public void Admin_Tries_To_Unmute_Himself() {
        System.out.println("-------- Test user tries to unmute himself --------");

        //Given admin exist

        //When an admin tries to unmute himself
        assertThrows(Exception.class, () -> userController.muteUnmute(adminUser.getNickName(),adminUser.getNickName()
                ,"unmute"));

        System.out.println("Admin cant unmute himself");
    }

    @Test
    void changeStatusToAway() {
        System.out.println("-------- Test Online user change status to away --------");

        User testUser = new User.UserBuilder("test@test1.com", "leon1234", "test1").build();
        testUser.setVerified(true);
        testUser.setUserStatus(UserStatus.ONLINE);
        userController.saveUserInDB(testUser);
        userController.awayOnline(testUser.getNickName());
        UserToPresent user = userController.getUserByNicknameToPresent(testUser.getNickName());
        assertEquals(UserStatus.AWAY , user.getUserStatus());
        authController.deleteUserByNickname(user.getNickName());
    }

    @Test
    void changeStatusToOnline() {
        System.out.println("-------- Test Online user change status to away --------");

        User testUser = new User.UserBuilder("test@test1.com", "leon1234", "test1").build();
        testUser.setVerified(true);
        testUser.setUserStatus(UserStatus.AWAY);
        userController.saveUserInDB(testUser);
        userController.awayOnline(testUser.getNickName());
        UserToPresent user = userController.getUserByNicknameToPresent(testUser.getNickName());
        assertEquals(UserStatus.ONLINE , user.getUserStatus());
        authController.deleteUserByNickname(user.getNickName());
    }

    @Test
    public void keepAlive_Success() {
        System.out.println("-------- Test the keep alive method --------");

        //Given there is a user in the DB
        Timestamp before = unmutedUser.getLast_Loggedin();

        //When we call the keep alive method on the user
        userController.keepAlive(unmutedUser.getNickName());

        //Then the timestamp of last log in has changed
        UserToPresent tempUser = userController.getUserByNicknameToPresent(unmutedUser.getNickName());

        assertNotEquals(tempUser.getLast_Loggedin() , before);

        System.out.println("The user timestamp has changed");
    }

    @Test
    public void keepAlive_With_A_User_That_Does_Not_Exist() {
        System.out.println("-------- Test the keep alive method on someone who's not in the DB --------");

        //Given the user we want to keep alive is not in the DB
        User newUser = new User();
        newUser.setNickName("Test!");

        //When we call the keep alive method on the user
         userController.keepAlive(newUser.getNickName());

        //Then it fails
        assertNull(newUser.getLast_Loggedin());
        System.out.println("Cant keep alive someone who is not in the DB");
    }

    @Test
    public void Get_User_By_Nickname_Successfully() {
        System.out.println("-------- Test get user by nickname successfully --------");

        //Given there is a user in the DB

        //When we try to get the user from the DB
        UserToPresent newUser = userController.getUserByNicknameToPresent(unmutedUser.getNickName());

        //Then we get the user from the DB
        assertEquals(newUser.getNickName() , unmutedUser.getNickName());

        System.out.println("We got the user from the DB successfully and the user is \n" + newUser);
    }

    @Test
    public void Test_Try_To_Get_User_By_Nickname_Who_Is_Not_In_The_DB() {
        System.out.println("-------- Test trying to get a user who is not in the DB --------");

        //Given the user we want to get is not in the DB

        //When we try to get the user from the DB Then we get nothing from the DB
        assertNull(userController.getUserByNickname("Not in DB Test User"));

        System.out.println("Cant retrieve user who is not in the DB!");
    }

    @Test
    public void check_Offline_Users(){

        Timestamp now = Timestamp.from(Instant.now());

        //Given there are two users one of them has exited the page without pressing the logout button a while ago
        mutedUser.setUserStatus(UserStatus.ONLINE);
        unmutedUser.setUserStatus(UserStatus.ONLINE);
        mutedUser.setLast_Loggedin(now);
        unmutedUser.setLast_Loggedin(Timestamp.valueOf("2018-09-01 09:01:15"));
        userController.saveUserInDB(mutedUser);
        userController.saveUserInDB(unmutedUser);

        //When we check if there are people who left the page without pressing the logout button
        userController.checkOfflineUsers();

        UserToPresent testOnline = userController.getUserByNicknameToPresent(mutedUser.getNickName());
        UserToPresent testOffline = userController.getUserByNicknameToPresent(unmutedUser.getNickName());


        //Then we see there is only one person who is logged in
        assertEquals(testOnline.getUserStatus(),UserStatus.ONLINE);
        assertEquals(testOffline.getUserStatus(),UserStatus.OFFLINE);

        System.out.println("There is only one user who is left online, the other one was logged out by the system");
    }
}