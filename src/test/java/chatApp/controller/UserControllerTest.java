package chatApp.controller;

import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
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
    List<User> users;

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
        User tempUser = userController.getUserByNickname(unmutedUser.getNickName());
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
        User tempUser = userController.getUserByNickname(mutedUser.getNickName());
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
}