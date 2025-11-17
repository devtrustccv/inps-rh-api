package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("notificacao_type")
public class NotificacaoService extends ConfigurationProcess<NotificacaoRequestDTO> {

  private final ParamNotificacaoEntityRepository notificacaoRepository;

  public NotificacaoService(
      Validator validator,
      ObjectMapper jsonMapper,
      ParamNotificacaoEntityRepository notificacaoRepository
  ) {
    super(validator, jsonMapper, NotificacaoRequestDTO.class);
    this.notificacaoRepository = notificacaoRepository;
  }

  @Override
  public Object create(NotificacaoRequestDTO dto) {

    var entity = new ParamNotificacaoEntity();
    // TODO 17/11/2025 15:33 add uuid field here ?
    entity.setReferencia(dto.getReferencia());
    entity.setAssunto(dto.getAssunto());
    entity.setCorpo(dto.getCorpo());
    entity.setEstado(Estado.A.getCode());
    notificacaoRepository.save(entity);

    return new ConfigurationResponseIdDTO(entity.getId().toString());
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
    var referencia = filters.get("referencia");
    var estado = filters.getOrDefault("estado", "A");

    Specification<ParamNotificacaoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), estado));

      if (StringUtils.hasText(referencia)) {
        predicates.add(cb.like(cb.lower(root.get("referencia")), referencia));
      }

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


