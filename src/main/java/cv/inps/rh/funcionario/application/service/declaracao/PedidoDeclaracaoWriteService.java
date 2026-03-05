package cv.inps.rh.funcionario.application.service.declaracao;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.NovoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.commands.SubmeterAnalisePedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidacaoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cv.inps.rh.shared.application.services.EmailService;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class PedidoDeclaracaoWriteService {

    private final FuncionarioEntityRepository funcionarioRepository;
    private final PedidoEntityRepository pedidoRepository;
    private final DeclaracaoEntityRepository declaracaoRepository;
    private final DocumentoMapper documentoMapper;
    private final DocumentoEntityRepository documentoEntityRepository;
    private final NotificacaoEntityRepository notificacaoRepository;
    private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
    private final EmailService emailService;

    private final FuncionarioRules funcionarioRules;

    @Transactional
    public Map<String, ?> saveNovoPedido(NovoPedidoDeclaracaoCommand command) {

        FuncionarioEntity funcionario = funcionarioRepository
                .findByUuidOrThrow(command.getPedidodeclaracao().getFunId());

        var tiposRelacionamento = funcionarioRules
            .getTipoRelacionamentoAtual(funcionario.getUuid());

        PedidoEntity pedido = new PedidoEntity();
        pedido.setFunId(funcionario);
        pedido.setOrigem("RH");
        pedido.setTipoPedido("DECLARACAO");
        pedido.setEtapa("PEDIDO");
        pedido.setEstado(Estado.A.name());
        pedido.setUuid(UUID.randomUUID());

        PedidoEntity savedPedido = pedidoRepository.save(pedido);


        DeclaracaoEntity declaracao = new DeclaracaoEntity();
        declaracao.setPedidoId(savedPedido);
        declaracao.setFunId(funcionario);
        declaracao.setTiprelId(tiposRelacionamento);

        declaracao.setTipoDeclaracao(command.getPedidodeclaracao().getTipoDeclaracao());
        declaracao.setFinalidade(command.getPedidodeclaracao().getFinalidade());
        declaracao.setEntidadeDestinado(command.getPedidodeclaracao().getEntidadeDestinado());
        declaracao.setDataPedido(command.getPedidodeclaracao().getDataPedido());
        declaracao.setObs(command.getPedidodeclaracao().getObs());
        declaracao.setEstado(Estado.P.name());
        declaracao.setUuid(UUID.randomUUID());


        if (command.getPedidodeclaracao().getDecisaoAnalise() != null) {
            declaracao.setDecisaoAnalise(command.getPedidodeclaracao().getDecisaoAnalise());
            declaracao.setObsAnalise(command.getPedidodeclaracao().getObsAnalise());
            savedPedido.setEtapa("ANALISE");
        }

        DeclaracaoEntity savedDeclaracao = declaracaoRepository.save(declaracao);

      if (command.getPedidodeclaracao().getAnexos() != null && !command.getPedidodeclaracao().getAnexos().isEmpty()) {
        List<DocumentoEntity> documentos = new java.util.ArrayList<>();
        for (var d : command.getPedidodeclaracao().getAnexos()) {
          var doc = documentoMapper.toEntity(
              d,
              Estado.P,
              TableName.RH_T_DECLARACAO.name(),
              savedDeclaracao.getId(),
              savedDeclaracao.getUuid(),
              1L,
              funcionario);
          doc.setUuid(UuidCreator.getTimeOrderedEpoch());
          documentos.add(doc);
        }
        documentoEntityRepository.saveAll(documentos);
      }


        return Map.of("id", savedDeclaracao.getId(), "uuid", savedDeclaracao.getUuid().toString());
    }

    @Transactional
    public Map<String, ?> submeterAnalise(SubmeterAnalisePedidoDeclaracaoCommand command) {
        DeclaracaoEntity declaracao = declaracaoRepository.findByIdOrThrow(Long.parseLong(command.getId()));

        declaracao.setDecisaoAnalise(command.getPedidodeclaracaoanalise().getDecisaoAnalise());
        declaracao.setObsAnalise(command.getPedidodeclaracaoanalise().getObsAnalise());

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

        if ("SIM".equalsIgnoreCase(declaracao.getDecisaoRh())) {
            declaracao.setEstado(Estado.A.name()); // Aprovado
            if (pedido != null) {
                pedido.setEtapa("FINALIZADO");
                pedidoRepository.save(pedido);
            }

            if ("SIM".equalsIgnoreCase(declaracao.getEntrega())) {
                // Enviar notificação por email
                enviarNotificacaoDeclaracao(declaracao);
            }
        } else {
            declaracao.setEstado(Estado.I.name());
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
        notificacao.setFunId(funcionario);
        notificacao.setReferenciaName("RH_T_DECLARACAO");
        notificacao.setReferenciaId(declaracao.getId());
        notificacao.setReferenciaUuid(declaracao.getUuid());

        notificacaoRepository.save(notificacao);

        // TODO: Anexar a declaração real ao email. Por agora, só envia o texto.
        emailService.sendEmail(notificacao.getEmail(), notificacao.getAssunto(), notificacao.getMessage());
    }
}
