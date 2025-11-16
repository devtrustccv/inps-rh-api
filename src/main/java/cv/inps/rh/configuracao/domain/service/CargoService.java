package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.CargoRequestDTO;
import cv.inps.rh.configuracao.application.dto.CargoResponseDTO;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.domain.ConfigurationUtils;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCargoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service("cargo_type")
public class CargoService extends ConfigurationProcess<CargoRequestDTO> {

  private final ParamCargoEntityRepository cargoRepository;
  private final ParamCarreiraEntityRepository careerRepository;

  public CargoService(
      Validator validator,
      ObjectMapper jsonMapper,
      ParamCargoEntityRepository cargoRepository,
      ParamCarreiraEntityRepository careerRepository
  ) {
    super(validator, jsonMapper, CargoRequestDTO.class);
    this.cargoRepository = cargoRepository;
    this.careerRepository = careerRepository;
  }

  @Override
  public Object create(CargoRequestDTO dto) {

    var cargo = new ParamCargoEntity();
    cargo.setUuid(UuidCreator.getTimeOrderedEpoch());
    cargo.setEstado(Estado.A);
    cargo.setNome(dto.getDescricao().trim());
    cargo.setDirigente(dto.getDirigente());

    if (StringUtils.hasText(dto.getCarreiraId())) {
      var career = careerRepository.findByUuidOrThrow(UUID.fromString(dto.getCarreiraId()));
      cargo.setParamCarrId(career);
    }

    cargoRepository.save(cargo);

    return new ConfigurationResponseIdDTO(cargo.getUuid().toString());
  }

  @Override
  public Object update(String uuid, CargoRequestDTO dto) {

    var cargo = cargoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    cargo.setNome(dto.getDescricao());
    cargo.setDirigente(dto.getDirigente());

    if (StringUtils.hasText(dto.getEstado()))
      cargo.setEstado(Estado.valueOf(dto.getEstado()));

    if (StringUtils.hasText(dto.getCarreiraId())) {
      var career = careerRepository.findByUuidOrThrow(UUID.fromString(dto.getCarreiraId()));
      cargo.setParamCarrId(career);
    } else
      cargo.setParamCarrId(null);

    cargoRepository.save(cargo);

    return "";
  }

  @Override
  public Object read(String uuid) {
    var cargo = cargoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    return buildResponse(cargo);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var cargo = filters.getOrDefault("cargo", null);
    var status = filters.containsKey("estado")
        ? Estado.valueOf(filters.get("estado"))
        : Estado.A;

    Specification<ParamCargoEntity> spec = (root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get("estado"), status));
      if (StringUtils.hasText(cargo))
        // TODO 16/11/2025 15:21 add normalized field to search here
        predicates.add(cb.like(cb.lower(root.get("nome")), "%" + cargo.toLowerCase() + "%"));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = cargoRepository.findAll(spec, pageable);

    return data.stream()
        .map(this::buildResponse)
        .toList();
  }

  @NotNull
  private Object buildResponse(ParamCargoEntity cargo) {
    var response = new CargoResponseDTO();
    response.setId(cargo.getUuid().toString());
    response.setDescricao(cargo.getNome());
    ofNullable(cargo.getParamCarrId()).map(c -> c.getUuid().toString()).ifPresent(response::setCarreiraId);
    response.setDirigente(cargo.getDirigente());
    response.setEstado(cargo.getEstado().getCode());
    return response;
  }

  @Override
  public void delete(String uuid) {
    var cargo = cargoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    cargo.setEstado(Estado.E);
    cargoRepository.save(cargo);
  }
}
