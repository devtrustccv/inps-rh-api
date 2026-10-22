package cv.inps.rh.emprestimo.domain.service.process;

import cv.inps.rh.shared.util.ValidationUtil;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.emprestimo.application.constants.ParecerProcesso;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.domain.service.EmprestimoDocumentService;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import cv.inps.rh.emprestimo.domain.service.constants.*;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoOutroEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoDecisaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AquisicaoViaturaService {

  private final EmprestimoEntityRepository emprestimoEntityRepository;
  private final PedidoDecisaoEntityRepository pedidoDecisaoEntityRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final BancoEntityRepository bancoEntityRepository;
  private final EmprestimoOutroEntityRepository emprestimoOutroEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final EmprestimoDocumentService documentService;
  private final EmprestimoWriteService emprestimoWriteService;
  private final ApplicationAuditorAware auditorAware;
  private final EmprestimoHelper emprestimoHelper;

  public IdDTO saveUpdatePedidoEmprestimo(String emprestimoId, PedidoEmprestimoRequestDTO request) {

    var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(UUID.fromString(request.getFuncionarioId()));
    var funId = currentRelation.getFunId();

    final var isLoanUpdate = StringUtils.hasText(emprestimoId);

    EmprestimoEntity entity;

    if (isLoanUpdate)
      entity = emprestimoEntityRepository.findByUuidOrThrow(emprestimoId);
    else {
      entity = new EmprestimoEntity();
      entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      entity.setEstado(StatusEmprestimo.POR_SUBMETER.name());
      entity.setTipoEmprestimo(TipoPedido.AQUISICAO_VIATURA.name());
      entity.setFinalidade(TipoPedido.AQUISICAO_VIATURA.name());
      entity.setTipoSituacao(request.getTipoSituacao());
      entity.setVersao(1L);
    }

    if (isLoanUpdate) {
      EmprestimoHelper.validarEtapaAtual(entity, EtapaEmprestimo.PEDIDO);
    }

    // Nome do fornecedor vem do NIF escolhido no pedido e é obrigatório.
    if (!StringUtils.hasText(request.getNomeFornecedor())) {
      throw IgrpResponseStatusException.badRequest("O nome do fornecedor é obrigatório (seleccione o NIF do fornecedor).");
    }
    entity.setNomeFornecedor(request.getNomeFornecedor().trim());
    entity.setBanco(Objects.nonNull(request.getBancoId()) ? bancoEntityRepository.findById(request.getBancoId()).orElseThrow() : null);
    entity.setTiprel(currentRelation);
    entity.setMarca(request.getMarca());
    entity.setAnoFabrico(request.getAnoFabrico());
    entity.setCilincrada(request.getCilindrada());
    entity.setTipoViatura(request.getTipoviatura());
    entity.setCombustivel(request.getCombustivel());
    entity.setEstadoViatura(request.getEstadoViatura());
    entity.setValorEmprestimo(request.getValorEmprestimo());
    entity.setValorDivida(request.getValorEmprestimo());
    entity.setNrPrestacao(request.getNumeroPrestacoes());
    entity.setJuro(request.getJuros());
    // Só validado se preenchido (21 dígitos), como no resto do sistema — antes era gravado sem validação.
    entity.setNib(ValidationUtil.sanitizeNib(request.getNib()));
    entity.setNif(request.getNif());

    PedidoEntity order;

    if (!isLoanUpdate) {
      order = new PedidoEntity();
      order.setFunId(funId);
      order.setUuid(UuidCreator.getTimeOrderedEpoch());
      order.setTipoPedido(TipoPedido.AQUISICAO_VIATURA.name());
      order.setOrigem("RH");
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      order.setEstado(Estado.A.name());

    } else {
      order = entity.getPedido();
    }

    // Seguinte submete o pedido também quando é criado já com Seguinte (antes só ao actualizar: o
    // pedido ficava POR_SUBMETER na etapa PEDIDO e a Análise RH é que forçava a etapa).
    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
      entity.setEstado(StatusEmprestimo.SUBMETIDO.name());
    }

    order = pedidoEntityRepository.save(order);
    entity.setPedido(order);
    entity = emprestimoEntityRepository.save(entity);

    var response = new IdDTO(entity.getUuid());

    documentService.saveDocuments(
        request.getDocumentos(),
        funId,
        response.getId(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.PEDIDO.name()
    );

    return response;
  }

  public void saveUpdateDecisaoAnaliseRh(String uuid, AnaliseRhRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.ANALISE_RH_PEDIDO);
    loan.setNrPrestacao(request.getNumeroPrestacao());
    loan.setValorEmprestimo(request.getValorEmprestimo());
    loan.setJuro(request.getJuros());

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());

    var isNextDecision = request.getAction().equals(ProcessStepAction.NEXT);

    if (request.getParecer() == ParecerProcesso.RETIFICACAO) {
      // Retificação do parecer técnico devolve já à etapa anterior — não
      // depende do responsável nem de action=NEXT (Gravar ou Seguinte têm
      // o mesmo efeito aqui).
      order.setEtapa(EtapaEmprestimo.PEDIDO.name());
      loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
    } else if (isNextDecision) {

      var responsavelParecer = Optional.ofNullable(request.getResponsavel())
          .map(AnaliseRhRequestDTO.Responsavel::parecer)
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("O parecer do responsável é obrigatório"));

      // Só a Validação Responsável (nível 2) avança a etapa — Conforme e
      // Não Conforme avançam ambos para Análise Financeira; só a
      // Retificação do responsável devolve à etapa anterior.
      switch (responsavelParecer) {
        case FAVORAVEL, DESFAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_RH.name());
        }
        case RETIFICACAO -> {
          order.setEtapa(EtapaEmprestimo.PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    emprestimoEntityRepository.save(loan);
    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_RH_PEDIDO.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          obj.setDataParecer(request.getData());
          Optional.ofNullable(request.getResponsavel())
              .ifPresent(resp -> {
                obj.setParecerResponsavel(resp.parecer().name());
                obj.setObservacaoResponsavel(resp.observacao());
                obj.setUtilizadorObservacaoResponsavel(auditorAware.getCurrentSubjectName());
                obj.setDataObservacaoResponsavel(LocalDateTime.now(ZoneId.systemDefault()));
                obj.setDataParecerResponsavel(resp.data());
              });
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setDataParecer(request.getData());
          // Antes só se gravava o responsável ao actualizar uma decisão existente: um Seguinte
          // sem Gravar antes perdia o parecer do responsável (RH_T_PEDIDO_DECISAO sem ele).
          Optional.ofNullable(request.getResponsavel())
              .ifPresent(resp -> {
                newObj.setParecerResponsavel(resp.parecer().name());
                newObj.setObservacaoResponsavel(resp.observacao());
                newObj.setUtilizadorObservacaoResponsavel(auditorAware.getCurrentSubjectName());
                newObj.setDataObservacaoResponsavel(LocalDateTime.now(ZoneId.systemDefault()));
                newObj.setDataParecerResponsavel(resp.data());
              });
          newObj.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    var fun = order.getFunId();

    var requestIds = request.getOutrosEmprestimos().stream()
        .map(OutrosEmprestimosDTO::id)
        .filter(StringUtils::hasText)
        .collect(Collectors.toSet());

    var paraInativar = emprestimoOutroEntityRepository
        .findByReferenciaOrigemAndFunAndEstado(order, fun, Estado.A.name())
        .stream()
        .filter(existente -> !requestIds.contains(existente.getUuid()))
        .peek(existente -> existente.setEstado(Estado.I.name()))
        .toList();

    var others = new ArrayList<>(paraInativar);

    for (var other : request.getOutrosEmprestimos()) {

      final EmprestimoOutroEntity anotherLoan;

      if (StringUtils.hasText(other.id())) {
        anotherLoan = emprestimoOutroEntityRepository.findByUuidOrThrow(other.id());
      } else {
        anotherLoan = new EmprestimoOutroEntity();
        anotherLoan.setReferenciaOrigem(order);
        anotherLoan.setFun(fun);
        anotherLoan.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
        anotherLoan.setEstado(Estado.A.name());
      }
      anotherLoan.setTiposEmprestimo(other.tipoEmprestimo());
      anotherLoan.setDataFim(other.dataTermino());
      anotherLoan.setDataInicio(other.dataEmprestimo());
      anotherLoan.setValorEmprestimo(other.valorEmprestimo());
      anotherLoan.setValorPrestacao(other.valorPrestacaoMensal());
      others.add(anotherLoan);
    }

    emprestimoOutroEntityRepository.saveAll(others);

    documentService.saveDocuments(
        request.getDocumentos(),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ANALISE_RH_PEDIDO.name()
    );
  }

  public void saveUpdateDecisaoAnaliseFinanceira(String uuid, AnaliseFinanceiroRequestDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO);
    loan.setDescCabimentacaoOrcamental(request.getCabimentacaoOrcamental());
    loan.setDescTaxaEsforco(request.getAvaliacaoTaxaEsforco());

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
    pedidoEntityRepository.save(order);

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      switch (request.getParecer()) {
        // Conforme avança de facto para a próxima etapa — estava a
        // definir-se para si própria (bug pré-existente), nunca avançava.
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_DFI.name());
        }
        case DESFAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.VALIDADO_DFI.name());
        }
        case RETIFICACAO -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    pedidoEntityRepository.save(order);
    emprestimoEntityRepository.save(loan);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          obj.setDataParecer(request.getData());
          obj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setDataParecer(request.getData());
          newObj.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });

    emprestimoEntityRepository.save(loan);
  }

  public void autorizarComissaoExecutiva(String uuid, AutorizacaoComissaoExecutivaDTO request) {

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO);

    var order = loan.getPedido();
    order.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name());

    if (request.getAction().equals(ProcessStepAction.NEXT)) {
      switch (request.getParecer()) {
        case FAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.AUTORIZADO.name());
        }
        case DESFAVORAVEL -> {
          order.setEtapa(EtapaEmprestimo.ANALISE_RH_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.NAO_AUTORIZADO.name());
        }
        case RETIFICACAO -> {
          // Retificação devolve à etapa imediatamente anterior a esta
          // (Análise Financeira), não à Análise RH.
          order.setEtapa(EtapaEmprestimo.ANALISE_FINANCEIRA_PEDIDO.name());
          loan.setEstado(StatusEmprestimo.EM_CORRECAO.name());
        }
      }
    }

    pedidoEntityRepository.save(order);

    var decisionOP = pedidoDecisaoEntityRepository.findByPedidoAndEtapaAndEstado(
        order,
        EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name(),
        Estado.A.name()
    );

    decisionOP.ifPresentOrElse(
        obj -> {
          obj.setDecisao(request.getParecer().name());
          obj.setObs(request.getObservacao());
          obj.setDataParecer(request.getData());
          obj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(obj);
        },
        () -> {
          var newObj = new PedidoDecisaoEntity();
          newObj.setPedido(order);
          newObj.setDecisao(request.getParecer().name());
          newObj.setObs(request.getObservacao());
          newObj.setDataParecer(request.getData());
          newObj.setEtapa(EtapaEmprestimo.AUTORIZAR_COMISSAO_EXECUTIVA_PEDIDO.name());
          newObj.setReferencia(ProcessType.EMPRESTIMO.name());
          newObj.setEstado(Estado.A.name());
          newObj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          newObj.setCreatedDate(request.getData().atStartOfDay());
          pedidoDecisaoEntityRepository.save(newObj);
        });
  }

  @Transactional
  public void elaborarContrato(String uuid, ElaboracaoContratoRequestDTO request) {

    var isNext = request.getAction().equals(ProcessStepAction.NEXT);

    var loan = emprestimoEntityRepository.findByUuidOrThrow(uuid);
    EmprestimoHelper.validarEtapaAtual(loan, EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO);
    if (Objects.nonNull(request.getDataInicioEmprestimo())) {
      loan.setDataInicio(request.getDataInicioEmprestimo());
      emprestimoEntityRepository.save(loan);
    }

    loan.setEstado(isNext ? StatusEmprestimo.CABIMENTADO.name() : loan.getEstado());

    emprestimoWriteService.generateSaveFinancialPlan(loan);

    var step = isNext ?
        EtapaEmprestimo.PAGAMENTO :
        EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO;

    var order = loan.getPedido();
    order.setEtapa(step.name());
    pedidoEntityRepository.save(order);

    documentService.saveDocuments(
        request.getDocumentos(),
        loan.getTiprel().getFunId(),
        loan.getUuid(),
        ReferenceName.RH_T_EMPRESTIMO + "_" + EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO.name()
    );
    emprestimoHelper.registarExecucaoEtapa(order, EtapaEmprestimo.ELABORAR_CONTRATO_PEDIDO);
  }
}
