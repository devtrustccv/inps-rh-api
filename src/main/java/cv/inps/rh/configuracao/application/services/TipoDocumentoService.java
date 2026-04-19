package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.TipoDocumentoRequestDTO;
import cv.inps.rh.configuracao.application.dto.TipoDocumentoResponseDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoDocumentoEntityRepository;
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
@Service("tipo_documento_type")
public class TipoDocumentoService extends ConfigurationProcess<TipoDocumentoRequestDTO> {

  private final TipoDocumentoEntityRepository tipoDocumentoRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  protected TipoDocumentoService(
      Validator validator, ObjectMapper jsonMapper,
      TipoDocumentoEntityRepository tipoDocumentoRepository, DocumentoEntityRepository documentoEntityRepository, DomainEntityRepository domainEntityRepository
  ) {

    super(validator, jsonMapper, TipoDocumentoRequestDTO.class);
    this.tipoDocumentoRepository = tipoDocumentoRepository;
    this.documentoEntityRepository = documentoEntityRepository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(TipoDocumentoRequestDTO dto) {

    var entity = new TipoDocumentoEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado(Estado.A);
    entity.setReferencia(dto.getReferencia());
    entity.setCodigo(dto.getCodigo());
    entity.setNome(dto.getDescricao());
    tipoDocumentoRepository.save(entity);

    return new ConfigurationResponseIdDTO(entity.getUuid().toString());
  }

  @Override
  public Object update(String uuid, TipoDocumentoRequestDTO dto) {

    var entity = tipoDocumentoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    entity.setReferencia(dto.getReferencia());
    entity.setCodigo(dto.getCodigo());
    entity.setNome(dto.getDescricao());

    if (StringUtils.hasText(dto.getEstado()))
      entity.setEstado(Estado.valueOf(dto.getEstado()));

    tipoDocumentoRepository.save(entity);

    return "";
  }

  @Override
  public Object read(String uuid) {
    var entity = tipoDocumentoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    var reference = domainEntityRepository.getActiveDomainByCode(Domains.ACCAO_REFERENTE.name());
    return buildResponse(entity, reference);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var descricao = filters.get("descricao");
    var estado = filters.containsKey("estado")
        ? Estado.valueOf(filters.get("estado"))
        : Estado.A;

    Specification<TipoDocumentoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), estado));

      if (StringUtils.hasText(descricao)) {
        predicates.add(cb.like(
            cb.lower(root.get("nome")),
            "%" + descricao.toLowerCase() + "%"
        ));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = tipoDocumentoRepository.findAll(spec, pageable);
    var reference = domainEntityRepository.getActiveDomainByCode(Domains.ACCAO_REFERENTE.name());

    return data.stream()
        .map(e -> buildResponse(e, reference))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {

    var entity = tipoDocumentoRepository.findByUuidOrThrow(UUID.fromString(uuid));

    if (documentoEntityRepository.existsByTpDocumentoId(entity))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    entity.setEstado(Estado.E);
    tipoDocumentoRepository.save(entity);
  }

  private TipoDocumentoResponseDTO buildResponse(TipoDocumentoEntity e, Map<String, String> reference) {
    var dto = new TipoDocumentoResponseDTO();
    dto.setId(e.getUuid().toString());
    dto.setEstadoDescricao(e.getEstado().getDescription());
    dto.setCodigo(e.getCodigo());
    dto.setDescricao(e.getNome());
    dto.setReferencia(e.getReferencia());
    dto.setReferenciaDesc(e.getReferencia() != null ? reference.get(e.getReferencia()) : null);
    dto.setEstado(e.getEstado().getCode());
    return dto;
  }
}

