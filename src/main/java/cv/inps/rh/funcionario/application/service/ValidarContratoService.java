package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final EntityManager entityManager;
  private final ColaboradorValidationRules colaboradorValidationRules;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ReconciliacaoMovimentoVinculoService reconciliacaoMovimentoVinculoService;

  @Transactional
  public ResponseEntity<DadosContratuaisRespDTO> validar(ValidarContratoCommand command) {

    var dto = command.getNovocontrato();

    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    var dadosContratuais = dto.getDadosContratuais();

    validarDadosContratuaisService.validar(dadosContratuais);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    if (dto.getValidar() != null && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente para o tipo de acao: INSERT e referencia: CONTRATO");
    }

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, dadosContratuais);

    var contrato = tiposRelacionamento.getContrVinculoId();
    // TODO(guard I/E temporariamente desativado): if (contrato != null) funcionarioRules.garantirEditavel(contrato.getEstado());
    contratoMapper.toUpdateEntity(contrato, dadosContratuais);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dadosContratuais);

    var carreira = tiposRelacionamento.getCarreiraId() != null ? tiposRelacionamento.getCarreiraId() : null;
    if (carreira != null) {
      carreiraMapper.toUpdateEntity(carreira, dadosContratuais);
    }

    var situacaoLaboral = tiposRelacionamento.getSituacLaboralId();
    dadosContratuaisMapper.toUpdateSituacaoLaboral(situacaoLaboral, dadosContratuais);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dadosContratuais);

    // Sincronizacao de subsidios/encargos so faz sentido para vinculos COM salario.
    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());
      colaboradorValidationRules.validarEncargosDescontosDuplicados(dadosContratuais.getEncargosDescontos());

      var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(),
          dadosContratuais.getSubsidios(), funcionario);
      var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(),
          dadosContratuais.getEncargosDescontos(), funcionario);
      funcionario.getDefinicoesRenumeracoes().addAll(definicoesRemuneracoes);
      funcionario.getDefinicoesPagamentos().addAll(definicoesPagamentos);

      // Subsidios (def_remuneracoes) seguem o periodo e a moeda do contrato (o SubsidioReqDTO
      // nao tem datas nem moeda -> senao ficariam com NOW/moeda null). Encargos ja recebem o
      // default do contrato no ValidarDadosContratuaisService.
      if (contrato != null) {
        var ci = contrato.getDataInicio();
        var cf = contrato.getDataFim();
        funcionario.getDefinicoesRenumeracoes().forEach(r -> {
          if (r != null && r.getEstado() != Estado.E) {
            r.setDataInicio(ci); r.setDataFim(cf);
            if (r.getMoeda() == null) r.setMoeda(dadosContratuais.getMoeda());
          }
        });
      }
    }

    // Transicao de estado (A/I) deve acontecer SEMPRE que ha decisao de validacao,
    // independentemente de o vinculo ter salario — conforme a especificacao ("ao
    // validar, actualizar todas as tabelas associadas para estado='A'"). Antes estava
    // dentro do bloco flgSalario==1, pelo que estagios (flg_salario=0) nunca transitavam.
    if (dto.getValidar() != null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      mudarEstado(funcionario, estado);
      if (estado == Estado.A) {
        // reconciliar os movimentos fixos do vinculo — SO na validacao positiva e SO com salario
        if (Objects.equals(1, paramVinculo.getFlgSalario())) {
          reconciliacaoMovimentoVinculoService.reconciliar(funcionario, contrato,
              dadosContratuais.getSalario(), dadosContratuais.getMoeda(),
              dadosContratuais.getDataInicio(), dadosContratuais.getDataFim());
        }
        ordemServicoWriteService.criar(funcionario, tiposRelacionamento, dto.getTipoOrdemServico());
      }
    }

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    tipoRelRemPagHelper.associarNovos(tiposRelacionamento, saved);

    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId());
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId());


    return ResponseEntity.ok(dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes));

  }


  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);
      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contratoHistoricoWriteService.transicionarEstado(contrato, estado);
      }

      var mob = tr.getMobId();
      if (mob != null)
        mob.setEstado(estado);

      var carreira = tr.getCarreiraId() != null ? tr.getCarreiraId() : null;
      if (carreira != null)
        carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null)
        regime.setEstado(estado);

      var situacaoLaboral = tr.getSituacLaboralId();
      if (situacaoLaboral != null)
        situacaoLaboral.setEstado(estado);

    }

    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.INSERT, Referencia.CONTRATO)
        .ifPresent(v -> v.setEstado(estado));

  }

}
