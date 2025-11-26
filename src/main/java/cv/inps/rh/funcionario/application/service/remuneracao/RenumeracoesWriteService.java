package cv.inps.rh.funcionario.application.service.remuneracao;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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

  public void novoRemuneracao(String funcionarioId, NovoRemuneracaoRequestDTO request) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var remuneracao = new DefinicaoRemuneracaoEntity();
    remuneracao.setPercentagem(request.getPercentagem());
    remuneracao.setValor(request.getValor());
    remuneracao.setEstado(Estado.P);
    remuneracao.setObs(request.getObservacao());
    remuneracao.setUuid(UuidCreator.getTimeOrderedEpoch());
    remuneracao.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(Long.valueOf(request.getMovimentoId())));
    remuneracao.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    remuneracao.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    remuneracao.setFunId(funcionario);
    remuneracao = definicaoRemuneracaoEntityRepository.save(remuneracao);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.RENDIMENTO.name());
    validation.setReferenciaId(remuneracao.getId());
    validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionario));
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
    pagamento.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(Long.valueOf(request.getMovimentoId())));
    pagamento.setDataInicio(DateFormatter.stringToLocalDate(request.getDataInicio()));
    pagamento.setDataFim(DateFormatter.stringToLocalDate(request.getDataFim()));
    pagamento.setFunId(funcionario);
    pagamento.setNib(request.getNib());
    pagamento.setBanco(request.getBanco());
    pagamento.setNif(request.getNif());
    pagamento.setEntidade(request.getEntidade());
    pagamento = defPagamentoEntityRepository.save(pagamento);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.DESCONTO.name());
    validation.setReferenciaId(pagamento.getId());
    validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionario));
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }


}
