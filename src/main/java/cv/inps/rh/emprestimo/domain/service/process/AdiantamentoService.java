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
    newLoan.setNrPrestacao(obj.getNumeroPrestacao());
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
    loan.setBanco(request.getBancoId() != null ? bancoEntityRepository.findById(request.getBancoId()).orElseThrow() : null);
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
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name());
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
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO);

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());

    // Retificar devolve já à etapa anterior (Gravar ou Seguinte), como na Análise RH; antes só
    // com Seguinte, e com Gravar o processo ficava parado na etapa com o parecer Retificação.
    if (request.getAction().equals(ProcessStepAction.NEXT) || request.getParecer() == ParecerProcesso.RETIFICACAO) {

      switch (request.getParecer()) {
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
          loan.setEstado(StatusEmprestimo.ATIVO.name());
          emprestimoEntityRepository.save(loan);
        }
        case RETIFICACAO -> order.setEtapa(EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name());
        default ->
            throw IgrpResponseStatusException.badRequest("Invalid action for this step %s", request.getAction());
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
  }
}
