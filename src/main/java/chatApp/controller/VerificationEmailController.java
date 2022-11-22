package chatApp.controller;

import chatApp.Entities.User;
import chatApp.service.EmailSenderServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class VerificationEmailController {

    @Autowired
    private EmailSenderServiceImplementation emailSenderService;
    public void sendEmail(User user) {
//        System.out.println("18 VerificationEmailController sendEmail-----------------------------> " + user.getEmail());
        this.emailSenderService.sendVerificationEmail(user.getEmail(), user.getVerificationCode(),user.getId());
    }
}
