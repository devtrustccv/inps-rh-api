package cv.inps.rh.funcionario.application.service.remuneracao;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.ValidarNovoPagamentoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarNovoRemuneracaoCommand;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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

  public void novoRemuneracao(String funcionarioId, NovoRemuneracaoRequestDTO request) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var remuneracao = new DefinicaoRemuneracaoEntity();
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setValor(request.getValor());
    remuneracao.setObs(request.getObservacao());
    remuneracao.setEstado(Estado.P);
    remuneracao.setUuid(UuidCreator.getTimeOrderedEpoch());
    remuneracao.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    remuneracao.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    remuneracao.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    remuneracao.setFunId(funcionario);
    remuneracao = definicaoRemuneracaoEntityRepository.save(remuneracao);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.RENDIMENTO.name());
    validation.setReferenciaId(remuneracao.getId());
    validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid()));
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }

  public void novoPagamento(String funcionarioId, NovoPagamentoRequestDTO request) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var pagamento = new DefPagamentoEntity();
    pagamento.setPercentagem(request.getPercentagem());
    pagamento.setValor(request.getValor());
    pagamento.setEstado(Estado.P);
    pagamento.setObs(request.getObservacao());
    pagamento.setUuid(UuidCreator.getTimeOrderedEpoch());
    pagamento.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(request.getMovimentoId()));
    pagamento.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    pagamento.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    pagamento.setFunId(funcionario);
    pagamento.setNib(request.getNib());
    var banco = entityManager.getReference(BancoEntity.class, request.getBanco());
    pagamento.setRhbId(banco);
    pagamento.setNif(request.getNif());
    var entidade = entityManager.getReference(EntidadeEntity.class, request.getEntidade());
    pagamento.setNmEntidade(entidade.getNome());
    pagamento.setEntId(entidade);
    pagamento = defPagamentoEntityRepository.save(pagamento);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.DESCONTO.name());
    validation.setReferenciaId(pagamento.getId());
    validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid()));
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }

  public void validarNovoRemuneracao(ValidarNovoRemuneracaoCommand command) {

    var data = command.getValidarremuneracaorequest();
    var request = data.getDados();

    ValidationUtil.validateDecision(data.getValidacao());

    var estado = data.getValidacao().equals("S") ? Estado.A : Estado.I;

    var remuneracao = definicaoRemuneracaoEntityRepository.findByUuidOrThrow(UUID.fromString(data.getRemuneracaoId()));
    remuneracao.setValor(request.getValor());
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setObs(request.getObservacao());
    remuneracao.setEstado(estado);
    remuneracao.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(Long.valueOf(request.getMovimentoId())));
    remuneracao.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    remuneracao.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    definicaoRemuneracaoEntityRepository.save(remuneracao);

    var validation = validacaoEntityRepository.findByUuidOrThrow(UUID.fromString(data.getValidacaoId()));
    validation.setEstado(estado);
    validacaoEntityRepository.save(validation);
  }

  public void validarNovoPagamento(ValidarNovoPagamentoCommand command) {

    var data = command.getValidarpagamentorequest();
    var request = data.getDados();

    ValidationUtil.validateDecision(data.getValidacao());

    var estado = data.getValidacao().equals("S") ? Estado.A : Estado.I;

    var pagamento = defPagamentoEntityRepository.findByUuidOrThrow(UUID.fromString(data.getPagamentoId()));
    pagamento.setPercentagem(request.getPercentagem());
    pagamento.setValor(request.getValor());
    pagamento.setEstado(estado);
    pagamento.setObs(request.getObservacao());
    pagamento.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(Long.valueOf(request.getMovimentoId())));
    pagamento.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    pagamento.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    pagamento.setNib(request.getNib());
    var banco = entityManager.getReference(BancoEntity.class, request.getBanco());
    pagamento.setRhbId(banco);
    pagamento.setNif(request.getNif());
    var entidade = entityManager.getReference(EntidadeEntity.class, request.getEntidade());
    pagamento.setNmEntidade(entidade.getNome());
    pagamento.setEntId(entidade);
    defPagamentoEntityRepository.save(pagamento);

    var validation = validacaoEntityRepository.findByUuidOrThrow(UUID.fromString(data.getValidacaoId()));
    validation.setEstado(estado);
    validacaoEntityRepository.save(validation);
  }
}
