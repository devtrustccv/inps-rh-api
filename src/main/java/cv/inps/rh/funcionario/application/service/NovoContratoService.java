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
import cv.inps.rh.shared.application.constants.custom.TipoSalarioVinculo;
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

  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ColaboradorValidationRules colaboradorValidationRules;
  private final ContratoEntityRepository contratoEntityRepository;


  @Transactional
  public DadosContratuaisRespDTO registrar(NovoContratoCommand command) {

    var dto = command.getNovocontrato();
    var idFunc = IdentificadorUnico.from(command.getIdFuncionario());
    var dadosContratuais = dto.getDadosContratuais();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc.valor());

    // D1 (DOSSIÊ, Novo Contrato): a data de início não pode ser futura ("não maior que sysdate").
    validarDadosContratuaisService.validar(dadosContratuais,
        ValidarDadosContratuaisService.RegraDataInicio.NAO_FUTURA);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    if (funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
        Referencia.CONTRATO)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario possui validacao de contrato pendente");
    }

    // D2 (DOSSIÊ, Novo Contrato): "o botão Novo Contrato só deve ficar visível caso NÃO exista um
    // contrato ativo". Enforçado no backend via query: um contrato em vigor (estado A e ainda dentro
    // do prazo) bloqueia o novo — a alteração de um contrato em vigor faz-se pela Renovação.
    var hoje = LocalDate.now();
    if (contratoEntityRepository.existeContratoEmVigor(funcionario, Estado.A, hoje)) {
      throw IgrpResponseStatusException.badRequest(
          "O funcionário já possui um contrato ativo. Para alterar o contrato em vigor, use a Renovação de Contrato.");
    }

    boolean isPrimeiroContrato = funcionario.getContratos().isEmpty();

    if (isPrimeiroContrato) {
      return primeiroContrato(funcionario, dadosContratuais);
    }

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(tipoRelacionamentoAtual.getEstado());
    // Fecha o tiprel anterior (DOSSIÊ, Novo Contrato 2.5): est_act_adm=0 e DATA_FIM = data de início
    // do NOVO registo. Antes usava contratoAtual.getDataFim(), que podia ser null (contrato sem termo)
    // e deixava o tiprel anterior por fechar.
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(dadosContratuais.getDataInicio());

    var contratoAtual = tipoRelacionamentoAtual.getContrVinculoId();
    contratoAtual.setEstado(Estado.I);

    var contratoNovo = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contratoNovo.setFunId(funcionario);
    // D3 (DOSSIÊ): num contrato NÃO-primeiro, TIPO_SITUACAO = CONTINUIDADE. O 1º contrato usa
    // INICIO e é tratado em primeiroContrato().
    contratoNovo.setTipoSituacao("CONTINUIDADE");
    // Regra (analista): a VERSAO do CONTRATO_VINCULO e SEMPRE 1 num novo contrato.
    // A renovacao NAO incrementa aqui — o incremento de versao vive no HISTORICO
    // (RH_T_CONTRATO_HISTORICO). Como versao=1, a constraint CK_CONTRATO_VERSAO_CONTRATO
    // (VERSAO=1 AND CONTRATO_ID IS NULL) exige CONTRATO_ID nulo: o novo contrato inicia
    // uma nova cadeia, sem contrato pai. A ligacao ao vinculo anterior fica no tiprel.
    contratoNovo.setVersao(1);
    contratoNovo.setContratoId(null);
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

    // Regime: só cria novo se houver alteração ou não existir regime activo; senão reutiliza (caso de teste).
    var regime = mudaRegimeOuManter(tipoRelacionamentoAtual.getRegimeId(), dadosContratuais, funcionario);

    var paramSituacaoLaboral = ValidationUtil.ref(entityManager, ParamSituacaoEntity.class, dadosContratuais.getSituacaoLaboralId());


    var situacaoLaboral = dadosContratuaisMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P,
        "NOVO_CONTRATO", "NOVO_CONTRATO");
    situacaoLaboral.setContrVinculoId(contratoNovo);
    contratoNovo.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tiposRelacionamentoNovo = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tiposRelacionamentoNovo.setFunId(funcionario);
    // D3 (DOSSIÊ, Novo Contrato 2.5): TIPO_SITUACAO = CONTINUIDADE (não-primeiro contrato).
    tiposRelacionamentoNovo.setTipoSituacao("CONTINUIDADE");
    // Caso de uso 1.2: TIPREL_ID = id do tipo de relacionamento anterior.
    tiposRelacionamentoNovo.setTiprelId(tipoRelacionamentoAtual);
    tiposRelacionamentoNovo.setContrVinculoId(contratoNovo);
    tiposRelacionamentoNovo.setCarreiraId(carreira);
    // Melhoria 2.1: vínculo sem carreira + SIM_PCCS → escalão gravado no tiprel + salário do escalão.
    colaboradorValidationRules.aplicarEscalaoTiprelSemCarreira(
        tiposRelacionamentoNovo, carreira, paramVinculo, dadosContratuais.getEscalaoReferenciaId());
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
    if (TipoSalarioVinculo.temSalario(paramVinculo.getFlgSalario())) {
      /******************** INI RENUMERACOES ********************************/
      colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());

      if (!CollectionUtils.isEmpty(dadosContratuais.getSubsidios())) {
        var remList = dadosContratuais.getSubsidios().stream()
            .map(s -> {
              // Caso de uso 1.2: DATA_INICIO/FIM = do novo contrato; OBS = "Novo Contrato".
              var r = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
              r.setDataInicio(contratoNovo.getDataInicio());
              r.setDataFim(contratoNovo.getDataFim());
              r.setObs("Novo Contrato");
              return r;
            })
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
            .map(e -> {
              // Caso de uso 1.2: OBS = "Novo Contrato".
              var p = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
              p.setObs("Novo Contrato");
              return p;
            })
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
    // So associamos ao tiprel novo os NOVOS pendentes (subsidios/encargos do DTO) — NAO se copiam
    // as ativas do tiprel anterior. O novo contrato defere a inactivacao/derivacao do salario para
    // o validar (ver NOTA acima): copiar o salario antigo aqui deixaria associacao orfa quando o
    // reconciliar o inactivasse. O salario certo (do novo vinculo) e ligado no validar via
    // reconciliar + associarNovos. Assim o ecra de validacao (GetContratoById) ja mostra os
    // subsidios/encargos que o utilizador meteu, sem o salario antigo obsoleto.
    tipoRelRemPagHelper.associarLista(tiposRelacionamentoNovo, novasRems, novosPags);

    contratoHistoricoWriteService.registrarNovo(contratoNovo);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(contratoNovo.getId());
          validacaoEntityRepository.save(e);
        });

    // Só os def vigentes deste tiprel (estado coincide com o do tiprel) — coerente com getById.
    var estadoTiprel = tiposRelacionamentoNovo.getEstado();
    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamentoNovo.getId())
        .stream().filter(r -> r.getEstado() == estadoTiprel).toList();
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamentoNovo.getId())
        .stream().filter(p -> p.getEstado() == estadoTiprel).toList();

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamentoNovo, pagamentos, remuneracoes);
  }

  // D4 (DOSSIÊ, Novo Contrato): num contrato NÃO-primeiro encerram-se SEMPRE os registos ativos
  // (DATA_FIM IS NULL) de carreira/mobilidade/regime — pondo DATA_FIM = data de início do novo
  // contrato — e cria-se um novo registo (CONTINUIDADE). Não se reutiliza o do contrato anterior:
  // assim o novo tiprel aponta para entidades PRÓPRIAS, o que mantém a separação e permite a
  // reversão limpa na validação NÃO (reabrir os antigos, inativar os novos).
  private CarreiraEntity mudaCarreiraOuManter(
      CarreiraEntity carreiraAtual,
      DadosContratuaisReqDTO dc) {

    if (carreiraAtual != null && carreiraAtual.getDataFim() == null) {
      carreiraAtual.setDataFim(dc.getDataInicio());
    }
    var nova = carreiraMapper.toCarreira(dc, Estado.P);
    nova.setTipoSituacao("CONTINUIDADE");
    return nova;
  }

  // D4: encerra SEMPRE a mobilidade ativa (DATA_FIM = início do novo) e cria uma nova (CONTINUIDADE).
  private MobilidadeEntity mudaMobilidadeOuManter(MobilidadeEntity mobilidadeAtual, DadosContratuaisReqDTO dc,
                                                  FuncionarioEntity funcionario) {

    if (mobilidadeAtual != null && mobilidadeAtual.getDataFim() == null) {
      mobilidadeAtual.setDataFim(dc.getDataInicio());
    }

    MobilidadeEntity nova = mobilidadeMapper.toMobilidade(dc, Estado.P);
    nova.setFunId(funcionario);
    nova.setTipoSituacao("CONTINUIDADE");
    funcionario.getMobilidades().add(nova);
    return nova;
  }


  // D4: encerra SEMPRE o regime ativo (DATA_FIM = início do novo) e cria um novo (CONTINUIDADE).
  private RegimeTrabalhoEntity mudaRegimeOuManter(RegimeTrabalhoEntity regimeAtual, DadosContratuaisReqDTO dc,
                                                  FuncionarioEntity funcionario) {
    if (regimeAtual != null && regimeAtual.getDataFim() == null) {
      regimeAtual.setDataFim(dc.getDataInicio());
    }
    var nova = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    if (nova != null) {
      nova.setFunId(funcionario);
      nova.setTipoSituacao("CONTINUIDADE");
      funcionario.getRegimesTrabalhos().add(nova);
    }
    return nova;
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
    // Melhoria 2.1: vínculo sem carreira + SIM_PCCS → escalão gravado no tiprel + salário do escalão.
    colaboradorValidationRules.aplicarEscalaoTiprelSemCarreira(
        tiposRelacionamento, carreira, paramVinculo, dadosContratuais.getEscalaoReferenciaId());
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
    if (TipoSalarioVinculo.temSalario(paramVinculo.getFlgSalario())) {
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
            .map(e -> {
              // Caso de uso 1.2: OBS = "Novo Contrato".
              var p = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
              p.setObs("Novo Contrato");
              return p;
            })
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

    // Só os def vigentes deste tiprel (estado coincide com o do tiprel) — coerente com getById.
    var estadoTiprel = tiposRelacionamento.getEstado();
    var remuneracoes = funcionarioRules
        .getRemuneracoesAssociados(tiposRelacionamento.getId())
        .stream().filter(r -> r.getEstado() == estadoTiprel).toList();
    var pagamentos = funcionarioRules
        .getPagamentosDescontosAssociados(tiposRelacionamento.getId())
        .stream().filter(p -> p.getEstado() == estadoTiprel).toList();

    return dadosContratuaisMapper.dadosContratuaisRespDTO(tiposRelacionamento, pagamentos, remuneracoes);

  }
}
