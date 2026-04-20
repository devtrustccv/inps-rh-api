package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.EscalaoRequestDTO;
import cv.inps.rh.configuracao.application.dto.EscalaoResponseDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCategoriaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Transactional
@Service("escalao_type")
public class EscalaoService extends ConfigurationProcess<EscalaoRequestDTO> {

  private final ParamEscalaoEntityRepository escalaoRepository;
  private final ParamCarreiraEntityRepository carreiraRepository;
  private final ParamCategoriaEntityRepository categoriaRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;

  protected EscalaoService(
      ParamEscalaoEntityRepository escalaoRepository,
      ParamCarreiraEntityRepository carreiraRepository,
      ParamCategoriaEntityRepository categoriaRepository, TiposRelacionamentoEntityRepository tiposRelacionamentoRepository,
      Validator validator, ObjectMapper jsonMapper
  ) {
    super(validator, jsonMapper, EscalaoRequestDTO.class);
    this.escalaoRepository = escalaoRepository;
    this.carreiraRepository = carreiraRepository;
    this.categoriaRepository = categoriaRepository;
    this.tiposRelacionamentoRepository = tiposRelacionamentoRepository;
  }

  @Override
  public Object create(EscalaoRequestDTO dto) {

    var entity = new ParamEscalaoEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado(Estado.A);
    entity.setEscalao(dto.getEscalao());
    entity.setNivelReferencia(dto.getNivelReferencia());
    entity.setCodigo(dto.getEscalao() + "||" + dto.getNivelReferencia());
    entity.setValor(new BigDecimal(dto.getSalario()));
    entity.setDataInicio(dto.getDataInicio());
    entity.setDataFim(dto.getDataFim());
    entity.setParamCarrId(carreiraRepository.findByUuidOrThrow(UUID.fromString(dto.getCarreiraId())));

    if (StringUtils.hasText(dto.getCategoriaId())) {
      var categoria = categoriaRepository.findByUuidOrThrow(UUID.fromString(dto.getCategoriaId()));
      entity.setParamCategoriaId(categoria);
    }

    escalaoRepository.save(entity);

    return new ConfigurationResponseIdDTO(entity.getUuid().toString());
  }

  @Override
  public Object update(String uuid, EscalaoRequestDTO dto) {

    var entity = escalaoRepository.findByUuidOrThrow(UUID.fromString(uuid));

    entity.setEscalao(dto.getEscalao());
    entity.setNivelReferencia(dto.getNivelReferencia());
    entity.setCodigo(dto.getEscalao() + dto.getNivelReferencia());
    entity.setValor(new BigDecimal(dto.getSalario()));
    entity.setDataInicio(dto.getDataInicio());
    entity.setDataFim(dto.getDataFim());
    entity.setParamCarrId(carreiraRepository.findByUuidOrThrow(UUID.fromString(dto.getCarreiraId())));

    if (StringUtils.hasText(dto.getEstado()))
      entity.setEstado(Estado.valueOf(dto.getEstado()));

    if (StringUtils.hasText(dto.getCategoriaId())) {
      var categoria = categoriaRepository.findByUuidOrThrow(UUID.fromString(dto.getCategoriaId()));
      entity.setParamCategoriaId(categoria);
    } else
      entity.setParamCategoriaId(null);

    escalaoRepository.save(entity);

    return "";
  }

  @Override
  public Object read(String uuid) {
    var entity = escalaoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    return buildResponse(entity);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var carreiraId = filters.get(ParamEscalaoEntity_.PARAM_CARR_ID);
    var escalao = filters.get(ParamEscalaoEntity_.ESCALAO);
    var estado = filters.containsKey(ParamEscalaoEntity_.ESTADO)
        ? Estado.valueOf(filters.get(ParamEscalaoEntity_.ESTADO))
        : Estado.A;

    Specification<ParamEscalaoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(ParamEscalaoEntity_.estado), estado));

      if (StringUtils.hasText(carreiraId))
        predicates.add(cb.equal(root.get(ParamEscalaoEntity_.paramCarrId).get(ParamCarreiraEntity_.uuid), UUID.fromString(carreiraId)));

      if (StringUtils.hasText(escalao))
        predicates.add(cb.like(cb.lower(root.get(ParamEscalaoEntity_.escalao)), "%" + escalao.toLowerCase() + "%"));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = escalaoRepository.findAll(spec, pageable);

    return page.stream()
        .map(this::buildResponse)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {

    var entity = escalaoRepository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoRepository.existsByCarreiraId_EscalaoId(entity))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    entity.setEstado(Estado.E);
    escalaoRepository.save(entity);
  }

  private EscalaoResponseDTO buildResponse(ParamEscalaoEntity e) {

    var dto = new EscalaoResponseDTO();
    dto.setId(e.getUuid().toString());
    dto.setEscalao(e.getEscalao());
    dto.setNivelReferencia(e.getNivelReferencia());
    dto.setSalario(e.getValor().toString());
    dto.setDataInicio(e.getDataInicio());
    dto.setDataFim(e.getDataFim());
    dto.setEstado(e.getEstado().getCode());
    dto.setEstadoDescricao(e.getEstado().getDescription());

    ofNullable(e.getParamCarrId())
        .ifPresent(c -> {
          dto.setCarreiraId(c.getUuid().toString());
          dto.setCarreiraDesc(c.getNome());
        });

    ofNullable(e.getParamCategoriaId())
        .ifPresent(c -> {
          dto.setCategoriaId(c.getUuid().toString());
          dto.setCategoriaDesc(c.getNome());
        });

    return dto;
  }
}

