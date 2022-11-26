package chatApp.controller;

import chatApp.Entities.User;
import chatApp.repository.UserRepository;
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
    private UserService userService;

    @Autowired
    private static UserRepository userRepository;

    List<User> users;

    @BeforeEach
    public void setup() {
        users = new ArrayList<>();
    }

    @AfterEach
    public void cleanup() {
        users = null;
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
}