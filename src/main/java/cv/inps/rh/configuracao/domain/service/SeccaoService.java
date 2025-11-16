package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.SeccaoRequestDTO;
import cv.inps.rh.configuracao.application.dto.SeccaoResponseDTO;
import cv.inps.rh.configuracao.domain.ConfigurationUtils;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.InstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SecaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("seccao_type")
public class SeccaoService extends ConfigurationProcess<SeccaoRequestDTO> {

  private final SecaoEntityRepository secaoRepository;
  private final InstituicaoEntityRepository instituicaoRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public SeccaoService(
      Validator validator,
      ObjectMapper jsonMapper,
      SecaoEntityRepository secaoRepository,
      InstituicaoEntityRepository instituicaoRepository, TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository
  ) {
    super(validator, jsonMapper, SeccaoRequestDTO.class);
    this.secaoRepository = secaoRepository;
    this.instituicaoRepository = instituicaoRepository;
    this.tiposRelacionamentoEntityRepository = tiposRelacionamentoEntityRepository;
  }

  @Override
  public Object create(SeccaoRequestDTO dto) {

    var section = new SecaoEntity();
    section.setUuid(UuidCreator.getTimeOrderedEpoch());
    section.setEstado(Estado.A);
    section.setNome(dto.getDescricao());
    section.setNomeNormalizado(ConfigurationUtils.normalizeAndSetToLowerCaseText(dto.getDescricao()));
    section.setInstId(instituicaoRepository.findByIdOrThrow(Long.valueOf(dto.getDirecaoId())));
    secaoRepository.save(section);

    return new ConfigurationResponseIdDTO(section.getUuid().toString());
  }

  @Override
  public Object update(String uuid, SeccaoRequestDTO dto) {

    var section = secaoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    section.setNome(dto.getDescricao());
    section.setInstId(instituicaoRepository.findByIdOrThrow(Long.valueOf(dto.getDirecaoId())));
    if (StringUtils.hasText(dto.getEstado()))
      section.setEstado(Estado.valueOf(dto.getEstado()));
    secaoRepository.save(section);

    return "";
  }

  @Override
  public Object read(String uuid) {
    var entity = secaoRepository.findByUuidOrThrow(UUID.fromString(uuid));
    return buildResponse(entity);
  }

  @Override
  public List<Object> list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var nome = filters.get("nome");
    var direcaoId = filters.get("direcao");
    var estado = filters.containsKey(SecaoEntity_.ESTADO)
        ? Estado.valueOf(filters.get(SecaoEntity_.ESTADO))
        : Estado.A;

    Specification<SecaoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(SecaoEntity_.ESTADO), estado));

      if (StringUtils.hasText(nome)) {
        var value = "%" + ConfigurationUtils.normalizeAndSetToLowerCaseText(nome) + "%";
        predicates.add(cb.like(cb.lower(root.get(SecaoEntity_.nomeNormalizado)), value));
      }

      if (StringUtils.hasText(direcaoId))
        predicates.add(cb.equal(root.get(SecaoEntity_.instId).get(InstituicaoEntity_.id), Long.valueOf(direcaoId)));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = secaoRepository.findAll(spec, pageable);

    return data.stream()
        .map(this::buildResponse)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {

    var section = secaoRepository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoEntityRepository.existsBySeccaoId(section))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    section.setEstado(Estado.E);
    secaoRepository.save(section);
  }

  private SeccaoResponseDTO buildResponse(SecaoEntity s) {
    var dto = new SeccaoResponseDTO();
    dto.setDirecaoId(s.getInstId().getId().toString());
    dto.setDireccao(s.getInstId().getNome());
    dto.setDescricao(s.getNome());
    dto.setId(s.getUuid().toString());
    dto.setEstadoDescricao(s.getEstado().getDescription());
    dto.setEstado(s.getEstado().getDescription());
    return dto;
  }
}
