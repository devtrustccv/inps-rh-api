package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.DomainConfigDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DomainEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service("domain_type")
public class DomainService extends ConfigurationProcess<DomainConfigDTO> {

  private final DomainEntityRepository repository;

  protected DomainService(DomainEntityRepository repository, Validator validator, ObjectMapper jsonMapper) {
    super(validator, jsonMapper, DomainConfigDTO.class);
    this.repository = repository;
  }

  @Override
  protected Object create(DomainConfigDTO payload) {
    var entity = new DomainEntity();
    entity.setDominio(payload.getDominio().trim());
    entity.setValor(payload.getValor().trim());
    entity.setDescricao(payload.getDescricao().trim());
    entity.setReferencia(StringUtils.hasText(payload.getReferencia()) ? payload.getReferencia().trim() : null);
    entity.setEstado(payload.getEstado() != null ? payload.getEstado() : Estado.A);
    repository.save(entity);
    return new ConfigurationResponseIdDTO(String.valueOf(entity.getId()));
  }

  @Override
  protected Object update(String id, DomainConfigDTO payload) {
    var entity = repository.findByIdOrThrow(Long.valueOf(id));
    entity.setDominio(payload.getDominio().trim());
    entity.setValor(payload.getValor().trim());
    entity.setDescricao(payload.getDescricao().trim());
    entity.setReferencia(StringUtils.hasText(payload.getReferencia()) ? payload.getReferencia().trim() : null);
    if (payload.getEstado() != null)
      entity.setEstado(payload.getEstado());
    repository.save(entity);
    return "";
  }

  @Override
  protected Object read(String id) {
    var entity = repository.findByIdOrThrow(Long.valueOf(id));
    var dto = new DomainConfigDTO();
    dto.setId(entity.getId());
    dto.setDominio(entity.getDominio());
    dto.setValor(entity.getValor());
    dto.setDescricao(entity.getDescricao());
    dto.setReferencia(entity.getReferencia());
    dto.setEstado(entity.getEstado());
    return dto;
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var dominio = filters.getOrDefault("dominio", null);
    var valor = filters.getOrDefault("valor", null);
    var descricao = filters.getOrDefault("descricao", null);
    var status = filters.containsKey("estado")
        ? Estado.valueOf(filters.get("estado"))
        : Estado.A;

    Specification<DomainEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
      predicates.add(cb.equal(root.get("estado"), status));
      if (StringUtils.hasText(dominio)) {
        predicates.add(cb.equal(root.get("dominio"), dominio));
      }
      if (StringUtils.hasText(valor)) {
        predicates.add(cb.equal(root.get("valor"), valor));
      }
      if (StringUtils.hasText(descricao)) {
        var normalized = "%" + ConfigurationUtils.normalizeAndSetToLowerCaseText(descricao) + "%";
        predicates.add(cb.like(cb.lower(root.get("descricao")), normalized));
      }
      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };

    var page = repository.findAll(spec, pageable);
    if (page.isEmpty()) {
      return List.of();
    }

    return page.stream()
        .map(e -> new DomainConfigDTO(
            e.getId(),
            e.getDominio(),
            e.getValor(),
            e.getDescricao(),
            e.getReferencia(),
            e.getEstado()
        )).collect(Collectors.toList());
  }

  @Override
  public void delete(String id) {
    var entity = repository.findByIdOrThrow(Long.valueOf(id));
    repository.delete(entity);
  }
}
