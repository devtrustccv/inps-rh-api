package cv.inps.rh.shared.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacaoDispatchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoDispatchService.class);

  private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final EmailService emailService;

  public void enviar(String tipoNotificacao, String emailDestino, String nomeReceptor,
                     Long referenciaId, String referenciaName, UUID referenciaUuid,
                     FuncionarioEntity funId, Map<String, String> vars) {
    var paramOpt = paramNotificacaoRepository.findByTipoNotificacao(tipoNotificacao);
    if (paramOpt.isEmpty()) {
      LOGGER.warn("Template de notificação não encontrado para tipo: {}", tipoNotificacao);
      return;
    }
    var param = paramOpt.get();
    String assunto = substituir(param.getAssunto(), vars);
    String corpo = substituir(param.getCorpo(), vars);

    String estado = "Enviado";
    try {
      emailService.sendEmail(emailDestino, assunto, corpo);
    } catch (Exception e) {
      LOGGER.error("Erro ao enviar email notificação {} para {}: {}", tipoNotificacao, emailDestino, e.getMessage());
      estado = "Erro";
    }

    var notificacao = new NotificacaoEntity();
    notificacao.setTipoNotificacao(tipoNotificacao);
    notificacao.setReferenciaId(referenciaId);
    notificacao.setReferenciaName(referenciaName);
    notificacao.setReferenciaUuid(referenciaUuid);
    notificacao.setAssunto(assunto);
    notificacao.setMessage(corpo);
    notificacao.setEmail(emailDestino);
    notificacao.setNomeReceptor(nomeReceptor);
    notificacao.setDataEnvio(LocalDate.now());
    notificacao.setEstado(estado);
    notificacao.setUuid(UuidCreator.getTimeOrderedEpoch());
    notificacao.setFunId(funId);
    notificacaoRepository.save(notificacao);
  }

  private String substituir(String template, Map<String, String> vars) {
    if (template == null || vars == null || vars.isEmpty()) return template;
    for (var entry : vars.entrySet()) {
      template = template.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
    }
    return template;
  }
}
