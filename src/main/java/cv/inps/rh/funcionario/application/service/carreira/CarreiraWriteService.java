package cv.inps.rh.funcionario.application.service.carreira;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidacaoCarreiraDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.CarreiraMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CarreiraWriteService {

  private final CarreiraEntityRepository carreiraEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final CarreiraMapper carreiraMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final FuncionarioRules funcionarioRules;
  private final EntityManager entityManager;

  public void novaCarreira(String funcionarioId, DadosContratuaisReqDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    if (carreiraEntityRepository.existsByContrVinculoIdFunIdAndEstado(funcionario, Estado.P))
      throw IgrpResponseStatusException.conflict("Existe um registo de carreira por validar!");

    if (!contratoEntityRepository.existsByFunIdAndEstado(funcionario, Estado.A))
      throw IgrpResponseStatusException.conflict("Este funcionário não possui um contrato ativo");

    // Doc (Regra Geral): "O campo processar salário deve ser obrigatório"
    if (dto.getFlgProcessa() == null)
      throw IgrpResponseStatusException.badRequest("O campo 'processar salário' é obrigatório");

    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario.getUuid());
    var relacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    // Doc: "somente uma carreira pode processar ao mesmo tempo". Exclui o vínculo actual, que será
    // fechado/substituído na validação (permite progredir uma carreira que já processa).
    if (Integer.valueOf(1).equals(dto.getFlgProcessa())
        && tiposRelacionamentoEntityRepository.existsByFunIdAndEstadoAndFlgProcessaAndDataFimIsNullAndIdNot(
            funcionario, Estado.A, 1, relacionamentoAtual.getId()))
      throw IgrpResponseStatusException.conflict("Já existe um vínculo ativo com processamento salarial para este funcionário");

    // Doc "Regra Geral": máx 2 carreiras activas; e não 2 do mesmo tipo (cargo nulo vs não-nulo).
    // Conta as activas em vigor, excluindo a carreira actual que será fechada na validação.
    var carreiraAtualIdGuard = relacionamentoAtual.getCarreiraId() != null ? relacionamentoAtual.getCarreiraId().getId() : null;
    var carreirasAtivas = carreiraEntityRepository
        .findAllByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A)
        .stream().filter(c -> !Objects.equals(c.getId(), carreiraAtualIdGuard)).toList();
    if (carreirasAtivas.size() >= 2)
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter mais de duas carreiras activas");
    boolean novoCargoNulo = dto.getCargoPosicaoId() == null;
    if (carreirasAtivas.stream().anyMatch(c -> (c.getCargoId() == null) == novoCargoNulo))
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter duas carreiras activas do mesmo tipo (cargo)");

    // Novo padrão: o REGISTO não cria/troca tipo_relacionamento, não fecha o vínculo/carreira atuais
    // e não cria def de remuneração/pagamento. Grava só a nova CARREIRA (P, est_act_adm=0 — não é a
    // atual até validar) + a validação pendente. Toda a consolidação (fechar anterior, criar tiprel,
    // def rem/pag, transferência) acontece no validarCarreira quando aprovado.
    var tipoCarreira = dto.getTipoCarreira() != null ? dto.getTipoCarreira() : "CARREIRA_NOVO";

    var novaCarreira = Objects.requireNonNull(carreiraMapper.toCarreira(dto, Estado.P));
    novaCarreira.setObs("CARREIRA");
    novaCarreira.setTipoSituacao(tipoCarreira);
    novaCarreira.setContrVinculoId(contratoAtual);
    novaCarreira.setEstActAdm(0);
    carreiraEntityRepository.save(novaCarreira);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.CARREIRA.name());
    validation.setReferenciaId(novaCarreira.getId());
    validation.setReferenciaUuid(novaCarreira.getUuid());
    // tiprelId = vínculo atual, apenas contexto/leitura — NÃO é alterado no registo.
    validation.setTiprelId(relacionamentoAtual);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);
  }

  private DefinicaoRemuneracaoEntity copiarRemuneracao(DefinicaoRemuneracaoEntity original, FuncionarioEntity funcionario, java.time.LocalDate dataInicio, String obs) {
    var copia = new DefinicaoRemuneracaoEntity();
    copia.setTmId(original.getTmId());
    copia.setValor(original.getValor());
    copia.setPercentagem(original.getPercentagem());
    copia.setMoeda(original.getMoeda());
    copia.setEstado(Estado.P);
    copia.setObs(obs);
    copia.setDataInicio(dataInicio);
    copia.setDataFim(null);
    copia.setFunId(funcionario);
    copia.setUuid(UuidCreator.getTimeOrderedEpoch());
    return copia;
  }

  private DefPagamentoEntity copiarPagamento(DefPagamentoEntity original, FuncionarioEntity funcionario, java.time.LocalDate dataInicio, String obs) {
    var copia = new DefPagamentoEntity();
    copia.setTmId(original.getTmId());
    copia.setValor(original.getValor());
    copia.setPercentagem(original.getPercentagem());
    copia.setNib(original.getNib());
    copia.setNif(original.getNif());
    copia.setNmEntidade(original.getNmEntidade());
    copia.setRhbId(original.getRhbId());
    copia.setEntId(original.getEntId());
    copia.setEstado(Estado.P);
    copia.setObs(obs);
    copia.setDataInicio(dataInicio);
    copia.setDataFim(null);
    copia.setFunId(funcionario);
    copia.setUuid(UuidCreator.getTimeOrderedEpoch());
    return copia;
  }

  @NotNull
  private DefinicaoRemuneracaoEntity getSalarioDefinicaoRemuneracaoEntity(DadosContratuaisReqDTO dto, FuncionarioEntity funcionario, String obs) {
    var salario = new DefinicaoRemuneracaoEntity();
    salario.setValor(dto.getSalario());
    salario.setEstado(Estado.P);
    salario.setObs(obs);
    salario.setDataInicio(dto.getDataInicio());
    salario.setDataFim(dto.getDataFim());
    salario.setFunId(funcionario);
    salario.setUuid(UuidCreator.getTimeOrderedEpoch());
    return salario;
  }

  public void validarCarreira(String funcionarioId, ValidacaoCarreiraDTO dto) {

    var aprovado = ValidationUtil.isAprovado(dto.getValidacao());
    var dados = dto.getDados();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    // A carreira pendente pode ter DATA_FIM (data fim do contrato); não filtrar por DataFimIsNull.
    var carreira = carreiraEntityRepository.findByContrVinculoIdFunIdAndEstado(funcionario, Estado.P);

    var validation = funcionarioRules
        .getValidacaoPendenteByReferenciaUuid(carreira.getUuid(), TipoAcao.INSERT, Referencia.CARREIRA)
        .orElse(null);

    if (!aprovado) {
      // Rejeição: nada foi criado/fechado no registo, por isso o vínculo/carreira atuais mantêm-se
      // (nada a reverter). Só a carreira pendente e a validação ficam I.
      carreira.setEstado(Estado.I);
      carreira.setObs("Não Validado");
      carreiraEntityRepository.save(carreira);
      if (validation != null) {
        validation.setEstado(Estado.I);
        validation.setObs("Não Validado");
        validacaoEntityRepository.save(validation);
      }
      return;
    }

    // === Aprovado: consolidação — a mesma mecânica que antes estava no novaCarreira, agora
    // executada só quando a carreira é aprovada. ===

    // Salário automático do escalão (spec DOSSIÊ: FLG_CARREIRA=1 -> salário preenchido do escalão).
    if (dados != null && dados.getEscalaoReferenciaId() != null) {
      var escalaoSel = entityManager.find(ParamEscalaoEntity.class, dados.getEscalaoReferenciaId());
      if (escalaoSel != null && escalaoSel.getValor() != null) dados.setSalario(escalaoSel.getValor());
    }

    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario.getUuid());
    var relacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var tipoCarreira = dados != null && dados.getTipoCarreira() != null ? dados.getTipoCarreira() : carreira.getTipoSituacao();
    var obsMovimento = tipoCarreira;

    // Opção A: data efetiva = data do pedido (dados.data_inicio). CK_TIPREL_PERIODO está DISABLED.
    var dataEfetiva = dados != null && dados.getDataInicio() != null ? dados.getDataInicio() : java.time.LocalDate.now();

    // Capturar activos ANTES de fechar.
    var remuneracoesAtivas = funcionarioRules.getRemuneracoesAssociadosAtivos(relacionamentoAtual.getId());
    var pagamentosAtivos = funcionarioRules.getPagamentosDescontosAssociadosAtivos(relacionamentoAtual.getId());

    // Fechar vínculo/rem-pag/carreira anteriores.
    relacionamentoAtual.setDataFim(dataEfetiva);
    relacionamentoAtual.setEstActAdm(0);
    tiposRelacionamentoEntityRepository.save(relacionamentoAtual);

    remuneracoesAtivas.forEach(obj -> {
      obj.setDataFim(dataEfetiva);
      obj.setEstado(Estado.I);
      definicaoRemuneracaoEntityRepository.save(obj);
    });
    pagamentosAtivos.forEach(obj -> {
      obj.setDataFim(dataEfetiva);
      obj.setEstado(Estado.I);
      defPagamentoEntityRepository.save(obj);
    });

    var carreiraAtual = relacionamentoAtual.getCarreiraId();
    if (carreiraAtual != null) {
      carreiraAtual.setDataFim(dataEfetiva);
      carreiraAtual.setEstActAdm(0);
      carreiraEntityRepository.save(carreiraAtual);
    }

    // Activar a carreira pendente (passa a ser a atual).
    carreira.setEstActAdm(1);
    carreira.setEstado(Estado.A);
    carreiraEntityRepository.save(carreira);

    // Criar o novo tiprel — só agora, na validação.
    var novoRelacionamento = contratuaisEntityMapper.toRelacionamento(dados, Estado.A);
    novoRelacionamento.setObs(obsMovimento);
    novoRelacionamento.setTipoSituacao(tipoCarreira);
    novoRelacionamento.setDataInicio(dataEfetiva);
    novoRelacionamento.setDataFim(null);
    novoRelacionamento.setTiprelId(relacionamentoAtual);
    novoRelacionamento.setContrVinculoId(contratoAtual);
    novoRelacionamento.setCarreiraId(carreira);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setMobId(relacionamentoAtual.getMobId());
    novoRelacionamento.setRegimeId(relacionamentoAtual.getRegimeId());
    novoRelacionamento.setSituacLaboralId(relacionamentoAtual.getSituacLaboralId());
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setReferente("CARREIRA");
    tiposRelacionamentoEntityRepository.save(novoRelacionamento);

    // def rem/pag (salário do escalão / subsídios / encargos) — criados já em A.
    var novasRemuneracoes = new ArrayList<DefinicaoRemuneracaoEntity>();
    var novosPagamentos = new ArrayList<DefPagamentoEntity>();
    var vinculoAtualId = contratoAtual.getVinculoId() != null ? contratoAtual.getVinculoId().getId() : null;
    var escalaoAtualId = relacionamentoAtual.getCarreiraId() != null && relacionamentoAtual.getCarreiraId().getEscalaoId() != null
        ? relacionamentoAtual.getCarreiraId().getEscalaoId().getId() : null;

    Long salarioTmId = null;
    if (houveMudancaSalario(vinculoAtualId, escalaoAtualId, dados, funcionario)) {
      var movREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(vinculoAtualId, "REM").stream().findFirst().orElse(null);
      if (movREM != null) {
        salarioTmId = movREM.getTmId() != null ? movREM.getTmId().getId() : null;
        var salario = getSalarioDefinicaoRemuneracaoEntity(dados, funcionario, obsMovimento);
        salario.setTmId(movREM.getTmId());
        salario.setEstado(Estado.A);
        definicaoRemuneracaoEntityRepository.save(salario);
        novasRemuneracoes.add(salario);
      }
    }

    if (dados != null && dados.getSubsidios() != null && !dados.getSubsidios().isEmpty()) {
      for (var s : dados.getSubsidios()) {
        var obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.A);
        obj.setObs(obsMovimento);
        definicaoRemuneracaoEntityRepository.save(obj);
        novasRemuneracoes.add(obj);
      }
    } else {
      final Long finalSalarioTmId = salarioTmId;
      for (var rem : remuneracoesAtivas) {
        if (finalSalarioTmId != null && rem.getTmId() != null
            && Objects.equals(rem.getTmId().getId(), finalSalarioTmId)) continue;
        var copia = copiarRemuneracao(rem, funcionario, dataEfetiva, obsMovimento);
        copia.setEstado(Estado.A);
        definicaoRemuneracaoEntityRepository.save(copia);
        novasRemuneracoes.add(copia);
      }
    }

    if (dados != null && dados.getEncargosDescontos() != null && !dados.getEncargosDescontos().isEmpty()) {
      for (var e : dados.getEncargosDescontos()) {
        var def = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.A);
        def.setObs(obsMovimento);
        defPagamentoEntityRepository.save(def);
        novosPagamentos.add(def);
      }
    } else {
      for (var pag : pagamentosAtivos) {
        var copia = copiarPagamento(pag, funcionario, dataEfetiva, obsMovimento);
        copia.setEstado(Estado.A);
        defPagamentoEntityRepository.save(copia);
        novosPagamentos.add(copia);
      }
    }

    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(relacionamentoAtual, novoRelacionamento, novasRemuneracoes, novosPagamentos);

    if (validation != null) {
      validation.setEstado(Estado.A);
      validation.setTiprelId(novoRelacionamento);
      validacaoEntityRepository.save(validation);
    }
  }

  public void eliminarCareira(String carreiraId) {

    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));
    if (!Estado.P.equals(carreira.getEstado()))
      throw IgrpResponseStatusException.badRequest("Esta carreira não se encontra no estado pendente");

    carreira.setEstado(Estado.E);
    carreiraEntityRepository.save(carreira);

    // Com o novo padrão, a carreira pendente ainda NÃO tem tipo_relacionamento nem def de
    // remuneração/pagamento associados (só são criados na validação). Elimina-se apenas a carreira
    // e a respetiva validação pendente.
    funcionarioRules.getValidacaoPendenteByReferenciaUuid(carreira.getUuid(), TipoAcao.INSERT, Referencia.CARREIRA)
        .ifPresent(v -> {
          v.setEstado(Estado.E);
          validacaoEntityRepository.save(v);
        });
  }

  public void atualizarCarreira(String carreiraId, String funcionarioId, DadosContratuaisReqDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));

    if (!carreira.getContrVinculoId().getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Carreira não pertence a este funcionário");

    // Salário automático do escalão (spec DOSSIÊ: FLG_CARREIRA=1 -> salário preenchido do escalão).
    if (dto.getEscalaoReferenciaId() != null) {
      var escalaoSel = entityManager.find(ParamEscalaoEntity.class, dto.getEscalaoReferenciaId());
      if (escalaoSel != null && escalaoSel.getValor() != null)
        dto.setSalario(escalaoSel.getValor());
    }

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(carreira.getEstado());

    var relacionamento = tiposRelacionamentoEntityRepository.findByCarreiraId_uuid(carreira.getUuid());

    // Spec 3.5.2.3.1 (Novo/Editar, PROCESSAMENTO > 0): com processamento associado, os campos
    // carreira/cargo/data início ficam fechados; a alteração de ESCALÃO implica um novo registo
    // (INSERT) em CARREIRA + TIPOS_RELACIONAMENTO + TIPREL_REM_PAG — é a progressão/promoção,
    // que o fluxo novaCarreira já cobre (fecha o registo anterior e cria o novo pendente).
    if (relacionamento != null && relacionamento.getUltProc() != null) {
      Long escalaoAtual = carreira.getEscalaoId() != null ? carreira.getEscalaoId().getId() : null;
      boolean mudouEscalao = !Objects.equals(escalaoAtual, dto.getEscalaoReferenciaId());
      if (mudouEscalao) {
        novaCarreira(funcionarioId, dto);
        return;
      }
      throw IgrpResponseStatusException.badRequest(
          "Carreira já processada: apenas a alteração de escalão é permitida (gera um novo registo)");
    }

    boolean revalidar = !Estado.P.equals(carreira.getEstado());

    carreiraMapper.toUpdateEntity(carreira, dto);
    if (revalidar) carreira.setEstado(Estado.P);
    carreiraEntityRepository.save(carreira);

    if (relacionamento != null) {
      if (dto.getCargoPosicaoId() != null)
        relacionamento.setCargoId(ValidationUtil.ref(entityManager, ParamCargoEntity.class, dto.getCargoPosicaoId()));
      relacionamento.setSalario(dto.getSalario());
      relacionamento.setMoeda(dto.getMoeda());
      if (dto.getTipoCarreira() != null) relacionamento.setTipoSituacao(dto.getTipoCarreira());
      if (dto.getFlgProcessa() != null) relacionamento.setFlgProcessa(dto.getFlgProcessa());
      if (revalidar) relacionamento.setEstado(Estado.P);
      tiposRelacionamentoEntityRepository.save(relacionamento);
    }

    var obsAtualizar = "MOBILIDADE-" + carreira.getTipoSituacao();

    if (!CollectionUtils.isEmpty(dto.getSubsidios())) {
      var remList = dto.getSubsidios().stream()
          .map(s -> {
            DefinicaoRemuneracaoEntity obj;
            if (s.getId() == null) {
              obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
              obj.setObs(obsAtualizar);
            } else {
              obj = definicaoRemuneracaoEntityRepository.findByIdOrThrow(s.getId());
              obj.setPercentagem(s.getPercentagem());
              obj.setValor(s.getValor());
              obj.setObs(ValidationUtil.trimToNull(s.getObservacoes()));
              var tmRef = ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, s.getTipoSubsidioId());
              if (tmRef != null) obj.setTmId(tmRef);
            }
            return obj;
          })
          .toList();
      definicaoRemuneracaoEntityRepository.saveAll(remList);
    }

    if (!CollectionUtils.isEmpty(dto.getEncargosDescontos())) {
      var pagList = dto.getEncargosDescontos().stream()
          .map(e -> {
            DefPagamentoEntity obj;
            if (e.getId() == null) {
              obj = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
              obj.setObs(obsAtualizar);
            } else {
              obj = defPagamentoEntityRepository.findByIdOrThrow(e.getId());
              obj.setValor(e.getValor());
              obj.setObs(ValidationUtil.trimToNull(e.getObservacoes()));
              var tmRef2 = ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, e.getTipoEncargoId());
              if (tmRef2 != null) obj.setTmId(tmRef2);
            }
            return obj;
          })
          .toList();
      defPagamentoEntityRepository.saveAll(pagList);
    }

    // Spec: "Caso for editado, deve passar novamente para validação"
    if (revalidar && relacionamento != null) {
      var remuneracoes = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
      remuneracoes.forEach(obj -> {
        obj.setEstado(Estado.P);
        definicaoRemuneracaoEntityRepository.save(obj);
      });

      var pagamentos = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
      pagamentos.forEach(obj -> {
        obj.setEstado(Estado.P);
        defPagamentoEntityRepository.save(obj);
      });

      var validation = new ValidacaoEntity();
      validation.setTipoAccao(TipoAcao.UPDATE.name());
      validation.setReferenciaName(Referencia.CARREIRA.name());
      validation.setReferenciaId(carreira.getId());
      validation.setReferenciaUuid(carreira.getUuid());
      validation.setTiprelId(relacionamento);
      validation.setEstado(Estado.P);
      validation.setUuid(UuidCreator.getTimeOrderedEpoch());
      validation.setFunId(funcionario);
      validacaoEntityRepository.save(validation);
    }
  }

  private boolean houveMudancaSalario(Long vinculoId, Long escalaoId, DadosContratuaisReqDTO dc, FuncionarioEntity funcionario) {
    if (escalaoId != null && escalaoId > 0) {
      if (!Objects.equals(escalaoId, dc.getEscalaoReferenciaId())) {
        return true;
      }
    }
    var vinculoTipoMovimentoREMList = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipo(vinculoId, "REM");
    var tmRem = vinculoTipoMovimentoREMList.stream()
        .findFirst()
        .map(ParamVinculoMovimentoEntity::getTmId)
        .orElse(null);
    if (tmRem == null) return true;
    var renumeracoes = definicaoRemuneracaoEntityRepository
        .findByFunIdAndTmIdAndEstado(funcionario, tmRem, Estado.A);
    var renumeracao = renumeracoes.stream().findFirst().orElse(null);
    if (renumeracao != null) {
      if (!Objects.equals(renumeracao.getValor(), dc.getSalario())) {
        return true;
      }
    }
    return false;
  }
}
