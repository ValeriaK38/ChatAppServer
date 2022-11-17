package chatApp;

import chatApp.Entities.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
        //User user = new User.UserBuilder("leon@test.com", "1234", "LeonTest").firstName("leon").build();
        //System.out.println(user);
    }
}