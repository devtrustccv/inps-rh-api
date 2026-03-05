package cv.inps.rh.funcionario.application.service.notificacao;

import cv.inps.rh.funcionario.application.commands.EnviarNotificacaoCommand;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamNotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificacaoWriteService {

    private final NotificacaoEntityRepository notificacaoRepository;
    private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
    private final EmailService emailService;

    @Transactional
    public Map<String, String> enviarNotificacao(EnviarNotificacaoCommand command) {
        NotificacaoEntity notificacao = notificacaoRepository.findByIdOrThrow(Long.parseLong(command.getId()));

        // 1. Buscar o template (parâmetro) da notificação
        ParamNotificacaoEntity param = paramNotificacaoRepository.findByTipoNotificacao(notificacao.getTipoNotificacao())
                .orElse(null);

        String assunto = (param != null) ? param.getAssunto() : "";
        String corpo = (param != null) ? param.getCorpo() : "";

        // 2. Verificar se há override do assunto e corpo no request
        if (StringUtils.hasText(command.getNotificacaoenviarrequest().getAssunto())) {
            assunto = command.getNotificacaoenviarrequest().getAssunto();
        }
        if (StringUtils.hasText(command.getNotificacaoenviarrequest().getCorpo())) {
            corpo = command.getNotificacaoenviarrequest().getCorpo();
        }

        // 3. Atualizar a entidade Notificacao com os dados finais
        notificacao.setAssunto(assunto);
        notificacao.setMessage(corpo); // O corpo da mensagem vai para o campo 'message'
        notificacao.setEmail(command.getNotificacaoenviarrequest().getEmail());
        notificacao.setNomeReceptor(command.getNotificacaoenviarrequest().getNomeReceptor());

        // 4. Enviar o email
        emailService.sendEmail(notificacao.getEmail(), notificacao.getAssunto(), notificacao.getMessage());

        // 5. Atualizar o estado e a data de envio
        notificacao.setEstado("Enviado"); // Usar um Enum/Constante seria melhor
        notificacao.setDataEnvio(LocalDate.now());

        notificacaoRepository.save(notificacao);

        return Map.of("message", "Notificação enviada com sucesso.");
    }
}
