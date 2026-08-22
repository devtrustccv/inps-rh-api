package cv.inps.rh.shared.infrastructure.services;

import cv.inps.rh.shared.application.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "notification.mail",
    name = "enabled",
    havingValue = "true"
)
public class NotificationEventListener {

  private final NotificationService notificationService;

  @Async
  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT,
      fallbackExecution = true
  )
  public void handle(NotificationEvent event) {
    if (event.html()) {
      notificationService.sendHtml(event.recipient(), event.subject(), event.body());
      return;
    }

    notificationService.send(event.recipient(), event.subject(), event.body());
  }
}
