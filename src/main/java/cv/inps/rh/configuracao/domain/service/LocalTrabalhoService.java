package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.LocalTrabalhoRequestDTO;
import cv.inps.rh.configuracao.application.dto.LocalTrabalhoResponseDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamLocalTrabEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Transactional
@Service("local_trabalho_type")
public class LocalTrabalhoService extends ConfigurationProcess<LocalTrabalhoRequestDTO> {

  private final ParamLocalTrabEntityRepository localRepository;
  private final GeografiaEntityRepository geografiaRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;

  protected LocalTrabalhoService(
      ParamLocalTrabEntityRepository localRepository,
      GeografiaEntityRepository geografiaRepository, TiposRelacionamentoEntityRepository tiposRelacionamentoRepository,
      Validator validator, ObjectMapper jsonMapper
  ) {
    super(validator, jsonMapper, LocalTrabalhoRequestDTO.class);
    this.localRepository = localRepository;
    this.geografiaRepository = geografiaRepository;
    this.tiposRelacionamentoRepository = tiposRelacionamentoRepository;
  }

  @Override
  public Object create(LocalTrabalhoRequestDTO dto) {

    var entity = new ParamLocalTrabEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado(Estado.A);
    entity.setNome(dto.getLocal());
    entity.setNomeNormalizado(ConfigurationUtils.normalizeAndSetToLowerCaseText(dto.getLocal()));
    entity.setUpsId(Long.valueOf(dto.getUps()));
    entity.setPaisId(geografiaRepository.findByIdOrThrow(Long.valueOf(dto.getPais())));

    if (StringUtils.hasText(dto.getIlha())) {
      var island = geografiaRepository.findByIdOrThrow(Long.valueOf(dto.getIlha()));
      entity.setIlhaId(island);
    }

    localRepository.save(entity);

    return new ConfigurationResponseIdDTO(entity.getUuid().toString());
  }

  @Override
  public Object update(String uuid, LocalTrabalhoRequestDTO dto) {

    var entity = localRepository.findByUuidOrThrow(UUID.fromString(uuid));
    entity.setNome(dto.getLocal());
    entity.setNomeNormalizado(ConfigurationUtils.normalizeAndSetToLowerCaseText(dto.getLocal()));
    entity.setUpsId(Long.valueOf(dto.getUps()));
    entity.setPaisId(geografiaRepository.findByIdOrThrow(Long.valueOf(dto.getPais())));

    if (StringUtils.hasText(dto.getEstado()))
      entity.setEstado(Estado.valueOf(dto.getEstado()));

    if (StringUtils.hasText(dto.getIlha())) {
      var island = geografiaRepository.findByIdOrThrow(Long.valueOf(dto.getIlha()));
      entity.setIlhaId(island);
    } else
      entity.setIlhaId(null);

    localRepository.save(entity);

    return "";
  }

  @Override
  public Object read(String uuid) {
    var entity = localRepository.findByUuidOrThrow(UUID.fromString(uuid));
    return buildResponse(entity);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);
    var workPlace = filters.get("local");
    var ups = filters.get(ParamLocalTrabEntity_.UPS_ID);

    var estado = filters.containsKey(ParamLocalTrabEntity_.ESTADO)
        ? Estado.valueOf(filters.get(ParamLocalTrabEntity_.ESTADO))
        : Estado.A;

    Specification<ParamLocalTrabEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(ParamLocalTrabEntity_.estado), estado));

      if (StringUtils.hasText(workPlace)) {
        var normalizeText = ConfigurationUtils.normalizeAndSetToLowerCaseText(workPlace);
        predicates.add(cb.like(cb.lower(root.get(ParamLocalTrabEntity_.nome)), "%" + normalizeText + "%"));
      }

      if (StringUtils.hasText(ups))
        predicates.add(cb.equal(root.get(ParamLocalTrabEntity_.upsId), Long.valueOf(ups)));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var page = localRepository.findAll(spec, pageable);

    return page.stream()
        .map(this::buildResponse)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {

    var entity = localRepository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoRepository.existsByLocTrabId(entity))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    entity.setEstado(Estado.E);
    localRepository.save(entity);
  }

  private LocalTrabalhoResponseDTO buildResponse(ParamLocalTrabEntity e) {

    var dto = new LocalTrabalhoResponseDTO();
    dto.setId(e.getUuid().toString());
    dto.setLocal(e.getNome());

    var country = e.getPaisId();
    dto.setPais(country.getNome());
    dto.setPaisId(country.getId().toString());
    dto.setEstado(e.getEstado().name());
    dto.setEstadoDescricao(e.getEstado().getDescription());
    dto.setUps(e.getUpsId().toString());

    ofNullable(e.getIlhaId()).ifPresent(island -> {
      dto.setIlhaId(island.getId().toString());
      dto.setIlha(island.getNome());
    });
    return dto;
  }
}

