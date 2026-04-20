package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.commands.CreateComponentesAvaliacaoCommand;
import cv.inps.rh.configuracao.application.commands.UpdateComponenteAvaliacaoCommand;
import cv.inps.rh.configuracao.application.dto.*;
import cv.inps.rh.configuracao.application.queries.GetComponenteAvaliacaoQuery;
import cv.inps.rh.configuracao.application.queries.GetListaComponentesAvaliacaoQuery;
import cv.inps.rh.configuracao.infrastructure.mappers.ComponenteAvaliacaoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ComponenteAvaliacaoService {

  private static final BigDecimal CEM = BigDecimal.valueOf(100);
  private static final String ESTADO_ATIVO = "A";
  private static final String ABRANGENCIA_DEFAULT = "INPS";

  private final ParamObjetivoDetEntityRepository detRepository;
  private final ParamObjetivoEntityRepository objetivoRepository;
  private final ComponenteAvaliacaoMapper mapper;

  public ComponenteAvaliacaoService(
      ParamObjetivoDetEntityRepository detRepository,
      ParamObjetivoEntityRepository objetivoRepository,
      ComponenteAvaliacaoMapper mapper) {
    this.detRepository = detRepository;
    this.objetivoRepository = objetivoRepository;
    this.mapper = mapper;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> registar(CreateComponentesAvaliacaoCommand command) {

    var dto = command.getComponenteavaliacaorequest();

    if (detRepository.existsByAno(dto.getAno())) {
      throw IgrpResponseStatusException.conflict("Já existe parametrização para o ano: " + dto.getAno());
    }

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

    var det = new ParamObjetivoDetEntity();
    det.setUuid(UuidCreator.getTimeOrderedEpoch());
    det.setAno(dto.getAno());
    det.setPesoComportamentais(dto.getPesoComportamentais());
    det.setPesoTecnica(dto.getPesoTecnica());
    det.setPonderacaoObjetivo(dto.getPonderacaoObjetivo());
    det.setPonderacaoCompetencia(dto.getPonderacaoCompetencia());
    det.setPonderacaoAtitudePess(dto.getPonderacaoAtitudePessoal());
    det.setVersao(1);
    det.setEstado(ESTADO_ATIVO);

    var linhas = new ArrayList<ParamObjetivoEntity>(dto.getObjectivosInps().size()
        + dto.getCompetenciasComportamentais().size()
        + dto.getCompetenciasTecnicas().size()
        + dto.getAtitudesPessoais().size());

    dto.getObjectivosInps().forEach(r -> {
      var e = mapper.toEntity(det, r, "OBJETIVO");
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    dto.getCompetenciasComportamentais().forEach(r -> {
      var e = mapper.toEntity(det, r, "COMPETENCIA_COMPORTAMENTAL", ABRANGENCIA_DEFAULT);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    dto.getCompetenciasTecnicas().forEach(r -> {
      var e = mapper.toEntity(det, r, "COMPETENCIA_TECNICA", ABRANGENCIA_DEFAULT);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    for (int i = 0; i < dto.getAtitudesPessoais().size(); i++) {
      var r = dto.getAtitudesPessoais().get(i);
      var e = mapper.toEntity(det, r, "ATITUDE_PESSOAL", ABRANGENCIA_DEFAULT, i + 1);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    }

    det.setObjetivos(linhas);

    detRepository.save(det);

    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", det.getUuid()));
  }

  @Transactional(readOnly = true)
  public ComponenteAvaliacaoResponseDTO obter(GetComponenteAvaliacaoQuery query) {
    return obter(query.getId());
  }

  @Transactional(readOnly = true)
  public ComponenteAvaliacaoResponseDTO obter(String id) {
    var uuid = parseUuid(id);
    var det = detRepository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamObjetivoDetEntity not found for id: " + uuid));
    return toResponse(det);
  }

  @Transactional(readOnly = true)
  public WrapperListComponenteAvaliacaoDTO listar(GetListaComponentesAvaliacaoQuery query) {
    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    var page = detRepository.findAll(pageable);

    var response = new WrapperListComponenteAvaliacaoDTO();
    PageMapper.fillPagination(page, response);
    response.setContent(page.getContent().stream().map(this::toResumo).toList());
    return response;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> atualizar(UpdateComponenteAvaliacaoCommand command) {
    var uuid = parseUuid(command.getId());
    var dto = command.getComponenteavaliacaorequest();

    var det = detRepository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("ParamObjetivoDetEntity not found for id: " + uuid));

    if (dto.getAno() != null && !dto.getAno().equals(det.getAno()) && detRepository.existsByAno(dto.getAno())) {
      throw IgrpResponseStatusException.conflict("Já existe parametrização para o ano: " + dto.getAno());
    }

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

    var existentes = det.getObjetivos();
    if (existentes != null && !existentes.isEmpty()) {
      objetivoRepository.deleteAll(existentes);
    }

    det.setAno(dto.getAno());
    det.setPesoComportamentais(dto.getPesoComportamentais());
    det.setPesoTecnica(dto.getPesoTecnica());
    det.setPonderacaoObjetivo(dto.getPonderacaoObjetivo());
    det.setPonderacaoCompetencia(dto.getPonderacaoCompetencia());
    det.setPonderacaoAtitudePess(dto.getPonderacaoAtitudePessoal());

    var linhas = new ArrayList<ParamObjetivoEntity>(dto.getObjectivosInps().size()
        + dto.getCompetenciasComportamentais().size()
        + dto.getCompetenciasTecnicas().size()
        + dto.getAtitudesPessoais().size());

    dto.getObjectivosInps().forEach(r -> {
      var e = mapper.toEntity(det, r, "OBJETIVO");
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    dto.getCompetenciasComportamentais().forEach(r -> {
      var e = mapper.toEntity(det, r, "COMPETENCIA_COMPORTAMENTAL", ABRANGENCIA_DEFAULT);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    dto.getCompetenciasTecnicas().forEach(r -> {
      var e = mapper.toEntity(det, r, "COMPETENCIA_TECNICA", ABRANGENCIA_DEFAULT);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    });

    for (int i = 0; i < dto.getAtitudesPessoais().size(); i++) {
      var r = dto.getAtitudesPessoais().get(i);
      var e = mapper.toEntity(det, r, "ATITUDE_PESSOAL", ABRANGENCIA_DEFAULT, i + 1);
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      linhas.add(e);
    }

    det.setObjetivos(linhas);

    detRepository.save(det);

    return ResponseEntity.ok(Map.of("id", det.getUuid()));
  }

  private ComponenteAvaliacaoResumoResponseDTO toResumo(ParamObjetivoDetEntity det) {
    var dto = new ComponenteAvaliacaoResumoResponseDTO();
    dto.setId(det.getId());
    dto.setUuid(det.getUuid() != null ? det.getUuid().toString() : null);
    dto.setAno(det.getAno());
    dto.setPesoComportamentais(det.getPesoComportamentais());
    dto.setPesoTecnica(det.getPesoTecnica());
    dto.setPonderacaoObjetivo(det.getPonderacaoObjetivo());
    dto.setPonderacaoCompetencia(det.getPonderacaoCompetencia());
    dto.setPonderacaoAtitudePessoal(det.getPonderacaoAtitudePess());
    dto.setEstado(det.getEstado());
    return dto;
  }

  private ComponenteAvaliacaoResponseDTO toResponse(ParamObjetivoDetEntity det) {
    var dto = new ComponenteAvaliacaoResponseDTO();
    dto.setId(det.getId());
    dto.setUuid(det.getUuid() != null ? det.getUuid().toString() : null);
    dto.setAno(det.getAno());
    dto.setPesoComportamentais(det.getPesoComportamentais());
    dto.setPesoTecnica(det.getPesoTecnica());
    dto.setPonderacaoObjetivo(det.getPonderacaoObjetivo());
    dto.setPonderacaoCompetencia(det.getPonderacaoCompetencia());
    dto.setPonderacaoAtitudePessoal(det.getPonderacaoAtitudePess());
    dto.setEstado(det.getEstado());

    var objetivos = det.getObjetivos() != null ? det.getObjetivos() : List.<ParamObjetivoEntity>of();

    dto.setObjectivosInps(objetivos.stream()
        .filter(o -> "OBJETIVO".equalsIgnoreCase(o.getComponente()))
        .sorted((a, b) -> {
          var na = a.getNumeroOrdem();
          var nb = b.getNumeroOrdem();
          if (na == null && nb == null) return 0;
          if (na == null) return 1;
          if (nb == null) return -1;
          return na.compareTo(nb);
        })
        .map(this::toObjectivoLinha)
        .toList());

    dto.setCompetenciasComportamentais(objetivos.stream()
        .filter(o -> "COMPETENCIA_COMPORTAMENTAL".equalsIgnoreCase(o.getComponente()))
        .sorted((a, b) -> {
          var na = a.getNumeroOrdem();
          var nb = b.getNumeroOrdem();
          if (na == null && nb == null) return 0;
          if (na == null) return 1;
          if (nb == null) return -1;
          return na.compareTo(nb);
        })
        .map(this::toCompComportamentalLinha)
        .toList());

    dto.setCompetenciasTecnicas(objetivos.stream()
        .filter(o -> "COMPETENCIA_TECNICA".equalsIgnoreCase(o.getComponente()))
        .sorted((a, b) -> {
          var na = a.getNumeroOrdem();
          var nb = b.getNumeroOrdem();
          if (na == null && nb == null) return 0;
          if (na == null) return 1;
          if (nb == null) return -1;
          return na.compareTo(nb);
        })
        .map(this::toCompTecnicaLinha)
        .toList());

    dto.setAtitudesPessoais(objetivos.stream()
        .filter(o -> "ATITUDE_PESSOAL".equalsIgnoreCase(o.getComponente()))
        .sorted((a, b) -> {
          var na = a.getNumeroOrdem();
          var nb = b.getNumeroOrdem();
          if (na == null && nb == null) return 0;
          if (na == null) return 1;
          if (nb == null) return -1;
          return na.compareTo(nb);
        })
        .map(this::toAtitudeLinha)
        .toList());

    return dto;
  }

  private ObjectivoInpsLinhaResponseDTO toObjectivoLinha(ParamObjetivoEntity e) {
    var dto = new ObjectivoInpsLinhaResponseDTO();
    fillBase(dto, e);
    setField(dto, "numeroOrdem", e.getNumeroOrdem());
    setField(dto, "abrangencia", e.getAbrangencia());
    setField(dto, "institId", e.getInstitId() != null ? e.getInstitId().getId() : null);
    setField(dto, "descricao", e.getDescricao());
    setField(dto, "kpi", e.getKpi());
    return dto;
  }

  private CompetenciaComportamentalLinhaResponseDTO toCompComportamentalLinha(ParamObjetivoEntity e) {
    var dto = new CompetenciaComportamentalLinhaResponseDTO();
    fillBase(dto, e);
    setField(dto, "abrangencia", e.getAbrangencia());
    dto.setNumeroOrdem(e.getNumeroOrdem());
    return dto;
  }

  private CompetenciaTecnicaLinhaResponseDTO toCompTecnicaLinha(ParamObjetivoEntity e) {
    var dto = new CompetenciaTecnicaLinhaResponseDTO();
    fillBase(dto, e);
    setField(dto, "abrangencia", e.getAbrangencia());
    dto.setNumeroOrdem(e.getNumeroOrdem());
    return dto;
  }

  private AtitudePessoalLinhaResponseDTO toAtitudeLinha(ParamObjetivoEntity e) {
    var dto = new AtitudePessoalLinhaResponseDTO();
    fillBase(dto, e);
    setField(dto, "abrangencia", e.getAbrangencia());
    setField(dto, "descricao", e.getDescricao());
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

  private void setField(Object target, String field, Object value) {
    try {
      var f = target.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
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
