package chatApp.service;

interface EmailSenderService {
    void sendVerificationEmail(String destinationEmail, String verificationCode, int id);

}
