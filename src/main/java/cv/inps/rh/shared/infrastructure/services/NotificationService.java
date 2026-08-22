package cv.inps.rh.shared.infrastructure.services;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

@Service
@ConditionalOnProperty(
    prefix = "notification.mail",
    name = "enabled",
    havingValue = "true"
)
public class NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  private final JavaMailSender mailSender;
  private final String fromAddress;

  public NotificationService(JavaMailSender mailSender, @Value("${mail.from:${spring.mail.username}}") String fromAddress) {
    this.mailSender = mailSender;
    this.fromAddress = fromAddress;
  }

  public void send(String recipient, String subject, String body) {
    send(recipient, subject, body, false);
  }

  public void sendHtml(String recipient, String subject, String body) {
    send(recipient, subject, body, true);
  }

  private void send(String recipient, String subject, String body, boolean html) {

    Assert.hasText(recipient, "Notification recipient must not be empty");
    Assert.notNull(subject, "Notification subject must not be null");
    Assert.notNull(body, "Notification body must not be null");

    var message = mailSender.createMimeMessage();

    try {
      var helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
      helper.setFrom(fromAddress);
      helper.setTo(recipient);
      helper.setSubject(subject);
      helper.setText(body, html);
    } catch (MessagingException exception) {
      throw new IllegalStateException("Unable to create notification email", exception);
    }

    try {
      mailSender.send(message);
      LOGGER.info("Notification email sent to {}", recipient);
    } catch (MailException exception) {
      LOGGER.error("Unable to send notification email to {}", recipient, exception);
    }
  }
}
