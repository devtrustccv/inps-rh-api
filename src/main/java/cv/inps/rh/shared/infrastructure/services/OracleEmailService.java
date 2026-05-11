package cv.inps.rh.shared.infrastructure.services;

import cv.inps.rh.shared.application.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OracleEmailService implements EmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OracleEmailService.class);

  private final JdbcTemplate jdbcTemplate;

  @Value("${mail.from:inps.notificacao@govcv.gov.cv}")
  private String fromAddress;

  @Override
  public void sendEmail(String to, String subject, String body) {
    LOGGER.info("Sending email to: {} subject: {}", to, subject);
    jdbcTemplate.execute((java.sql.Connection conn) -> {
      try (var cs = conn.prepareCall(
          "BEGIN sipsv0.SEND_MAIL_V1(" +
          "p_from_name => ?, p_to_name => ?, p_subject => ?, " +
          "p_message_body => ?, p_cc_name => ?, p_message_type => ?); END;")) {
        cs.setString(1, fromAddress);
        cs.setString(2, to);
        cs.setString(3, subject);
        cs.setString(4, body);
        cs.setString(5, "");
        cs.setString(6, "text/html");
        cs.execute();
      }
      return null;
    });
  }
}
