package cv.inps.rh.shared.application.events;

public record NotificationEvent(
    String recipient,
    String subject,
    String body,
    Long referenciaId,
    String referenciaUuid,
    boolean html
) {

  public static NotificationEvent text(String recipient, String subject, String body, Long referenciaId, String referenciaUuid) {
    return new NotificationEvent(recipient, subject, body, referenciaId, referenciaUuid, false);
  }

  public static NotificationEvent html(String recipient, String subject, String body, Long referenciaId, String referenciaUuid) {
    return new NotificationEvent(recipient, subject, body, referenciaId, referenciaUuid, true);
  }
}
