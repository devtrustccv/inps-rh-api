package cv.inps.rh.emprestimo.domain.service.process;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.VerificarAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.application.dto.BaseDecisaoDTO;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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
  private final PlanoFinanceiroEntityRepository planoFinanceiroEntityRepository;
  private final EmprestimoHelper emprestimoHelper;
  private final ApplicationAuditorAware auditorAware;

  // todo caso emprestimo senao forem validados positivamente activar ou desativar os planos

  @Transactional
  public String saveUpdatePedidoAdiantamento(PedidoAdiantamentoRequestDTO obj) {

    var base = emprestimoEntityRepository.findByUuidOrThrow(obj.getEmprestimoId());
    // Reabrir o pedido de um subprocesso ainda não concluído (Gravar e depois Seguinte, voltar ao
    // pedido depois de submetido, Retificação) actualiza essa versão. Só o empréstimo em vigor dá
    // origem a uma versão nova — antes um subprocesso já SUBMETIDO ganhava outra versão encadeada.
    var emEdicao = EmprestimoHelper.isSubprocessoEmCurso(base);
    if (emEdicao) {
      EmprestimoHelper.validarEtapaAtual(base, EtapaEmprestimo.PEDIDO);
    }
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
    // Como no Reforço, a nova versão herda o capital e a dívida actual do empréstimo anterior;
    // EmprestimoHelper.saveByTipoSituacao aplica depois o adiantamento sobre essa dívida. Antes
    // gravava-se o valor adiantado nos dois campos: a dívida ficava a 0 no NEXT do pagamento
    // antecipado e a "Diminuição da Prestação" (sem valor) falhava com VALOR_EMPRESTIMO a null.
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
    // A avaliação da taxa de esforço é da Análise Financeira de cada processo: não se herda (a
    // Renegociação abria com a do empréstimo anterior já preenchida).
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

      // Como na Viatura, o Seguinte do pedido passa o processo para a Análise RH — antes a etapa
      // ficava PEDIDO: a barra não marcava o pedido como feito e a lista retomava no pedido.
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
      // Retificação do parecer técnico devolve já ao pedido — não depende do responsável.
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
    } else if (request.getAction().equals(ProcessStepAction.NEXT)) {

      // Como na Análise RH do pedido: quem valida o avanço é o responsável (nível 2). Usa o
      // "responsavel" herdado de BaseDecisaoDTO (o GET devolve-o no mesmo formato).
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

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          setResponsavel(obj, request.getResponsavel());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          setResponsavel(newObj, request.getResponsavel());
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_ADIANTAMENTO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }

  private void setResponsavel(PedidoDecisaoEntity decisao, BaseDecisaoDTO.Responsavel responsavel) {
    var parecer = Optional.ofNullable(responsavel)
        .map(BaseDecisaoDTO.Responsavel::parecer)
        .flatMap(ParecerProcesso::fromCode);
    if (parecer.isEmpty()) {
      return;
    }
    decisao.setParecerResponsavel(parecer.get().name());
    decisao.setObservacaoResponsavel(responsavel.observacao());
    decisao.setUtilizadorObservacaoResponsavel(auditorAware.getCurrentSubjectName());
    decisao.setDataObservacaoResponsavel(LocalDateTime.now(ZoneId.systemDefault()));
  }

  public void anexarComprovativo(String emprestimoId, DocumentoDTO request, String action) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO);

    documentService.saveDocuments(
        List.of(request),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANEXAR_CONTRATO_ADIANTAMENTO.name()
    );

    // O endpoint não tinha acção: o Seguinte gravava o documento mas o pedido ficava em
    // ANEXAR_CONTRATO_ADIANTAMENTO até à primeira gravação da Verificação (a lista retomava
    // o subprocesso na etapa errada).
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

    if (request.getAction().equals(ProcessStepAction.NEXT)) {

      switch (request.getParecer()) {
        case FAVORAVEL -> {
          // Última etapa da Amortização: a nova versão passa a ser a que está em vigor (lista).
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

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setEtapa(EtapaEmprestimo.VERIFICACAO_ADIANTAMENTO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }
}
