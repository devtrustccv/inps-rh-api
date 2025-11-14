package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.configurationengine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.service.configurationengine.ConfigurationServiceBeanNames;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.Utils;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(ConfigurationServiceBeanNames.PARAM_VINCULO)
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
    e.setFlgContrato(Utils.parseFlag(dto.getContrato()));
    e.setFlgCarreira(Utils.parseFlag(dto.getCarreira()));
    e.setFlgSalario(Utils.parseFlag(dto.getRemuneracao()));
    e.setFlgTempoServico(Utils.parseFlag(dto.getTempoServico()));
    e.setEstado(Estado.A);
    var saved = repository.save(e);
    return buildResponse(dto, saved);
  }

  @NotNull
  private VinculoLaboralResponseDTO buildResponse(VinculoLaboralRequestDTO dto, ParamVinculoEntity e) {
    var response = new VinculoLaboralResponseDTO();
    BeanUtils.copyProperties(dto, response);
    response.setId(e.getUuid().toString());
    response.setEstado(e.getEstado().getCode());
    response.setDescricaoEstado(e.getEstado().getDescription());
    return response;
  }

  @Override
  public VinculoLaboralResponseDTO update(String uuid, VinculoLaboralRequestDTO dto) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao().trim());
    e.setFlgContrato(Utils.parseFlag(dto.getContrato()));
    e.setFlgCarreira(Utils.parseFlag(dto.getCarreira()));
    e.setFlgSalario(Utils.parseFlag(dto.getRemuneracao()));
    e.setFlgTempoServico(Utils.parseFlag(dto.getTempoServico()));
    var saved = repository.save(e);
    return buildResponse(dto, saved);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var data = repository.findAll();
    if (data.isEmpty())
      return List.of();

    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    return data.stream()
        .map(e -> {
          var response = new VinculoLaboralResponseDTO();
          response.setId(e.getUuid().toString());
          response.setCodigo(e.getCodigo());
          response.setDescricao(e.getNome());
          response.setContrato(domain.get(e.getFlgContrato().toString()));
          response.setCarreira(domain.get(e.getFlgCarreira().toString()));
          response.setRemuneracao(domain.get(e.getFlgSalario().toString()));
          response.setTempoServico(domain.get(e.getFlgTempoServico().toString()));
          response.setEstado(e.getEstado().getCode());
          response.setDescricaoEstado(e.getEstado().getDescription());
          return response;
        })
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
