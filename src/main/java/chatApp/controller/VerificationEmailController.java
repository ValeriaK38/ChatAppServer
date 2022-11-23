package chatApp.controller;

import chatApp.Entities.User;
import chatApp.repository.UserRepository;
import chatApp.service.EmailSenderService;
import chatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class VerificationEmailController {

    @Autowired
    private EmailSenderService emailSenderService;


    public void sendEmail(User user, String url) {
        this.emailSenderService.sendVerificationEmail(user.getEmail(), user.getId(), url);
    }
}
