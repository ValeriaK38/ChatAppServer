package chatApp.service;

import chatApp.Entities.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Created custom url based on user's id and sends verification email with link to activate the account
     * @param user - user's email, id
     * @param url              - activation link url
     */
    public void sendVerificationEmail(User user , String url) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("lera38lera@gmail.com");
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Verification email for chat-app");
        String sid = Long.toString(user.getId());
        String html = "<html><body><p>  Verify email on the link below </p>" + "<a href= '" + url + ".html?id=" + sid +"?token="+user.getVerificationCode()+ "'>Verification page</a></body></html>";
        simpleMailMessage.setText(html);
        this.mailSender.send(simpleMailMessage);
    }
}