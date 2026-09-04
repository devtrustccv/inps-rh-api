package cv.inps.rh.funcionario.application.service.remuneracao;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.ValidarNovoPagamentoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarNovoRemuneracaoCommand;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RenumeracoesWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RenumeracoesWriteService.class);

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final EntityManager entityManager;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;

  public SuccessResponseDTO novoRemuneracao(String funcionarioId, NovoRemuneracaoRequestDTO request) {
    validarRemuneracaoOuPagamento(request);

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var remuneracao = new DefinicaoRemuneracaoEntity();
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setValor(request.getValor());
    // Moeda opcional: quando nao vem no pedido assume-se CVE por defeito.
    remuneracao.setMoeda(
        request.getMoeda() != null && !request.getMoeda().isBlank() ? request.getMoeda() : "CVE");
    // Caso de uso 1.8: OBS = "Novo Registo".
    remuneracao.setObs("Novo Registo");
    remuneracao.setEstado(Estado.P);
    remuneracao.setUuid(UuidCreator.getTimeOrderedEpoch());
    remuneracao.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    remuneracao.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    remuneracao.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    remuneracao.setFunId(funcionario);
    remuneracao = definicaoRemuneracaoEntityRepository.save(remuneracao);

    var assocRem = new TipoRelRemPagEntity();
    assocRem.setTiprelId(tipoRel);
    assocRem.setRemId(remuneracao);
    tipoRelRemPagEntityRepository.save(assocRem);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.RENDIMENTO.name());
    validation.setReferenciaId(remuneracao.getId());
    validation.setReferenciaUuid(remuneracao.getUuid());
    validation.setTiprelId(tipoRel);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);

    return new SuccessResponseDTO(true, remuneracao.getUuid().toString(), "Remuneração registada.", List.of());
  }

  public SuccessResponseDTO novoPagamento(String funcionarioId, NovoPagamentoRequestDTO request) {
    validarRemuneracaoOuPagamento(request);

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var pagamento = new DefPagamentoEntity();
    pagamento.setPercentagem(request.getPercentagem());
    pagamento.setValor(request.getValor());
    pagamento.setEstado(Estado.P);
    // Caso de uso 1.8: OBS = "Novo Registo".
    pagamento.setObs("Novo Registo");
    pagamento.setUuid(UuidCreator.getTimeOrderedEpoch());
    pagamento.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    pagamento.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    pagamento.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    pagamento.setFunId(funcionario);
    pagamento.setNib(ValidationUtil.sanitizeNib(request.getNib()));
    pagamento.setRhbId(ValidationUtil.ref(entityManager, BancoEntity.class, request.getBanco()));
    pagamento.setNif(request.getNif());
    pagamento.setEntId(request.getEntidade());
    pagamento = defPagamentoEntityRepository.save(pagamento);

    var assocPag = new TipoRelRemPagEntity();
    assocPag.setTiprelId(tipoRel);
    assocPag.setPagId(pagamento);
    tipoRelRemPagEntityRepository.save(assocPag);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.DESCONTO.name());
    validation.setReferenciaId(pagamento.getId());
    validation.setReferenciaUuid(pagamento.getUuid());
    validation.setTiprelId(tipoRel);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);

    return new SuccessResponseDTO(true, pagamento.getUuid().toString(), "Pagamento/desconto registado.", List.of());
  }

  public SuccessResponseDTO validarNovoRemuneracao(ValidarNovoRemuneracaoCommand command) {

    var data = command.getValidarremuneracaorequest();

    var remuneracao = definicaoRemuneracaoEntityRepository.findByUuidOrThrow(UUID.fromString(command.getRemuneracaoId()));

    // CORRIGIR (checker devolve ao maker): rendimento pendente P -> C e validação P -> C, SEM aplicar
    // payload. O maker corrige e reenvia por este mesmo endpoint com validacao=null (C -> P). Âncora =
    // remuneracao.uuid.
    if (ValidationUtil.isCorrigir(data.getValidacao())) {
      if (remuneracao.getEstado() != Estado.P
          || funcionarioRules.getValidacaoPendenteByReferenciaUuid(remuneracao.getUuid(), TipoAcao.INSERT, Referencia.RENDIMENTO).isEmpty()) {
        throw IgrpResponseStatusException.badRequest("Só é possível devolver para correção um rendimento pendente de validação.");
      }
      funcionarioRules.devolverParaCorrecao(remuneracao.getUuid(), Estado.P, Referencia.RENDIMENTO);
      remuneracao.setEstado(Estado.C);
      definicaoRemuneracaoEntityRepository.save(remuneracao);
      LOGGER.info("[CORRIGIR] RENDIMENTO devolvido para correção (remuneracao={}).", remuneracao.getUuid());
      return new SuccessResponseDTO(true, remuneracao.getUuid().toString(),
          "Rendimento devolvido para correção.", List.of());
    }

    // Guard: rendimento em correção não pode ser validado antes de reenviado pelo maker.
    if (remuneracao.getEstado() == Estado.C && data.getValidacao() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Rendimento em correção: não pode ser validado. Corrija e reenvie primeiro.");
    }

    var request = data.getDados();
    validarRemuneracaoOuPagamento(request);

    funcionarioRules.garantirEditavel(remuneracao.getEstado());
    remuneracao.setValor(request.getValor());
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setObs(ValidationUtil.trimToNull(request.getObservacao()));
    remuneracao.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    remuneracao.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    remuneracao.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));

    // Maker reenvia a correção (C -> P): edições aplicadas acima; reabre para validação.
    if (remuneracao.getEstado() == Estado.C) {
      remuneracao.setEstado(Estado.P);
      var validacaoReaberta = funcionarioRules.reabrirParaValidacao(remuneracao.getUuid(), Referencia.RENDIMENTO);
      // Auto-audit (JaVers): carimba o save da correção; baseline vem do registo (novoRemuneracao).
      try {
        cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext.set(
            validacaoReaberta.getId(), validacaoReaberta.getUuid(), "RH_T_DEF_REMUNERACOES");
        definicaoRemuneracaoEntityRepository.save(remuneracao);
      } finally {
        cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext.clear();
      }
      return new SuccessResponseDTO(true, remuneracao.getUuid().toString(),
          "Rendimento corrigido e reenviado para validação.", List.of());
    }

    if (data.getValidacao() != null) {
      if (remuneracao.getEstado() == Estado.P) {
        var novoEstado = ValidationUtil.isAprovado(data.getValidacao()) ? Estado.A : Estado.I;
        remuneracao.setEstado(novoEstado);

        var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
        var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

        funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.RENDIMENTO)
            .ifPresent(v -> {
              v.setEstado(novoEstado);
              validacaoEntityRepository.save(v);
            });
      }
    }

    definicaoRemuneracaoEntityRepository.save(remuneracao);

    return new SuccessResponseDTO(true, remuneracao.getUuid().toString(), "Remuneração actualizada.", List.of());
  }

  public SuccessResponseDTO validarNovoPagamento(ValidarNovoPagamentoCommand command) {

    var data = command.getValidarpagamentorequest();

    var pagamento = defPagamentoEntityRepository.findByUuidOrThrow(UUID.fromString(command.getPagamentoId()));

    // CORRIGIR (checker devolve ao maker): desconto pendente P -> C e validação P -> C, SEM aplicar
    // payload. O maker corrige e reenvia por este mesmo endpoint com validacao=null (C -> P). Âncora =
    // pagamento.uuid.
    if (ValidationUtil.isCorrigir(data.getValidacao())) {
      if (pagamento.getEstado() != Estado.P
          || funcionarioRules.getValidacaoPendenteByReferenciaUuid(pagamento.getUuid(), TipoAcao.INSERT, Referencia.DESCONTO).isEmpty()) {
        throw IgrpResponseStatusException.badRequest("Só é possível devolver para correção um desconto pendente de validação.");
      }
      funcionarioRules.devolverParaCorrecao(pagamento.getUuid(), Estado.P, Referencia.DESCONTO);
      pagamento.setEstado(Estado.C);
      defPagamentoEntityRepository.save(pagamento);
      LOGGER.info("[CORRIGIR] DESCONTO devolvido para correção (pagamento={}).", pagamento.getUuid());
      return new SuccessResponseDTO(true, pagamento.getUuid().toString(),
          "Desconto devolvido para correção.", List.of());
    }

    // Guard: desconto em correção não pode ser validado antes de reenviado pelo maker.
    if (pagamento.getEstado() == Estado.C && data.getValidacao() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Desconto em correção: não pode ser validado. Corrija e reenvie primeiro.");
    }

    var request = data.getDados();
    validarRemuneracaoOuPagamento(request);

    funcionarioRules.garantirEditavel(pagamento.getEstado());

    pagamento.setPercentagem(request.getPercentagem());
    pagamento.setValor(request.getValor());
    pagamento.setObs(ValidationUtil.trimToNull(request.getObservacao()));
    pagamento.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    pagamento.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    pagamento.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    pagamento.setNib(ValidationUtil.sanitizeNib(request.getNib()));
    pagamento.setRhbId(ValidationUtil.ref(entityManager, BancoEntity.class, request.getBanco()));
    pagamento.setNif(request.getNif());
    pagamento.setEntId(request.getEntidade());

    // Maker reenvia a correção (C -> P): edições aplicadas acima; reabre para validação.
    if (pagamento.getEstado() == Estado.C) {
      pagamento.setEstado(Estado.P);
      var validacaoReaberta = funcionarioRules.reabrirParaValidacao(pagamento.getUuid(), Referencia.DESCONTO);
      // Auto-audit (JaVers): carimba o save da correção; baseline vem do registo (novoPagamento).
      try {
        cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext.set(
            validacaoReaberta.getId(), validacaoReaberta.getUuid(), "RH_T_DEF_PAGAMENTOS");
        defPagamentoEntityRepository.save(pagamento);
      } finally {
        cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext.clear();
      }
      return new SuccessResponseDTO(true, pagamento.getUuid().toString(),
          "Desconto corrigido e reenviado para validação.", List.of());
    }

    if (data.getValidacao() != null) {

      if (pagamento.getEstado() == Estado.P) {
        var novoEstado = ValidationUtil.isAprovado(data.getValidacao()) ? Estado.A : Estado.I;
        pagamento.setEstado(novoEstado);

        var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
        var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

        funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.DESCONTO)
            .ifPresent(v -> {
              v.setEstado(novoEstado);
              validacaoEntityRepository.save(v);
            });
      }
    }

    defPagamentoEntityRepository.save(pagamento);

    return new SuccessResponseDTO(true, pagamento.getUuid().toString(), "Pagamento/desconto actualizado.", List.of());
  }

  private void validarRemuneracaoOuPagamento(NovoRemuneracaoRequestDTO request) {
    ValidationUtil.validateValorNaoNegativo(request.getValor());
    ValidationUtil.validatePercentagem(request.getPercentagem());
    ValidationUtil.validateIntervaloData(
        DateFormatter.stringToLocalDate(request.getDataInicio()),
        DateFormatter.stringToLocalDate(request.getDataFim()));
  }
}
