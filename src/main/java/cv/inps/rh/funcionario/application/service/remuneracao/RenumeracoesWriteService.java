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
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RenumeracoesWriteService {

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final EntityManager entityManager;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;

  public void novoRemuneracao(String funcionarioId, NovoRemuneracaoRequestDTO request) {
    validarRemuneracaoOuPagamento(request);

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var remuneracao = new DefinicaoRemuneracaoEntity();
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setValor(request.getValor());
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
    validation.setTiprelId(tipoRel);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }

  public void novoPagamento(String funcionarioId, NovoPagamentoRequestDTO request) {
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
    validation.setTiprelId(tipoRel);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }

  public void validarNovoRemuneracao(ValidarNovoRemuneracaoCommand command) {

    var data = command.getValidarremuneracaorequest();
    var request = data.getDados();
    validarRemuneracaoOuPagamento(request);

    var remuneracao = definicaoRemuneracaoEntityRepository.findByUuidOrThrow(UUID.fromString(command.getRemuneracaoId()));
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(remuneracao.getEstado());
    remuneracao.setValor(request.getValor());
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setObs(ValidationUtil.trimToNull(request.getObservacao()));
    remuneracao.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    remuneracao.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    remuneracao.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));

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
  }

  public void validarNovoPagamento(ValidarNovoPagamentoCommand command) {

    var data = command.getValidarpagamentorequest();
    var request = data.getDados();
    validarRemuneracaoOuPagamento(request);

    var pagamento = defPagamentoEntityRepository.findByUuidOrThrow(UUID.fromString(command.getPagamentoId()));
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(pagamento.getEstado());

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
  }

  private void validarRemuneracaoOuPagamento(NovoRemuneracaoRequestDTO request) {
    ValidationUtil.validateValorNaoNegativo(request.getValor());
    ValidationUtil.validatePercentagem(request.getPercentagem());
    ValidationUtil.validateIntervaloData(
        DateFormatter.stringToLocalDate(request.getDataInicio()),
        DateFormatter.stringToLocalDate(request.getDataFim()));
  }
}
