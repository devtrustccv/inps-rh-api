package cv.inps.rh.shared.application.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
