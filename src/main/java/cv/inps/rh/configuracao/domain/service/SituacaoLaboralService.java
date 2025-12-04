package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.SituacaoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.SituacaoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
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
@Service("situacao_laboral_type")
public class SituacaoLaboralService extends ConfigurationProcess<SituacaoLaboralRequestDTO> {

  private final ParamSitLaboralEntityRepository repository;
  private final DomainEntityRepository domainEntityRepository;

  public SituacaoLaboralService(
      Validator validator,
      ObjectMapper jsonMapper,
      ParamSitLaboralEntityRepository repository,
      DomainEntityRepository domainEntityRepository
  ) {
    super(validator, jsonMapper, SituacaoLaboralRequestDTO.class);
    this.repository = repository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(SituacaoLaboralRequestDTO dto) {

    var e = new ParamSitLaboralEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setTipoSituacao(dto.getTipo());
    e.setFlgRenumeracao(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setFlgAfetaCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgContaTempServico(ConfigurationUtils.parseFlag(dto.getTempoServico()));
    e.setFlgCessaProgressao(ConfigurationUtils.parseFlag(dto.getProgressaoPromocao()));
    e.setFlgEstadoContrato(ConfigurationUtils.parseFlag(dto.getEstadoContrato()));
    e.setEstado(Estado.A);
    repository.save(e);

    return new ConfigurationResponseIdDTO(e.getUuid().toString());
  }

  @Override
  public Object update(String uuid, SituacaoLaboralRequestDTO dto) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setFlgAfetaCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgRenumeracao(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setFlgContaTempServico(ConfigurationUtils.parseFlag(dto.getTempoServico()));
    e.setFlgCessaProgressao(ConfigurationUtils.parseFlag(dto.getProgressaoPromocao()));
    e.setFlgEstadoContrato(ConfigurationUtils.parseFlag(dto.getEstadoContrato()));
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setTipoSituacao(dto.getTipo());
    if (StringUtils.hasText(dto.getEstado()))
      e.setEstado(Estado.valueOf(dto.getEstado()));

    repository.save(e);

    return "";
  }

  @Override
  public Object read(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());
    var type = domainEntityRepository.getActiveDomainByCode(Domains.SITUACAO_LABORAL.name());
    var contractStatus = domainEntityRepository.getActiveDomainByCode(Domains.ESTADO_CONTRATO.name());

    return buildResponse(e, domain, type, contractStatus);
  }

  @NotNull
  private Object buildResponse(ParamSitLaboralEntity e, Map<String, String> domain, Map<String, String> type, Map<String, String> contractStatus) {
    var response = new SituacaoLaboralResponseDTO();
    response.setId(e.getUuid().toString());
    response.setCodigo(e.getCodigo());
    response.setDescricao(e.getNome());
    response.setTipo(type.get(e.getTipoSituacao()));
    response.setRemuneracao(domain.get(e.getFlgRenumeracao().toString()));
    response.setCarreira(domain.get(e.getFlgAfetaCarreira().toString()));
    response.setTempoServico(domain.get(e.getFlgContaTempServico().toString()));
    response.setProgressaoPromocao(domain.get(e.getFlgCessaProgressao().toString()));
    response.setEstadoContrato(contractStatus.get(e.getFlgEstadoContrato().toString()));

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
    var type = domainEntityRepository.getActiveDomainByCode(Domains.SITUACAO_LABORAL.name());
    var contractStatus = domainEntityRepository.getActiveDomainByCode(Domains.ESTADO_CONTRATO.name());

    return data.stream()
        .map(e -> buildResponse(e, domain, type, contractStatus))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {
    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setEstado(Estado.E);
    repository.save(e);
  }
}
