package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.CarreiraRequestDTO;
import cv.inps.rh.configuracao.application.dto.CarreiraResponseDTO;
import cv.inps.rh.configuracao.application.dto.CategoriaCarreiraResponseDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCategoriaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCategoriaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service("carreira_type")
public class CarreiraService extends ConfigurationProcess<CarreiraRequestDTO> {

  private final ParamCarreiraEntityRepository carreiraRepository;
  private final ParamCategoriaEntityRepository categoriaRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;
  private final DomainEntityRepository domainEntityRepository;

  public CarreiraService(Validator validator, ObjectMapper jsonMapper, ParamCarreiraEntityRepository carreiraRepository, ParamCategoriaEntityRepository categoriaRepository, TiposRelacionamentoEntityRepository tiposRelacionamentoRepository, DomainEntityRepository domainEntityRepository) {
    super(validator, jsonMapper, CarreiraRequestDTO.class);
    this.carreiraRepository = carreiraRepository;
    this.categoriaRepository = categoriaRepository;
    this.tiposRelacionamentoRepository = tiposRelacionamentoRepository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(CarreiraRequestDTO dto) {

    var e = new ParamCarreiraEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setEstado(Estado.A);
    e.setNome(dto.getDescricao().trim());
    e.setCodigo(dto.getCodigo());
    carreiraRepository.save(e);

    var category = new ArrayList<CategoriaCarreiraResponseDTO>();

    if (dto.getCategorias() != null) {
      for (var cat : dto.getCategorias()) {

        var c = new ParamCategoriaEntity();
        c.setUuid(UuidCreator.getTimeOrderedEpoch());
        c.setEstado(Estado.A);
        c.setParamCarrId(e);
        c.setNome(cat.getCategoria());
        c.setCodigo(cat.getCodigo());
        categoriaRepository.save(c);

        var categoryResp = new CategoriaCarreiraResponseDTO();
        categoryResp.setId(c.getUuid().toString());
        categoryResp.setCodigo(c.getCodigo());
        categoryResp.setCategoria(cat.getCategoria());
        categoryResp.setEstado(c.getEstado());
        category.add(categoryResp);
      }
    }

    var response = buildResponse(e);
    response.setCategorias(category);
    return response;
  }

  @Override
  public Object update(String uuid, CarreiraRequestDTO dto) {

    var e = carreiraRepository.findByUuidOrThrow(UUID.fromString(uuid));

    e.setNome(dto.getDescricao());
    e.setCodigo(dto.getCodigo());
    if (StringUtils.hasText(dto.getEstado()))
      e.setEstado(Estado.valueOf(dto.getEstado()));

    carreiraRepository.save(e);

    var category = new ArrayList<CategoriaCarreiraResponseDTO>();

    if (dto.getCategorias() != null) {
      for (var cat : dto.getCategorias()) {
        var categoryResp = new CategoriaCarreiraResponseDTO();
        ParamCategoriaEntity c;
        if (StringUtils.hasText(cat.getId())) {
          c = categoriaRepository.findByUuidOrThrow(UUID.fromString(cat.getId()));
        } else {
          c = new ParamCategoriaEntity();
          c.setUuid(UuidCreator.getTimeOrderedEpoch());
          c.setEstado(Estado.A);
          c.setParamCarrId(e);
        }
        c.setCodigo(cat.getCodigo());
        c.setNome(cat.getCategoria());
        categoriaRepository.save(c);

        categoryResp.setId(c.getUuid().toString());
        categoryResp.setCategoria(cat.getCategoria());
        categoryResp.setCodigo(c.getCodigo());
        categoryResp.setEstado(c.getEstado());
        category.add(categoryResp);
      }
    }

    var response = buildResponse(e);
    response.setCategorias(category);
    return response;
  }

  @Override
  public Object read(String uuid) {

    var c = carreiraRepository.findByUuidOrThrow(UUID.fromString(uuid));

    var response = buildResponse(c);

    var categories = categoriaRepository.findAllByEstadoAndParamCarrId(Estado.A, c);
    if (!categories.isEmpty()) {

      var dom = domainEntityRepository.getActiveDomainByCode("CATEGORIA_PCCS");

      var mapped = categories
          .stream().map(cat -> {
            var r = new CategoriaCarreiraResponseDTO();
            r.setId(cat.getUuid().toString());
            r.setCategoria(cat.getNome());
            r.setCodigo(cat.getCodigo());
            r.setCodigoDesc(dom.getOrDefault(cat.getCodigo(), cat.getCodigo()));
            r.setEstado(cat.getEstado());
            return r;
          }).toList();
      response.setCategorias(mapped);
    }

    return response;
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var data = carreiraRepository.findAll(pageable);

    return data.stream()
        .map(this::buildResponse)
        .collect(Collectors.toList());
  }

  private CarreiraResponseDTO buildResponse(ParamCarreiraEntity c) {
    var dto = new CarreiraResponseDTO();
    dto.setId(c.getUuid().toString());
    dto.setDescricao(c.getNome());
    dto.setCodigo(c.getCodigo());
    dto.setEstadoDescricao(c.getEstado().getDescription());
    dto.setEstado(c.getEstado().getCode());
    return dto;
  }

  @Override
  public void delete(String uuid) {

    var e = carreiraRepository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoRepository.existsByCarreiraId_CarrPccsId(e))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    e.setEstado(Estado.E);
    carreiraRepository.save(e);
  }
}
