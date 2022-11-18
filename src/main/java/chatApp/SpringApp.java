package chatApp;

import chatApp.Entities.Enums.UserType;
import chatApp.Entities.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
//        User user = new User.UserBuilder("leon@test.com", "1234", "LeonTest").firstName("leon").build();
//        User muteUser = new User.UserBuilder("mute@test.com", "1234", "muteTest").build();
//        //System.out.println(user);
//
//        System.out.println(user.getUserType());
//        System.out.println(muteUser.getUserType());
//        user.adminMuteUser(muteUser);
//        System.out.println(muteUser.isMuted());
//        System.out.println("~~~~~~~~~~");
//        user.setUserType(UserType.ADMIN);
//        user.adminMuteUser(muteUser);
//        System.out.println(muteUser.isMuted());
//        user.adminUnmuteUser(muteUser);
//        System.out.println(muteUser.isMuted());
    }
}