package cv.inps.rh.shared.application.events;

public record NotificationEvent(
    String recipient,
    String subject,
    String body,
    boolean html
) {

  public static NotificationEvent text(String recipient, String subject, String body) {
    return new NotificationEvent(recipient, subject, body, false);
  }

  public static NotificationEvent html(String recipient, String subject, String body) {
    return new NotificationEvent(recipient, subject, body, true);
  }
}
