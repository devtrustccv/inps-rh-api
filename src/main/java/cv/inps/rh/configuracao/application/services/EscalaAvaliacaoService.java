package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.commands.CreateEscalaAvaliacaoCommand;
import cv.inps.rh.configuracao.application.commands.UpdateEscalaAvaliacaoCommand;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRequestDTO;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListaEscalaAvaliacaoDTO;
import cv.inps.rh.configuracao.application.queries.GetEscalaAvaliacaoQuery;
import cv.inps.rh.configuracao.application.queries.GetListaEscalaAvaliacaoQuery;
import cv.inps.rh.configuracao.infrastructure.mappers.EscalaAvaliacaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaAvaliacaoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EscalaAvaliacaoService {

  private final ParamEscalaAvaliacaoEntityRepository repository;
  private final EscalaAvaliacaoMapper mapper;
  private final cv.inps.rh.shared.application.service.DominioService dominioService;

  public EscalaAvaliacaoService(
      ParamEscalaAvaliacaoEntityRepository repository,
      EscalaAvaliacaoMapper mapper,
      cv.inps.rh.shared.application.service.DominioService dominioService) {
    this.repository = repository;
    this.mapper = mapper;
    this.dominioService = dominioService;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> registar(CreateEscalaAvaliacaoCommand command) {

    var request = command.getEscalaavaliacaorequest();
    var rows = request != null ? request.getRow() : null;

    if (CollectionUtils.isEmpty(rows)) {
      throw IgrpResponseStatusException.badRequest("Lista de escala de avaliação não pode estar vazia");
    }

    validarIntervalos(rows);
    validarDominios(rows);

    var uuids = new ArrayList<String>(rows.size());
    Set<Long> keepIds = new HashSet<>();

    for (var row : rows) {
      if (row.getQuantitativaDe() != null
          && row.getQuantitativaAte() != null
          && row.getQuantitativaDe().compareTo(row.getQuantitativaAte()) > 0) {
        throw IgrpResponseStatusException.badRequest("quantitativaDe não pode ser maior que quantitativaAte");
      }

      var entity = row.getId() != null
          ? repository.findById(row.getId())
              .orElseThrow(() -> IgrpResponseStatusException.of(org.springframework.http.HttpStatus.NOT_FOUND,
                  "ParamEscalaAvaliacaoEntity not found for id: " + row.getId()))
          : mapper.toEntity(row);

      if (row.getId() != null) {
        entity.setNivel(row.getNivel());
        entity.setQualitativa(row.getQualitativa());
        entity.setDescricao(row.getDescricao());
        entity.setQuantitativaDe(row.getQuantitativaDe());
        entity.setQuantitativaAte(row.getQuantitativaAte());
      }

      if (entity.getUuid() == null) {
        entity.setUuid(UuidCreator.getTimeOrderedEpoch());
      }
      entity.setEstado(Estado.A);

      repository.save(entity);
      keepIds.add(entity.getId());
      uuids.add(entity.getUuid().toString());
    }

    Specification<ParamEscalaAvaliacaoEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), Estado.A));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var toDeactivate = new ArrayList<ParamEscalaAvaliacaoEntity>();
    for (var entity : repository.findAll(spec)) {
      if (!keepIds.contains(entity.getId())) {
        entity.setEstado(Estado.I);
        toDeactivate.add(entity);
      }
    }
    if (!toDeactivate.isEmpty()) {
      repository.saveAll(toDeactivate);
    }

    return ResponseEntity.ok(Map.of(
        "id", uuids.getFirst(),
        "ids", uuids));
  }

  @Transactional(readOnly = true)
  public EscalaAvaliacaoRequestDTO obter(GetEscalaAvaliacaoQuery query) {
    Specification<ParamEscalaAvaliacaoEntity> spec = (root, _,
        cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), Estado.A));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var rows = repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"))
        .stream()
        .map(entity -> {
          var row = new EscalaAvaliacaoRowDTO();
          row.setId(entity.getId());
          row.setNivel(entity.getNivel());
          row.setQualitativa(entity.getQualitativa());
          row.setDescricao(entity.getDescricao());
          row.setQuantitativaDe(entity.getQuantitativaDe());
          row.setQuantitativaAte(entity.getQuantitativaAte());
          return row;
        })
        .toList();

    var dto = new EscalaAvaliacaoRequestDTO();
    dto.setRow(rows);
    return dto;
  }

  @Transactional(readOnly = true)
  public WrapperListaEscalaAvaliacaoDTO listar(GetListaEscalaAvaliacaoQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());
    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));

    Specification<ParamEscalaAvaliacaoEntity> spec = (root, _,
        cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), Estado.A));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = repository.findAll(spec, pageable);

    var response = new WrapperListaEscalaAvaliacaoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent().stream().map(mapper::toResponse).toList());
    return response;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> atualizar(UpdateEscalaAvaliacaoCommand command) {

    var uuid = parseUuid(command.getId());
    var dto = command.getEscalaavaliacaorow();

    if (dto.getQuantitativaDe() != null
        && dto.getQuantitativaAte() != null
        && dto.getQuantitativaDe().compareTo(dto.getQuantitativaAte()) > 0) {
      throw IgrpResponseStatusException.badRequest("quantitativaDe não pode ser maior que quantitativaAte");
    }

    var entity = repository.findByUuidOrThrow(uuid);
    entity.setNivel(dto.getNivel());
    entity.setQualitativa(dto.getQualitativa());
    entity.setDescricao(dto.getDescricao());
    entity.setQuantitativaDe(dto.getQuantitativaDe());
    entity.setQuantitativaAte(dto.getQuantitativaAte());

    repository.save(entity);

    return ResponseEntity.ok(Map.of("id", entity.getUuid().toString()));
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

  /**
   * A spec define o nível e a classificação qualitativa por domínio
   * (NIVEIS_AVD e CLASSIFICACAO_QUALIT_AVD), não como texto livre — é o que permite
   * ao negócio mudar a régua sem tocar no código.
   *
   * <p>Enquanto o domínio estiver por preencher (só com a linha de exemplo), não se
   * valida: bloquear aqui impediria configurar a escala antes de o negócio definir
   * os valores.</p>
   */
  private void validarDominios(java.util.List<cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO> rows) {
    validarContraDominio(rows, "NIVEIS_AVD", "nível",
        r -> r.getNivel() != null ? String.valueOf(r.getNivel()) : null);
    validarContraDominio(rows, "CLASSIFICACAO_QUALIT_AVD", "classificação qualitativa",
        cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO::getQualitativa);
  }

  private void validarContraDominio(
      java.util.List<cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO> rows,
      String dominio, String rotulo,
      java.util.function.Function<cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO, String> extrator) {

    var permitidos = dominioService.getDominioMap(dominio).keySet();
    if (permitidos.size() <= 1) {
      return; // domínio ainda por preencher pelo negócio
    }

    for (var r : rows) {
      var valor = extrator.apply(r);
      if (valor != null && !permitidos.contains(valor.trim())) {
        throw IgrpResponseStatusException.badRequest(
            "O " + rotulo + " '" + valor + "' não existe no domínio " + dominio
                + ". Valores aceites: " + permitidos + ".");
      }
    }
  }

  /**
   * Os escalões têm de formar uma régua sem ambiguidades: nenhum pode começar antes de o
   * anterior acabar. Com intervalos sobrepostos, a classificação qualitativa de uma nota
   * dependeria da ordem em que os escalões são lidos — duas notas iguais podiam sair com
   * classificações diferentes.
   */
  private void validarIntervalos(java.util.List<cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO> rows) {
    var comIntervalo = rows.stream()
        .filter(r -> r.getQuantitativaDe() != null && r.getQuantitativaAte() != null)
        .sorted(java.util.Comparator.comparing(
            cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO::getQuantitativaDe))
        .toList();

    for (int i = 1; i < comIntervalo.size(); i++) {
      var anterior = comIntervalo.get(i - 1);
      var atual = comIntervalo.get(i);
      if (atual.getQuantitativaDe().compareTo(anterior.getQuantitativaAte()) <= 0) {
        throw IgrpResponseStatusException.badRequest(
            "Os intervalos da escala não podem sobrepor-se: ["
                + anterior.getQuantitativaDe() + "-" + anterior.getQuantitativaAte() + "] e ["
                + atual.getQuantitativaDe() + "-" + atual.getQuantitativaAte() + "].");
      }
    }
  }
}
