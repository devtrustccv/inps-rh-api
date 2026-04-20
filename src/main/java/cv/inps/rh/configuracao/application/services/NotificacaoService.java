package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.NotificacaoRequestDTO;
import cv.inps.rh.configuracao.application.dto.NotificacaoResponseDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamNotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
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
  private final DomainEntityRepository domainEntityRepository;

  protected NotificacaoService(
      Validator validator, ObjectMapper jsonMapper,
      ParamNotificacaoEntityRepository notificacaoRepository,
      DomainEntityRepository domainEntityRepository
  ) {

    super(validator, jsonMapper, NotificacaoRequestDTO.class);
    this.notificacaoRepository = notificacaoRepository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(NotificacaoRequestDTO dto) {

    var entity = new ParamNotificacaoEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setTipoNotificacao(dto.getTipoNotificacao());
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
    var tpNotif = domainEntityRepository.getActiveDomainByCode(Domains.TIPO_ALERTA_NOTIFICACAO.name());
    return buildResponse(entity, tpNotif);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);
    var reference = filters.get("tipoNotificacao");
    var estado = filters.containsKey("estado") ? filters.get("estado") : Estado.A.getCode();

    Specification<ParamNotificacaoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), estado));

      if (StringUtils.hasText(reference))
        predicates.add(cb.like(cb.lower(root.get("tipoNotificacao")), reference.toLowerCase()));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = notificacaoRepository.findAll(spec, pageable);
    var tpNotif = domainEntityRepository.getActiveDomainByCode(Domains.TIPO_ALERTA_NOTIFICACAO.name());
    return data.stream()
        .map(e -> buildResponse(e, tpNotif))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String id) {
    var entity = notificacaoRepository.findByUuidOrThrow(UUID.fromString(id));
    entity.setEstado(Estado.E.getCode());
    notificacaoRepository.save(entity);
  }

  private NotificacaoResponseDTO buildResponse(ParamNotificacaoEntity e, Map<String, String> tpNotif) {
    var dto = new NotificacaoResponseDTO();
    dto.setId(e.getId().toString());
    dto.setUuid(e.getUuid().toString());
    dto.setEstadoDescricao(e.getEstado());
    dto.setAssunto(e.getAssunto());
    dto.setCorpo(e.getCorpo());
    dto.setTipoNotificacao(e.getTipoNotificacao());
    dto.setTipoNotificacaoDesc(e.getTipoNotificacao() != null ? tpNotif.get(e.getTipoNotificacao()) : null);
    dto.setEstado(e.getEstado());
    return dto;
  }
}


