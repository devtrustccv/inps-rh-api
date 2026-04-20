package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.DomainConfigDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListDomainDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DomainEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;

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
  public Object list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var dominio = filters.getOrDefault("dominio", null);
    var valor = filters.getOrDefault("valor", null);
    var descricao = filters.getOrDefault("descricao", null);
    var search = filters.getOrDefault("search", null);
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
      if (StringUtils.hasText(search)) {
        var normalized = "%" + ConfigurationUtils.normalizeAndSetToLowerCaseText(search) + "%";
        var dominioLike = cb.like(cb.lower(root.get("dominio")), normalized);
        var valorLike = cb.like(cb.lower(root.get("valor")), normalized);
        var descricaoLike = cb.like(cb.lower(root.get("descricao")), normalized);
        predicates.add(cb.or(dominioLike, valorLike, descricaoLike));
      }
      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };

    var resultPage = repository.findAll(spec, pageable);

    var response = new WrapperListDomainDTO();
    PageMapper.fillPagination(resultPage, response);
    response.setContent(resultPage.getContent().stream().map(this::toResponse).toList());

    return response;
  }

  private DomainConfigDTO toResponse(DomainEntity e) {
    var dto = new DomainConfigDTO();
    dto.setId(e.getId());
    dto.setDominio(e.getDominio());
    dto.setValor(e.getValor());
    dto.setDescricao(e.getDescricao());
    dto.setReferencia(e.getReferencia());
    dto.setEstado(e.getEstado());
    return dto;
  }

  @Override
  public void delete(String id) {
    var entity = repository.findByIdOrThrow(Long.valueOf(id));
    repository.delete(entity);
  }
}
