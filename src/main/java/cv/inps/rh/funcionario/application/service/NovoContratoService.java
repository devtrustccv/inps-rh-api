package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NovoContratoService {

  private final ContratoMapper contratoMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;

  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ValidacaoEntityRepository validacaoEntityRepository;

  private final TipoMovimentoHelper tipoMovimentoHelper;

  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final EntityManager entityManager;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;

  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ColaboradorValidationRules colaboradorValidationRules;


  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var dadosContratuais = dto.getDadosContratuais();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    validarDadosContratuaisService.validar(dadosContratuais);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario possui validacao de contrato pendente");
    }

    boolean isPrimeiroContrato = funcionario.getContratos().isEmpty();

    if (isPrimeiroContrato) {
      return primeiroContrato(funcionario, dadosContratuais);
    }

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    funcionarioRules.garantirEditavel(tipoRelacionamentoAtual.getEstado());
    tipoRelacionamentoAtual.setEstActAdm(0);

    var contratoAtual = tipoRelacionamentoAtual.getContrVinculoId();
    contratoAtual.setEstado(Estado.I);
    var fim = contratoAtual.getDataFim();
    tipoRelacionamentoAtual.setDataFim(fim);

    if (tipoRelacionamentoAtual.getRegimeId() != null && tipoRelacionamentoAtual.getRegimeId().getDataFim() == null) {
      tipoRelacionamentoAtual.getRegimeId().setDataFim(fim);
    }

    var contratoNovo = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contratoNovo.setFunId(funcionario);
    contratoNovo.setTipoSituacao("CONTINUIDADE");
    contratoNovo.setVersao(contratoAtual.getVersao() + 1);
    contratoNovo.setContratoId(contratoAtual); // contrato pai
    funcionario.getContratos().add(contratoNovo);

    //**************** INI verifica se mudou carreira e tambem se foi escolhido carreira***********/
    CarreiraEntity carreira = null;
    if (Objects.equals(1, paramVinculo.getFlgCarreira()) && dadosContratuais.getCarreiraId() != null) {

      CarreiraEntity atual = tipoRelacionamentoAtual.getCarreiraId() != null ? tipoRelacionamentoAtual.getCarreiraId() : null;
      if (atual != null) {
        carreira = mudaCarreiraOuManter(atual, dadosContratuais);
        carreira.setContrVinculoId(contratoNovo);
        contratoNovo.getCarreiras().add(carreira);
      }

    }

    //**************** FIM verifica se mudou carreira e tambem se foi escolhido carreira***********/

    var mobilidade = mudaMobilidadeOuManter(tipoRelacionamentoAtual.getMobId(), dadosContratuais,
        funcionario);

    var regime = regimeTrabalhoMapper.toRegime(dadosContratuais, Estado.P);
    if (regime != null) {
      regime.setFunId(funcionario);
      funcionario.getRegimesTrabalhos().add(regime);
    }

    var paramSituacaoLaboral = ValidationUtil.ref(entityManager, ParamSituacaoEntity.class, dadosContratuais.getSituacaoLaboralId());


    var situacaoLaboral = dadosContratuaisMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P,
        "NOVO_CONTRATO", "NOVO_CONTRATO");
    situacaoLaboral.setContrVinculoId(contratoNovo);
    contratoNovo.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tiposRelacionamentoNovo = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tiposRelacionamentoNovo.setFunId(funcionario);
    tiposRelacionamentoNovo.setContrVinculoId(contratoNovo);
    tiposRelacionamentoNovo.setCarreiraId(carreira);
    tiposRelacionamentoNovo.setRegimeId(regime);
    tiposRelacionamentoNovo.setMobId(mobilidade);
    tiposRelacionamentoNovo.setFlgProcessa(0);
    tiposRelacionamentoNovo.setEstActAdm(1);
    tiposRelacionamentoNovo.setSituacLaboralId(situacaoLaboral);
    funcionario.getTiposrelacionamentos().add(tiposRelacionamentoNovo);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiposRelacionamentoNovo);
    valid.setReferenciaUuid(contratoNovo.getUuid());
    funcionario.getValidacoes().add(valid);

    // verifica se vinculo tem salario
    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      /******************** INI RENUMERACOES ********************************/
      colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());

      if (!CollectionUtils.isEmpty(dadosContratuais.getSubsidios())) {
        var remList = dadosContratuais.getSubsidios().stream()
            .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesRenumeracoes(remList);
      }

      // NOTA: inactivacao do salario antigo + derivacao do novo (REM do vinculo) foram movidas
      // para a validacao positiva (ValidarContratoService.reconciliarMovimentosDoVinculo).
      // Aqui persistem-se apenas os EXTRAS do utilizador (subsidios do DTO).
      /******************** FIM RENUMERACOES ********************************/

      /******************** INI PAGAMENTOS DESCONTOS ********************************/
      colaboradorValidationRules.validarEncargosDescontosDuplicados(dadosContratuais.getEncargosDescontos());

      if (!CollectionUtils.isEmpty(dadosContratuais.getEncargosDescontos())) {
        var pagList = dadosContratuais.getEncargosDescontos().stream()
            .map(e -> defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesPagamentos(pagList);
      }

      // NOTA: inactivacao dos PAG do vinculo antigo + derivacao dos do novo foram movidas
      // para a validacao positiva (ValidarContratoService.reconciliarMovimentosDoVinculo).
    }
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/

    // funcionario esta MANAGED (findByUuidOrThrow na mesma tx) e NAO e novo, logo
    // repository.saveAndFlush faria merge() — criaria COPIAS dos filhos novos
    // (contratoNovo/tiposRelacionamentoNovo) e deixaria os originais transient (id null),
    // rebentando depois em transferir/registrarNovo. entityManager.flush() persiste os
    // filhos novos IN-PLACE via cascade PERSIST, atribuindo ids aos proprios objetos.
    entityManager.flush();

    List<DefinicaoRemuneracaoEntity> novasRems = funcionario.getDefinicoesRenumeracoes() != null
        ? funcionario.getDefinicoesRenumeracoes().stream().filter(r -> r.getEstado() == Estado.P).collect(Collectors.toList())
        : List.of();
    List<DefPagamentoEntity> novosPags = funcionario.getDefinicoesPagamentos() != null
        ? funcionario.getDefinicoesPagamentos().stream().filter(p -> p.getEstado() == Estado.P).collect(Collectors.toList())
        : List.of();
    // transferir corre queries (getRemuneracoesAssociadosAtivos/...) que forcam auto-flush;
    // faze-lo logo apos o saveAndFlush limpo, ANTES de registrarNovo deixar historico pendente na sessao.
    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tipoRelacionamentoAtual, tiposRelacionamentoNovo, novasRems, novosPags);

    contratoHistoricoWriteService.registrarNovo(contratoNovo);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contratoNovo.getId());
          validacaoEntityRepository.save(e);
        });

    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamentoNovo.getId());
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamentoNovo.getId());

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamentoNovo, pagamentos, remuneracoes);
  }

  private CarreiraEntity mudaCarreiraOuManter(
      CarreiraEntity carreiraAtual,
      DadosContratuaisReqDTO dc) {

    if (!houveMudancaFuncionalCarreira(carreiraAtual, dc)) {
      return carreiraAtual;
    }

    carreiraAtual.setDataFim(LocalDate.now());
    return carreiraMapper.toCarreira(dc, Estado.P);
  }

  private boolean houveMudancaSalario(Long vinculoId, Long escalaoId, DadosContratuaisReqDTO dc, FuncionarioEntity funcionario) {

    if (escalaoId != null && escalaoId > 0) {
      if (!Objects.equals(escalaoId, dc.getEscalaoReferenciaId())) {
        return true;
      }
    }

    // se nao tiver escalao procuramos pelo vinculo associado ao tipo movimento, e depois vamos procurar na remuneracao
    var vinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipo(vinculoId,
            "REM");
    if (CollectionUtils.isEmpty(vinculoTipoMovimentoREM))
      return true;

    var renumeracaoList = definicaoRemuneracaoEntityRepository
        .findByFunIdAndTmIdAndEstado(funcionario, vinculoTipoMovimentoREM.getFirst().getTmId(), Estado.A);
    if (renumeracaoList.isEmpty()) {
      return true;
    }

    var renumeracao = renumeracaoList.getFirst();
    return !Objects.equals(renumeracao.getValor(), dc.getSalario());
  }

  private boolean houveMudancaFuncionalCarreira(CarreiraEntity atual, DadosContratuaisReqDTO dc) {

    if (atual == null) {
      return true;
    }

    Long atualCargoId = atual.getCargoId() != null ? atual.getCargoId().getId() : null;
    if (!Objects.equals(atualCargoId, dc.getCargoPosicaoId())) {
      return true;
    }

    Long atualEscalaoId = atual.getEscalaoId() != null ? atual.getEscalaoId().getId() : null;
    if (!Objects.equals(atualEscalaoId, dc.getEscalaoReferenciaId())) {
      return true;
    }

    Long atualCategoriaId = atual.getCategoriaId() != null ? atual.getCategoriaId().getId() : null;
    if (!Objects.equals(atualCategoriaId, dc.getCategoriaId())) {
      return true;
    }

    Long atualCarreiraId = atual.getCarrPccsId() != null ? atual.getCarrPccsId().getId() : null;
    return !Objects.equals(atualCarreiraId, dc.getCarreiraId());

  }

  private MobilidadeEntity mudaMobilidadeOuManter(MobilidadeEntity mobilidadeAtual, DadosContratuaisReqDTO dc,
                                                  FuncionarioEntity funcionario) {

    if (!houveMudancaFuncionalMobilidade(mobilidadeAtual, dc)) {
      return mobilidadeAtual;
    }
    mobilidadeAtual.setDataFim(LocalDate.now());

    MobilidadeEntity nova = mobilidadeMapper.toMobilidade(dc, Estado.P);
    nova.setFunId(funcionario);
    funcionario.getMobilidades().add(nova);
    return nova;
  }


  private boolean houveMudancaFuncionalMobilidade(MobilidadeEntity atual, DadosContratuaisReqDTO dc) {

    if (!Objects.equals(atual.getLocalTrabId().getId(), dc.getLocalTrabalhoId())) {
      return true;
    }

    if (!Objects.equals(atual.getSecaoId() != null ? atual.getSecaoId().getId() : null, dc.getSeccaoId())) {
      return true;
    }

    if (!Objects.equals(atual.getInstidId() != null ? atual.getInstidId().getId() : null, dc.getDirecaoId())) {
      return true;
    }

    return false;
  }


  private DadosContratuaisRespDTO primeiroContrato(FuncionarioEntity funcionario, DadosContratuaisReqDTO dadosContratuais) {

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    var contrato = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contrato.setFunId(funcionario);
    contrato.setTipoSituacao("INICIO");
    contrato.setVersao(1);
    contrato.setContratoId(null);

    var regime = regimeTrabalhoMapper.toRegime(dadosContratuais, Estado.P);
    if (regime != null) {
      regime.setFunId(funcionario);
      funcionario.getRegimesTrabalhos().add(regime);
    }

    var mobilidade = mobilidadeMapper.toMobilidade(dadosContratuais, Estado.P);
    if (mobilidade != null) {
      mobilidade.setFunId(funcionario);
      funcionario.getMobilidades().add(mobilidade);
    }

    CarreiraEntity carreira = null;
    if (Objects.equals(1, paramVinculo.getFlgCarreira()) && dadosContratuais.getCarreiraId() != null) {
      carreira = carreiraMapper.toCarreira(dadosContratuais, Estado.P);
      if (carreira != null) {
        carreira.setContrVinculoId(contrato);
        contrato.getCarreiras().add(carreira);
      }
    }

    var paramSituacaoLaboral = ValidationUtil.ref(entityManager, ParamSituacaoEntity.class, dadosContratuais.getSituacaoLaboralId());


    var situacaoLaboral = dadosContratuaisMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P,
        "NOVO_CONTRATO", "NOVO_CONTRATO");
    situacaoLaboral.setContrVinculoId(contrato);
    contrato.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tiposRelacionamento = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tiposRelacionamento.setFunId(funcionario);
    tiposRelacionamento.setContrVinculoId(contrato);
    tiposRelacionamento.setCarreiraId(carreira);
    tiposRelacionamento.setRegimeId(regime);
    tiposRelacionamento.setMobId(mobilidade);
    tiposRelacionamento.setFlgProcessa(0);
    tiposRelacionamento.setEstActAdm(1);
    tiposRelacionamento.setSituacLaboralId(situacaoLaboral);
    funcionario.getTiposrelacionamentos().add(tiposRelacionamento);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.CONTRATO.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tiposRelacionamento);
    valid.setReferenciaUuid(contrato.getUuid());
    funcionario.getValidacoes().add(valid);


    // verifica se vinculo tem salario
    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      /******************** INI RENUMERACOES ********************************/
      colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());

      if (dadosContratuais.getSubsidios() != null && !dadosContratuais.getSubsidios().isEmpty()) {
        var remList = dadosContratuais.getSubsidios().stream()
            .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesRenumeracoes(remList);
      }

      // NOTA: a derivacao dos movimentos FIXOS do vinculo (REM salario + PAG) foi movida
      // para a validacao positiva (ValidarContratoService.derivarMovimentosDoVinculo).
      // Aqui persistem-se apenas os EXTRAS do utilizador (subsidios/encargos do DTO).
      /******************** FIM RENUMERACOES ********************************/

      /******************** INI PAGAMENTOS DESCONTOS ********************************/
      colaboradorValidationRules.validarEncargosDescontosDuplicados(dadosContratuais.getEncargosDescontos());

      if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
        var pagList = dadosContratuais.getEncargosDescontos().stream()
            .map(e -> defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P))
            .collect(Collectors.toList());
        funcionario.setDefinicoesPagamentos(pagList);
      }
    }


    // ver nota em registrar(): saveAndFlush faria merge() (funcionario nao e novo) e deixaria
    // os filhos novos (contrato/tiposRelacionamento) transient. flush() persiste-os in-place.
    entityManager.flush();

    contratoHistoricoWriteService.registrarNovo(contrato);

    tipoRelRemPagHelper.associarNovos(tiposRelacionamento, funcionario);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contrato.getId());
          validacaoEntityRepository.save(e);
        });

    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId());
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId());

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes);

  }
}
