package chatApp;

import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
import chatApp.controller.AuthControllerTemp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Map;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
       // User user = new User.UserBuilder("leon@test.com", "1234", "LeonTest").firstName("leon").build();

    }
}