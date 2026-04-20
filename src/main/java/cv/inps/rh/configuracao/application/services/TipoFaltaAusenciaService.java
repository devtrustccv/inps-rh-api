package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.TipoFaltaAusenciaDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoFaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoFaltaEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoFaltaEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service("tipo_falta_ausencia_type")
public class TipoFaltaAusenciaService extends ConfigurationProcess<TipoFaltaAusenciaDTO> {

  private final TipoFaltaEntityRepository tipoFaltaRepository;

  protected TipoFaltaAusenciaService(
      Validator validator, ObjectMapper jsonMapper,
      TipoFaltaEntityRepository tipoFaltaRepository
  ) {
    super(validator, jsonMapper, TipoFaltaAusenciaDTO.class);
    this.tipoFaltaRepository = tipoFaltaRepository;
  }

  @Override
  public Object create(TipoFaltaAusenciaDTO dto) {

    if (tipoFaltaRepository.existsByTipo(dto.getCodigo()))
      throw IgrpResponseStatusException.conflict("Já existe um tipo de falta com o código informado");

    var entity = new TipoFaltaEntity();
    entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    entity.setEstado(Estado.A);
    entity.setNome(dto.getDescricao());
    entity.setFalta(dto.getDescricao());
    entity.setTipo(dto.getCodigo());
    entity.setSituacao(dto.getSituacao());
    entity.setDescontoRemuneracao(dto.getDescontoRemuneracao());
    entity.setTfId(dto.getAssociacao());
    tipoFaltaRepository.save(entity);

    return new ConfigurationResponseIdDTO(entity.getUuid());
  }

  @Override
  public Object update(String uuid, TipoFaltaAusenciaDTO dto) {

    var entity = tipoFaltaRepository.findByUuidOrThrow(uuid);

    if (tipoFaltaRepository.existsByTipoAndUuidNot(dto.getCodigo(), uuid))
      throw IgrpResponseStatusException.conflict("O código informado já está em uso por outro tipo de falta");

    entity.setNome(dto.getDescricao());
    entity.setFalta(dto.getDescricao());
    entity.setTipo(dto.getCodigo());
    entity.setSituacao(dto.getSituacao());
    entity.setDescontoRemuneracao(dto.getDescontoRemuneracao());
    entity.setTfId(dto.getAssociacao());
    if (dto.getEstado() != null)
      entity.setEstado(dto.getEstado());
    tipoFaltaRepository.save(entity);

    return "";
  }

  @Override
  public Object read(String uuid) {
    var entity = tipoFaltaRepository.findByUuidOrThrow(uuid);
    return buildResponse(entity);
  }

  @Override
  public Object list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var situacao = filters.get("situacao");
    var falta = filters.get("falta");

    Specification<TipoFaltaEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(TipoFaltaEntity_.estado), Estado.A));

      if (StringUtils.hasText(situacao))
        predicates.add(cb.like(root.get(TipoFaltaEntity_.situacao), situacao));

      if (StringUtils.hasText(falta))
        predicates.add(cb.equal(root.get(TipoFaltaEntity_.falta), falta));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = tipoFaltaRepository.findAll(spec, pageable);

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent().stream()
        .map(this::buildResponse)
        .collect(Collectors.toList()));
    return response;
  }

  @Override
  public void delete(String uuid) {
    var entity = tipoFaltaRepository.findByUuidOrThrow(uuid);
    entity.setEstado(Estado.E);
    tipoFaltaRepository.save(entity);
  }

  private TipoFaltaAusenciaDTO buildResponse(TipoFaltaEntity e) {
    var dto = new TipoFaltaAusenciaDTO();
    dto.setId(UUID.fromString(e.getUuid()));
    dto.setDescricao(e.getNome());
    dto.setSituacao(e.getSituacao());
    dto.setDescontoRemuneracao(e.getDescontoRemuneracao());
    dto.setAssociacao(e.getTfId());
    dto.setCodigo(e.getTipo());
    dto.setEstado(e.getEstado());
    return dto;
  }
}
