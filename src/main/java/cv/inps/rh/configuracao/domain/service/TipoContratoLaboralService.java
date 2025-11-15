package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.ConfigurationUtils;
import cv.inps.rh.configuracao.domain.service.configurationengine.ConfigurationProcess;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamContratoEntityRepository;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service("tipo_contrato_laboral_type")
public class TipoContratoLaboralService extends ConfigurationProcess<TipoContratoLaboralRequestDTO> {

  private final ParamContratoEntityRepository repository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  public TipoContratoLaboralService(Validator validator, ObjectMapper jsonMapper, ParamContratoEntityRepository repository, ContratoEntityRepository contratoEntityRepository, DomainEntityRepository domainEntityRepository) {
    super(validator, jsonMapper, TipoContratoLaboralRequestDTO.class);
    this.repository = repository;
    this.contratoEntityRepository = contratoEntityRepository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(TipoContratoLaboralRequestDTO dto) {

    var e = new ParamContratoEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setEstado(Estado.A);
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setNatureza(dto.getNatureza());
    e.setFlgRenovavel(ConfigurationUtils.parseFlag(dto.getRenovavel()));
    e.setDuracaoRenovavel(dto.getDuracao());
    e.setMaxRenovacao(dto.getMaxNumeroRenovacao());
    e.setPrazoObrigatorio(ConfigurationUtils.parseFlag(dto.getPrazo()));
    repository.save(e);

    return new ConfigurationResponseIdDTO(e.getUuid().toString());
  }

  @Override
  public Object update(String uuid, TipoContratoLaboralRequestDTO dto) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setNatureza(dto.getNatureza());
    e.setMaxRenovacao(dto.getMaxNumeroRenovacao());
    e.setFlgRenovavel(ConfigurationUtils.parseFlag(dto.getRenovavel()));
    e.setDuracaoRenovavel(dto.getDuracao());
    e.setPrazoObrigatorio(ConfigurationUtils.parseFlag(dto.getPrazo()));
    if (StringUtils.hasText(dto.getEstado()))
      e.setEstado(Estado.valueOf(dto.getEstado()));
    repository.save(e);
    return "";
  }

  @Override
  public Object read(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    var nature = domainEntityRepository.getActiveDomainByCode(Domains.NATUREZA_VINCULO.name());
    var yesNo = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    return buildResponse(e, nature, yesNo);
  }

  @NotNull
  private Object buildResponse(ParamContratoEntity e, Map<String, String> nature, Map<String, String> yesNo) {
    var r = new TipoContratoLaboralResponseDTO();
    r.setId(e.getUuid().toString());
    r.setCodigo(e.getCodigo());
    r.setDescricao(e.getNome());
    r.setNatureza(nature.get(e.getNatureza()));
    r.setDuracao(e.getDuracaoRenovavel());
    r.setMaxNumeroRenovacao(e.getMaxRenovacao());
    r.setEstado(e.getEstado().getCode());
    r.setDescricaoEstado(e.getEstado().getDescription());
    ofNullable(e.getFlgRenovavel()).ifPresent(y -> r.setRenovavel(yesNo.get(y.toString())));
    ofNullable(e.getPrazoObrigatorio()).ifPresent(x -> r.setPrazo(yesNo.get(x.toString())));

    return r;
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var data = repository.findAll(pageable);
    if (data.isEmpty())
      return List.of();

    var nature = domainEntityRepository.getActiveDomainByCode(Domains.NATUREZA_VINCULO.name());
    var yesNo = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    return data.stream()
        .map(e -> buildResponse(e, nature, yesNo))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    if (contratoEntityRepository.existsByTpContratoId(e))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    e.setEstado(Estado.E);
    repository.save(e);
  }
}
