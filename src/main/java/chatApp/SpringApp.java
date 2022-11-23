package chatApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
        //User user = new User.UserBuilder("leon@test.com", "1234", "LeonTest").firstName("leon").build();
        //System.out.println(user);
    }
}