package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.EstabelecimentoRequestDTO;
import cv.inps.rh.shared.application.dto.EstabelecimentoResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.EstabelecimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EstabelecimentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service("estabelecimento_type")
public class EstabelecimentoService extends ConfigurationProcess<EstabelecimentoRequestDTO> {

  private final EstabelecimentoEntityRepository estabelecimentoRepository;
  private final GeografiaEntityRepository geografiaRepository;

  public EstabelecimentoService(
      EstabelecimentoEntityRepository estabelecimentoRepository,
      GeografiaEntityRepository geografiaRepository,
      Validator validator,
      ObjectMapper jsonMapper) {

    super(validator, jsonMapper, EstabelecimentoRequestDTO.class);
    this.estabelecimentoRepository = estabelecimentoRepository;
    this.geografiaRepository = geografiaRepository;
  }

  @Override
  public Object create(EstabelecimentoRequestDTO dto) {

    var estabelecimento = new EstabelecimentoEntity();
    estabelecimento.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    estabelecimento.setEstado(Estado.A.getCode());
    estabelecimento.setNome(dto.getNome());

    if (dto.getPaisId() != null) {
      var pais = geografiaRepository.findByIdOrThrow(dto.getPaisId());
      estabelecimento.setPais(pais);
    }

    estabelecimentoRepository.save(estabelecimento);

    return new ConfigurationResponseIdDTO(estabelecimento.getUuid());
  }

  @Override
  public Object update(String uuid, EstabelecimentoRequestDTO dto) {

    var estabelecimento = estabelecimentoRepository.findByUuidOrThrow(uuid);
    estabelecimento.setNome(dto.getNome());

    if (dto.getPaisId() != null) {
      var pais = geografiaRepository.findByIdOrThrow(dto.getPaisId());
      estabelecimento.setPais(pais);
    } else
      estabelecimento.setPais(null);

    estabelecimentoRepository.save(estabelecimento);

    return "";
  }

  @Override
  public Object read(String uuid) {

    var estabelecimento = estabelecimentoRepository.findByUuidOrThrow(uuid);

    return buildResponse(estabelecimento);
  }

  @Override
  public Object list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var nome = filters.get("nome");
    var paisId = filters.get("paisId");

    Specification<EstabelecimentoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();

      predicates.add(cb.equal(root.get("estado"), Estado.A.getCode()));

      if (StringUtils.hasText(nome)) {
        predicates.add(
            cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%")
        );
      }

      if (StringUtils.hasText(paisId)) {
        predicates.add(cb.equal(root.get("pais").get("id"), Long.valueOf(paisId)));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = estabelecimentoRepository.findAll(spec, pageable);

    var response = new WrapperListDTO();

    PageMapper.fillPagination(data, response);

    response.setContent(
        data.getContent()
            .stream()
            .map(this::buildResponse)
            .collect(Collectors.toUnmodifiableList())
    );

    return response;
  }

  private EstabelecimentoResponseDTO buildResponse(EstabelecimentoEntity estabelecimento) {

    var response = new EstabelecimentoResponseDTO();
    response.setUuid(estabelecimento.getUuid());
    response.setNome(estabelecimento.getNome());

    if (estabelecimento.getPais() != null) {
      response.setPaisId(estabelecimento.getPais().getId());
      response.setPais(estabelecimento.getPais().getNome());
    }

    return response;
  }

  @Override
  public void delete(String uuid) {

    var estabelecimento = estabelecimentoRepository.findByUuidOrThrow(uuid);

    estabelecimento.setEstado(Estado.E.getCode());

    estabelecimentoRepository.save(estabelecimento);
  }
}
