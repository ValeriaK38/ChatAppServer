package chatApp.controller;

import chatApp.Entities.User;
import chatApp.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@Controller
public class VerificationEmailController {
    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Sends verification link to the user to activate the account
     * @param user
     * @param url
     */
    public void sendEmail(User user, String url) {
        this.emailSenderService.sendVerificationEmail(user.getEmail(), user.getId(), url);
    }
}