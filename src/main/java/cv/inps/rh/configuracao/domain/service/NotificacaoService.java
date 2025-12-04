package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.NotificacaoRequestDTO;
import cv.inps.rh.configuracao.application.dto.NotificacaoResponseDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamNotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
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

@Transactional
@Service("notificacao_type")
public class NotificacaoService extends ConfigurationProcess<NotificacaoRequestDTO> {

  private final ParamNotificacaoEntityRepository notificacaoRepository;

  protected NotificacaoService(
      Validator validator,
      ObjectMapper jsonMapper,
      ParamNotificacaoEntityRepository notificacaoRepository
  ) {
    super(validator, jsonMapper);
    this.notificacaoRepository = notificacaoRepository;
  }

  @Override
  protected Class<NotificacaoRequestDTO> getType() {
    return NotificacaoRequestDTO.class;
  }

  @Override
  public Object create(NotificacaoRequestDTO dto) {

    var entity = new ParamNotificacaoEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setReferencia(dto.getReferencia());
    entity.setAssunto(dto.getAssunto());
    entity.setCorpo(dto.getCorpo());
    entity.setEstado(Estado.A.getCode());
    notificacaoRepository.save(entity);

    return new ConfigurationResponseIdDTO(entity.getUuid().toString());
  }

  @Override
  public Object update(String id, NotificacaoRequestDTO dto) {

    var entity = notificacaoRepository.findByUuidOrThrow(UUID.fromString(id));
    if (StringUtils.hasText(dto.getEstado())) {
      entity.setEstado(dto.getEstado());
      notificacaoRepository.save(entity);
    }

    return "";
  }

  @Override
  public Object read(String id) {
    var entity = notificacaoRepository.findByUuidOrThrow(UUID.fromString(id));
    return buildResponse(entity);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);
    var reference = filters.get("referencia");
    var estado = filters.containsKey("estado") ? filters.get("estado") : Estado.A.getCode();

    Specification<ParamNotificacaoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), estado));

      if (StringUtils.hasText(reference))
        predicates.add(cb.like(cb.lower(root.get("referencia")), reference));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = notificacaoRepository.findAll(spec, pageable);

    return data.stream()
        .map(this::buildResponse)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String id) {
    var entity = notificacaoRepository.findByUuidOrThrow(UUID.fromString(id));
    entity.setEstado(Estado.E.getCode());
    notificacaoRepository.save(entity);
  }

  private NotificacaoResponseDTO buildResponse(ParamNotificacaoEntity e) {
    var dto = new NotificacaoResponseDTO();
    dto.setId(e.getId().toString());
    dto.setEstadoDescricao(e.getEstado());
    dto.setAssunto(e.getAssunto());
    dto.setCorpo(e.getCorpo());
    dto.setReferencia(e.getReferencia());
    dto.setEstado(e.getEstado());
    return dto;
  }
}


