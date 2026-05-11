package cv.inps.rh.shared.infrastructure.services;

import cv.inps.rh.shared.application.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class MockEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Simula o envio de email
        LOGGER.info("Simulating email sending to: {}", to);
        LOGGER.info("Subject: {}", subject);
        LOGGER.info("Body: {}", body);
        // Em um ambiente real, aqui estaria a integração com o provedor de email
    }
}
