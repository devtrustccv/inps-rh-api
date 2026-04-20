package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Transactional
@Service("param_vinculo_type")
public class ParamVinculoService extends ConfigurationProcess<VinculoLaboralRequestDTO> {

  private final ParamVinculoEntityRepository repository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final DomainEntityRepository domainEntityRepository;
  private final ParamSitLaboralEntityRepository situacaoLaboralEntityRepository;
  private final ParamSituacaoEntityRepository paramSituacaoEntityRepository;
  private final ParamContratoEntityRepository paramContratoEntityRepository;

  protected ParamVinculoService(ParamVinculoEntityRepository repository, TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository, DomainEntityRepository domainEntityRepository, Validator validator, ObjectMapper jsonMapper, ParamSitLaboralEntityRepository situacaoLaboralEntityRepository, ParamSituacaoEntityRepository paramSituacaoEntityRepository, ParamContratoEntityRepository paramContratoEntityRepository) {
    super(validator, jsonMapper, VinculoLaboralRequestDTO.class);
    this.repository = repository;
    this.tiposRelacionamentoEntityRepository = tiposRelacionamentoEntityRepository;
    this.domainEntityRepository = domainEntityRepository;
    this.situacaoLaboralEntityRepository = situacaoLaboralEntityRepository;
    this.paramSituacaoEntityRepository = paramSituacaoEntityRepository;
    this.paramContratoEntityRepository = paramContratoEntityRepository;
  }

  public void associarVinculoSituacao(String vinculoId, String situacaoId) {

    var vinculo = repository.findByUuidOrThrow(UUID.fromString(vinculoId));

    var situacao = paramSituacaoEntityRepository.findByUuidOrThrow(UUID.fromString(situacaoId));

    var obj = new ParamSitLaboralEntity();
    obj.setParamSit(situacao);
    obj.setVinculo(vinculo);
    obj.setEstado(Estado.A.name());
    situacaoLaboralEntityRepository.save(obj);
  }

  @Override
  public Object create(VinculoLaboralRequestDTO dto) {
    var e = new ParamVinculoEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getDescricao());
    e.setNomeNormalizado(ConfigurationUtils.normalizeAndSetToLowerCaseText(dto.getDescricao()));
    e.setFlgContrato(ConfigurationUtils.parseFlag(dto.getContrato()));
    e.setFlgCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgSalario(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setFlgTempoServico(ConfigurationUtils.parseFlag(dto.getTempoServico()));
    e.setParamContratoId(StringUtils.hasText(dto.getContratoId()) ? paramContratoEntityRepository.findByUuidOrThrow(UUID.fromString(dto.getContratoId())) : null);
    e.setEstado(Estado.A);
    repository.save(e);
    return new ConfigurationResponseIdDTO(e.getUuid().toString());
  }

  @Override
  public Object update(String uuid, VinculoLaboralRequestDTO dto) {
    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setCodigo(dto.getCodigo());
    e.setFlgContrato(ConfigurationUtils.parseFlag(dto.getContrato()));
    e.setNome(dto.getDescricao().trim());
    e.setNomeNormalizado(ConfigurationUtils.normalizeAndSetToLowerCaseText(dto.getDescricao()));
    e.setFlgCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgSalario(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setParamContratoId(StringUtils.hasText(dto.getContratoId()) ? paramContratoEntityRepository.findByUuidOrThrow(UUID.fromString(dto.getContratoId())) : null);
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
    response.setSalario(e.getFlgSalario());
    response.setContrato(e.getFlgContrato().toString());
    response.setContratoDesc(domain.get(e.getFlgContrato().toString()));
    response.setCarreira(e.getFlgCarreira().toString());
    response.setCarreiraDesc(domain.get(e.getFlgCarreira().toString()));
    response.setRemuneracao(e.getFlgSalario().toString());
    response.setRemuneracaoDesc(domain.get(e.getFlgSalario().toString()));
    response.setTempoServico(e.getFlgTempoServico().toString());
    response.setTempoServicoDesc(domain.get(e.getFlgTempoServico().toString()));
    response.setEstado(e.getEstado().getCode());
    response.setEstadoDescricao(e.getEstado().getDescription());

    var paramContratoId = e.getParamContratoId();
    if (paramContratoId != null) {
      response.setContratoId(paramContratoId.getUuid().toString());
      response.setParamContratoDesc(paramContratoId.getNome());
    }

    return response;
  }

  @Override
  public Object list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var nome = filters.getOrDefault("descricao", null);
    var status = filters.containsKey(ParamVinculoEntity_.ESTADO)
        ? Estado.valueOf(filters.get(ParamVinculoEntity_.ESTADO))
        : Estado.A;

    Specification<ParamVinculoEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(ParamVinculoEntity_.estado), status));
      if (StringUtils.hasText(nome)) {
        var normalizedVal = "%" + ConfigurationUtils.normalizeAndSetToLowerCaseText(nome) + "%";
        predicates.add(cb.like(root.get(ParamVinculoEntity_.nomeNormalizado), normalizedVal));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var data = repository.findAll(spec, pageable);
    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent().stream()
        .map(e -> buildResponse(e, domain))
            .toList());
    return response;
  }

  @Override
  public void delete(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    if (tiposRelacionamentoEntityRepository.existsByContrVinculoId_VinculoId(e))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    e.setEstado(Estado.E);
    repository.save(e);
  }
}
