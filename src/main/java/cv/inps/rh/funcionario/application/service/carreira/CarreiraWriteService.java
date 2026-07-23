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
import java.util.List;
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

    // Doc "Regra Geral": máx 2 carreiras activas e não 2 do mesmo tipo (cargo nulo=CATEGORIA vs
    // cargo não-nulo=CARGO). "Em vigor" = estado A e ainda não terminada (data_fim nula ou futura) —
    // NÃO usar data_fim IS NULL só, pois uma carreira activa pode ter data_fim = fim do contrato.
    // Se a nova é do MESMO tipo que uma em vigor → é progressão (substitui essa na validação) e é
    // permitida; se é de tipo DIFERENTE → acumula uma 2ª carreira, só permitida se ainda não houver 2.
    // A unicidade do "processa salário" (flg_processa) deixa de ser bloqueada aqui — é garantida na
    // validação (ao aprovar uma que processa, o código tira o flag das outras).
    boolean novoCargoNulo = dto.getCargoPosicaoId() == null;
    var emVigor = carreiraEntityRepository.findEmVigorByFuncionario(funcionario, java.time.LocalDate.now());
    boolean existeMesmoTipo = emVigor.stream().anyMatch(c -> (c.getCargoId() == null) == novoCargoNulo);
    if (!existeMesmoTipo && emVigor.size() >= 2)
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter mais de duas carreiras activas");

    // Salário automático do escalão (spec DOSSIÊ: FLG_CARREIRA=1 -> salário preenchido do escalão).
    if (dto.getEscalaoReferenciaId() != null) {
      var escalaoSel = entityManager.find(ParamEscalaoEntity.class, dto.getEscalaoReferenciaId());
      if (escalaoSel != null && escalaoSel.getValor() != null) dto.setSalario(escalaoSel.getValor());
    }

    var tipoCarreira = dto.getTipoCarreira() != null ? dto.getTipoCarreira() : "CARREIRA_NOVO";
    var obsMovimento = tipoCarreira;
    var dataEfetiva = dto.getDataInicio() != null ? dto.getDataInicio() : java.time.LocalDate.now();

    // Convenção: o REGISTO cria TUDO em P (carreira + tiprel [est_act_adm=0] + def), mas NÃO toca no
    // vínculo/carreira/def atuais. Assim o pendente é completo e legível (o detalhe da validação mostra
    // tudo), não é o atual (est_act_adm=0), e a consolidação (fechar anterior, est_act_adm=1/flg,
    // despromoção) acontece só no validarCarreira (SIM). A rejeição é P->I, sem reverter nada.
    var novaCarreira = Objects.requireNonNull(carreiraMapper.toCarreira(dto, Estado.P));
    novaCarreira.setObs("CARREIRA");
    novaCarreira.setTipoSituacao(tipoCarreira);
    novaCarreira.setContrVinculoId(contratoAtual);
    novaCarreira.setEstActAdm(0);
    novaCarreira.setFlgProcessa(dto.getFlgProcessa());
    carreiraEntityRepository.save(novaCarreira);

    // Tiprel pendente (P, est_act_adm=0 — NÃO é o atual). Clona o contexto do vínculo atual.
    var novoTiprel = contratuaisEntityMapper.toRelacionamento(dto, Estado.P);
    novoTiprel.setObs(obsMovimento);
    novoTiprel.setTipoSituacao(tipoCarreira);
    novoTiprel.setDataInicio(dataEfetiva);
    novoTiprel.setDataFim(null);
    novoTiprel.setTiprelId(relacionamentoAtual);
    novoTiprel.setContrVinculoId(contratoAtual);
    novoTiprel.setCarreiraId(novaCarreira);
    novoTiprel.setFunId(funcionario);
    novoTiprel.setMobId(relacionamentoAtual.getMobId());
    novoTiprel.setRegimeId(relacionamentoAtual.getRegimeId());
    novoTiprel.setSituacLaboralId(relacionamentoAtual.getSituacLaboralId());
    novoTiprel.setEstActAdm(0);
    novoTiprel.setFlgProcessa(dto.getFlgProcessa());
    novoTiprel.setReferente("CARREIRA");
    tiposRelacionamentoEntityRepository.save(novoTiprel);

    // def (P): salário do escalão + subsídios/encargos do DTO. Numa progressão (mesmo tipo em vigor)
    // sem subsídios/encargos no DTO, copiam-se os activos desse track (para o novo herdar).
    var novasRem = new ArrayList<DefinicaoRemuneracaoEntity>();
    var novosPag = new ArrayList<DefPagamentoEntity>();
    var vinculoAtualId = contratoAtual.getVinculoId() != null ? contratoAtual.getVinculoId().getId() : null;
    var carreiraMesmoTipo = emVigor.stream().filter(c -> (c.getCargoId() == null) == novoCargoNulo).findFirst().orElse(null);
    var tiprelMesmoTipo = carreiraMesmoTipo != null
        ? tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreiraMesmoTipo.getUuid()).orElse(null) : null;
    List<DefinicaoRemuneracaoEntity> remsMesmoTipo = tiprelMesmoTipo != null
        ? funcionarioRules.getRemuneracoesAssociadosAtivos(tiprelMesmoTipo.getId()) : List.of();
    List<DefPagamentoEntity> pagsMesmoTipo = tiprelMesmoTipo != null
        ? funcionarioRules.getPagamentosDescontosAssociadosAtivos(tiprelMesmoTipo.getId()) : List.of();
    var escalaoMesmoTipoId = carreiraMesmoTipo != null && carreiraMesmoTipo.getEscalaoId() != null
        ? carreiraMesmoTipo.getEscalaoId().getId() : null;

    Long salarioTmId = null;
    boolean criarSalario = (carreiraMesmoTipo == null) || houveMudancaSalario(vinculoAtualId, escalaoMesmoTipoId, dto, funcionario);
    if (criarSalario) {
      var movREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(vinculoAtualId, "REM").stream().findFirst().orElse(null);
      if (movREM != null) {
        salarioTmId = movREM.getTmId() != null ? movREM.getTmId().getId() : null;
        var salario = getSalarioDefinicaoRemuneracaoEntity(dto, funcionario, obsMovimento);
        salario.setTmId(movREM.getTmId());
        definicaoRemuneracaoEntityRepository.save(salario);
        novasRem.add(salario);
      }
    }

    if (dto.getSubsidios() != null && !dto.getSubsidios().isEmpty()) {
      for (var s : dto.getSubsidios()) {
        var obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
        obj.setObs(obsMovimento);
        definicaoRemuneracaoEntityRepository.save(obj);
        novasRem.add(obj);
      }
    } else if (carreiraMesmoTipo != null) {
      final Long finalSalarioTmId = salarioTmId;
      for (var rem : remsMesmoTipo) {
        if (finalSalarioTmId != null && rem.getTmId() != null
            && Objects.equals(rem.getTmId().getId(), finalSalarioTmId)) continue;
        var copia = copiarRemuneracao(rem, funcionario, dataEfetiva, obsMovimento);
        definicaoRemuneracaoEntityRepository.save(copia);
        novasRem.add(copia);
      }
    }

    if (dto.getEncargosDescontos() != null && !dto.getEncargosDescontos().isEmpty()) {
      for (var e : dto.getEncargosDescontos()) {
        var def = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
        def.setObs(obsMovimento);
        defPagamentoEntityRepository.save(def);
        novosPag.add(def);
      }
    } else if (carreiraMesmoTipo != null) {
      for (var pag : pagsMesmoTipo) {
        var copia = copiarPagamento(pag, funcionario, dataEfetiva, obsMovimento);
        defPagamentoEntityRepository.save(copia);
        novosPag.add(copia);
      }
    }

    tipoRelRemPagHelper.associarLista(novoTiprel, novasRem, novosPag);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.CARREIRA.name());
    validation.setReferenciaId(novaCarreira.getId());
    validation.setReferenciaUuid(novaCarreira.getUuid());
    validation.setTiprelId(novoTiprel);
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
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    // Carreira pendente + o seu tiprel pendente (ambos criados em P no registo, est_act_adm=0).
    var carreira = carreiraEntityRepository.findByContrVinculoIdFunIdAndEstado(funcionario, Estado.P);
    var tiprelPendente = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreira.getUuid()).orElse(null);
    var validation = funcionarioRules
        .getValidacaoPendenteByReferenciaUuid(carreira.getUuid(), TipoAcao.INSERT, Referencia.CARREIRA)
        .orElse(null);

    if (!aprovado) {
      // Rejeição: carreira/tiprel/def pendentes -> I. O vínculo/carreira atuais nunca foram tocados no
      // registo, por isso NADA a reverter.
      carreira.setEstado(Estado.I);
      carreira.setObs("Não Validado");
      carreiraEntityRepository.save(carreira);
      if (tiprelPendente != null) {
        tiprelPendente.setEstado(Estado.I);
        tiprelPendente.setObs("Não Validado");
        tiposRelacionamentoEntityRepository.save(tiprelPendente);
        // Rejeitar SÓ os def desta carreira (pela associação do tiprel), não todos os fun+P.
        funcionarioRules.getRemuneracoesAssociadosPendentes(tiprelPendente.getId())
            .forEach(o -> { o.setEstado(Estado.I); definicaoRemuneracaoEntityRepository.save(o); });
        funcionarioRules.getPagamentosDescontosAssociadosPendentes(tiprelPendente.getId())
            .forEach(o -> { o.setEstado(Estado.I); defPagamentoEntityRepository.save(o); });
      }
      if (validation != null) {
        validation.setEstado(Estado.I);
        validation.setObs("Não Validado");
        validacaoEntityRepository.save(validation);
      }
      return;
    }

    // === Aprovado: ATIVAR o pendente (carreira/tiprel/def P->A) + consolidar (est_act_adm=flg,
    // despromoção, fecho do track na progressão). Nada é CRIADO aqui — foi tudo criado no registo. ===
    var dataEfetiva = tiprelPendente != null && tiprelPendente.getDataInicio() != null
        ? tiprelPendente.getDataInicio() : java.time.LocalDate.now();
    boolean novoCargoNulo = carreira.getCargoId() == null;
    Integer novoFlgProcessa = carreira.getFlgProcessa() != null ? carreira.getFlgProcessa() : 0;
    boolean novaProcessa = Integer.valueOf(1).equals(novoFlgProcessa);

    // COMPÕE sobre o ATUAL do momento: as dimensões PARTILHADAS (mob/regime/situação) e a lineage
    // (tiprelId) vêm do tiprel atual de AGORA — não do snapshot fotografado no registo. Assim uma
    // mobilidade/regime/situação que tenha validado ENTRE o registo e esta validação não se perde:
    // só o carr_id (a dimensão desta carreira) fica o novo. Ler ANTES de a progressão fechar o atual.
    var tiprelAtual = tiposRelacionamentoEntityRepository.findAtualByFuncionarioUuid(funcionario.getUuid()).orElse(null);

    // Carreiras em vigor (estado A, data_fim null ou futura), excluindo a pendente.
    var emVigor = carreiraEntityRepository.findEmVigorByFuncionario(funcionario, java.time.LocalDate.now())
        .stream().filter(c -> !Objects.equals(c.getId(), carreira.getId())).toList();
    var carreiraMesmoTipo = emVigor.stream()
        .filter(c -> (c.getCargoId() == null) == novoCargoNulo).findFirst().orElse(null);

    // PROGRESSÃO (mesmo tipo em vigor): fecha o track substituído (tiprel + carreira + rem/pag).
    if (carreiraMesmoTipo != null) {
      var tiprelSubstituido = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreiraMesmoTipo.getUuid()).orElse(null);
      if (tiprelSubstituido != null) {
        funcionarioRules.getRemuneracoesAssociadosAtivos(tiprelSubstituido.getId())
            .forEach(o -> { o.setDataFim(dataEfetiva); o.setEstado(Estado.I); definicaoRemuneracaoEntityRepository.save(o); });
        funcionarioRules.getPagamentosDescontosAssociadosAtivos(tiprelSubstituido.getId())
            .forEach(o -> { o.setDataFim(dataEfetiva); o.setEstado(Estado.I); defPagamentoEntityRepository.save(o); });
        tiprelSubstituido.setDataFim(dataEfetiva);
        tiprelSubstituido.setEstActAdm(0);
        tiprelSubstituido.setFlgProcessa(0);
        tiposRelacionamentoEntityRepository.save(tiprelSubstituido);
      }
      carreiraMesmoTipo.setDataFim(dataEfetiva);
      carreiraMesmoTipo.setEstActAdm(0);
      carreiraMesmoTipo.setFlgProcessa(0);
      carreiraEntityRepository.save(carreiraMesmoTipo);
    }

    // DOSSIÊ Caso 2: o "atual" (est_act_adm=1) É a que PROCESSA. Fallback: se a nova não processa mas
    // não há outra a processar, a nova assume o atual (nunca deixar 0 vínculos atuais).
    boolean outraProcessa = emVigor.stream()
        .filter(c -> carreiraMesmoTipo == null || !Objects.equals(c.getId(), carreiraMesmoTipo.getId()))
        .anyMatch(c -> Integer.valueOf(1).equals(c.getFlgProcessa()));
    int novoEst = (novaProcessa || !outraProcessa) ? 1 : 0;

    if (novoEst == 1) {
      // O novo assume o atual: tirar est_act_adm e flg_processa às outras em vigor. A despromovida NÃO
      // leva DATA_FIM — fica activa/parqueada (para haver genuinamente 2 carreiras activas).
      for (var c : emVigor) {
        if (carreiraMesmoTipo != null && Objects.equals(c.getId(), carreiraMesmoTipo.getId())) continue; // já fechada
        var t = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(c.getUuid()).orElse(null);
        if (Integer.valueOf(1).equals(c.getFlgProcessa())) {
          c.setFlgProcessa(0);
          carreiraEntityRepository.save(c);
        }
        if (t != null && (Integer.valueOf(1).equals(t.getFlgProcessa()) || Integer.valueOf(1).equals(t.getEstActAdm()))) {
          t.setFlgProcessa(0);
          t.setEstActAdm(0);
          tiposRelacionamentoEntityRepository.save(t);
        }
      }
    }

    // ATIVAR a carreira pendente.
    carreira.setEstActAdm(novoEst);
    carreira.setEstado(Estado.A);
    carreira.setFlgProcessa(novoFlgProcessa);
    carreiraEntityRepository.save(carreira);

    // ATIVAR o tiprel pendente (P->A) com o est_act_adm/flg finais. As dimensões PARTILHADAS e a
    // lineage vêm do atual do momento (composição — ver acima); só o carr_id fica o desta carreira.
    if (tiprelPendente != null) {
      if (tiprelAtual != null && !Objects.equals(tiprelAtual.getId(), tiprelPendente.getId())) {
        tiprelPendente.setMobId(tiprelAtual.getMobId());
        tiprelPendente.setRegimeId(tiprelAtual.getRegimeId());
        tiprelPendente.setSituacLaboralId(tiprelAtual.getSituacLaboralId());
        tiprelPendente.setTiprelId(tiprelAtual);
      }
      tiprelPendente.setEstActAdm(novoEst);
      tiprelPendente.setEstado(Estado.A);
      tiprelPendente.setFlgProcessa(novoFlgProcessa);
      tiposRelacionamentoEntityRepository.save(tiprelPendente);
    }

    // ATIVAR os def pendentes (P->A) — SÓ os desta carreira, pela ASSOCIAÇÃO do tiprel pendente
    // (TIPREL_REM_PAG). Não usar fun+estado=P: misturaria def de outros pendentes (outra carreira,
    // rendimento/desconto) que não têm coluna de carreira.
    if (tiprelPendente != null) {
      funcionarioRules.getRemuneracoesAssociadosPendentes(tiprelPendente.getId())
          .forEach(o -> { o.setEstado(Estado.A); definicaoRemuneracaoEntityRepository.save(o); });
      funcionarioRules.getPagamentosDescontosAssociadosPendentes(tiprelPendente.getId())
          .forEach(o -> { o.setEstado(Estado.A); defPagamentoEntityRepository.save(o); });
    }

    if (validation != null) {
      validation.setEstado(Estado.A);
      validation.setTiprelId(tiprelPendente);
      validacaoEntityRepository.save(validation);
    }
  }

  public void eliminarCareira(String carreiraId) {

    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));
    if (!Estado.P.equals(carreira.getEstado()))
      throw IgrpResponseStatusException.badRequest("Esta carreira não se encontra no estado pendente");

    var funcionario = carreira.getContrVinculoId() != null ? carreira.getContrVinculoId().getFunId() : null;

    carreira.setEstado(Estado.E);
    carreiraEntityRepository.save(carreira);

    // O pendente inclui tiprel + def (criados em P no registo) — também passam a E.
    var tiprelPendente = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreira.getUuid()).orElse(null);
    if (tiprelPendente != null) {
      tiprelPendente.setEstado(Estado.E);
      tiposRelacionamentoEntityRepository.save(tiprelPendente);
    }
    if (funcionario != null) {
      definicaoRemuneracaoEntityRepository.findByFunIdAndEstado(funcionario, Estado.P)
          .forEach(o -> { o.setEstado(Estado.E); definicaoRemuneracaoEntityRepository.save(o); });
      defPagamentoEntityRepository.findByFunIdAndEstado(funcionario, Estado.P)
          .forEach(o -> { o.setEstado(Estado.E); defPagamentoEntityRepository.save(o); });
    }

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

    var relacionamento = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreira.getUuid()).orElse(null);

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
