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
    // TODO(guard I/E temporariamente desativado):
    // if (relacionamentoAtual.getCarreiraId() != null)
    //   funcionarioRules.garantirEditavel(relacionamentoAtual.getCarreiraId().getEstado());

    // Doc: "somente uma carreira pode processar ao mesmo tempo". Exclui o vínculo actual, que é
    // fechado/substituído nesta operação (permite progredir uma carreira que já processa).
    if (Integer.valueOf(1).equals(dto.getFlgProcessa())
        && tiposRelacionamentoEntityRepository.existsByFunIdAndEstadoAndFlgProcessaAndIdNot(
            funcionario, Estado.A, 1, relacionamentoAtual.getId()))
      throw IgrpResponseStatusException.conflict("Já existe um vínculo ativo com processamento salarial para este funcionário");

    // Doc "Regra Geral": máx 2 carreiras activas; e não 2 do mesmo tipo (cargo nulo vs não-nulo).
    // Conta as activas em vigor, excluindo a carreira actual que será fechada nesta operação.
    var carreiraAtualIdGuard = relacionamentoAtual.getCarreiraId() != null ? relacionamentoAtual.getCarreiraId().getId() : null;
    var carreirasAtivas = carreiraEntityRepository
        .findAllByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A)
        .stream().filter(c -> !Objects.equals(c.getId(), carreiraAtualIdGuard)).toList();
    if (carreirasAtivas.size() >= 2)
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter mais de duas carreiras activas");
    boolean novoCargoNulo = dto.getCargoPosicaoId() == null;
    if (carreirasAtivas.stream().anyMatch(c -> (c.getCargoId() == null) == novoCargoNulo))
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter duas carreiras activas do mesmo tipo (cargo)");

    // Capturar ativos ANTES de fechar — o helper filtra por Estado.A
    var remuneracoesAtivas = funcionarioRules.getRemuneracoesAssociadosAtivos(relacionamentoAtual.getId());
    var pagamentosAtivos = funcionarioRules.getPagamentosDescontosAssociadosAtivos(relacionamentoAtual.getId());

    // Caso de uso 1.5: anterior fecha com DATA_FIM = data do registo.
    var dataFimAnterior = java.time.LocalDate.now();
    relacionamentoAtual.setDataFim(dataFimAnterior);
    relacionamentoAtual.setEstActAdm(0);
    tiposRelacionamentoEntityRepository.save(relacionamentoAtual);

    var defRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    defRemuneracao.forEach(obj -> {
      obj.setDataFim(dataFimAnterior);
      obj.setEstado(Estado.I);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var defPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);
    defPagamento.forEach(obj -> {
      obj.setDataFim(dataFimAnterior);
      obj.setEstado(Estado.I);
      defPagamentoEntityRepository.save(obj);
    });

    var carreiraAtual = relacionamentoAtual.getCarreiraId();
    carreiraAtual.setDataFim(dataFimAnterior);
    carreiraAtual.setEstActAdm(0);
    carreiraEntityRepository.save(carreiraAtual);

    // Caso de uso 1.5: tipo situação de carreira (default CARREIRA_NOVO); OBS = tipo situação.
    var tipoCarreira = dto.getTipoCarreira() != null ? dto.getTipoCarreira() : "CARREIRA_NOVO";
    var obsMovimento = tipoCarreira;

    var novaCarreira = Objects.requireNonNull(carreiraMapper.toCarreira(dto, Estado.P));
    novaCarreira.setObs("CARREIRA");
    // Caso de uso 1.5: TIPO_SITUACAO = tipo situação de carreira (default CARREIRA_NOVO).
    novaCarreira.setTipoSituacao(tipoCarreira);
    novaCarreira.setContrVinculoId(contratoAtual);
    novaCarreira.setEstActAdm(1);
    carreiraEntityRepository.save(novaCarreira);

    var novoRelacionamento = contratuaisEntityMapper.toRelacionamento(dto, Estado.P);
    novoRelacionamento.setObs(obsMovimento);
    novoRelacionamento.setTipoSituacao(tipoCarreira);
    // Caso de uso 1.5: novo tiprel DATA_INICIO = data do registo, DATA_FIM = null, TIPREL_ID = anterior.
    novoRelacionamento.setDataInicio(java.time.LocalDate.now());
    novoRelacionamento.setDataFim(null);
    novoRelacionamento.setTiprelId(relacionamentoAtual);
    novoRelacionamento.setContrVinculoId(contratoAtual);
    novoRelacionamento.setCarreiraId(novaCarreira);
    novoRelacionamento.setFunId(funcionario);
    novoRelacionamento.setMobId(relacionamentoAtual.getMobId());
    novoRelacionamento.setRegimeId(relacionamentoAtual.getRegimeId());
    novoRelacionamento.setSituacLaboralId(relacionamentoAtual.getSituacLaboralId());
    novoRelacionamento.setEstActAdm(1);
    novoRelacionamento.setReferente("CARREIRA");
    tiposRelacionamentoEntityRepository.save(novoRelacionamento);

    var novasRemuneracoes = new ArrayList<DefinicaoRemuneracaoEntity>();
    var novosPagamentos = new ArrayList<DefPagamentoEntity>();

    var vinculoAtualId = contratoAtual.getVinculoId() != null ? contratoAtual.getVinculoId().getId() : null;
    var escalaoAtualId = relacionamentoAtual.getCarreiraId() != null ? relacionamentoAtual.getCarreiraId().getEscalaoId().getId() : null;

    // Salário: criar novo se houve mudança, guardando o tm_id para não duplicar na cópia
    Long salarioTmId = null;
    var criarNovoSalario = houveMudancaSalario(vinculoAtualId, escalaoAtualId, dto, funcionario);
    if (criarNovoSalario) {
      // A carreira NÃO muda de vínculo (campo Vínculo é read-only na ficha) — usar o vínculo
      // actual, ignorando dto.getTipoVinculoLaboralId().
      var movREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(vinculoAtualId, "REM")
          .stream().findFirst().orElse(null);
      if (movREM != null) {
        salarioTmId = movREM.getTmId() != null ? movREM.getTmId().getId() : null;
        var salario = getSalarioDefinicaoRemuneracaoEntity(dto, funcionario, obsMovimento);
        salario.setTmId(movREM.getTmId());
        definicaoRemuneracaoEntityRepository.save(salario);
        novasRemuneracoes.add(salario);
      }
    }

    // Subsídios: usar DTO se fornecido, senão copiar os ativos anteriores
    if (dto.getSubsidios() != null && !dto.getSubsidios().isEmpty()) {
      for (var s : dto.getSubsidios()) {
        var obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
        obj.setObs(obsMovimento);
        definicaoRemuneracaoEntityRepository.save(obj);
        novasRemuneracoes.add(obj);
      }
    } else {
      // Copiar rems ativos, excluindo o de salário se foi criado novo acima
      final Long finalSalarioTmId = salarioTmId;
      for (var rem : remuneracoesAtivas) {
        if (finalSalarioTmId != null && rem.getTmId() != null
            && Objects.equals(rem.getTmId().getId(), finalSalarioTmId)) continue;
        var copia = copiarRemuneracao(rem, funcionario, dto.getDataInicio(), obsMovimento);
        definicaoRemuneracaoEntityRepository.save(copia);
        novasRemuneracoes.add(copia);
      }
    }

    // Encargos: usar DTO se fornecido; senão copiar os PAG activos. A carreira NÃO muda de vínculo,
    // por isso não se derivam PAG de outro vínculo (o campo Vínculo é read-only na ficha).
    if (dto.getEncargosDescontos() != null && !dto.getEncargosDescontos().isEmpty()) {
      for (var e : dto.getEncargosDescontos()) {
        var def = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
        def.setObs(obsMovimento);
        defPagamentoEntityRepository.save(def);
        novosPagamentos.add(def);
      }
    } else {
      for (var pag : pagamentosAtivos) {
        var copia = copiarPagamento(pag, funcionario, dto.getDataInicio(), obsMovimento);
        defPagamentoEntityRepository.save(copia);
        novosPagamentos.add(copia);
      }
    }

    tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(relacionamentoAtual, novoRelacionamento, novasRemuneracoes, novosPagamentos);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.CARREIRA.name());
    validation.setReferenciaId(novaCarreira.getId());
    validation.setReferenciaUuid(novaCarreira.getUuid());
    validation.setTiprelId(novoRelacionamento);
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
    var estado = aprovado ? Estado.A : Estado.I;

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var carreira = carreiraEntityRepository.findByContrVinculoIdFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    carreira.setEstado(estado);
    if (!aprovado) carreira.setObs("Não Validado");
    carreiraEntityRepository.save(carreira);

    var relacionamento = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    relacionamento.setEstado(estado);
    if (!aprovado) relacionamento.setObs("Não Validado");
    tiposRelacionamentoEntityRepository.save(relacionamento);

    var definicoesRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    definicoesRemuneracao.forEach(obj -> {
      obj.setEstado(estado);
      if (!aprovado) obj.setObs("Não Validado");
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var definicoesPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    definicoesPagamento.forEach(obj -> {
      obj.setEstado(estado);
      if (!aprovado) obj.setObs("Não Validado");
      defPagamentoEntityRepository.save(obj);
    });

    var validation = validacaoEntityRepository.findByTiprelIdAndEstadoAndReferenciaName(relacionamento, Estado.P, "CARREIRA");
    validation.setEstado(estado);
    if (!aprovado) validation.setObs("Não Validado");
    validacaoEntityRepository.save(validation);
  }

  public void eliminarCareira(String carreiraId) {

    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));
    if (!Estado.P.equals(carreira.getEstado()))
      throw IgrpResponseStatusException.badRequest("Esta carreira não se encontra no estado pendente");

    var funcionario = Objects.requireNonNull(carreira.getContrVinculoId()).getFunId();

    carreira.setEstado(Estado.E);
    carreiraEntityRepository.save(carreira);

    var relacionamentoAtual = tiposRelacionamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    relacionamentoAtual.setEstado(Estado.E);
    tiposRelacionamentoEntityRepository.save(relacionamentoAtual);

    var defRemuneracao = definicaoRemuneracaoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    defRemuneracao.forEach(obj -> {
      obj.setEstado(Estado.E);
      definicaoRemuneracaoEntityRepository.save(obj);
    });

    var defPagamento = defPagamentoEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.P);
    defPagamento.forEach(obj -> {
      obj.setEstado(Estado.E);
      defPagamentoEntityRepository.save(obj);
    });

    var validation = validacaoEntityRepository.findByTiprelIdAndEstadoAndReferenciaName(relacionamentoAtual, Estado.P, "CARREIRA");
    validation.setEstado(Estado.E);
    validacaoEntityRepository.save(validation);
  }

  public void atualizarCarreira(String carreiraId, String funcionarioId, DadosContratuaisReqDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));

    if (!carreira.getContrVinculoId().getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Carreira não pertence a este funcionário");

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
