package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.configurationengine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.service.configurationengine.ConfigurationServiceBeanNames;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamContratoEntityRepository;
import cv.inps.rh.shared.util.Utils;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service(ConfigurationServiceBeanNames.TIPO_CONTRATO_LABORAL)
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
  public TipoContratoLaboralResponseDTO create(TipoContratoLaboralRequestDTO dto) {
    var e = new ParamContratoEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setEstado(Estado.A);
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setNatureza(dto.getNatureza());
    e.setFlgRenovavel(Utils.parseFlag(dto.getRenovavel()));
    e.setDuracaoRenovavel(dto.getDuracao());
    e.setMaxRenovacao(dto.getMaxNumeroRenovacao());
    e.setPrazoObrigatorio(Utils.parseFlag(dto.getPrazo()));

    var saved = repository.save(e);
    return buildResponse(dto, saved);
  }

  @Override
  public TipoContratoLaboralResponseDTO update(String uuid, TipoContratoLaboralRequestDTO dto) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setNatureza(dto.getNatureza());
    e.setMaxRenovacao(dto.getMaxNumeroRenovacao());
    e.setFlgRenovavel(Utils.parseFlag(dto.getRenovavel()));
    e.setDuracaoRenovavel(dto.getDuracao());
    e.setPrazoObrigatorio(Utils.parseFlag(dto.getPrazo()));
    if (dto.getEstado() != null)
      e.setEstado(Estado.valueOf(dto.getEstado()));

    var saved = repository.save(e);
    return buildResponse(dto, saved);
  }

  @NotNull
  private TipoContratoLaboralResponseDTO buildResponse(TipoContratoLaboralRequestDTO dto, ParamContratoEntity e) {

    var response = new TipoContratoLaboralResponseDTO();
    BeanUtils.copyProperties(dto, response);
    response.setId(e.getUuid().toString());
    response.setEstado(e.getEstado().getCode());
    response.setDescricaoEstado(e.getEstado().getDescription());
    ofNullable(e.getPrazoObrigatorio()).ifPresent(x -> response.setPrazo(x.toString()));

    return response;
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

   /* var page = Integer.parseInt(pagina);
    var size = Integer.parseInt(tamanho);
    var pageable = PageRequest.of(page, size);*/

    var data = repository.findAll();
    if (data.isEmpty())
      return List.of();

    var nature = domainEntityRepository.getActiveDomainByCode(Domains.NATUREZA_VINCULO.name());
    var yesNo = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    return data.stream()
        .map(e -> {
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
        })
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
