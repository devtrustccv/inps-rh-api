package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service("param_vinculo_type")
public class ParamVinculoService extends ConfigurationProcess<VinculoLaboralRequestDTO> {

  private final ParamVinculoEntityRepository repository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  public ParamVinculoService(Validator validator, ObjectMapper jsonMapper, ParamVinculoEntityRepository repository, TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository, DomainEntityRepository domainEntityRepository) {
    super(validator, jsonMapper, VinculoLaboralRequestDTO.class);
    this.repository = repository;
    this.tiposRelacionamentoEntityRepository = tiposRelacionamentoEntityRepository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(VinculoLaboralRequestDTO dto) {
    var e = new ParamVinculoEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setFlgContrato(ConfigurationUtils.parseFlag(dto.getContrato()));
    e.setFlgCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgSalario(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setFlgTempoServico(ConfigurationUtils.parseFlag(dto.getTempoServico()));
    e.setEstado(Estado.A);
    repository.save(e);
    return new ConfigurationResponseIdDTO(e.getUuid().toString());
  }

  @Override
  public Object update(String uuid, VinculoLaboralRequestDTO dto) {
    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao().trim());
    e.setFlgContrato(ConfigurationUtils.parseFlag(dto.getContrato()));
    e.setFlgCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgSalario(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setFlgTempoServico(ConfigurationUtils.parseFlag(dto.getTempoServico()));
    if (StringUtils.hasText(dto.getEstado()))
      e.setEstado(Estado.valueOf(dto.getEstado()));
    repository.save(e);
    return "";
  }

  @Override
  public Object read(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    return buildResponse(e, domain);
  }

  @NotNull
  private Object buildResponse(ParamVinculoEntity e, Map<String, String> domain) {
    var response = new VinculoLaboralResponseDTO();
    response.setId(e.getUuid().toString());
    response.setCodigo(e.getCodigo());
    response.setDescricao(e.getNome());
    response.setContrato(domain.get(e.getFlgContrato().toString()));
    response.setCarreira(domain.get(e.getFlgCarreira().toString()));
    response.setRemuneracao(domain.get(e.getFlgSalario().toString()));
    response.setTempoServico(domain.get(e.getFlgTempoServico().toString()));
    response.setEstado(e.getEstado().getCode());
    response.setEstadoDescricao(e.getEstado().getDescription());
    return response;
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var data = repository.findAll(pageable);
    if (data.isEmpty())
      return List.of();

    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    return data.stream()
        .map(e -> buildResponse(e, domain))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoEntityRepository.existsByVinculoId(e))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    e.setEstado(Estado.E);
    repository.save(e);
  }
}
