package cv.inps.rh.funcionario.application.service.declaracao;

import cv.inps.rh.funcionario.application.commands.NovoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.commands.SubmeterAnalisePedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidacaoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DeclaracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class PedidoDeclaracaoWriteService {

    private final FuncionarioEntityRepository funcionarioRepository;
    private final PedidoEntityRepository pedidoRepository;
    private final DeclaracaoEntityRepository declaracaoRepository;
    private final DocumentoMapper documentoMapper;
    private final NotificacaoEntityRepository notificacaoRepository;
    private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
    private final EmailService emailService;

    @Transactional
    public Map<String, ?> saveNovoPedido(NovoPedidoDeclaracaoCommand command) {

        FuncionarioEntity funcionario = funcionarioRepository
                .findByUuidOrThrow(command.getPedidodeclaracao().getFunId());

        // 1. Criar PedidoEntity
        PedidoEntity pedido = new PedidoEntity();
        pedido.setFunId(funcionario);
        pedido.setOrigem("RH"); // Conforme documentação
        pedido.setTipoPedido("DECLARACAO"); // Conforme documentação
        pedido.setEtapa("PEDIDO"); // Etapa inicial
        pedido.setEstado(Estado.A.name());
        pedido.setUuid(UUID.randomUUID());

        PedidoEntity savedPedido = pedidoRepository.save(pedido);

        // 2. Criar DeclaracaoEntity
        DeclaracaoEntity declaracao = new DeclaracaoEntity();
        declaracao.setPedidoId(savedPedido);
        declaracao.setTipoDeclaracao(command.getPedidodeclaracao().getTipoDeclaracao());
        declaracao.setFinalidade(command.getPedidodeclaracao().getFinalidade());
        declaracao.setEntidadeDestinado(command.getPedidodeclaracao().getEntidadeDestinado());
        declaracao.setDataPedido(command.getPedidodeclaracao().getDataPedido());
        declaracao.setObs(command.getPedidodeclaracao().getObs());
        declaracao.setEstado(Estado.P.name()); // Pendente
        declaracao.setUuid(UUID.randomUUID());

        // Lógica para BackOffice vs Portal (conforme documentação)
        // Se for BackOffice, a análise já vem junto.
        if (command.getPedidodeclaracao().getDecisaoAnalise() != null) {
            declaracao.setDecisaoAnalise(command.getPedidodeclaracao().getDecisaoAnalise());
            declaracao.setObsAnalise(command.getPedidodeclaracao().getObsAnalise());
            savedPedido.setEtapa("ANALISE"); // Avança a etapa
        }

        DeclaracaoEntity savedDeclaracao = declaracaoRepository.save(declaracao);

        // 3. Processar Anexos
        /*if (command.getPedidodeclaracao().getAnexos() != null && !command.getPedidodeclaracao().getAnexos().isEmpty()) {
            documentoMapper.syncDocumentos(
                    savedDeclaracao.getAnexos(),
                    command.getPedidodeclaracao().getAnexos(),
                    "RH_T_DECLARACAO",
                    savedDeclaracao.getId(),
                    savedDeclaracao.getUuid(),
                    null, // docId
                    funcionario);
        }*/

        return Map.of("id", savedDeclaracao.getId(), "uuid", savedDeclaracao.getUuid().toString());
    }

    @Transactional
    public Map<String, ?> submeterAnalise(SubmeterAnalisePedidoDeclaracaoCommand command) {
        DeclaracaoEntity declaracao = declaracaoRepository.findByIdOrThrow(Long.parseLong(command.getId()));

        declaracao.setDecisaoAnalise(command.getPedidodeclaracaoanalise().getDecisaoAnalise());
        declaracao.setObsAnalise(command.getPedidodeclaracaoanalise().getObsAnalise());

        // Avança a etapa no pedido associado
        PedidoEntity pedido = declaracao.getPedidoId();
        if (pedido != null) {
            pedido.setEtapa("ANALISE");
            pedidoRepository.save(pedido);
        }

        declaracaoRepository.save(declaracao);

        return Map.of("message", "Análise submetida com sucesso.");
    }

    @Transactional
    public Map<String, ?> validarPedido(ValidacaoPedidoDeclaracaoCommand command) {
        DeclaracaoEntity declaracao = declaracaoRepository.findByIdOrThrow(Long.parseLong(command.getId()));
        PedidoEntity pedido = declaracao.getPedidoId();

        declaracao.setDecisaoRh(command.getPedidodeclaracaovalidacao().getValidar());
        declaracao.setEntrega(command.getPedidodeclaracaovalidacao().getEntregaPorEmail());

        if ("Sim".equalsIgnoreCase(declaracao.getDecisaoRh())) {
            declaracao.setEstado(Estado.A.name()); // Aprovado
            if (pedido != null) {
                pedido.setEtapa("FINALIZADO");
                pedidoRepository.save(pedido);
            }

            if ("Sim".equalsIgnoreCase(declaracao.getEntrega())) {
                // Enviar notificação por email
                enviarNotificacaoDeclaracao(declaracao);
            }
        } else {
            declaracao.setEstado(Estado.I.name()); // Indeferido/Rejeitado
            if (pedido != null) {
                pedido.setEtapa("FINALIZADO");
                pedidoRepository.save(pedido);
            }
        }

        declaracaoRepository.save(declaracao);

        return Map.of("message", "Pedido validado com sucesso.");
    }

    private void enviarNotificacaoDeclaracao(DeclaracaoEntity declaracao) {
        FuncionarioEntity funcionario = declaracao.getPedidoId().getFunId();
        if (funcionario == null || !StringUtils.hasText(funcionario.getContactos().get(0).getContacto())) {
            // Log ou lança exceção: não é possível notificar sem email.
            return;
        }

        final String TIPO_NOTIFICACAO = "ENVIO_DECLARACAO"; // Usar uma constante

        var param = paramNotificacaoRepository.findByTipoNotificacao(TIPO_NOTIFICACAO).orElse(null);
        String assunto = (param != null) ? param.getAssunto() : "Sua Declaração foi Emitida";
        String corpo = (param != null) ? param.getCorpo() : "Em anexo, a sua declaração solicitada.";

        NotificacaoEntity notificacao = new NotificacaoEntity();
        notificacao.setTipoNotificacao(TIPO_NOTIFICACAO);
        notificacao.setAssunto(assunto);
        notificacao.setMessage(corpo);
        notificacao.setEmail(funcionario.getContactos().get(0).getContacto());
        notificacao.setNomeReceptor(funcionario.getNome());
        notificacao.setEstado("Enviado");
        notificacao.setDataEnvio(LocalDate.now());
        notificacao.setReferenciaName("RH_T_DECLARACAO");
        notificacao.setReferenciaId(declaracao.getId());
        notificacao.setReferenciaUuid(declaracao.getUuid());

        notificacaoRepository.save(notificacao);

        // TODO: Anexar a declaração real ao email. Por agora, só envia o texto.
        emailService.sendEmail(notificacao.getEmail(), notificacao.getAssunto(), notificacao.getMessage());
    }
}
