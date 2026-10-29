package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.BancoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoDecisaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class AdiantamentoService {

  // Changelog v2 §11 (antes 'ADIANTAMENTO PAGAMENTO DIVIDA' / '... NAO ACEITE')
  static final String MOTIVO_FECHO_ACEITE = "AMORTIZAÇÃO DIVIDA";
  static final String MOTIVO_FECHO_NAO_ACEITE = "AMORTIZAÇÃO DIVIDA NAO ACEITE";

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final EmprestimoDocumentService documentService;
  private final EmprestimoHelper emprestimoHelper;
  private final ApplicationAuditorAware auditorAware;

  @Transactional
  public String saveUpdatePedidoAdiantamento(PedidoAdiantamentoRequestDTO obj) {

    var base = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());

    var emEdicao = EmprestimoHelper.isSubprocessoEmCurso(base);
    if (emEdicao)
      EmprestimoHelper.validarEtapaAtual(base, EtapaEmprestimo.PEDIDO);

    var initialLoan = emEdicao ? base.getEmprestimo() : base;

    var tipoSituacao = TipoSituacao.valueOf(obj.getTipoSituacao());

    var newLoan = emEdicao ? base : new EmprestimoEntity();
    if (!emEdicao) {
      newLoan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      emprestimoHelper.iniciarSubprocesso(initialLoan.getPedido());
    }
    newLoan.setValorAdiantado(obj.getValorAdiantamento());
    newLoan.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
    newLoan.setTipoSituacao(tipoSituacao.name());
    newLoan.setVersao(initialLoan.getVersao() + 1);
    newLoan.setValorDivida(initialLoan.getValorDivida());
    newLoan.setValorEmprestimo(initialLoan.getValorEmprestimo());
    newLoan.setEmprestimo(initialLoan);
    // Amortização Parcial/Total não pedem o nº de prestações (é calculado ao submeter)
    newLoan.setNrPrestacao(obj.getNumeroPrestacao() != null ? obj.getNumeroPrestacao() : initialLoan.getNrPrestacao());
    newLoan.setEstado(StatusEmprestimo.POR_SUBMETER.name());
    newLoan.setPedido(initialLoan.getPedido());
    newLoan.setJuro(initialLoan.getJuro());
    newLoan.setTiprel(initialLoan.getTiprel());
    newLoan.setMarca(initialLoan.getMarca());
    newLoan.setAnoFabrico(initialLoan.getAnoFabrico());
    newLoan.setCilincrada(initialLoan.getCilincrada());
    newLoan.setTipoViatura(initialLoan.getTipoViatura());
    newLoan.setCombustivel(initialLoan.getCombustivel());
    newLoan.setEstadoViatura(initialLoan.getEstadoViatura());
    newLoan.setBanco(initialLoan.getBanco());
    newLoan.setNib(initialLoan.getNib());
    newLoan.setNif(initialLoan.getNif());
    newLoan.setSwift(initialLoan.getSwift());
    newLoan.setValorPrestacao(initialLoan.getValorPrestacao());
    newLoan.setRenogociacao(initialLoan.getRenogociacao());
    newLoan.setTmId(initialLoan.getTmId());
    newLoan.setFinalidade(initialLoan.getFinalidade());
    newLoan.setTipoRenogociacao(initialLoan.getTipoRenogociacao());
    newLoan.setMotivoFecho(initialLoan.getMotivoFecho());
    newLoan.setValorReforco(initialLoan.getValorReforco());
    newLoan.setValorJuroTotal(initialLoan.getValorJuroTotal());
    newLoan.setDataInicio(EmprestimoHelper.dataInicioOriginal(initialLoan));
    newLoan = emprestimoEntityRepository.save(newLoan);

    documentService.saveDocuments(
        obj.getDocumentos(),
        initialLoan.getTiprel().getFunId(),
        newLoan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name()
    );

    if (obj.getAction().equals(ProcessStepAction.NEXT)) {

      newLoan.setEstado(StatusEmprestimo.SUBMETIDO.name());
      newLoan = emprestimoEntityRepository.save(newLoan);

      var order = newLoan.getPedido();
      order.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());
      pedidoEntityRepository.save(order);

      emprestimoHelper.saveByTipoSituacao(
          tipoSituacao,
          newLoan,
          obj.getValorAdiantamento(),
          obj.getNumeroPrestacao()
      );
    }

    return newLoan.getUuid();
  }

  public void saveAnaliseRh(String emprestimoId, AnaliseRhAdiantamentoRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO);
    // Banco por escolher chegava como 0 e findById(0).orElseThrow() dava erro 500.
    loan.setBanco(request.getBancoId() != null && request.getBancoId() > 0
        ? bancoEntityRepository.findById(request.getBancoId())
            .orElseThrow(() -> IgrpResponseStatusException.badRequest("O banco selecionado não existe."))
        : null);
    loan.setNib(ValidationUtil.sanitizeNib(request.getNib()));
    loan.setSwift(request.getSwift());

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());

    if (request.getParecer() == ParecerProcesso.RETIFICACAO) {
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
    } else if (request.getAction().equals(ProcessStepAction.NEXT)) {
      var responsavelParecer = Optional.ofNullable(request.getResponsavel())
          .map(BaseDecisaoDTO.Responsavel::parecer)
          .flatMap(ParecerProcesso::fromCode)
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("O parecer do responsável é obrigatório"));

      switch (responsavelParecer) {
        // Changelog v2 §11: o Anexar Comprovativo passou para dentro da Verificação.
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case DESFAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.PEDIDO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case RETIFICACAO -> {
          order.setEtapa(EtapaEmprestimo.PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name(),
        Estado.A.name()
    );
    decisionOP.ifPresent(
        obj -> {
          obj.setEstado(Estado.I.name());
          pedidoDecisaoEntityRepository.save(obj);
        });

    var newObj = new PedidoDecisaoEntity();
    newObj.setReferenciaId(loan.getId());
    newObj.setPedido(order);
    newObj.setDecisao(request.getParecer().name());
    newObj.setObs(request.getObservacao());
    newObj.setDataParecer(request.getData());
    setResponsavel(newObj, request.getResponsavel());
    newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());
    newObj.setReferencia(ProcessType.EMPRESTIMO.name());
    newObj.setEstado(Estado.A.name());
    newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    pedidoDecisaoEntityRepository.save(newObj);

    emprestimoEntityRepository.save(loan);
  }

  private void setResponsavel(PedidoDecisaoEntity decisao, BaseDecisaoDTO.Responsavel responsavel) {
    var parecer = Optional.ofNullable(responsavel)
        .map(BaseDecisaoDTO.Responsavel::parecer)
        .flatMap(ParecerProcesso::fromCode);
    if (parecer.isEmpty())
      return;

    decisao.setParecerResponsavel(parecer.get().name());
    decisao.setObservacaoResponsavel(responsavel.observacao());
    decisao.setUtilizadorObservacaoResponsavel(auditorAware.getCurrentSubjectName());
    decisao.setDataObservacaoResponsavel(LocalDateTime.now(ZoneId.systemDefault()));
    decisao.setDataParecerResponsavel(responsavel.dataParecer());
  }

  public void anexarComprovativo(String emprestimoId, DocumentoDTO request, String action) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO);

    documentService.addDocuments(
        List.of(request),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name()
    );

    if (ProcessStepAction.NEXT.name().equals(action)) {
      var order = loan.getPedido();
      order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
      pedidoEntityRepository.save(order);
    }
  }

  public void verificar(String emprestimoId, VerificarAdiantamentoRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    var order = loan.getPedido();
    // Processos que ficaram na antiga etapa Anexar Comprovativo continuam já na Verificação.
    if (EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name().equals(order.getEtapa())) {
      order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
    }
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO);

    // Changelog v2 §11: o comprovativo de depósito/transferência anexa-se na própria Verificação.
    if (request.getDocumentos() != null) {
      documentService.saveDocuments(
          request.getDocumentos(),
          loan.getTiprel().getFunId(),
          loan.getUuid(),
          ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name()
      );
    }

    // Retificar devolve já à etapa anterior (Gravar ou Seguinte), como na Análise RH; antes só
    // com Seguinte, e com Gravar o processo ficava parado na etapa com o parecer Retificação.
    if (request.getAction().equals(ProcessStepAction.NEXT) || request.getParecer() == ParecerProcesso.RETIFICACAO) {

      switch (request.getParecer()) {
        case FAVORAVEL -> {
          var temComprovativo = request.getDocumentos() != null && request.getDocumentos().stream()
              .anyMatch(d -> d != null && d.getTipoDocumentoId() != null && d.getTipoDocumentoId() > 0
                  && org.springframework.util.StringUtils.hasText(d.getUrl()));
          if (!temComprovativo) {
            throw IgrpResponseStatusException.badRequest(
                "Anexe o comprovativo de depósito/transferência antes de concluir a Verificação.");
          }
          loan.setEstado(StatusEmprestimo.ATIVO.name());
          // a versão anterior fecha por amortização
          if (loan.getEmprestimo() != null) {
            loan.getEmprestimo().setMotivoFecho(MOTIVO_FECHO_ACEITE);
            emprestimoEntityRepository.save(loan.getEmprestimo());
          }
        }
        // Não Conforme: a amortização não é aceite — o subprocesso termina e volta a valer o
        // plano do empréstimo anterior.
        case DESFAVORAVEL -> {
          loan.setEstado(StatusEmprestimo.NAO_AUTORIZADO.name());
          loan.setMotivoFecho(MOTIVO_FECHO_NAO_ACEITE);
        }
        case RETIFICACAO -> order.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());
        default ->
            throw IgrpResponseStatusException.badRequest("Parecer inválido para a Verificação.");
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name(),
        Estado.A.name()
    );
    decisionOP.ifPresent(
        obj -> {
          obj.setEstado(Estado.I.name());
          pedidoDecisaoEntityRepository.save(obj);
        });

    var newObj = new PedidoDecisaoEntity();
    newObj.setReferenciaId(loan.getId());
    newObj.setPedido(order);
    newObj.setDecisao(request.getParecer().name());
    newObj.setObs(request.getObservacao());
    newObj.setDataParecer(request.getData());
    newObj.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
    newObj.setReferencia(ProcessType.EMPRESTIMO.name());
    newObj.setEstado(Estado.A.name());
    newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    pedidoDecisaoEntityRepository.save(newObj);

    emprestimoEntityRepository.save(loan);

    if (StatusEmprestimo.NAO_AUTORIZADO.name().equals(loan.getEstado()) && loan.getEmprestimo() != null) {
      emprestimoHelper.reporEmprestimoAnterior(loan);
    }
  }
}
