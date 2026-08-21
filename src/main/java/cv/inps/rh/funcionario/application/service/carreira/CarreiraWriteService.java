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
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.audit.ValidacaoAuditContext;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(CarreiraWriteService.class);

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

  /**
   * {@code true} se a carreira representa uma Progressão/Promoção — o VALOR guardado em
   * {@code tipo_situacao} tem referência {@link #REF_PROG_PROMO} no domínio {@link #DOMINIO_TIPO_MOV}
   * (valores PROGRESSAO/PROMOCAO). Usado no {@code validarCarreira} para rotear a invocação do
   * procedure REGISTO_SALARIO: só progressões/promoções registam evolução salarial.
   *
   * <p>Ao contrário de {@link #contexto(String)}, NÃO lança para valores sem referência de carreira:
   * {@code tipo_situacao} pode ser INICIO (registo), CARGO_NOVO/MUDANCA_CARREIRA (novo) ou o literal
   * CARREIRA_NOVO (default do POST sem tipoCarreira) — todos contam como não-progressão.</p>
   */
  private boolean ehProgressaoPromocao(CarreiraEntity carreira) {
    var valor = carreira != null ? carreira.getTipoSituacao() : null;
    if (valor == null) return false;
    return domainEntityRepository
        .findByDominioAndValorAndEstado(DOMINIO_TIPO_MOV, valor, Estado.A)
        .stream().anyMatch(d -> REF_PROG_PROMO.equals(d.getReferencia()));
  }

  public SuccessResponseDTO novaCarreira(String funcionarioId, CarreiraNovoDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    if (carreiraEntityRepository.existsByContrVinculoIdFunIdAndEstado(funcionario, Estado.P))
      throw IgrpResponseStatusException.conflict("Existe um registo de carreira por validar!");

    if (!contratoEntityRepository.existsByFunIdAndEstado(funcionario, Estado.A))
      throw IgrpResponseStatusException.conflict("Este funcionário não possui um contrato ativo");

    // Doc (Regra Geral): "O campo processar salário deve ser obrigatório"
    if (dto.getFlgProcessa() == null)
      throw IgrpResponseStatusException.badRequest("O campo 'processar salário' é obrigatório");

    var contratoAtual = funcionarioRules.getContratoAtual(funcionario.getUuid());
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
    // Escopadas ao contrato ATUAL — carreiras de contratos anteriores (encerrados) não contam para o
    // limite de 2 activas, mesmo que o seu data_fim ainda seja futuro (ver validarCarreira).
    var emVigor = carreiraEntityRepository.findEmVigorByFuncionario(funcionario, java.time.LocalDate.now())
        .stream().filter(c -> mesmoContrato(c, contratoAtual)).toList();
    // if (emVigor.stream().anyMatch(c -> (c.getCargoId() == null) == novoCargoNulo))
    //   throw IgrpResponseStatusException.conflict(
    //       "Já existe uma carreira activa do mesmo tipo. Para alterá-la use Progressão/Promoção.");
    if (emVigor.size() >= 2)
      throw IgrpResponseStatusException.conflict("O colaborador não pode ter mais de duas carreiras activas");

    criarPendenteContentor(funcionario, dto, contratoAtual, relacionamentoAtual, null);

    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Carreira registada.", List.of());
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

    // Baseline JaVers do REGISTO: a validação ainda não existe aqui (precisa do id da carreira), por
    // isso pré-geramos o seu UUID e carimbamos JÁ o PRIMEIRO save — que é o que cria o snapshot
    // INITIAL. Carimbar um save POSTERIOR seria no-op (a entidade não mudou → o JaVers não faz commit),
    // e a grelha do registo saía vazia. O mesmo UUID é depois usado na ValidacaoEntity abaixo.
    var validacaoUuid = UuidCreator.getTimeOrderedEpoch();
    try {
      ValidacaoAuditContext.set(null, validacaoUuid, "RH_T_CARREIRA");
      carreiraEntityRepository.save(novaCarreira);
    } finally {
      ValidacaoAuditContext.clear();
    }

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
        .findByVinculoId_IdAndTipoAndEstado(vinculoAtualId, "REM", Estado.A).stream().findFirst().orElse(null);
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
    validation.setUuid(validacaoUuid); // mesmo UUID já carimbado no baseline (ver save acima)
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
    var contratoAtual = funcionarioRules.getContratoAtual(funcionario.getUuid());
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
    return paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoId, tipo, Estado.A).stream()
        .filter(m -> m.getTmId() != null)
        .map(m -> m.getTmId().getId())
        .collect(java.util.stream.Collectors.toSet());
  }

  /** Uma carreira pertence ao mesmo contrato (contr_vinculo) dado. Usado para escopar as "carreiras
   *  em vigor" ao contrato atual — evita que carreiras de contratos anteriores (encerrados, mas com
   *  data_fim ainda futuro) sejam consideradas na progressão/limite. */
  private boolean mesmoContrato(CarreiraEntity c, ContratoEntity contrato) {
    return c != null && c.getContrVinculoId() != null && contrato != null
        && Objects.equals(c.getContrVinculoId().getId(), contrato.getId());
  }

  /** Tm do movimento de SALÁRIO do vínculo do contrato (REM). Usado na progressão para identificar
   *  o vencimento antigo a fechar (os subsídios/descontos re-associam-se; só o salário é substituído). */
  private Long salarioTmIdDoContrato(ContratoEntity contrato) {
    Long vinculoId = contrato != null && contrato.getVinculoId() != null ? contrato.getVinculoId().getId() : null;
    if (vinculoId == null) return null;
    var movREM = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipoAndEstado(vinculoId, "REM", Estado.A).stream().findFirst().orElse(null);
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

  public SuccessResponseDTO validarCarreira(String funcionarioId, ValidacaoCarreiraDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    // Carreira pendente + o seu tiprel pendente (ambos criados em P no registo, est_act_adm=0).
    var carreira = carreiraEntityRepository.findByContrVinculoIdFunIdAndEstado(funcionario, Estado.P);

    // CORRIGIR (checker devolve ao maker): carreira pendente P -> C, validação P -> C, SEM aplicar
    // payload nem tocar no tiprel/def pendentes. O maker corrige e reenvia via atualizarCarreira
    // (C -> P). Espelha o ciclo do registo de colaborador / mobilidade.
    if (ValidationUtil.isCorrigir(dto.getValidacao())) {
      if (carreira == null) {
        throw IgrpResponseStatusException.badRequest("Não há carreira pendente para devolver para correção.");
      }
      var validacaoC = funcionarioRules.devolverParaCorrecao(carreira.getUuid(), carreira.getEstado(), Referencia.CARREIRA);
      carreira.setEstado(Estado.C);
      carreiraEntityRepository.save(carreira);
      validacaoEntityRepository.save(validacaoC);
      LOGGER.info("[CORRIGIR] CARREIRA devolvida para correção (carreira={}).", carreira.getUuid());
      return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira devolvida para correção.", List.of());
    }

    var aprovado = ValidationUtil.isAprovado(dto.getValidacao());
    if (carreira == null) {
      throw IgrpResponseStatusException.badRequest("Não há carreira pendente para validar.");
    }
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
      return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira rejeitada.", List.of());
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

    // Carreiras em vigor (estado A, data_fim null ou futura), excluindo a pendente. ESCOPADAS ao
    // MESMO contrato da pendente: num funcionário multi-contrato (após um novo contrato), a carreira
    // do contrato ANTERIOR ainda tem data_fim = fim desse contrato (futuro), pelo que o findEmVigor a
    // devolveria — e a progressão substituiria a carreira ERRADA (do contrato antigo), transferindo os
    // def do contrato antigo. A "carreira do mesmo tipo em vigor" só faz sentido dentro do contrato atual.
    var emVigor = carreiraEntityRepository.findEmVigorByFuncionario(funcionario, java.time.LocalDate.now())
        .stream().filter(c -> !Objects.equals(c.getId(), carreira.getId()))
        .filter(c -> mesmoContrato(c, carreira.getContrVinculoId()))
        .toList();
    var carreiraMesmoTipo = emVigor.stream()
        .filter(c -> (c.getCargoId() == null) == novoCargoNulo).findFirst().orElse(null);

    // PROGRESSÃO (mesmo tipo em vigor): substitui o track. Doc 29/07: SÓ o VENCIMENTO é substituído
    // (novo salário do escalão) → fecha-se; os subsídios/descontos RE-ASSOCIAM ao novo tiprel
    // (mesmas linhas, via transferir — como renovação/mobilidade: "pega todos os registos do tiprel
    // anterior"). Não se fecham nem se copiam os subsídios. Fecha-se depois o tiprel/carreira antigos.
    if (carreiraMesmoTipo != null) {
      var tiprelSubstituido = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(carreiraMesmoTipo.getUuid()).orElse(null);
      if (tiprelSubstituido != null) {
        // 1. Fecha SÓ o vencimento antigo (o def REM cujo tm é o salário do vínculo). Doc: fechar por
        //    DATA_FIM e MANTER estado 'A' (o 'I' é só para rejeição). Fecha em dataEfetiva-1 (o dia
        //    antes do novo período) para dar fronteira de período limpa: o antigo cobre até ao dia
        //    anterior; o novo começa em dataEfetiva. Assim as vistas por período separam-nos sem sobrepor.
        Long salarioTmId = salarioTmIdDoContrato(carreira.getContrVinculoId());
        var fimAntigo = dataEfetiva.minusDays(1);
        var salariosFechadosIds = new java.util.HashSet<Long>();
        funcionarioRules.getRemuneracoesAssociadosAtivos(tiprelSubstituido.getId()).stream()
            .filter(r -> salarioTmId != null && r.getTmId() != null && Objects.equals(r.getTmId().getId(), salarioTmId))
            .forEach(o -> { o.setDataFim(fimAntigo); definicaoRemuneracaoEntityRepository.save(o); salariosFechadosIds.add(o.getId()); });
        // 2. Re-associa os restantes ativos (subsídios + descontos) ao novo tiprel — mesmas linhas,
        //    datas intactas. O vencimento antigo mantém-se 'A' (para não sumir da vista inicial), por
        //    isso já não é o estado 'I' que o exclui do transfer: passa-se o seu id em excluirRemIds
        //    para ele NÃO transitar (o novo tiprel já recebe o salário novo do escalão).
        if (tiprelPendente != null)
          tipoRelRemPagHelper.transferirParaNovoTipoRelacionamento(tiprelSubstituido, tiprelPendente,
              List.of(), List.of(), salariosFechadosIds, java.util.Collections.emptySet());
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

    // PROGRESSÃO/PROMOÇÃO: registar o novo salário do escalão na BD via procedure Oracle, passando a
    // carreira ANTERIOR (substituída) e a NOVA. Mesma transação: flush() antes para a procedure ver as
    // linhas já ativadas; se rebentar, faz rollback de toda a progressão e propaga a mensagem Oracle.
    //
    // O proc SÓ corre quando a carreira validada É mesmo uma Progressão/Promoção (tipo_situacao com
    // referência CARREIRA_PROG_PROMO no domínio TIPO_MOV_LABORAL: valores PROGRESSAO/PROMOCAO). Uma
    // carreira NOVA do mesmo tipo (CARGO_NOVO/MUDANCA_CARREIRA/CARREIRA_NOVO) também substitui o track
    // aqui, mas NÃO é uma progressão salarial — não deve invocar o proc. Sem este guard, um POST de
    // "carreira nova" do mesmo tipo disparava o REGISTO_SALARIO indevidamente.
    if (carreiraMesmoTipo != null && ehProgressaoPromocao(carreira)) {
      entityManager.flush();
      registarSalarioProgressao(carreiraMesmoTipo.getId(), carreira.getId());
    }

    return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira validada.", List.of());
  }

  /**
   * Chama pkg_aumento_salarial.REGISTO_SALARIO(P_CARREIRA_ID_OLD, P_CARREIRA_ID_NEW,
   * P_USER_REGISTO_ID, P_USER_REGISTO_NAME) na transação corrente.
   * TODO: P_USER_REGISTO_ID/NAME hardcoded — trocar pelo utilizador do login quando disponível.
   */
  private void registarSalarioProgressao(Long carreiraIdOld, Long carreiraIdNew) {
    final long userRegistoId = 1L;
    final String userRegistoName = "SYSTEM";
    entityManager.unwrap(org.hibernate.Session.class).doWork(conn -> {
      try (java.sql.CallableStatement cs =
               conn.prepareCall("{call pkg_aumento_salarial.REGISTO_SALARIO(?,?,?,?)}")) {
        cs.setLong(1, carreiraIdOld);
        cs.setLong(2, carreiraIdNew);
        cs.setLong(3, userRegistoId);
        cs.setString(4, userRegistoName);
        cs.execute();
      } catch (java.sql.SQLException e) {
        // DIAGNÓSTICO (temporário — remover após correção do proc na BD): captura o stack Oracle
        // completo (com o ORA-06512 "at PKG_AUMENTO_SALARIAL, line NNN") para enviar ao DBA. NÃO
        // altera o comportamento — o erro é RE-LANÇADO e a transação continua a fazer rollback.
        LOGGER.error("[PROGRESSAO][REGISTO_SALARIO] FALHOU pkg_aumento_salarial.REGISTO_SALARIO "
            + "(P_CARREIRA_ID_OLD={}, P_CARREIRA_ID_NEW={}, P_USER_REGISTO_ID={}, P_USER_REGISTO_NAME={}) "
            + "| SQLState={} ErrorCode={} | Oracle: {}",
            carreiraIdOld, carreiraIdNew, userRegistoId, userRegistoName,
            e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
        throw e;
      }
    });
  }

  public SuccessResponseDTO eliminarCareira(String carreiraId) {

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

    return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira eliminada.", List.of());
  }

  public SuccessResponseDTO atualizarCarreira(String carreiraId, String funcionarioId, CarreiraNovoDTO dto) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var carreira = carreiraEntityRepository.findByUuidOrThrow(UUID.fromString(carreiraId));

    if (!carreira.getContrVinculoId().getFunId().getId().equals(funcionario.getId()))
      throw IgrpResponseStatusException.badRequest("Carreira não pertence a este funcionário");

    // Correção de REGISTO devolvido pelo checker (carreira em C): é um registo ainda por validar, por
    // isso salta o roteamento de progressão/processada e cai no caminho editar-in-place; no fim
    // reactiva a validação INSERT (C -> P) em vez de criar uma UPDATE nova. Ciclo maker-checker.
    boolean correcaoRegisto = Estado.C.equals(carreira.getEstado());

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
    if (!correcaoRegisto && dto.getTipoCarreira() != null && contexto(dto.getTipoCarreira()) == ContextoCarreira.PROG_PROMO) {
      progredirCarreira(funcionario, carreira, dto);
      return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira actualizada.", List.of());
    }

    // Editar de carreira JÁ processada (PROCESSAMENTO > 0, doc l.4851-4905): só Escalão, Data Fim e
    // Processa Salário são editáveis. Escalão → progressão (novo INSERT). Os flips de flg e a Data
    // Fim são IMEDIATOS (não vão a validação). "Processada" segue a vista RH_V_CARREIRA (existe
    // registo em RH_T_PROC_FUNCIONARIOS para um tiprel desta carreira).
    if (!correcaoRegisto && carreiraProcessada(carreira)) {
      Long escalaoAtual = carreira.getEscalaoId() != null ? carreira.getEscalaoId().getId() : null;
      if (!Objects.equals(escalaoAtual, dto.getEscalaoReferenciaId())) {
        progredirCarreira(funcionario, carreira, dto);
        return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira actualizada.", List.of());
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
      return new SuccessResponseDTO(true, carreira.getUuid().toString(), "Carreira actualizada.", List.of());
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
    boolean revalidar = correcaoRegisto || (mudouChave && !Estado.P.equals(carreira.getEstado()));

    // Correção reenviada pelo maker (C -> P): reactiva a validação INSERT que o checker deixou em C —
    // não cria uma UPDATE nova. O seu UUID/ID carimbam a auditoria JaVers da correção (abaixo).
    ValidacaoEntity validacaoCorrecao = correcaoRegisto
        ? funcionarioRules.reabrirParaValidacao(carreira.getUuid(), Referencia.CARREIRA)
        : null;

    carreiraMapper.toUpdateEntity(carreira, dto);
    if (revalidar) carreira.setEstado(Estado.P);

    // Auditoria JaVers da EDIÇÃO: como no registo, o diff tem de ser carimbado no PRÓPRIO save que
    // captura a alteração (o auto-audit dispara aqui). Numa correção reenvia-se a validação INSERT
    // existente (id/uuid reais); numa edição normal pré-gera-se o UUID da validação UPDATE (criada
    // mais abaixo, só se revalidar). Sem revalidação não há grelha, logo grava-se sem contexto.
    UUID validacaoUuidEdit = validacaoCorrecao != null ? validacaoCorrecao.getUuid()
        : (revalidar ? UuidCreator.getTimeOrderedEpoch() : null);
    Long validacaoIdEdit = validacaoCorrecao != null ? validacaoCorrecao.getId() : null;
    if (revalidar) {
      try {
        ValidacaoAuditContext.set(validacaoIdEdit, validacaoUuidEdit, "RH_T_CARREIRA");
        carreiraEntityRepository.save(carreira);
      } finally {
        ValidacaoAuditContext.clear();
      }
    } else {
      carreiraEntityRepository.save(carreira);
    }

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
        // Editar é uma correção IN-PLACE na MESMA versão (mesmo tiprel): o salário novo é associado
        // a este mesmo relacionamento (linha abaixo). Ao contrário da progressão/renovação (transição
        // de versão, tiprels distintos), aqui o valor antigo NÃO é uma versão à parte — é só histórico
        // da correção. Por isso fecha-se por DATA_FIM E inactiva-se ('I'): senão o mesmo tiprel ficaria
        // com dois salários 'A' e a vista (inicial e atual, ambas este tiprel) mostraria os dois.
        salarioAntigo.setDataFim(dataEfetiva.minusDays(1));
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

      if (correcaoRegisto) {
        // Maker reenvia a correção: a validação INSERT já foi reactivada (C -> P) acima; só falta
        // religar o tiprel e gravar. NÃO se cria uma validação UPDATE nova.
        validacaoCorrecao.setTiprelId(relacionamento);
        validacaoEntityRepository.save(validacaoCorrecao);
      } else {
        var validation = new ValidacaoEntity();
        validation.setTipoAccao(TipoAcao.UPDATE.name());
        validation.setReferenciaName(Referencia.CARREIRA.name());
        validation.setReferenciaId(carreira.getId());
        validation.setReferenciaUuid(carreira.getUuid());
        validation.setTiprelId(relacionamento);
        validation.setEstado(Estado.P);
        validation.setUuid(validacaoUuidEdit); // mesmo UUID já carimbado no save da edição (ver acima)
        validation.setFunId(funcionario);
        validacaoEntityRepository.save(validation);
      }
    }

    return new SuccessResponseDTO(true, carreira.getUuid().toString(),
        correcaoRegisto ? "Correção reenviada para validação." : "Carreira actualizada.", List.of());
  }
}
