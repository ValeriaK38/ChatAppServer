package chatApp.service;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
import chatApp.controller.AuthController;
import chatApp.controller.UserController;
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
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthController authController;

    List<User> users;
    User test1,test2,test3;

    @BeforeEach
    public void setup() {
        //Creates users for the tests and saves them in the database so anyone can run the tests at any time.

        users = new ArrayList<>();

        test1 = new User.UserBuilder("test@test7.com", "leon1234", "test7").build();
        test2 = new User.UserBuilder("test@test8.com", "leon1234", "test8").build();
        test3 = new User.UserBuilder("test@test9.com", "1234", "test9").build();

        test1.setUserStatus(UserStatus.ONLINE);
        test2.setUserStatus(UserStatus.ONLINE);
        test3.setUserStatus(UserStatus.OFFLINE);

        userService.saveUserInDB(test1);
        userService.saveUserInDB(test2);
        userService.saveUserInDB(test3);
    }

    @AfterEach
    public void cleanup() {
        //Deletes the users from the DB so when you run the tests again they won't exist.

        authController.deleteUserByNickname(test1.getNickName());
        authController.deleteUserByNickname(test2.getNickName());
        authController.deleteUserByNickname(test3.getNickName());

        users = null;
        test1 = null;
        test2 = null;
        test3 = null;
    }


    @Test
    void getAllOnlineUsers() {
        System.out.println("-------- Test getting only the online users --------");

        //Given there are online and offline users

        //When I try to get only users who are online(Can be away as well)
        users = userService.getAllOnlineUsers();

        //Then I get only the online users
        assertEquals(users.size(),2);

        System.out.println("There are two online users only!");
    }
}