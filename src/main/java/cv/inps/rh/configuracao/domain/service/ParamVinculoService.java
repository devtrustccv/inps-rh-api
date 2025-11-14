package cv.inps.rh.configuracao.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.Utils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParamVinculoService {

  private final ParamVinculoEntityRepository repository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  public VinculoLaboralResponseDTO create(VinculoLaboralRequestDTO dto) {

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

  public List<VinculoLaboralResponseDTO> getAll(String pagina, String tamanho) {

    var page = Integer.parseInt(pagina);
    var size = Integer.parseInt(tamanho);
    var pageable = PageRequest.of(page, size);

    var data = repository.findAll(pageable);
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
        .toList();
  }

  public void delete(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoEntityRepository.existsByVinculoId(e))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    e.setEstado(Estado.E);
    repository.save(e);
  }
}
