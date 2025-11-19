package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidarContratoService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ContratoMapper contratoMapper;

  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final FuncionarioRules funcionarioRules;


  @Transactional
  public ResponseEntity<DadosContratuaisRespDTO> validar(ValidarContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getId());

    var funcionario = funcionarioEntityRepository.findByUuid(idFunc.getValor()).orElseThrow(
        () -> IgrpResponseStatusException.notFound("Funcionario com id '%s' não encontrado".formatted(idFunc))
    );

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, dto.getDadosContratuais());

    var dc = dto.getDadosContratuais();

    var contrato = tiposRelacionamento.getContratoId();
    contratoMapper.toUpdateEntity(contrato, dc);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dc);

    var carreira = tiposRelacionamento.getCarreiraId();
    carreiraMapper.toUpdateEntity(carreira, dc);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dc);


    var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(), dc.getSubsidios());
    var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(), dc.getEncargosDescontos());

    funcionario.setDefinicoesRenumeracoes(definicoesRemuneracoes);
    funcionario.setDefinicoesPagamentos(definicoesPagamentos);

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mudarEstado(funcionario,estado);
    }

    var saved = funcionarioEntityRepository.save(funcionario);

    return ResponseEntity.ok(dadosContratuaisMapper.dadosContratuaisRespDTO(saved));

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity);
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContratoId();
      if (contrato != null) contrato.setEstado(estado);

      var mob = tr.getMobId();
      if (mob != null) mob.setEstado(estado);

      var carreira = tr.getCarreiraId();
      if (carreira != null) carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null) regime.setEstado(estado);
    }

    /*funcionarioEntity.getValidacoes().stream()
        .filter(v -> "REGISTO_COLABORADOR".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));*/

    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> "CONTRATO".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));


    funcionarioEntity.getSituacoesLaborais()
        .stream()
        .filter(o -> o.getEstado() == Estado.P)
        .findFirst().ifPresent(situacaoLaboralEntity -> situacaoLaboralEntity.setEstado(estado));
  }
}
