package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.InativarAtivarColaboradorCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MudarSituacaoLaboralColaboradorService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;
  private final TiposRelacionamentoEntityRepository tipoRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;

  @Transactional
  public void execute(InativarAtivarColaboradorCommand command) {

    var dto = command.getAtivarinativarcolaborador();
    if (!List.of("ATIVO", "CESSADO").contains(dto.getSituacaoLaboral()))
      throw IgrpResponseStatusException.badRequest("Situacao laboral inválida: " + dto.getSituacaoLaboral());

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).getValor();

    var isAtivo = dto.getSituacaoLaboral().equals("ATIVO");

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);
    funcionario.setEstado(isAtivo ? Estado.A : Estado.I);
    funcionarioEntityRepository.save(funcionario);

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    if (tipoRelacionamentoAtual == null)
      throw IgrpResponseStatusException.badRequest("funcionario nao possui tipo de relacionamento ativo");
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(LocalDate.now());
    tipoRelacionamentoAtual.setObs(dto.getObservacao());
    tipoRelacionamentoAtual.setMotivoSitLab(dto.getMotivo());
    tipoRelacionamentoEntityRepository.save(tipoRelacionamentoAtual);

    var pamSitLaboral = paramSitLaboralEntityRepository.findByUuidOrThrow(UUID.fromString(dto.getSituacaoLaboral()));

    var tipoRelacionamentoNovo = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);
    tipoRelacionamentoNovo.setEstActAdm(1);
    tipoRelacionamentoNovo.setDataInicio(LocalDate.now());
    tipoRelacionamentoNovo.setEstado(Estado.P);
    tipoRelacionamentoNovo.setTipoSituacao("SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setReferente("MUDANCA_SITUACAO_LAB");
    tipoRelacionamentoNovo.setSituacLaboralId(pamSitLaboral);
    tipoRelacionamentoEntityRepository.save(tipoRelacionamentoNovo);

    var contract = funcionarioRules.getContratoComMaiorVersao(funcionario);

    var sl = new SituacaoLaboralEntity();
    sl.setSituacaoLaboralId(pamSitLaboral);
    sl.setDataInicio(contract.getDataInicio());
    sl.setDataFim(contract.getDataFim());
    sl.setEstado(Estado.P);
    sl.setUuid(UuidCreator.getTimeOrderedEpoch());
    sl.setFunId(funcionario);
    sl.setContratoId(contract.getId());
    situacaoLaboralEntityRepository.save(sl);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao("UPDATE");
    validation.setReferenciaName("ESTADO_COLABORADOR");
    validation.setReferenciaId(funcionario.getId());
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);

    if (!isAtivo) {

      // TODO 22/11/2025 20:50 validate this conditions for funcionario and Estado.A and data fim NULL

      contract.setDataFim(LocalDate.now());
      contratoEntityRepository.save(contract);

      var carreira = carreiraEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
      carreira.setDataFim(LocalDate.now());
      carreiraEntityRepository.save(carreira);

      var mobilidade = mobilidadeEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
      mobilidade.setDataFim(LocalDate.now());
      mobilidadeEntityRepository.save(mobilidade);

      var defPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
      defPagamento.setDataFim(LocalDate.now());
      defPagamentoEntityRepository.save(defPagamento);

      var defRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
      defRemuneracao.setDataFim(LocalDate.now());
      definicaoRemuneracaoEntityRepository.save(defRemuneracao);
    }
  }
}
