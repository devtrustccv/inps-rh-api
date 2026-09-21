package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.commands.ClonarComponenteAvaliacaoCommand;
import cv.inps.rh.configuracao.application.commands.CreateComponentesAvaliacaoCommand;
import cv.inps.rh.configuracao.application.commands.InativarComponenteAvaliacaoCommand;
import cv.inps.rh.configuracao.application.commands.UpdateComponenteAvaliacaoCommand;
import cv.inps.rh.configuracao.application.dto.*;
import cv.inps.rh.configuracao.application.queries.GetComponenteAvaliacaoQuery;
import cv.inps.rh.configuracao.application.queries.GetListaComponentesAvaliacaoQuery;
import cv.inps.rh.configuracao.infrastructure.mappers.ComponenteAvaliacaoMapper;
import cv.inps.rh.shared.application.constants.TipoPeriodicidade;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ComponenteAvaliacaoService {

  private static final BigDecimal CEM = BigDecimal.valueOf(100);
  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_INATIVO = "I";
  private static final String ABRANGENCIA_DEFAULT = "INPS";

  private static final String COMPONENTE_OBJETIVO = "OBJETIVO";
  private static final String COMPONENTE_COMP_COMPORTAMENTAL = "COMPETENCIA_COMPORTAMENTAL";
  private static final String COMPONENTE_COMP_TECNICA = "COMPETENCIA_TECNICA";
  private static final String COMPONENTE_ATITUDE = "ATITUDE_PESSOAL";

  /** Ordena por número de ordem, empurrando os nulos para o fim. */
  private static final Comparator<ParamObjetivoEntity> POR_ORDEM =
      Comparator.comparing(ParamObjetivoEntity::getNumeroOrdem,
          Comparator.nullsLast(Comparator.naturalOrder()));

  private final ParamObjetivoDetEntityRepository detRepository;
  private final ParamObjetivoEntityRepository objetivoRepository;
  private final AvaliacaoEntityRepository avaliacaoRepository;
  private final ComponenteAvaliacaoMapper mapper;

  public ComponenteAvaliacaoService(
      ParamObjetivoDetEntityRepository detRepository,
      ParamObjetivoEntityRepository objetivoRepository,
      AvaliacaoEntityRepository avaliacaoRepository,
      ComponenteAvaliacaoMapper mapper) {
    this.detRepository = detRepository;
    this.objetivoRepository = objetivoRepository;
    this.avaliacaoRepository = avaliacaoRepository;
    this.mapper = mapper;
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> registar(CreateComponentesAvaliacaoCommand command) {

    var dto = command.getComponenteavaliacaorequest();

    if (detRepository.existsByAno(dto.getAno())) {
      throw IgrpResponseStatusException.conflict("Já existe parametrização para o ano: " + dto.getAno());
    }

    var periodicidade = TipoPeriodicidade.fromValorOrThrow(dto.getPeriodicidade());
    validarPesosEPonderacoes(dto);

    var det = new ParamObjetivoDetEntity();
    det.setUuid(UuidCreator.getTimeOrderedEpoch());
    det.setVersao(1);
    det.setEstado(ESTADO_ATIVO);
    aplicarCabecalho(det, dto, periodicidade);
    det.setObjetivos(construirLinhas(det, dto));

    detRepository.save(det);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(sucesso(det, "Parametrização criada para o ano " + det.getAno() + "."));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> atualizar(UpdateComponenteAvaliacaoCommand command) {
    var dto = command.getComponenteavaliacaorequest();
    var det = obterEntidade(command.getId());

    if (dto.getAno() != null && !dto.getAno().equals(det.getAno()) && detRepository.existsByAno(dto.getAno())) {
      throw IgrpResponseStatusException.conflict("Já existe parametrização para o ano: " + dto.getAno());
    }

    // As linhas sao apagadas e recriadas; se ja houver avaliacoes a apontar para elas
    // a BD recusa (FK_AVD_PARAM_OBJECTIVO). Recusar aqui da uma mensagem util em vez
    // de deixar escapar um ORA-02292 para o cliente.
    if (avaliacaoRepository.existsByAno(det.getAno())) {
      throw IgrpResponseStatusException.conflict(
          "Não é possível editar: já existem objectivos definidos para o ano " + det.getAno()
              + ". Clone a parametrização para um ano novo.");
    }

    var periodicidade = TipoPeriodicidade.fromValorOrThrow(dto.getPeriodicidade());
    validarPesosEPonderacoes(dto);

    var existentes = det.getObjetivos();
    if (existentes != null && !existentes.isEmpty()) {
      objetivoRepository.deleteAll(existentes);
    }

    aplicarCabecalho(det, dto, periodicidade);
    det.setVersao(det.getVersao() != null ? det.getVersao() + 1 : 1);
    det.setObjetivos(construirLinhas(det, dto));

    detRepository.save(det);

    return ResponseEntity.ok(sucesso(det, "Parametrização do ano " + det.getAno() + " atualizada."));
  }

  /**
   * Clona uma parametrização para um ano novo, copiando cabeçalho e todas as linhas.
   *
   * <p>Funciona independentemente do estado da origem — clonar um ciclo inativo é a forma
   * normal de reaproveitar a parametrização do ano anterior. O clone nasce sempre ativo e
   * na versão 1, porque é um ciclo novo, não uma revisão do antigo.</p>
   */
  @Transactional
  public ResponseEntity<SuccessResponseDTO> clonar(ClonarComponenteAvaliacaoCommand command) {
    var origem = obterEntidade(command.getId());
    var pedido = command.getClonarcomponenteavaliacaorequest();

    if (pedido == null || pedido.getAno() == null) {
      throw IgrpResponseStatusException.badRequest("O ano do novo ciclo é obrigatório.");
    }
    if (detRepository.existsByAno(pedido.getAno())) {
      throw IgrpResponseStatusException.conflict("Já existe parametrização para o ano: " + pedido.getAno());
    }

    // Em branco herda a periodicidade da origem; preenchida, tem de ser válida.
    var periodicidade = StringUtils.hasText(pedido.getPeriodicidade())
        ? TipoPeriodicidade.fromValorOrThrow(pedido.getPeriodicidade()).name()
        : origem.getPeriodicidade();

    var clone = new ParamObjetivoDetEntity();
    clone.setUuid(UuidCreator.getTimeOrderedEpoch());
    clone.setAno(pedido.getAno());
    clone.setPeriodicidade(periodicidade);
    clone.setPesoComportamentais(origem.getPesoComportamentais());
    clone.setPesoTecnica(origem.getPesoTecnica());
    clone.setPonderacaoObjetivo(origem.getPonderacaoObjetivo());
    clone.setPonderacaoCompetencia(origem.getPonderacaoCompetencia());
    clone.setPonderacaoAtitudePess(origem.getPonderacaoAtitudePess());
    clone.setVersao(1);
    clone.setEstado(ESTADO_ATIVO);

    var linhasOrigem = origem.getObjetivos() != null ? origem.getObjetivos() : List.<ParamObjetivoEntity>of();
    var linhas = new ArrayList<ParamObjetivoEntity>(linhasOrigem.size());
    linhasOrigem.forEach(o -> linhas.add(clonarLinha(clone, o)));
    clone.setObjetivos(linhas);

    detRepository.save(clone);

    return ResponseEntity.status(HttpStatus.CREATED).body(sucesso(clone,
        "Parametrização do ano " + origem.getAno() + " clonada para " + clone.getAno()
            + " (" + linhas.size() + " linhas)."));
  }

  /**
   * Inativa a parametrização de um ano.
   *
   * <p>Só é possível enquanto não houver nenhuma avaliação lançada nesse ano em RH_T_AVD:
   * a partir daí o ciclo está em curso e desligá-lo deixaria avaliações a apontar para
   * componentes inativos.</p>
   */
  @Transactional
  public ResponseEntity<SuccessResponseDTO> inativar(InativarComponenteAvaliacaoCommand command) {
    var det = obterEntidade(command.getId());

    if (ESTADO_INATIVO.equalsIgnoreCase(det.getEstado())) {
      throw IgrpResponseStatusException.conflict(
          "A parametrização do ano " + det.getAno() + " já está inativa.");
    }
    if (avaliacaoRepository.existsByAno(det.getAno())) {
      throw IgrpResponseStatusException.conflict(
          "Não é possível inativar: já existem objectivos definidos para o ano " + det.getAno() + ".");
    }

    det.setEstado(ESTADO_INATIVO);
    if (det.getObjetivos() != null) {
      det.getObjetivos().forEach(o -> o.setEstado(ESTADO_INATIVO));
    }
    detRepository.save(det);

    return ResponseEntity.ok(sucesso(det, "Parametrização do ano " + det.getAno() + " inativada."));
  }

  @Transactional(readOnly = true)
  public ComponenteAvaliacaoResponseDTO obter(GetComponenteAvaliacaoQuery query) {
    return obter(query.getId());
  }

  @Transactional(readOnly = true)
  public ComponenteAvaliacaoResponseDTO obter(String id) {
    return toResponse(obterEntidade(id));
  }

  @Transactional(readOnly = true)
  public WrapperListComponenteAvaliacaoDTO listar(GetListaComponentesAvaliacaoQuery query) {
    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "ano", "id"));
    var page = detRepository.findAll(filtro(query), pageable);

    // Um exists por ano distinto da página, em vez de um por linha.
    var anosComAvaliacao = page.getContent().stream()
        .map(ParamObjetivoDetEntity::getAno)
        .distinct()
        .filter(avaliacaoRepository::existsByAno)
        .toList();

    var response = new WrapperListComponenteAvaliacaoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent().stream()
        .map(det -> toResumo(det, !anosComAvaliacao.contains(det.getAno())))
        .toList());
    return response;
  }

  private Specification<ParamObjetivoDetEntity> filtro(GetListaComponentesAvaliacaoQuery query) {
    return (root, criteria, cb) -> {
      var predicates = new ArrayList<Predicate>();
      if (query.getAno() != null) {
        predicates.add(cb.equal(root.get("ano"), query.getAno()));
      }
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(cb.upper(root.get("estado")), query.getEstado().trim().toUpperCase()));
      }
      return predicates.isEmpty() ? null : cb.and(predicates.toArray(Predicate[]::new));
    };
  }

  private void validarPesosEPonderacoes(ComponenteAvaliacaoRequestDTO dto) {
    var somaPonderacoes = dto.getPonderacaoObjetivo()
        .add(dto.getPonderacaoCompetencia())
        .add(dto.getPonderacaoAtitudePessoal());
    if (somaPonderacoes.compareTo(CEM) != 0) {
      throw IgrpResponseStatusException.badRequest("A soma das ponderações globais deve ser 100%");
    }

    var somaPesosCompetencias = dto.getPesoComportamentais().add(dto.getPesoTecnica());
    if (somaPesosCompetencias.compareTo(CEM) != 0) {
      throw IgrpResponseStatusException.badRequest("A soma dos pesos das competências deve ser 100%");
    }
  }

  private void aplicarCabecalho(ParamObjetivoDetEntity det, ComponenteAvaliacaoRequestDTO dto,
      TipoPeriodicidade periodicidade) {
    det.setAno(dto.getAno());
    det.setPeriodicidade(periodicidade.name());
    det.setPesoComportamentais(dto.getPesoComportamentais());
    det.setPesoTecnica(dto.getPesoTecnica());
    det.setPonderacaoObjetivo(dto.getPonderacaoObjetivo());
    det.setPonderacaoCompetencia(dto.getPonderacaoCompetencia());
    det.setPonderacaoAtitudePess(dto.getPonderacaoAtitudePessoal());
  }

  private List<ParamObjetivoEntity> construirLinhas(ParamObjetivoDetEntity det,
      ComponenteAvaliacaoRequestDTO dto) {

    var linhas = new ArrayList<ParamObjetivoEntity>();

    dto.getObjectivosInps().forEach(r ->
        porCargo(r, c -> linhas.add(novaLinha(mapper.toEntity(det, r, COMPONENTE_OBJETIVO)))));

    dto.getCompetenciasComportamentais().forEach(r ->
        porCargo(r, c -> linhas.add(novaLinha(
            mapper.toEntity(det, r, COMPONENTE_COMP_COMPORTAMENTAL, ABRANGENCIA_DEFAULT)))));

    dto.getCompetenciasTecnicas().forEach(r ->
        porCargo(r, c -> linhas.add(novaLinha(
            mapper.toEntity(det, r, COMPONENTE_COMP_TECNICA, ABRANGENCIA_DEFAULT)))));

    // A atitude pessoal não traz número de ordem no pedido: usa-se a ordem do array.
    for (int i = 0; i < dto.getAtitudesPessoais().size(); i++) {
      var r = dto.getAtitudesPessoais().get(i);
      var ordem = i + 1;
      porCargo(r, c -> linhas.add(novaLinha(
          mapper.toEntity(det, r, COMPONENTE_ATITUDE, ABRANGENCIA_DEFAULT, ordem))));
    }

    return linhas;
  }

  /**
   * Executa a criação da linha uma vez por cargo escolhido.
   *
   * <p>No ecrã o cargo é um multiselect, mas RH_T_PARAM_OBJETIVO.CARGO_ID só guarda um,
   * por isso cada cargo dá origem à sua própria linha. Com "aplicar a todos", ou com um
   * único cargo, corre uma vez só. O {@code cargoId} da linha é posicionado antes de cada
   * passagem para o mapper o ler.</p>
   */
  private void porCargo(ParamLinhaBaseRequestDTO linha, java.util.function.Consumer<Long> criar) {
    var cargos = linha.getCargoIds();

    if (Boolean.TRUE.equals(linha.getAplicarATodos()) || cargos == null || cargos.isEmpty()) {
      criar.accept(linha.getCargoId());
      return;
    }

    var originais = linha.getCargoId();
    try {
      cargos.stream().filter(java.util.Objects::nonNull).distinct().forEach(c -> {
        linha.setCargoId(c);
        criar.accept(c);
      });
    } finally {
      linha.setCargoId(originais);
    }
  }

  private ParamObjetivoEntity novaLinha(ParamObjetivoEntity e) {
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setEstado(ESTADO_ATIVO);
    return e;
  }

  private ParamObjetivoEntity clonarLinha(ParamObjetivoDetEntity destino, ParamObjetivoEntity origem) {
    var e = new ParamObjetivoEntity();
    e.setParamObjetivoDet(destino);
    e.setComponente(origem.getComponente());
    e.setAbrangencia(origem.getAbrangencia());
    e.setNumeroOrdem(origem.getNumeroOrdem());
    e.setDescricao(origem.getDescricao());
    e.setKpi(origem.getKpi());
    e.setPonderacao(origem.getPonderacao());
    e.setCargo(origem.getCargo());
    e.setCarreira(origem.getCarreira());
    e.setInstitId(origem.getInstitId());
    e.setSeccaoId(origem.getSeccaoId());
    return novaLinha(e);
  }

  private ParamObjetivoDetEntity obterEntidade(String id) {
    var uuid = parseUuid(id);
    return detRepository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "ParamObjetivoDetEntity not found for id: " + uuid));
  }

  private SuccessResponseDTO sucesso(ParamObjetivoDetEntity det, String mensagem) {
    var dto = new SuccessResponseDTO();
    dto.setSucesso(true);
    dto.setId(det.getUuid() != null ? det.getUuid().toString() : null);
    dto.setMensagem(mensagem);
    return dto;
  }

  private ComponenteAvaliacaoResumoResponseDTO toResumo(ParamObjetivoDetEntity det, boolean podeInativar) {
    var dto = new ComponenteAvaliacaoResumoResponseDTO();
    dto.setId(det.getId());
    dto.setUuid(det.getUuid() != null ? det.getUuid().toString() : null);
    dto.setAno(det.getAno());
    dto.setPeriodicidade(det.getPeriodicidade());
    dto.setPeriodicidadeDescricao(descricaoPeriodicidade(det.getPeriodicidade()));
    dto.setPesoComportamentais(det.getPesoComportamentais());
    dto.setPesoTecnica(det.getPesoTecnica());
    dto.setPonderacaoObjetivo(det.getPonderacaoObjetivo());
    dto.setPonderacaoCompetencia(det.getPonderacaoCompetencia());
    dto.setPonderacaoAtitudePessoal(det.getPonderacaoAtitudePess());
    dto.setEstado(det.getEstado());
    dto.setEstadoDescricao(ESTADO_INATIVO.equalsIgnoreCase(det.getEstado()) ? "Inativo" : "Ativo");
    dto.setVersao(det.getVersao());
    dto.setPodeInativar(podeInativar && !ESTADO_INATIVO.equalsIgnoreCase(det.getEstado()));
    return dto;
  }

  private ComponenteAvaliacaoResponseDTO toResponse(ParamObjetivoDetEntity det) {
    var dto = new ComponenteAvaliacaoResponseDTO();
    dto.setId(det.getId());
    dto.setUuid(det.getUuid() != null ? det.getUuid().toString() : null);
    dto.setAno(det.getAno());
    dto.setPeriodicidade(det.getPeriodicidade());
    dto.setPesoComportamentais(det.getPesoComportamentais());
    dto.setPesoTecnica(det.getPesoTecnica());
    dto.setPonderacaoObjetivo(det.getPonderacaoObjetivo());
    dto.setPonderacaoCompetencia(det.getPonderacaoCompetencia());
    dto.setPonderacaoAtitudePessoal(det.getPonderacaoAtitudePess());
    dto.setEstado(det.getEstado());

    var objetivos = det.getObjetivos() != null ? det.getObjetivos() : List.<ParamObjetivoEntity>of();

    dto.setObjectivosInps(linhasDe(objetivos, COMPONENTE_OBJETIVO, this::toObjectivoLinha));
    dto.setCompetenciasComportamentais(
        linhasDe(objetivos, COMPONENTE_COMP_COMPORTAMENTAL, this::toCompComportamentalLinha));
    dto.setCompetenciasTecnicas(
        linhasDe(objetivos, COMPONENTE_COMP_TECNICA, this::toCompTecnicaLinha));
    dto.setAtitudesPessoais(linhasDe(objetivos, COMPONENTE_ATITUDE, this::toAtitudeLinha));

    return dto;
  }

  private <T> List<T> linhasDe(List<ParamObjetivoEntity> objetivos, String componente,
      java.util.function.Function<ParamObjetivoEntity, T> converter) {
    return objetivos.stream()
        .filter(o -> componente.equalsIgnoreCase(o.getComponente()))
        .sorted(POR_ORDEM)
        .map(converter)
        .toList();
  }

  private ObjectivoInpsLinhaResponseDTO toObjectivoLinha(ParamObjetivoEntity e) {
    var dto = new ObjectivoInpsLinhaResponseDTO();
    fillBase(dto, e);
    dto.setNumeroOrdem(e.getNumeroOrdem());
    dto.setAbrangencia(e.getAbrangencia());
    dto.setInstitId(e.getInstitId() != null ? e.getInstitId().getId() : null);
    dto.setDescricao(e.getDescricao());
    dto.setKpi(e.getKpi());
    return dto;
  }

  private CompetenciaComportamentalLinhaResponseDTO toCompComportamentalLinha(ParamObjetivoEntity e) {
    var dto = new CompetenciaComportamentalLinhaResponseDTO();
    fillBase(dto, e);
    dto.setAbrangencia(e.getAbrangencia());
    dto.setNumeroOrdem(e.getNumeroOrdem());
    return dto;
  }

  private CompetenciaTecnicaLinhaResponseDTO toCompTecnicaLinha(ParamObjetivoEntity e) {
    var dto = new CompetenciaTecnicaLinhaResponseDTO();
    fillBase(dto, e);
    dto.setAbrangencia(e.getAbrangencia());
    dto.setNumeroOrdem(e.getNumeroOrdem());
    return dto;
  }

  private AtitudePessoalLinhaResponseDTO toAtitudeLinha(ParamObjetivoEntity e) {
    var dto = new AtitudePessoalLinhaResponseDTO();
    fillBase(dto, e);
    dto.setAbrangencia(e.getAbrangencia());
    dto.setDescricao(e.getDescricao());
    return dto;
  }

  private void fillBase(ParamLinhaBaseResponseDTO dto, ParamObjetivoEntity e) {
    dto.setId(e.getId());
    dto.setUuid(e.getUuid() != null ? e.getUuid().toString() : null);
    dto.setAplicarATodos(e.getCargo() == null);
    dto.setCargoId(e.getCargo() != null ? e.getCargo().getId() : null);
    dto.setCarrPccsId(e.getCarreira() != null ? e.getCarreira().getId() : null);
    dto.setPonderacao(e.getPonderacao());
    dto.setComponente(e.getComponente());
    dto.setEstado(e.getEstado());
  }

  /** "SEMESTRAL" -> "Semestral". A grelha mostra o rótulo, não o código. */
  private String descricaoPeriodicidade(String codigo) {
    if (!StringUtils.hasText(codigo)) {
      return null;
    }
    var c = codigo.trim();
    return c.charAt(0) + c.substring(1).toLowerCase();
  }

  private UUID parseUuid(String raw) {
    if (!StringUtils.hasText(raw)) {
      throw IgrpResponseStatusException.badRequest("UUID inválido: " + raw);
    }
    try {
      return UUID.fromString(raw.trim());
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("UUID inválido: " + raw);
    }
  }
}
