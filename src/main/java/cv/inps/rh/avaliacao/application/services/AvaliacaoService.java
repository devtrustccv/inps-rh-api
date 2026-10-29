package cv.inps.rh.avaliacao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.avaliacao.application.commands.DefinicaoObjetivoCommand;
import cv.inps.rh.avaliacao.application.dto.DefinicaoObjectivoDTO;
import cv.inps.rh.avaliacao.application.dto.PeriodoResumoDTO;
import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.WrapperListaDefinicaoObjetivoDTO;
import cv.inps.rh.avaliacao.application.queries.GetListaAvaliacaoQuery;
import cv.inps.rh.avaliacao.application.queries.GetListaDefinicaoObjectivosQuery;
import cv.inps.rh.avaliacao.infrastructure.mappers.AvaliacaoListagemMapper;
import cv.inps.rh.avaliacao.infrastructure.mappers.AvaliacaoMapper;
import cv.inps.rh.shared.application.constants.AbrangenciaAvaliacao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.DirecaoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

  private static final String ESTADO_ATIVO = "A";

  /** Vínculo laboral "Efetivo" (RH_V_RELACAO_LABORAL.VINCULO_ID): só estes têm objectivos individuais. */
  private static final Long VINCULO_EFETIVO_ID = 1L;

  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final AvaliacaoObjectivoEntityRepository objectivoRepository;
  private final AvaliacaoCompetenciaEntityRepository competenciaRepository;
  private final AvaliacaoAtitudePessoalEntityRepository atitudeRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DirecaoEntityRepository instituicaoRepository;
  private final SecaoEntityRepository secaoRepository;
  private final ParamCargoEntityRepository cargoRepository;
  private final ParamCarreiraEntityRepository carreiraRepository;
  private final ParamObjetivoDetEntityRepository objetivoDetRepository;
  private final ParamManualFuncaoEntityRepository manualFuncaoRepository;
  private final ParamEscalaAvaliacaoEntityRepository escalaAvaliacaoRepository;
  private final AvaliacaoMapper avaliacaoMapper;
  private final AvaliacaoListagemMapper avaliacaoListagemMapper;
  private final AvaliacaoPeriodoService periodoService;
  private final RhVRelacaoLaboralEntityRepository relacaoLaboralRepository;
  private final cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;

  public AvaliacaoService(
      AvaliacaoEntityRepository avaliacaoRepository,
      AvaliacaoObjectivoEntityRepository objectivoRepository,
      AvaliacaoCompetenciaEntityRepository competenciaRepository,
      AvaliacaoAtitudePessoalEntityRepository atitudeRepository,
      FuncionarioEntityRepository funcionarioRepository,
      DirecaoEntityRepository instituicaoRepository,
      SecaoEntityRepository secaoRepository,
      ParamCargoEntityRepository cargoRepository,
      ParamCarreiraEntityRepository carreiraRepository,
      ParamObjetivoDetEntityRepository objetivoDetRepository,
      ParamManualFuncaoEntityRepository manualFuncaoRepository,
      ParamEscalaAvaliacaoEntityRepository escalaAvaliacaoRepository,
      AvaliacaoMapper avaliacaoMapper,
      AvaliacaoListagemMapper avaliacaoListagemMapper,
      AvaliacaoPeriodoService periodoService,
      RhVRelacaoLaboralEntityRepository relacaoLaboralRepository,
      cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository tiposRelacionamentoRepository) {
    this.avaliacaoRepository = avaliacaoRepository;
    this.objectivoRepository = objectivoRepository;
    this.competenciaRepository = competenciaRepository;
    this.atitudeRepository = atitudeRepository;
    this.funcionarioRepository = funcionarioRepository;
    this.instituicaoRepository = instituicaoRepository;
    this.secaoRepository = secaoRepository;
    this.cargoRepository = cargoRepository;
    this.carreiraRepository = carreiraRepository;
    this.objetivoDetRepository = objetivoDetRepository;
    this.manualFuncaoRepository = manualFuncaoRepository;
    this.escalaAvaliacaoRepository = escalaAvaliacaoRepository;
    this.avaliacaoMapper = avaliacaoMapper;
    this.avaliacaoListagemMapper = avaliacaoListagemMapper;
    this.periodoService = periodoService;
    this.relacaoLaboralRepository = relacaoLaboralRepository;
    this.tiposRelacionamentoRepository = tiposRelacionamentoRepository;
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> definicaoObjetivos(DefinicaoObjetivoCommand command) {

    var dto = command.getDefinicaoobjectivo();

    // Sem abrangência assume-se INDIVIDUAL: é o comportamento anterior ao refactor.
    var abrangencia = StringUtils.hasText(dto.getAbrangencia())
        ? AbrangenciaAvaliacao.fromValorOrThrow(dto.getAbrangencia())
        : AbrangenciaAvaliacao.INDIVIDUAL;

    var det = objetivoDetRepository.findTopByAnoOrderByIdDesc(dto.getAno())
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "ParamObjetivoDetEntity not found for ano: " + dto.getAno()));

    var secao = dto.getSeccaoId() != null
        ? secaoRepository.findByIdOrThrow(dto.getSeccaoId())
        : null;

    var cargo = dto.getCargoId() != null
        ? cargoRepository.findById(dto.getCargoId())
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "ParamCargoEntity not found for id: " + dto.getCargoId()))
        : null;

    var carreira = dto.getCarrPccsId() != null
        ? carreiraRepository.findByIdOrThrow(dto.getCarrPccsId())
        : null;

    var mapParamObjectives = det.getObjetivos().stream()
        .collect(Collectors.toMap(ParamObjetivoEntity::getId, Function.identity()));

    var created = new ArrayList<String>();
    var alertas = new ArrayList<String>();

    if (!abrangencia.exigeColaborador()) {
      // Objectivos comuns: sem colaborador. INPS dá uma linha; DIRECAO dá uma por direção,
      // porque o ecrã permite juntar várias direções na mesma gravação.
      // Spec: a periodicidade "deve aparecer somente no momento de avaliação" e "esse
      // registo é somente em avaliação" — a definição é anual e não cria nada por período.
      for (var instit : resolverDirecoes(dto, abrangencia)) {
        var institId = instit != null ? instit.getId() : null;

        // Reenviar o mesmo formulário não pode duplicar objectivos: se já existir a
        // avaliação comum deste ano/abrangência/direção, fica como está.
        var existente = avaliacaoRepository
            .findComuns(dto.getAno(), abrangencia.name(), institId)
            .stream().findFirst().orElse(null);

        if (existente != null) {
          alertas.add("Já existiam objectivos " + abrangencia.name()
              + (instit != null ? " da direção " + instit.getNome() : "")
              + " no ano " + dto.getAno() + "; não foram alterados.");
          created.add(existente.getUuid().toString());
          continue;
        }

        var avaliacao = novaAvaliacao(dto, abrangencia, null, instit, secao, cargo, carreira, det);
        avaliacaoRepository.save(avaliacao);
        criarLinhasAvaliacao(avaliacao, det.getObjetivos(), mapParamObjectives, dto, det,
            resolverDescricaoManual(institId, dto.getSeccaoId(), dto.getCargoId(), dto.getCarrPccsId()));
        created.add(avaliacao.getUuid().toString());
      }

      var respostaComuns = sucesso(created, "Objectivos comuns (" + abrangencia.name()
          + ") definidos em " + created.size() + " registo(s) para o ano " + dto.getAno() + ".");
      alertas.forEach(a -> respostaComuns.getBody().getAlertas().add(a));
      return respostaComuns;
    }

    var periodos = resolverPeriodos(dto);

    var instit = dto.getInstitId() != null ? instituicaoRepository.findByIdOrThrow(dto.getInstitId()) : null;
    var funUuids = resolverColaboradores(dto, carreira, instit);
    var ignorados = new ArrayList<String>();

    for (var funUuid : funUuids) {

      var funcionario = funcionarioRepository.findByUuidOrThrow(funUuid);

      // O ciclo é por ano, não por período: se já existir avaliação do colaborador nesse ano,
      // os períodos novos acrescentam-se a ela em vez de criar um registo duplicado.
      var existente = avaliacaoRepository.findAllByFuncionario_IdAndAno(funcionario.getId(), dto.getAno())
          .stream().findFirst().orElse(null);

      if (existente != null) {
        periodos.forEach(pp -> periodoService.obterOuCriarDetalhe(existente, pp));
        if (existente.getUuid() != null && existente.getUuid().toString().equals(dto.getUuid())) {
          // Edição desta mesma definição: grava o que mudou nas linhas (meta, KPI, objectivo
          // individual, competência, atitude) — antes as alterações eram ignoradas.
          atualizarLinhasAvaliacao(existente, dto,
              resolverDescricaoManual(instit != null ? instit.getId() : null,
                  dto.getSeccaoId(), cargoDoColaborador(funUuid, dto.getCargoId()),
                  carreiraDoColaborador(funUuid, dto.getCarrPccsId())));
          created.add(existente.getUuid().toString());
        } else {
          ignorados.add(funcionario.getNome());
        }
        continue;
      }

      var avaliacao = novaAvaliacao(dto, abrangencia, funcionario, instit, secao, cargo, carreira, det);
      avaliacaoRepository.save(avaliacao);
      criarLinhasAvaliacao(avaliacao, det.getObjetivos(), mapParamObjectives, dto, det,
          resolverDescricaoManual(instit != null ? instit.getId() : null,
              dto.getSeccaoId(), cargoDoColaborador(funUuid, dto.getCargoId()),
              carreiraDoColaborador(funUuid, dto.getCarrPccsId())));
      periodos.forEach(pp -> periodoService.obterOuCriarDetalhe(avaliacao, pp));

      created.add(avaliacao.getUuid().toString());
    }

    var resposta = sucesso(created, created.size() + " objectivo(s) definido(s) para "
        + String.join(", ", periodos) + ".");
    if (!ignorados.isEmpty()) {
      resposta.getBody().getAlertas().add(
          "Já existia definição no ano " + dto.getAno() + " para: " + String.join(", ", ignorados)
              + ". Foram apenas acrescentados os períodos " + String.join(", ", periodos) + ".");
    }
    alertas.forEach(a -> resposta.getBody().getAlertas().add(a));
    return resposta;
  }

  /**
   * Os períodos a definir. O ecrã dos objectivos comuns é multiselect e o do registo
   * individual é um select simples, por isso aceita-se a lista ou o campo singular.
   * Todos são validados contra o ciclo parametrizado no ano.
   */
  private List<String> resolverPeriodos(DefinicaoObjectivoDTO dto) {
    var pedidos = dto.getPeriodicidades() != null && !dto.getPeriodicidades().isEmpty()
        ? dto.getPeriodicidades()
        : (StringUtils.hasText(dto.getPeriodicidade()) ? List.of(dto.getPeriodicidade()) : List.<String>of());

    if (pedidos.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(
          "É obrigatório indicar pelo menos um período (periodicidade ou periodicidades).");
    }

    var validados = new LinkedHashSet<String>();
    pedidos.forEach(pp -> validados.add(periodoService.validarPeriodo(dto.getAno(), pp)));
    return List.copyOf(validados);
  }

  /**
   * Os colaboradores da definição individual. Sem colaboradores indicados, a definição aplica-se a
   * todos os colaboradores activos da Carreira/Cargo (e da direção, se indicada), por isso um dos
   * dois é obrigatório. Com Carreira/Cargo, os colaboradores indicados têm de pertencer a ela.
   * Em qualquer caso, só entram colaboradores com vínculo efetivo.
   */
  private List<UUID> resolverColaboradores(
      DefinicaoObjectivoDTO dto, ParamCarreiraEntity carreira, DirecaoEntity instit) {
    var indicados = dto.getFunUuids() == null
        ? List.<UUID>of()
        : dto.getFunUuids().stream().filter(java.util.Objects::nonNull).distinct().toList();

    if (indicados.isEmpty()) {
      if (carreira == null) {
        throw IgrpResponseStatusException.badRequest(
            "Indique a Carreira / Cargo ou pelo menos um colaborador.");
      }
      var daCarreira = tiposRelacionamentoRepository.findFuncionarioUuidsDaCarreiraPccs(
          carreira.getId(), instit != null ? instit.getId() : null, VINCULO_EFETIVO_ID);
      if (daCarreira.isEmpty()) {
        throw IgrpResponseStatusException.badRequest(
            "Não há colaboradores efetivos activos na Carreira / Cargo " + carreira.getNome()
                + (instit != null ? " da direção " + instit.getNome() : "") + ".");
      }
      return daCarreira;
    }

    // Só colaboradores com vínculo efetivo têm objectivos individuais. A edição de uma definição
    // já existente (pedido com uuid) não é bloqueada, para não prender as criadas antes da regra.
    var edicao = StringUtils.hasText(dto.getUuid());
    var naoEfetivos = edicao ? List.<String>of() : indicados.stream()
        .filter(u -> !VINCULO_EFETIVO_ID.equals(relacaoLaboralRepository
            .findFirstByFuncionarioUuidAndEstActAdm(u.toString(), 1L)
            .map(RhVRelacaoLaboralEntity::getVinculoId)
            .orElse(null)))
        .map(u -> funcionarioRepository.findByUuidOrThrow(u).getNome())
        .toList();
    if (!naoEfetivos.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(
          "Só colaboradores com vínculo efetivo podem ter objectivos definidos. Não efetivo(s): "
              + String.join(", ", naoEfetivos) + ".");
    }

    if (carreira != null) {
      var foraDaCarreira = indicados.stream()
          .filter(u -> !carreira.getId().equals(carreiraDoColaborador(u, null)))
          .map(u -> funcionarioRepository.findByUuidOrThrow(u).getNome())
          .toList();
      if (!foraDaCarreira.isEmpty()) {
        throw IgrpResponseStatusException.badRequest(
            "Não pertence(m) à Carreira / Cargo " + carreira.getNome() + ": "
                + String.join(", ", foraDaCarreira) + ".");
      }
    }
    return indicados;
  }

  /**
   * As direções a abranger. INPS não tem nenhuma (uma linha com INSTIT_ID nulo);
   * DIRECAO exige pelo menos uma e aceita várias, porque o ecrã tem
   * "+ Adicionar Direção à Lista".
   */
  private List<DirecaoEntity> resolverDirecoes(DefinicaoObjectivoDTO dto, AbrangenciaAvaliacao abrangencia) {
    if (abrangencia != AbrangenciaAvaliacao.DIRECAO) {
      return java.util.Collections.singletonList(null);
    }

    var comColaboradores = instituicaoRepository.findComColaboradoresActivos();
    var ids = new LinkedHashSet<Long>();
    if (dto.getInstitIds() != null) {
      dto.getInstitIds().stream().filter(java.util.Objects::nonNull).forEach(ids::add);
    }
    if (dto.getInstitId() != null) {
      ids.add(dto.getInstitId());
    }
    // Só se definem objectivos para direções com colaboradores; sem direções indicadas
    // abrangem-se todas as que têm colaboradores.
    if (ids.isEmpty()) {
      if (comColaboradores.isEmpty()) {
        throw IgrpResponseStatusException.badRequest("Não há direções com colaboradores activos.");
      }
      return comColaboradores;
    }
    var filtradas = comColaboradores.stream().filter(d -> ids.contains(d.getId())).toList();
    if (filtradas.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(
          "Nenhuma das direções indicadas tem colaboradores activos.");
    }
    return filtradas;
  }

  /** Direções com colaboradores activos — as que recebem os objectivos comuns de Direção. */
  @Transactional(readOnly = true)
  public List<cv.inps.rh.avaliacao.application.dto.DirecaoResumoDTO> getDirecoesComColaboradores() {
    return instituicaoRepository.findComColaboradoresActivos().stream()
        .map(d -> new cv.inps.rh.avaliacao.application.dto.DirecaoResumoDTO(d.getId(), d.getNome()))
        .toList();
  }

  /**
   * Carreiras/Cargo com colaboradores activos — só as da direção indicada, ou todas sem direção.
   * Alimenta o combo Carreira / Cargo da definição de objectivos individuais.
   */
  @Transactional(readOnly = true)
  public List<cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO> getCarreirasComColaboradores(Long institId) {
    var ids = tiposRelacionamentoRepository.findCarreirasPccsComColaboradores(institId, VINCULO_EFETIVO_ID).stream()
        .filter(java.util.Objects::nonNull)
        .toList();
    return carreiraRepository.findAllById(ids).stream()
        .sorted(Comparator.comparing(c -> Objects.toString(c.getNome(), "")))
        .map(c -> new cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO(c.getId(), c.getNome()))
        .toList();
  }

  /**
   * Carreira/Cargo atual do colaborador, pela mesma regra usada ao validar a definição individual
   * (carreira PCCS ou, sem ela, a do escalão). Sem carreira identificável, id e nome vêm nulos.
   */
  @Transactional(readOnly = true)
  public cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO getCarreiraDoColaborador(UUID funUuid) {
    var id = carreiraDoColaborador(funUuid, null);
    if (id == null) {
      return new cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO();
    }
    var carreira = carreiraRepository.findByIdOrThrow(id);
    return new cv.inps.rh.avaliacao.application.dto.CarreiraResumoDTO(carreira.getId(), carreira.getNome());
  }

  private AvaliacaoEntity novaAvaliacao(
      DefinicaoObjectivoDTO dto,
      AbrangenciaAvaliacao abrangencia,
      FuncionarioEntity funcionario,
      DirecaoEntity instit,
      SecaoEntity secao,
      ParamCargoEntity cargo,
      ParamCarreiraEntity carreira,
      ParamObjetivoDetEntity det) {

    var avaliacao = new AvaliacaoEntity();
    avaliacao.setUuid(UuidCreator.getTimeOrderedEpoch());
    avaliacao.setFuncionario(funcionario);
    avaliacao.setAno(dto.getAno());
    avaliacao.setAbrangencia(abrangencia.name());
    avaliacao.setInstitId(instit);
    avaliacao.setSeccaoId(secao);
    avaliacao.setCargo(cargo);
    avaliacao.setCarreira(carreira);
    avaliacao.setEstado(ESTADO_ATIVO);
    avaliacao.setPesoComportamentais(det.getPesoComportamentais());
    avaliacao.setPesoTecnica(det.getPesoTecnica());
    return avaliacao;
  }

  private ResponseEntity<SuccessResponseDTO> sucesso(List<String> ids, String mensagem) {
    var dto = new SuccessResponseDTO();
    dto.setSucesso(true);
    dto.setId(ids.isEmpty() ? null : String.join(",", ids));
    dto.setMensagem(mensagem);
    return ResponseEntity.ok(dto);
  }

  @Transactional(readOnly = true)
  public WrapperListaDefinicaoObjetivoDTO getListaDefinicaoObjectivos(GetListaDefinicaoObjectivosQuery query) {

    var pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

    Specification<AvaliacaoEntity> spec = (root, cq, cb) -> {
      if (cq.getResultType() != Long.class) {
        root.fetch("funcionario", JoinType.LEFT);
        root.fetch("institId", JoinType.LEFT);
        root.fetch("seccaoId", JoinType.LEFT);
        root.fetch("cargo", JoinType.LEFT);
        root.fetch("carreira", JoinType.LEFT);
      }

      List<Predicate> predicates = new ArrayList<>();

      if (query.getAno() != null) {
        predicates.add(cb.equal(root.get("ano"), query.getAno()));
      }
      if (StringUtils.hasText(query.getAbrangencia())) {
        predicates.add(cb.equal(cb.upper(root.get("abrangencia")),
            query.getAbrangencia().trim().toUpperCase()));
      }
      if (query.getSeccaoId() != null) {
        predicates.add(cb.equal(root.get("seccaoId").get("id"), query.getSeccaoId()));
      }
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estado"), query.getEstado()));
      }
      if (query.getInstitId() != null) {
        predicates.add(cb.equal(root.get("institId").get("id"), query.getInstitId()));
      }
      if (query.getCargoId() != null) {
        predicates.add(cb.equal(root.get("cargo").get("id"), query.getCargoId()));
      }
      if (query.getCarreiraId() != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), query.getCarreiraId()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = avaliacaoRepository.findAll(spec, pageable);

    var response = new WrapperListaDefinicaoObjetivoDTO();
    cv.inps.rh.shared.util.PageMapper.fillPagination(page, response);
    // Um único select para os períodos de toda a página.
    var periodosPorAvd = periodoService
        .detalhesDe(page.getContent().stream().map(AvaliacaoEntity::getId).toList())
        .stream()
        .collect(Collectors.groupingBy(d -> d.getAvaliacao().getId(),
            Collectors.mapping(AvaliacaoDetalheEntity::getPeriodicidade, Collectors.toList())));

    response.setContent(page.getContent().stream()
        .map(a -> avaliacaoMapper.toResumo(a,
            periodosPorAvd.getOrDefault(a.getId(), List.of()).stream().sorted().toList()))
        .toList());
    return response;
  }

  @Transactional(readOnly = true)
  public WrapperListaAvaliacaoDTO getListaAvaliacao(GetListaAvaliacaoQuery query) {

    var pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    var pageable = PageRequest.of(pageNumber, pageSize);

    Specification<AvaliacaoEntity> spec = (root, cq, cb) -> {
      if (cq.getResultType() != Long.class) {
        root.fetch("funcionario", JoinType.LEFT);
        root.fetch("institId", JoinType.LEFT);
        root.fetch("seccaoId", JoinType.LEFT);
        root.fetch("cargo", JoinType.LEFT);
        root.fetch("carreira", JoinType.LEFT);
      }

      List<Predicate> predicates = new ArrayList<>();

      if (query.getAno() != null) {
        predicates.add(cb.equal(root.get("ano"), query.getAno()));
      }
      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("institId").get("id"), query.getDirecao()));
      }
      if (query.getCargo() != null) {
        predicates.add(cb.equal(root.get("cargo").get("id"), query.getCargo()));
      }
      if (query.getSeccaoId() != null) {
        predicates.add(cb.equal(root.get("seccaoId").get("id"), query.getSeccaoId()));
      }
      if (query.getCarreiraId() != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), query.getCarreiraId()));
      }
      if (StringUtils.hasText(query.getPeriodicidade())) {
        // O período vive em RH_T_AVD_DETALHE: filtra-se por existência do detalhe.
        var sub = cq.subquery(Long.class);
        var det = sub.from(AvaliacaoDetalheEntity.class);
        sub.select(cb.literal(1L)).where(
            cb.equal(det.get("avaliacao").get("id"), root.get("id")),
            cb.equal(cb.upper(det.get("periodicidade")), query.getPeriodicidade().trim().toUpperCase()));
        predicates.add(cb.exists(sub));
      }
      if (StringUtils.hasText(query.getColaborador())) {
        var raw = query.getColaborador().trim();
        try {
          predicates.add(cb.equal(root.get("funcionario").get("uuid"), UUID.fromString(raw)));
        } catch (Exception ignored) {
          try {
            predicates.add(cb.equal(root.get("funcionario").get("id"), Long.valueOf(raw)));
          } catch (Exception ignored2) {
            predicates.add(cb.like(cb.lower(root.get("funcionario").get("nome")), "%" + raw.toLowerCase() + "%"));
          }
        }
      }

      predicates.add(cb.notEqual(root.get("estado"), "E"));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var rows = avaliacaoRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "ano").and(Sort.by("id")));

    var grouped = new LinkedHashMap<GroupKey, List<AvaliacaoEntity>>();

    for (var e : rows) {
      var key = new GroupKey(
          e.getAno(),
          e.getInstitId() != null ? e.getInstitId().getId() : null,
          e.getCargo() != null ? e.getCargo().getId() : null,
          e.getFuncionario() != null ? e.getFuncionario().getUuid() : null);
      grouped.computeIfAbsent(key, _ -> new ArrayList<>()).add(e);
    }

    var escala = escalaAvaliacaoRepository.findAll();

    // Um único select para os detalhes de todas as avaliações da página.
    var detalhesPorAvd = periodoService
        .detalhesDe(rows.stream().map(AvaliacaoEntity::getId).toList())
        .stream()
        .collect(Collectors.groupingBy(d -> d.getAvaliacao().getId()));

    var rotulos = periodoService.descricoesDosPeriodos();

    // O que já foi registado em cada período (avaliação e/ou autoavaliação): a lista só mostra
    // os períodos com registo e a ação "Ver …" correspondente.
    var comAvaliacao = new java.util.HashSet<String>();
    var comAutoAvaliacao = new java.util.HashSet<String>();
    for (var m : periodoService.medicoesDe(rows.stream().map(AvaliacaoEntity::getId).toList())) {
      if (m.getAvaliacao() == null) continue;
      var chave = m.getAvaliacao().getId() + "|" + m.getPeriodicidade();
      if (m.getAvaliacaoValor() != null || StringUtils.hasText(m.getRealizado())) comAvaliacao.add(chave);
      if (m.getAutoAvaliacao() != null || StringUtils.hasText(m.getAutoRealizado())) comAutoAvaliacao.add(chave);
    }

    var contentAll = grouped.values().stream().map(list -> {
      var base = list.getFirst();

      var detalhes = list.stream()
          .flatMap(a -> detalhesPorAvd.getOrDefault(a.getId(), List.<AvaliacaoDetalheEntity>of()).stream())
          .toList();

      // A ordem dos períodos é a do ciclo, não a de inserção; sem parametrização
      // no ano não há ciclo conhecido e mostram-se os detalhes como estão.
      List<AvaliacaoDetalheEntity> ordenados;
      Map<String, BigDecimal> ponderacoes;
      try {
        var tipo = periodoService.tipoDoAno(base.getAno());
        var ordem = tipo.periodos();
        ordenados = detalhes.stream()
            .sorted(Comparator.comparingInt(d -> {
              var i = ordem.indexOf(d.getPeriodicidade());
              return i < 0 ? Integer.MAX_VALUE : i;
            }))
            .toList();
        ponderacoes = periodoService.ponderacoesDoCiclo(tipo);
      } catch (RuntimeException e) {
        ordenados = detalhes;
        ponderacoes = Map.of();
      }

      var periodos = ordenados.stream().map(d -> {
        var dto = new PeriodoResumoDTO();
        dto.setUuid(d.getUuid() != null ? d.getUuid().toString() : null);
        dto.setPeriodicidade(d.getPeriodicidade());
        dto.setDescricao(rotulos.getOrDefault(d.getPeriodicidade(), d.getPeriodicidade()));
        dto.setAvaliacaoFinal(d.getAvaliacaoFinal());
        dto.setAvaliacaoQualitativa(d.getAvaliacaoQualitativa());
        dto.setEstado(d.getEstado());
        var chave = d.getAvaliacao().getId() + "|" + d.getPeriodicidade();
        dto.setTemAvaliacao(comAvaliacao.contains(chave) || d.getAvaliacaoFinal() != null);
        dto.setTemAutoAvaliacao(comAutoAvaliacao.contains(chave));
        return dto;
      }).toList();

      // Nota do ano: soma dos períodos pesada por AVD_PONDERACAO_FINAL.
      BigDecimal notaFinal = null;
      for (var d : ordenados) {
        if (d.getAvaliacaoFinal() == null) {
          continue;
        }
        var peso = ponderacoes.get(d.getPeriodicidade());
        var contributo = peso != null
            ? AvaliacaoPeriodoService.aplicarPercentagem(d.getAvaliacaoFinal(), peso)
            : d.getAvaliacaoFinal();
        notaFinal = notaFinal == null ? contributo : notaFinal.add(contributo);
      }
      notaFinal = AvaliacaoPeriodoService.escala2(notaFinal);

      var estadoGrupo = resolveEstadoGrupo(list);
      var qualitativa = notaFinal != null ? resolveQualitativa(escala, notaFinal) : null;

      return avaliacaoListagemMapper.toListagem(base, estadoGrupo, periodos, notaFinal, qualitativa);
    }).toList();

    var start = Math.min(pageNumber * pageSize, contentAll.size());
    var end = Math.min(start + pageSize, contentAll.size());
    var page = new PageImpl<>(contentAll.subList(start, end), pageable, contentAll.size());

    var response = new WrapperListaAvaliacaoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent());
    return response;
  }

  /**
   * Atualiza no lugar as linhas de uma definição existente, ligadas pela linha do componente
   * (paramId). Não apaga nem recria linhas, para não perder avaliações já registadas nelas;
   * as regras de origem dos textos são as mesmas da criação.
   */
  private void atualizarLinhasAvaliacao(AvaliacaoEntity avaliacao, DefinicaoObjectivoDTO dto,
      String manualDescricao) {
    var uuid = avaliacao.getUuid();

    var objectivos = objectivoRepository.findAllByAvaliacaoObj_Uuid(uuid);
    for (var obj : listaOuVazia(dto.getObjectivos())) {
      objectivos.stream()
          .filter(e -> e.getParamObjetivo() != null && e.getParamObjetivo().getId().equals(obj.getParamId()))
          .findFirst()
          .ifPresent(e -> {
            var p = e.getParamObjetivo();
            if ("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia())) {
              e.setObjectivos(primeiroPreenchido(obj.getObjectivo(), e.getObjectivos(), manualDescricao));
              e.setKpi(primeiroPreenchido(obj.getKpi(), p.getKpi()));
            }
            e.setMeta(obj.getMeta());
            objectivoRepository.save(e);
          });
    }

    var competencias = competenciaRepository.findAllByAvaliacao_Uuid(uuid);
    var linhasCompetencia = new ArrayList<CompetenciaLinha>();
    listaOuVazia(dto.getCompetenciasComportamentais())
        .forEach(c -> linhasCompetencia.add(new CompetenciaLinha(c.getParamId(), c.getCompetencia())));
    listaOuVazia(dto.getCompetenciasTecnicas())
        .forEach(c -> linhasCompetencia.add(new CompetenciaLinha(c.getParamId(), c.getCompetencia())));
    for (var c : linhasCompetencia) {
      competencias.stream()
          .filter(e -> e.getParamObjetivo() != null && e.getParamObjetivo().getId().equals(c.paramId()))
          .findFirst()
          .ifPresent(e -> {
            e.setDescricao(primeiroPreenchido(c.competencia(), e.getDescricao(), manualDescricao));
            competenciaRepository.save(e);
          });
    }

    var atitudes = atitudeRepository.findAllByAvaliacao_Uuid(uuid);
    for (var a : listaOuVazia(dto.getAtitudesPessoais())) {
      atitudes.stream()
          .filter(e -> e.getParamObjetivo() != null && e.getParamObjetivo().getId().equals(a.getParamId()))
          .findFirst()
          .ifPresent(e -> {
            e.setDescricao(primeiroPreenchido(a.getAtitudePessoal(), e.getDescricao(),
                e.getParamObjetivo().getDescricao()));
            atitudeRepository.save(e);
          });
    }
  }

  private record CompetenciaLinha(Long paramId, String competencia) {
  }

  private void criarLinhasAvaliacao(
      AvaliacaoEntity avaliacao,
      List<ParamObjetivoEntity> params,
      Map<Long, ParamObjetivoEntity> mapParamObjectives,
      DefinicaoObjectivoDTO dto,
      ParamObjetivoDetEntity det,
      String manualDescricao) {
    if (params == null)
      return;

    listaOuVazia(dto.getObjectivos()).forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoObjectivoEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacaoObj(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      // Spec: na abrangência INDIVIDUAL a descrição vem do manual de funções do cargo;
      // nas restantes vem da parametrização. O que o ecrã enviar só serve de recurso
      // quando não há manual configurado para aquele cargo.
      var individual = "INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia());
      e.setObjectivos(individual
          ? primeiroPreenchido(manualDescricao, obj.getObjectivo(), p.getDescricao())
          : p.getDescricao());
      e.setKpi(individual ? primeiroPreenchido(obj.getKpi(), p.getKpi()) : p.getKpi());
      e.setMeta(obj.getMeta());
      e.setPonderacao(p.getPonderacao());
      objectivoRepository.save(e);
    });

    listaOuVazia(dto.getCompetenciasComportamentais()).forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoCompetenciaEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacao(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      // Spec: "Preenchido apartir de Tabela RH_T_PARAM_MFUNCAO, CUJO cargo = cargo do colaborador"
      e.setDescricao(primeiroPreenchido(manualDescricao, obj.getCompetencia(), p.getDescricao()));
      e.setPonderacao(p.getPonderacao());
      e.setComponente(p.getComponente());
      e.setPeso(det.getPesoComportamentais());
      competenciaRepository.save(e);
    });

    listaOuVazia(dto.getCompetenciasTecnicas()).forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoCompetenciaEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacao(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      e.setDescricao(primeiroPreenchido(manualDescricao, obj.getCompetencia(), p.getDescricao()));
      e.setPonderacao(p.getPonderacao());
      e.setComponente(p.getComponente());
      e.setPeso(det.getPesoTecnica());
      competenciaRepository.save(e);
    });

    listaOuVazia(dto.getAtitudesPessoais()).forEach(obj -> {
      var p = mapParamObjectives.get(obj.getParamId());
      if (p == null) throw IgrpResponseStatusException.badRequest(
          "ParamObjetivo não encontrado: id=" + obj.getParamId() + " para o ano " + det.getAno());
      var e = new AvaliacaoAtitudePessoalEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setAvaliacao(avaliacao);
      e.setParamObjetivo(p);
      e.setNumeroOrdem(p.getNumeroOrdem());
      e.setAbrangencia(p.getAbrangencia());
      // O texto escrito no ecrã (campo editável); sem ele, o da parametrização.
      e.setDescricao(primeiroPreenchido(obj.getAtitudePessoal(), p.getDescricao()));
      e.setPonderacao(p.getPonderacao());
      atitudeRepository.save(e);
    });


    /*for (var p : params) {
      if (p == null || !StringUtils.hasText(p.getComponente())) {
        continue;
      }
      if (!aplicaAoContexto(p, institId, seccaoId, cargoId, carrPccsId)) {
        continue;
      }

      if ("OBJETIVO".equalsIgnoreCase(p.getComponente())) {
        if ("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia()) && manualDescricao == null) {
          throw IgrpResponseStatusException.badRequest("Manual de funções não encontrado para abrangencia INDIVIDUAL");
        }
        var e = new AvaliacaoObjectivoEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setEstado(ESTADO_ATIVO);
        e.setAvaliacaoObj(avaliacao);
        e.setParamObjetivo(p);
        e.setNumeroOrdem(p.getNumeroOrdem());
        e.setAbrangencia(p.getAbrangencia());
        e.setObjectivos("INDIVIDUAL".equalsIgnoreCase(p.getAbrangencia()) && manualDescricao != null ? manualDescricao
            : p.getDescricao());
        e.setKpi(p.getKpi());
        e.setPonderacao(p.getPonderacao());
        objectivoRepository.save(e);
      } else if ("COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(p.getComponente())
          || "COMPETENCIA_TECNICA".equalsIgnoreCase(p.getComponente())) {
        if (manualDescricao == null) {
          throw IgrpResponseStatusException.badRequest("Manual de funções não encontrado para competências");
        }
        var e = new AvaliacaoCompetenciaEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setEstado(ESTADO_ATIVO);
        e.setAvaliacao(avaliacao);
        e.setParamObjetivo(p);
        e.setNumeroOrdem(p.getNumeroOrdem());
        e.setAbrangencia(p.getAbrangencia());
        e.setDescricao(manualDescricao);
        e.setPonderacao(p.getPonderacao());
        e.setComponente(p.getComponente());
        competenciaRepository.save(e);
      } else if ("ATITUDE_PESSOAL".equalsIgnoreCase(p.getComponente())) {
        var e = new AvaliacaoAtitudePessoalEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setEstado(ESTADO_ATIVO);
        e.setAvaliacao(avaliacao);
        e.setParamObjetivo(p);
        e.setAbrangencia(p.getAbrangencia());
        e.setPonderacao(p.getPonderacao());
        atitudeRepository.save(e);
      }
    }*/
  }

  /**
   * O cargo a usar para procurar o manual de funções.
   *
   * <p>A spec diz "cujo cargo = cargo do colaborador", por isso lê-se da relação laboral
   * corrente (EST_ACT_ADM = 1). O cargo do formulário só entra quando o colaborador não
   * tem relação corrente — caso em que é a única indicação disponível.</p>
   */
  private Long cargoDoColaborador(UUID funUuid, Long cargoDoFormulario) {
    return relacaoLaboralRepository
        .findFirstByFuncionarioUuidAndEstActAdm(funUuid.toString(), 1L)
        .map(RhVRelacaoLaboralEntity::getCargoId)
        .filter(java.util.Objects::nonNull)
        .orElse(cargoDoFormulario);
  }

  /**
   * Uma lista do pedido que o cliente pode omitir: o formulário dos objectivos comuns não tem
   * competências nem atitudes, por isso essas listas podem nem vir no corpo.
   */
  private static <T> List<T> listaOuVazia(List<T> lista) {
    return lista != null ? lista : List.of();
  }

  /** O primeiro valor com texto, pela ordem dada. */
  private String primeiroPreenchido(String... valores) {
    for (var v : valores) {
      if (StringUtils.hasText(v)) {
        return v;
      }
    }
    return null;
  }

  /**
   * Carreira/Cargo (RH_T_PARAM_CARREIRA) do vínculo actual do colaborador: a carreira PCCS ou, nos
   * vínculos sem carreira, a do escalão gravado no tiprel. Sem vínculo, a do formulário.
   */
  private Long carreiraDoColaborador(UUID funUuid, Long carreiraDoFormulario) {
    var tr = tiposRelacionamentoRepository.findAtualByFuncionarioUuid(funUuid).orElse(null);
    if (tr != null) {
      if (tr.getCarreiraId() != null && tr.getCarreiraId().getCarrPccsId() != null) {
        return tr.getCarreiraId().getCarrPccsId().getId();
      }
      var escalao = tr.getCarreiraId() != null ? tr.getCarreiraId().getEscalaoId() : tr.getEscalaoId();
      if (escalao != null && escalao.getParamCarrId() != null) {
        return escalao.getParamCarrId().getId();
      }
    }
    return carreiraDoFormulario;
  }

  /**
   * Manual de funções do colaborador: o da direção + Carreira/Cargo (+ secção, se indicada). O
   * manual deixou de ter o campo Cargo ("Carreira / Cargo" é a carreira); os manuais antigos só com
   * cargo continuam a ser encontrados quando não há carreira.
   */
  private String resolverDescricaoManual(Long institId, Long seccaoId, Long cargoId, Long carrPccsId) {
    if (institId == null || (carrPccsId == null && cargoId == null)) {
      return null;
    }

    Specification<cv.inps.rh.shared.infrastructure.persistence.entity.ParamManualFuncaoEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), ESTADO_ATIVO));
      predicates.add(cb.equal(root.get("institId").get("id"), institId));
      if (carrPccsId != null) {
        predicates.add(cb.equal(root.get("carreira").get("id"), carrPccsId));
      } else {
        predicates.add(cb.equal(root.get("cargo").get("id"), cargoId));
      }
      if (seccaoId != null) {
        predicates.add(cb.equal(root.get("seccaoId").get("id"), seccaoId));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = manualFuncaoRepository.findAll(spec, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
    return page.hasContent() ? page.getContent().getFirst().getDescricao() : null;
  }

  private boolean aplicaAoContexto(
      ParamObjetivoEntity p,
      Long institId,
      Long seccaoId,
      Long cargoId,
      Long carrPccsId) {
    if (p.getCargo() != null && cargoId != null && !p.getCargo().getId().equals(cargoId)) {
      return false;
    }
    if (p.getCargo() != null && cargoId == null) {
      return false;
    }
    if (p.getCarreira() != null && carrPccsId != null && !p.getCarreira().getId().equals(carrPccsId)) {
      return false;
    }
    if (p.getCarreira() != null && carrPccsId == null) {
      return false;
    }

    if (StringUtils.hasText(p.getAbrangencia()) && "DIRECAO".equalsIgnoreCase(p.getAbrangencia())) {
      if (p.getInstitId() == null || !p.getInstitId().getId().equals(institId)) {
        return false;
      }
    }

    if (p.getSeccaoId() != null && seccaoId != null && !p.getSeccaoId().getId().equals(seccaoId)) {
      return false;
    }
    return p.getSeccaoId() == null || seccaoId != null;
  }

  /**
   * Estado do grupo: 'C' se alguma das avaliações do ano já está concluída, 'P' se alguma
   * está em curso, 'A' caso contrário. Quem decide o 'C' é o
   * {@code ProcessoAvaliacaoService}, que o marca quando todos os períodos do ciclo têm nota.
   */
  private String resolveEstadoGrupo(List<AvaliacaoEntity> list) {
    if (list.stream().anyMatch(a -> "C".equalsIgnoreCase(a.getEstado()))) {
      return "C";
    }
    if (list.stream().anyMatch(a -> "P".equalsIgnoreCase(a.getEstado()))) {
      return "P";
    }
    return "A";
  }

  private String resolveQualitativa(List<ParamEscalaAvaliacaoEntity> escala, BigDecimal notaFinal) {
    if (notaFinal == null)
      return null;
    for (var e : escala) {
      if (e == null || e.getEstado() != cv.inps.rh.shared.application.constants.Estado.A)
        continue;
      if (e.getQuantitativaDe() == null || e.getQuantitativaAte() == null)
        continue;
      boolean ge = notaFinal.compareTo(e.getQuantitativaDe()) >= 0;
      boolean le = notaFinal.compareTo(e.getQuantitativaAte()) <= 0;
      if (ge && le) {
        return e.getQualitativa();
      }
    }
    return null;
  }

  private record GroupKey(Integer ano, Long institId, Long cargoId, UUID funUuid) {
  }
}
