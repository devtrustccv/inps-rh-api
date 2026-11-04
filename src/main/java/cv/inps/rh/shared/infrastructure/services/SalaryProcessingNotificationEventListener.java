package cv.inps.rh.shared.infrastructure.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.constants.EstadoNotificacao;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.events.NotificationEvent;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "notification.mail",
    name = "enabled",
    havingValue = "true"
)
public class SalaryProcessingNotificationEventListener {

  private final NotificationService notificationService;
  private final NotificacaoEntityRepository notificacaoRepository;

  @Async
  @TransactionalEventListener(
      phase = TransactionPhase.AFTER_COMMIT,
      fallbackExecution = true
  )
  public void handle(NotificationEvent event) {

    boolean sent;

    if (event.html()) {
      sent = notificationService.sendHtml(event.recipient(), event.subject(), event.body());
    } else {
      sent = notificationService.send(event.recipient(), event.subject(), event.body());
    }

    var n = new NotificacaoEntity();
    n.setUuid(UuidCreator.getTimeOrderedEpoch());
    n.setTipoNotificacao("VALIDACAO_PROCESSAMENTO"); // TODO 03/11/2026 21:51 validate this
    n.setReferenciaId(event.referenciaId());
    n.setReferenciaUuid(event.referenciaUuid() == null ? null : UUID.fromString(event.referenciaUuid()));
    n.setReferenciaName(TableName.RH_T_PROC_SALARIOS.name());
    n.setAssunto(event.subject());
    n.setMessage(event.body());
    n.setEmail(event.recipient());
    n.setDataEnvio(LocalDate.now(ZoneId.systemDefault()));
    n.setEstado(sent ? EstadoNotificacao.ENVIADO.name() : EstadoNotificacao.POR_ENVIAR.name());
    notificacaoRepository.save(n);
  }
}
