package chatApp.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String destinationEmail, Long id, String url) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("lera38lera@gmail.com");
        simpleMailMessage.setTo(destinationEmail);
        simpleMailMessage.setSubject("Verification email for chat-app");
        String sid = Long.toString(id);
        String html = "<html><body><p>  Verify email on the link below </p>" + "<a href= '" + url + ".html?id=" + sid + "'>Verification page</a></body></html>";
        simpleMailMessage.setText(html);
        this.mailSender.send(simpleMailMessage);
    }
}