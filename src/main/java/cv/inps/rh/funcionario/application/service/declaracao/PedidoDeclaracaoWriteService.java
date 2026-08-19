package cv.inps.rh.funcionario.application.service.declaracao;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.NovoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.commands.SubmeterAnalisePedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidacaoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PedidoDeclaracaoWriteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoDeclaracaoWriteService.class);

    private final FuncionarioEntityRepository funcionarioRepository;
    private final PedidoEntityRepository pedidoRepository;
    private final DeclaracaoEntityRepository declaracaoRepository;
    private final DocumentoMapper documentoMapper;
    private final DocumentoEntityRepository documentoEntityRepository;
    private final NotificacaoEntityRepository notificacaoRepository;
    private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
    private final EmailService emailService;

    private final FuncionarioRules funcionarioRules;
    private final OrdemServicoWriteService ordemServicoWriteService;

    @Transactional
    public SuccessResponseDTO saveNovoPedido(NovoPedidoDeclaracaoCommand command) {

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

        declaracao.setTipoDeclaracao(ValidationUtil.trimToNull(command.getPedidodeclaracao().getTipoDeclaracao()));
        declaracao.setFinalidade(ValidationUtil.trimToNull(command.getPedidodeclaracao().getFinalidade()));
        declaracao.setEntidadeDestinado(ValidationUtil.trimToNull(command.getPedidodeclaracao().getEntidadeDestinado()));
        declaracao.setDataPedido(command.getPedidodeclaracao().getDataPedido());
        declaracao.setObs(ValidationUtil.trimToNull(command.getPedidodeclaracao().getObs()));
        declaracao.setEstado(Estado.P.name());
        declaracao.setUuid(UUID.randomUUID());


        if (command.getPedidodeclaracao().getDecisaoAnalise() != null) {
            declaracao.setDecisaoAnalise(ValidationUtil.trimToNull(command.getPedidodeclaracao().getDecisaoAnalise()));
            declaracao.setObsAnalise(ValidationUtil.trimToNull(command.getPedidodeclaracao().getObsAnalise()));
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


        return new SuccessResponseDTO(true, savedDeclaracao.getUuid().toString(), "Pedido de declaração registado.", List.of());
    }

    @Transactional
    public SuccessResponseDTO submeterAnalise(SubmeterAnalisePedidoDeclaracaoCommand command) {
        DeclaracaoEntity declaracao = declaracaoRepository.findByIdOrThrow(Long.parseLong(command.getId()));

        declaracao.setDecisaoAnalise(ValidationUtil.trimToNull(command.getPedidodeclaracaoanalise().getDecisaoAnalise()));
        declaracao.setObsAnalise(ValidationUtil.trimToNull(command.getPedidodeclaracaoanalise().getObsAnalise()));

        PedidoEntity pedido = declaracao.getPedidoId();
        if (pedido != null) {
            pedido.setEtapa("ANALISE");
            pedidoRepository.save(pedido);
        }

        declaracaoRepository.save(declaracao);

        return new SuccessResponseDTO(true, declaracao.getUuid().toString(), "Análise submetida com sucesso.", List.of());
    }

    @Transactional
    public SuccessResponseDTO validarPedido(ValidacaoPedidoDeclaracaoCommand command) {

        // Terceiro caminho da validação (SIM / NAO / CORRIGIR). O fluxo de correção ainda não está
        // implementado: por agora CORRIGIR é um NO-OP — regista no log e devolve 200 com mensagem, SEM
        // validar, actualizar ou mudar qualquer estado. Guard no topo para não tocar em nada.
        if (ValidationUtil.isCorrigir(command.getPedidodeclaracaovalidacao().getValidar())) {
            LOGGER.info("[CORRIGIR] DECLARACAO (id={}): opção 'Corrigir' ainda não implementada; nenhuma alteração aplicada.",
                command.getId());
            return new SuccessResponseDTO(false, null, ValidationUtil.MSG_CORRIGIR_NAO_IMPLEMENTADO, List.of());
        }

        DeclaracaoEntity declaracao = declaracaoRepository.findByIdOrThrow(Long.parseLong(command.getId()));
        PedidoEntity pedido = declaracao.getPedidoId();

        declaracao.setDecisaoRh(ValidationUtil.trimToNull(command.getPedidodeclaracaovalidacao().getValidar()));
        declaracao.setEntrega(command.getPedidodeclaracaovalidacao().getEntregaPorEmail());

        if ("SIM".equalsIgnoreCase(declaracao.getDecisaoRh())) {
            declaracao.setEstado(Estado.A.name());
            if (pedido != null) {
                pedido.setEtapa("FINALIZADO");
                pedidoRepository.save(pedido);
            }

            ordemServicoWriteService.criar(
                declaracao.getFunId(),
                declaracao.getTiprelId(),
                command.getPedidodeclaracaovalidacao().getTipoOrdemServico());

            if ("SIM".equalsIgnoreCase(declaracao.getEntrega())) {
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

        var mensagem = "SIM".equalsIgnoreCase(declaracao.getDecisaoRh())
            ? "Pedido validado com sucesso."
            : "Pedido rejeitado.";
        return new SuccessResponseDTO(true, declaracao.getUuid().toString(), mensagem, List.of());
    }

    private void enviarNotificacaoDeclaracao(DeclaracaoEntity declaracao) {
        FuncionarioEntity funcionario = declaracao.getPedidoId().getFunId();
      if (funcionario == null || !StringUtils.hasText(funcionario.getContactos().getFirst().getContacto())) {
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
      notificacao.setEmail(funcionario.getContactos().getFirst().getContacto());
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
