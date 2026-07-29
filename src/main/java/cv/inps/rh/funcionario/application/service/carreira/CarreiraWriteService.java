package cv.inps.rh.funcionario.application.service.carreira;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.CarreiraNovoDTO;
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
  private final DomainEntityRepository domainEntityRepository;
  private final ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;

  /**
   * "Carreira processada" = igual à vista RH_V_CARREIRA.PROCESSAMENTO: existe um tiprel desta
   * carreira com registo em RH_T_PROC_FUNCIONARIOS (processada em folha). Substitui o proxy
   * ULT_PROC, que não reflecte a definição de negócio.
   */
  private boolean carreiraProcessada(CarreiraEntity carreira) {
    return carreira != null && carreira.getId() != null
        && processamentoFuncionarioRepository.existsByTiprel_CarreiraId_Id(carreira.getId());
  }

  /** Contexto do fluxo de carreira, dado pela REFERENCIA do domínio TIPO_MOV_LABORAL. */
  private enum ContextoCarreira { NOVO, EDITAR, PROG_PROMO }

  private static final String DOMINIO_TIPO_MOV = "TIPO_MOV_LABORAL";
  private static final String REF_NOVO = "CARREIRA_NOVO";
  private static final String REF_EDITAR = "CARREIRA_EDITAR";
  private static final String REF_PROG_PROMO = "CARREIRA_PROG_PROMO";

  /**
   * Resolve o contexto a partir do VALOR enviado em tipoCarreira (ex.: CARGO_NOVO, PROGRESSAO,
   * NOVO_CONTRATO): lê a REFERENCIA no domínio TIPO_MOV_LABORAL. Só a REFERENCIA importa para a
   * lógica; o VALOR guarda-se em tipo_situacao (registo do que foi feito).
   */
  private ContextoCarreira contexto(String tipoCarreira) {
    // Match por (dominio, valor) + REFERENCIA comparada DIRETO com as 3 de carreira que conhecemos.
    // O mesmo VALOR (ex.: NOVO_CONTRATO) existe noutros contextos (CONTRATO, MOBILIDADE) — por isso
    // não basta o valor; filtramos a referência pelas 3 conhecidas.
    var refs = domainEntityRepository
        .findByDominioAndValorAndEstado(DOMINIO_TIPO_MOV, tipoCarreira, Estado.A)
        .stream().map(DomainEntity::getReferencia).collect(java.util.stream.Collectors.toSet());
    if (refs.contains(REF_NOVO)) return ContextoCarreira.NOVO;
    if (refs.contains(REF_EDITAR)) return ContextoCarreira.EDITAR;
    if (refs.contains(REF_PROG_PROMO)) return ContextoCarreira.PROG_PROMO;
    throw IgrpResponseStatusException.badRequest(
        "Tipo de carreira inválido (sem referência de carreira em " + DOMINIO_TIPO_MOV + "): " + tipoCarreira);
  }

  public void novaCarreira(String funcionarioId, CarreiraNovoDTO dto) {

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

    // POST = contexto CARREIRA_NOVO. Se vier tipoCarreira, validar que a REFERENCIA é mesmo NOVO —
    // progressão/promoção e editar entram pelo PUT (atualizarCarreira), não por aqui.
    if (dto.getTipoCarreira() != null && contexto(dto.getTipoCarreira()) != ContextoCarreira.NOVO)
      throw IgrpResponseStatusException.badRequest(
          "Este endpoint regista uma carreira NOVA. Para Progressão/Promoção ou Editar use o PUT.");

    // Normaliza "sem cargo": o frontend envia cargoPosicaoId=0 para carreira tipo CATEGORIA (sem
    // cargo). Tratamos 0 como null para a classificação de tipo (cargo null=CATEGORIA vs não-nulo=
    // CARGO) ficar correta em TODO o fluxo (guard, validação, gravação) — senão 0 conta como CARGO.
    if (dto.getCargoPosicaoId() != null && dto.getCargoPosicaoId() == 0L) dto.setCargoPosicaoId(null);

    // Doc "Regra Geral" (l.4831-4835): máx 2 carreiras activas; NÃO duas do mesmo tipo (cargo
    // nulo=CATEGORIA vs cargo não-nulo=CARGO). "Em vigor" = estado A e não terminada.
    // NOTA: o guard "mesmo tipo → Progressão" está COMENTADO temporariamente — pendente de confirmação
    // com o negócio se pode haver 2 carreiras activas do MESMO tipo. Enquanto comentado, permite-se
    // registar 2ª carreira de qualquer tipo (o "só 1 a processar" é garantido pelo flip na validação).
    // Reativar após decisão. (cargo=0 já foi normalizado para null acima → classificação de tipo correta.)
    // boolean novoCargoNulo = dto.getCargoPosicaoId() == null;
    var emVigor = carreiraEntityRepository.findEmVigorByFuncionario(funcionario, java.time.LocalDate.now());
    // if (emVigor.stream().anyMatch(c -> (c.getCargoId() == null) == novoCargoNulo))
    //   throw IgrpResponseStatusException.conflict(
    //       "Já existe uma carreira activa do mesmo tipo. Para alterá-la use Progressão/Promoção.");
    if (emVigor.size() >= 2)
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter mais de duas carreiras activas");

    criarPendenteContentor(funcionario, dto, contratoAtual, relacionamentoAtual, null);
  }

  /**
   * Cria o PENDENTE (contentor): carreira P + tiprel-contentor P (clona o vínculo atual) + def P
   * (salário do escalão + subsídios/encargos do DTO) + validação P. NÃO toca no atual — a
   * consolidação (fechar anterior, est_act_adm/flg, substituição) acontece no validarCarreira.
   *
   * <p>Se {@code fonte} != null (Progressão/Promoção), os subsídios/encargos não fornecidos no DTO
   * são HERDADOS dos activos da carreira-fonte. Se {@code fonte} == null (Novo), não copia nada.</p>
   */
  private void criarPendenteContentor(FuncionarioEntity funcionario, CarreiraNovoDTO dto,
      ContratoEntity contratoAtual, TiposRelacionamentoEntity relacionamentoAtual, CarreiraEntity fonte) {

    // Salário automático do escalão (spec DOSSIÊ: FLG_CARREIRA=1 -> salário preenchido do escalão).
    if (dto.getEscalaoReferenciaId() != null) {
      var escalaoSel = entityManager.find(ParamEscalaoEntity.class, dto.getEscalaoReferenciaId());
      if (escalaoSel != null && escalaoSel.getValor() != null) dto.setSalario(escalaoSel.getValor());
    }

    var tipoCarreira = dto.getTipoCarreira() != null ? dto.getTipoCarreira() : "CARREIRA_NOVO";
    var obsMovimento = tipoCarreira;
    var dataEfetiva = dto.getDataInicio() != null ? dto.getDataInicio() : java.time.LocalDate.now();

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

    var novasRem = new ArrayList<DefinicaoRemuneracaoEntity>();
    var novosPag = new ArrayList<DefPagamentoEntity>();
    var vinculoAtualId = contratoAtual.getVinculoId() != null ? contratoAtual.getVinculoId().getId() : null;
    // Fixos do vinculo (salario REM + INPS/IUR/Valor Liquido PAG): NAO se recriam a partir do DTO.
    // O getById devolve-os na lista de subsidios/encargos (para o utilizador os ver) e o frontend
    // reenvia-os; sem este skip, cada progressao/edicao duplicava o salario. O salario e derivado do
    // escalao (acima) e os fixos PAG pelo reconciliar/associacao — nunca pelo DTO.
    var tmsFixosRem = tmsFixosDoVinculo(vinculoAtualId, "REM");
    var tmsFixosPag = tmsFixosDoVinculo(vinculoAtualId, "PAG");

    // Salario: SEMPRE registo novo (valor do escalao). Na progressao o salario antigo e fechado no
    // validar; os subsidios/descontos existentes NAO se copiam — RE-ASSOCIAM-se ao novo tiprel no
    // validar (doc 29/07: TIPREL_REM_PAG "pega todos os registos do tiprel anterior").
    var movREM = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipo(vinculoAtualId, "REM").stream().findFirst().orElse(null);
    if (movREM != null) {
      var salario = getSalarioDefinicaoRemuneracaoEntity(dto, funcionario, obsMovimento);
      salario.setTmId(movREM.getTmId());
      definicaoRemuneracaoEntityRepository.save(salario);
      novasRem.add(salario);
    }

    // Subsidios/encargos: cria SO os NOVOS (sem id) do DTO — os manuais acrescentados de raiz — com a
    // DATA_INICIO efetiva. Na progressao, os ecoados do getById (com id) NAO se recriam aqui: sao
    // re-associados no validar (mesmas linhas). Em carreira nova (fonte == null) nao ha o que
    // re-associar, logo cria todos os do DTO.
    if (dto.getSubsidios() != null) {
      for (var s : dto.getSubsidios()) {
        if (s.getTipoSubsidioId() != null && tmsFixosRem.contains(s.getTipoSubsidioId())) continue;
        if (fonte != null && s.getId() != null) continue; // existente → re-associa no validar
        var obj = definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, funcionario, Estado.P);
        obj.setObs(obsMovimento);
        obj.setDataInicio(dataEfetiva);
        obj.setDataFim(null);
        definicaoRemuneracaoEntityRepository.save(obj);
        novasRem.add(obj);
      }
    }

    if (dto.getEncargosDescontos() != null) {
      for (var e : dto.getEncargosDescontos()) {
        if (e.getTipoEncargoId() != null && tmsFixosPag.contains(e.getTipoEncargoId())) continue;
        if (fonte != null && e.getId() != null) continue; // existente → re-associa no validar
        var def = defPagamentoMapper.toDefPagamento(e, funcionario, Estado.P);
        def.setObs(obsMovimento);
        def.setDataInicio(dataEfetiva);
        def.setDataFim(null);
        defPagamentoEntityRepository.save(def);
        novosPag.add(def);
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

  /**
   * Progressão/Promoção (CARREIRA_PROG_PROMO, via PUT {carreiraId}): cria um pendente SOBRE a
   * carreira-fonte, herdando os def activos dela; na validação, o {@code validarCarreira} fecha a
   * fonte (mesma-tipo em vigor) e activa a nova.
   */
  private void progredirCarreira(FuncionarioEntity funcionario, CarreiraEntity fonte, CarreiraNovoDTO dto) {
    if (carreiraEntityRepository.existsByContrVinculoIdFunIdAndEstado(funcionario, Estado.P))
      throw IgrpResponseStatusException.conflict("Existe um registo de carreira por validar!");
    if (dto.getFlgProcessa() == null)
      throw IgrpResponseStatusException.badRequest("O campo 'processar salário' é obrigatório");
    var contratoAtual = funcionarioRules.getContratoComMaiorVersao(funcionario.getUuid());
    var relacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    criarPendenteContentor(funcionario, dto, contratoAtual, relacionamentoAtual, fonte);
  }

  /**
   * Editar carreira processada — FLG_PROCESSA 0→1 (doc l.4869-4891). IMEDIATO (não vai a validação):
   * cria um NOVO tiprel clonando o último vínculo (mob/regime/situação), EXCEPTO carreira_id que fica
   * ESTA carreira; EST_ACT_ADM=1, FLG=1. Copia os def activos DESTA carreira para o novo (via
   * TIPREL_REM_PAG). Despromove o atual anterior (est=0, flg=0) — fica parqueado, sem data_fim.
   */
  private void marcarParaProcessar(FuncionarioEntity funcionario, CarreiraEntity carreira, CarreiraNovoDTO dto) {
    var atual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var dataEfetiva = dto.getDataInicio() != null ? dto.getDataInicio() : java.time.LocalDate.now();
    var tiprelDestaCarreira = tiposRelacionamentoEntityRepository
        .findFirstByCarreiraId_UuidOrderByIdDesc(carreira.getUuid()).orElse(null);

    // Novo tiprel: clona o atual (partilhadas), mas carreira_id/cargo/salário = ESTA carreira.
    var novoTiprel = contratuaisEntityMapper.clone(atual);
    novoTiprel.setCarreiraId(carreira);
    novoTiprel.setCargoId(carreira.getCargoId());
    novoTiprel.setSalario(carreira.getSalario());
    novoTiprel.setTipoSituacao(carreira.getTipoSituacao());
    novoTiprel.setEstActAdm(1);
    novoTiprel.setFlgProcessa(1);
    novoTiprel.setEstado(Estado.A);
    novoTiprel.setDataInicio(dataEfetiva);
    novoTiprel.setDataFim(null);
    novoTiprel.setTiprelId(atual);
    novoTiprel.setReferente("CARREIRA");
    tiposRelacionamentoEntityRepository.save(novoTiprel);

    // TIPREL_REM_PAG: os def a copiar são os DESTA carreira (não de outra) — doc "Nota".
    if (tiprelDestaCarreira != null)
      tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tiprelDestaCarreira, novoTiprel, List.of(), List.of());

    // Despromove o atual anterior (a que processava): est=0, flg=0, sem data_fim (fica parqueada,
    // para continuar a haver 2 carreiras activas).
    if (atual != null && !Objects.equals(atual.getId(), novoTiprel.getId())) {
      atual.setEstActAdm(0);
      atual.setFlgProcessa(0);
      tiposRelacionamentoEntityRepository.save(atual);
      var carreiraAtual = atual.getCarreiraId();
      if (carreiraAtual != null && !Objects.equals(carreiraAtual.getId(), carreira.getId())) {
        carreiraAtual.setFlgProcessa(0);
        carreiraEntityRepository.save(carreiraAtual);
      }
    }

    carreira.setFlgProcessa(1);
    carreira.setEstActAdm(1);
    carreira.setDataFim(null);
    carreiraEntityRepository.save(carreira);
  }

  /**
   * Editar carreira processada — FLG_PROCESSA 1→0 (doc l.4894-4903). IMEDIATO: a carreira deixa de
   * processar — FLG=0, DATA_FIM obrigatória, e o tiprel fecha (EST_ACT_ADM=0 + DATA_FIM). O DOSSIÊ
   * NÃO exige que outra carreira passe a processar (só "nunca duas a processar"); logo é permitido
   * fechar a última que processa (ex.: colaborador a sair).
   */
  private void desmarcarProcessar(CarreiraEntity carreira,
      TiposRelacionamentoEntity relacionamento, CarreiraNovoDTO dto) {
    if (dto.getDataFim() == null)
      throw IgrpResponseStatusException.badRequest("Ao desmarcar 'processar salário', a Data Fim é obrigatória.");

    carreira.setFlgProcessa(0);
    carreira.setEstActAdm(0);
    carreira.setDataFim(dto.getDataFim());
    carreiraEntityRepository.save(carreira);

    relacionamento.setFlgProcessa(0);
    relacionamento.setEstActAdm(0);
    relacionamento.setDataFim(dto.getDataFim());
    tiposRelacionamentoEntityRepository.save(relacionamento);
  }

  /** Tms dos movimentos FIXOS do vinculo (REM=salario; PAG=INPS/IUR/Valor Liquido). Usado para
   *  NAO recriar/duplicar fixos a partir da lista de subsidios/encargos do DTO (que o getById
   *  devolve para visualizacao e o frontend reenvia). Conjunto vazio se o vinculo for nulo. */
  private java.util.Set<Long> tmsFixosDoVinculo(Long vinculoId, String tipo) {
    if (vinculoId == null) return java.util.Set.of();
    return paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(vinculoId, tipo).stream()
        .filter(m -> m.getTmId() != null)
        .map(m -> m.getTmId().getId())
        .collect(java.util.stream.Collectors.toSet());
  }

  /** Tm do movimento de SALÁRIO do vínculo do contrato (REM). Usado na progressão para identificar
   *  o vencimento antigo a fechar (os subsídios/descontos re-associam-se; só o salário é substituído). */
  private Long salarioTmIdDoContrato(ContratoEntity contrato) {
    Long vinculoId = contrato != null && contrato.getVinculoId() != null ? contrato.getVinculoId().getId() : null;
    if (vinculoId == null) return null;
    var movREM = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipo(vinculoId, "REM").stream().findFirst().orElse(null);
    return movREM != null && movREM.getTmId() != null ? movREM.getTmId().getId() : null;
  }

  @NotNull
  private DefinicaoRemuneracaoEntity getSalarioDefinicaoRemuneracaoEntity(CarreiraNovoDTO dto, FuncionarioEntity funcionario, String obs) {
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

    // PROGRESSÃO (mesmo tipo em vigor): substitui o track. Doc 29/07: SÓ o VENCIMENTO é substituído
    // (novo salário do escalão) → fecha-se; os subsídios/descontos RE-ASSOCIAM ao novo tiprel
    // (mesmas linhas, via transferir — como renovação/mobilidade: "pega todos os registos do tiprel
    // anterior"). Não se fecham nem se copiam os subsídios. Fecha-se depois o tiprel/carreira antigos.
    if (carreiraMesmoTipo != null) {
      var tiprelSubstituido = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreiraMesmoTipo.getUuid()).orElse(null);
      if (tiprelSubstituido != null) {
        // 1. Fecha SÓ o vencimento antigo (o def REM cujo tm é o salário do vínculo).
        Long salarioTmId = salarioTmIdDoContrato(carreira.getContrVinculoId());
        funcionarioRules.getRemuneracoesAssociadosAtivos(tiprelSubstituido.getId()).stream()
            .filter(r -> salarioTmId != null && r.getTmId() != null && Objects.equals(r.getTmId().getId(), salarioTmId))
            .forEach(o -> { o.setDataFim(dataEfetiva); o.setEstado(Estado.I); definicaoRemuneracaoEntityRepository.save(o); });
        // 2. Re-associa os restantes ativos (subsídios + descontos) ao novo tiprel — mesmas linhas,
        //    datas intactas (o vencimento já foi fechado acima, logo não transita).
        if (tiprelPendente != null)
          tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tiprelSubstituido, tiprelPendente, List.of(), List.of());
        // 3. Fecha o tiprel antigo (est_act_adm=0, I). Os def re-associados ficam ativos.
        tiprelSubstituido.setDataFim(dataEfetiva);
        tiprelSubstituido.setEstActAdm(0);
        tiprelSubstituido.setFlgProcessa(0);
        tiprelSubstituido.setEstado(Estado.I);
        tiposRelacionamentoEntityRepository.save(tiprelSubstituido);
      }
      carreiraMesmoTipo.setDataFim(dataEfetiva);
      carreiraMesmoTipo.setEstActAdm(0);
      carreiraMesmoTipo.setFlgProcessa(0);
      carreiraMesmoTipo.setEstado(Estado.I);
      carreiraEntityRepository.save(carreiraMesmoTipo);
    }

    // DOSSIÊ Caso 2: o "atual" (est_act_adm=1) É a que PROCESSA. Fallback: se a nova não processa mas
    // não há outra a processar, a nova assume o atual (nunca deixar 0 vínculos atuais).
    boolean outraProcessa = emVigor.stream()
        .filter(c -> carreiraMesmoTipo == null || !Objects.equals(c.getId(), carreiraMesmoTipo.getId()))
        .anyMatch(c -> Integer.valueOf(1).equals(c.getFlgProcessa()));
    int novoEst = (novaProcessa || !outraProcessa) ? 1 : 0;
    // Invariante (doc): o atual (est_act_adm=1) É a que processa. Acoplar flg ao est — evita o caso
    // fallback em que a nova assumia o atual (est=1) mas ficava com flg=0 (ninguém a processar).
    novoFlgProcessa = novoEst;

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

    carreira.setEstado(Estado.E);
    carreiraEntityRepository.save(carreira);

    // O pendente inclui tiprel + def (criados em P no registo) — também passam a E. Os def SÓ desta
    // carreira, pela ASSOCIAÇÃO do tiprel (não fun+estado, que misturaria outros pendentes).
    var tiprelPendente = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreira.getUuid()).orElse(null);
    if (tiprelPendente != null) {
      tiprelPendente.setEstado(Estado.E);
      tiposRelacionamentoEntityRepository.save(tiprelPendente);
      funcionarioRules.getRemuneracoesAssociadosPendentes(tiprelPendente.getId())
          .forEach(o -> { o.setEstado(Estado.E); definicaoRemuneracaoEntityRepository.save(o); });
      funcionarioRules.getPagamentosDescontosAssociadosPendentes(tiprelPendente.getId())
          .forEach(o -> { o.setEstado(Estado.E); defPagamentoEntityRepository.save(o); });
    }

    funcionarioRules.getValidacaoPendenteByReferenciaUuid(carreira.getUuid(), TipoAcao.INSERT, Referencia.CARREIRA)
        .ifPresent(v -> {
          v.setEstado(Estado.E);
          validacaoEntityRepository.save(v);
        });
  }

  public void atualizarCarreira(String carreiraId, String funcionarioId, CarreiraNovoDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));

    if (!carreira.getContrVinculoId().getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Carreira não pertence a este funcionário");

    // "Sem cargo" (CATEGORIA): o frontend envia cargoPosicaoId=0. Tratar 0 como null para a
    // classificação de tipo (mudouChave) e a gravação do cargo ficarem corretas.
    if (dto.getCargoPosicaoId() != null && dto.getCargoPosicaoId() == 0L) dto.setCargoPosicaoId(null);

    // Salário automático do escalão (spec DOSSIÊ: FLG_CARREIRA=1 -> salário preenchido do escalão).
    if (dto.getEscalaoReferenciaId() != null) {
      var escalaoSel = entityManager.find(ParamEscalaoEntity.class, dto.getEscalaoReferenciaId());
      if (escalaoSel != null && escalaoSel.getValor() != null)
        dto.setSalario(escalaoSel.getValor());
    }

    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(carreira.getEstado());

    var relacionamento = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreira.getUuid()).orElse(null);

    // Roteamento (doc): Progressão/Promoção (CARREIRA_PROG_PROMO) cria um NOVO pendente SOBRE esta
    // carreira (herda os def e substitui na validação). Editar (CARREIRA_EDITAR) altera in place e
    // revalida só se mudar CARGO/CARR_PCCS/ESCALÃO. Sem tipoCarreira, mantém-se EDITAR.
    if (dto.getTipoCarreira() != null && contexto(dto.getTipoCarreira()) == ContextoCarreira.PROG_PROMO) {
      progredirCarreira(funcionario, carreira, dto);
      return;
    }

    // Editar de carreira JÁ processada (PROCESSAMENTO > 0, doc l.4851-4905): só Escalão, Data Fim e
    // Processa Salário são editáveis. Escalão → progressão (novo INSERT). Os flips de flg e a Data
    // Fim são IMEDIATOS (não vão a validação). "Processada" segue a vista RH_V_CARREIRA (existe
    // registo em RH_T_PROC_FUNCIONARIOS para um tiprel desta carreira).
    if (carreiraProcessada(carreira)) {
      Long escalaoAtual = carreira.getEscalaoId() != null ? carreira.getEscalaoId().getId() : null;
      if (!Objects.equals(escalaoAtual, dto.getEscalaoReferenciaId())) {
        progredirCarreira(funcionario, carreira, dto);
        return;
      }
      boolean eraProcessa = Integer.valueOf(1).equals(carreira.getFlgProcessa());
      boolean passaProcessa = Integer.valueOf(1).equals(dto.getFlgProcessa());
      if (!eraProcessa && passaProcessa) {
        marcarParaProcessar(funcionario, carreira, dto);        // 0->1
      } else if (eraProcessa && !passaProcessa) {
        desmarcarProcessar(carreira, relacionamento, dto);   // 1->0
      } else if (dto.getDataFim() != null && !Objects.equals(carreira.getDataFim(), dto.getDataFim())) {
        // Só Data Fim (fechar): actualização em RH_T_CARREIRA (doc l.4861-4863).
        carreira.setDataFim(dto.getDataFim());
        carreiraEntityRepository.save(carreira);
      }
      return;
    }

    // Doc (caso de uso l.462-474): editar só volta a validação se mudar CARGO_ID / CARR_PCCS_ID /
    // ESCALAO_ID; e apenas se estiver validada (se já está P, mantém-se pendente). Outros campos
    // (data fim, subsídios/encargos) fazem UPDATE in place sem nova validação. Calcular ANTES do
    // toUpdateEntity, que sobrepõe os campos da carreira.
    Long escAtual = carreira.getEscalaoId() != null ? carreira.getEscalaoId().getId() : null;
    Long cargoAtual = carreira.getCargoId() != null ? carreira.getCargoId().getId() : null;
    Long carrPccsAtual = carreira.getCarrPccsId() != null ? carreira.getCarrPccsId().getId() : null;
    boolean mudouChave = !Objects.equals(escAtual, dto.getEscalaoReferenciaId())
        || !Objects.equals(cargoAtual, dto.getCargoPosicaoId())
        || !Objects.equals(carrPccsAtual, dto.getCarreiraId());
    boolean revalidar = mudouChave && !Estado.P.equals(carreira.getEstado());

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

    // Doc (RH_T_DEF_REMUNERACOES, "Atualização Carreira"): quando o ESCALÃO muda, "faz um novo registo
    // de salário". O salário (REM fixo) é saltado no update de subsídios abaixo, por isso trata-se aqui:
    // fecha o salário antigo (I) e cria um novo com o valor do novo escalão, associado ao tiprel (P,
    // acompanha a revalidação). Carreira/tiprel continuam UPDATE in place (Caso 1). Só age quando há um
    // salário ATIVO no tiprel (edição de carreira já validada); num pendente ainda por validar, não.
    boolean mudouEscalao = !Objects.equals(escAtual, dto.getEscalaoReferenciaId());
    if (mudouEscalao && relacionamento != null) {
      Long salarioTmId = salarioTmIdDoContrato(carreira.getContrVinculoId());
      var salarioAntigo = salarioTmId == null ? null :
          funcionarioRules.getRemuneracoesAssociadosAtivos(relacionamento.getId()).stream()
              .filter(r -> r.getTmId() != null && Objects.equals(r.getTmId().getId(), salarioTmId))
              .findFirst().orElse(null);
      if (salarioAntigo != null) {
        var dataEfetiva = dto.getDataInicio() != null ? dto.getDataInicio() : java.time.LocalDate.now();
        salarioAntigo.setDataFim(dataEfetiva);
        salarioAntigo.setEstado(Estado.I);
        definicaoRemuneracaoEntityRepository.save(salarioAntigo);
        var salarioNovo = getSalarioDefinicaoRemuneracaoEntity(dto, funcionario, obsAtualizar);
        salarioNovo.setDataInicio(dataEfetiva);
        salarioNovo.setDataFim(null);
        salarioNovo.setTmId(salarioAntigo.getTmId());
        definicaoRemuneracaoEntityRepository.save(salarioNovo);
        tipoRelRemPagHelper.associarLista(relacionamento, List.of(salarioNovo), List.of());
      }
    }

    // Fixos do vinculo (salario/INPS/IUR/Valor Liquido): reenviados pelo getById, NAO se recriam nem
    // editam pela lista de subsidios/encargos (geridos pelo escalao/reconciliar). Skip = evita duplicar.
    var vinculoEditId = carreira.getContrVinculoId() != null && carreira.getContrVinculoId().getVinculoId() != null
        ? carreira.getContrVinculoId().getVinculoId().getId() : null;
    var tmsFixosRemEdit = tmsFixosDoVinculo(vinculoEditId, "REM");
    var tmsFixosPagEdit = tmsFixosDoVinculo(vinculoEditId, "PAG");

    if (!CollectionUtils.isEmpty(dto.getSubsidios())) {
      var remList = dto.getSubsidios().stream()
          .filter(s -> s.getTipoSubsidioId() == null || !tmsFixosRemEdit.contains(s.getTipoSubsidioId()))
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
          .filter(e -> e.getTipoEncargoId() == null || !tmsFixosPagEdit.contains(e.getTipoEncargoId()))
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
}
