package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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
  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;


  @Transactional
  public ResponseEntity<DadosContratuaisRespDTO> validar(ValidarContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var dadosContratuais = dto.getDadosContratuais();

    validarDadosContratuaisService.validar(dadosContratuais);

    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente para o tipo de acao: INSERT e referencia: CONTRATO");
    }

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, dadosContratuais);


    var contrato = tiposRelacionamento.getContrVinculoId();
    contratoMapper.toUpdateEntity(contrato, dadosContratuais);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dadosContratuais);

    var carreira = tiposRelacionamento.getCarreiraId();
    carreiraMapper.toUpdateEntity(carreira, dadosContratuais);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dadosContratuais);


    var definicoesRemuneracoes = definicaoRemuneracaoMapper
        .syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(), dadosContratuais.getSubsidios());
    var definicoesPagamentos = defPagamentoMapper
        .syncPagamentos(funcionario.getDefinicoesPagamentos(), dadosContratuais.getEncargosDescontos());

    funcionario.setDefinicoesRenumeracoes(definicoesRemuneracoes);
    funcionario.setDefinicoesPagamentos(definicoesPagamentos);


    //atualizar renumeracao de tipo salario
    var tmSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
    var remTiprels = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(tiposRelacionamento, Estado.P);
    for (var rt : remTiprels) {
      var rem = rt.getRemId();
      if (rem != null && rem.getTmId() != null && rem.getTmId().getId().equals(tmSalario.getId())) {
        rem.setValor(dadosContratuais.getSalario());
        rem.setDataInicio(dadosContratuais.getDataInicio());
        rem.setDataFim(dadosContratuais.getDataFim());
        definicaoRemuneracaoEntityRepository.save(rem);
      }
    }

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mudarEstado(funcionario,estado);

      var linksRem = remuneracaoTiprelEntityRepository.findByTiprelIdAndEstado(tiposRelacionamento, Estado.P);
      for (var lr : linksRem) {
        lr.setEstado(estado);
        remuneracaoTiprelEntityRepository.save(lr);
      }

      var linksPag = pagTiprelEntityRepository.findByTiprelIdAndEstado(tiposRelacionamento, Estado.P);
      for (var lp : linksPag) {
        lp.setEstado(estado);
        pagTiprelEntityRepository.save(lp);
      }

    }

    var saved = funcionarioEntityRepository.save(funcionario);

    return ResponseEntity.ok(dadosContratuaisMapper.dadosContratuaisRespDTO(saved));

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity);
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contrato.setEstado(estado);
        contrato.getSituacoesLaborais().stream()
            .filter(o -> o.getEstado() == Estado.P)
            .findFirst().ifPresent(situacaoLaboralEntity -> situacaoLaboralEntity.setEstado(estado));
      }

      var mob = tr.getMobId();
      if (mob != null) mob.setEstado(estado);

      var carreira = tr.getCarreiraId();
      if (carreira != null) carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null) regime.setEstado(estado);

      var situacaoLaboral = tr.getSituacLaboralId();
      if (situacaoLaboral != null)
        situacaoLaboral.setEstado(estado);

    }

    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> "CONTRATO".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));




  }
}
