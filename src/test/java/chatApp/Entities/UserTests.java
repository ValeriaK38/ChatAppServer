package chatApp.Entities;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private static User testUser;
    private static User testAdminUser;
    private static User testUser2;

    @BeforeEach
    public void setup() {
        testUser = new User.UserBuilder("leon@test.com", "1234", "LeonTest").build();
        testUser2 = new User.UserBuilder("test@test.com", "1234", "TestTest").build();
        testAdminUser = new User.UserBuilder("admin@test.com", "1234", "AdminTest").build();
        testAdminUser.setUserType(UserType.ADMIN);
    }

    @AfterEach
    public void cleanup() {
        testUser = null;
        testUser2 = null;
        testAdminUser = null;
    }

    @Test
    public void Admin_Mutes_User_Successfully() {
        System.out.println("-------- Test admin mutes user successfully --------");

        //Given an admin and unmuted registered user exist

        testAdminUser.adminMuteUser(testUser);

        assertEquals(testUser.isMuted(), true);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void Admin_Unmutes_User_Successfully() {
        System.out.println("-------- Test admin unmutes user successfully --------");

        testUser.setMuted(true);

        testAdminUser.adminUnmuteUser(testUser);

        assertEquals(testUser.isMuted(), false);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void Admin_Tries_To_Mute_Other_Admin() {
        System.out.println("-------- Test admin tries to mute other admin --------");

        testUser.setUserType(UserType.ADMIN);

        testAdminUser.adminMuteUser(testUser);

        assertEquals(testUser.isMuted(), false);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void User_Tries_To_Mute_Other_User() {
        System.out.println("-------- Test user tries to mute other user --------");

        //Given two users exist

        testUser.adminMuteUser(testUser2);

        assertEquals(testUser.isMuted(), false);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void User_Tries_To_Unmute_Other_User() {
        System.out.println("-------- Test user tries to unmute other user --------");

        //Given two users exist one of them is muted
        testUser.setMuted(true);

        testUser2.adminMuteUser(testUser);

        assertEquals(testUser.isMuted(), true);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void User_Tries_To_Mute_Himself() {
        System.out.println("-------- Test user tries to mute himself --------");

        //Given  a user exist


        testUser.adminMuteUser(testUser);

        assertEquals(testUser.isMuted(), false);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void User_Tries_To_Unmute_Himself() {
        System.out.println("-------- Test user tries to unmute himself --------");

        //Given  a muted user exist
        testUser.setMuted(true);

        testUser.adminUnmuteUser(testUser);

        assertEquals(testUser.isMuted(), true);
        System.out.println("The user is muted: " + testUser.isMuted());
    }

    @Test
    public void User_Switch_Status_To_Away() {
        System.out.println("-------- Test user switch status to away --------");

        //Given an online user exists

        testUser.switchUserStatus(UserStatus.AWAY);

        assertEquals(testUser.getUserStatus(), UserStatus.AWAY);
        System.out.println("The user is now: " + testUser.getUserStatus());
    }

    @Test
    public void User_Switch_Status_To_Online() {
        System.out.println("-------- Test user switch status to online --------");

        //Given a user with a status that is not online exists
        testUser.switchUserStatus(UserStatus.AWAY);

        testUser.switchUserStatus(UserStatus.ONLINE);

        assertEquals(testUser.getUserStatus(), UserStatus.ONLINE);
        System.out.println("The user is now: " + testUser.getUserStatus());
    }

    @Test
    public void User_Switch_Status_To_Offline() {
        System.out.println("-------- Test user switch status to away --------");

        //Given an online user exists

        testUser.switchUserStatus(UserStatus.OFFLINE);

        assertEquals(testUser.getUserStatus(), UserStatus.OFFLINE);
        System.out.println("The user is now: " + testUser.getUserStatus());
    }
}
