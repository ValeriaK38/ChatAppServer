package chatApp.Entities;

import chatApp.Entities.Enums.UserType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.runner.RunWith;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests {

    private static User testUser;
    private static User testAdminUser;

    @BeforeTest
    static void setup(){
        testUser = new User.UserBuilder("leon@test.com", "1234", "LeonTest").build();
        testAdminUser =  new User.UserBuilder("admin@test.com", "1234", "AdminTest").build();
        testAdminUser.setUserType(UserType.ADMIN);
    }

    @AfterTest
    static void cleanup(){
        testUser = null;
    }

    @Test
    static void Admin_Mutes_User_Successfully(){

        testAdminUser.adminMuteUser(testUser);

        assertEquals(testUser.isMuted(), true);

    }


}
