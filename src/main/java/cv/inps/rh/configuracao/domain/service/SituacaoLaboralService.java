package cv.inps.rh.configuracao.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.dto.SituacaoLaboralMotivoRequestDTO;
import cv.inps.rh.configuracao.application.dto.SituacaoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.SituacaoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.domain.utils.ConfigurationUtils;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoEntityRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service("situacao_laboral_type")
public class SituacaoLaboralService extends ConfigurationProcess<SituacaoLaboralRequestDTO> {

  private final ParamSituacaoEntityRepository repository;
  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  protected SituacaoLaboralService(
      Validator validator, ObjectMapper jsonMapper,
      ParamSituacaoEntityRepository repository,
      ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository,
      DomainEntityRepository domainEntityRepository
  ) {

    super(validator, jsonMapper, SituacaoLaboralRequestDTO.class);
    this.repository = repository;
    this.paramSituacaoDetalheEntityRepository = paramSituacaoDetalheEntityRepository;
    this.domainEntityRepository = domainEntityRepository;
  }

  @Override
  public Object create(SituacaoLaboralRequestDTO dto) {

    var e = new ParamSituacaoEntity();
    e.setUuid(UuidCreator.getTimeOrderedEpoch());
    populateObject(e, dto);
    e.setEstado(Estado.A);
    var saved = repository.save(e);

    saveAssociations(dto.getAssociacao(), saved);

    return new ConfigurationResponseIdDTO(e.getUuid().toString());
  }

  @Override
  public Object update(String uuid, SituacaoLaboralRequestDTO dto) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    populateObject(e, dto);
    if (StringUtils.hasText(dto.getEstado()))
      e.setEstado(Estado.valueOf(dto.getEstado()));

    var saved = repository.save(e);

    saveAssociations(dto.getAssociacao(), saved);

    return "";
  }

  private void populateObject(ParamSituacaoEntity e, SituacaoLaboralRequestDTO dto) {
    e.setCodigo(dto.getCodigo());
    e.setNome(dto.getNome());
    e.setClassificacaoArea(dto.getAreaClassificacao());
    e.setFlgFaltaDecontoSal(ConfigurationUtils.parseFlag(dto.getDescontoSalario()));
    e.setTipoAusencia(dto.getTipoAusencia());
    e.setFlgAbonoBeneficio(ConfigurationUtils.parseFlag(dto.getAbonoBeneficio()));
    e.setFlgSituacaoLaboral(ConfigurationUtils.parseFlag(dto.getAfetaSituacaoLaboral()));
    e.setTipoSituacao(dto.getTipoSituacaoLaboral());
    e.setTipoContagemDias(dto.getTipoContagem());
    e.setNumDiasAbonos(dto.getNumeroDias());
    e.setNumDiasDescontoRh(dto.getNumeroDiasDescontado());
    e.setNumDiasNdescontoRh(dto.getNumeroDiasNaoDescontado());
    e.setFlgAusencia(ConfigurationUtils.parseFlag(dto.getAusenciaLocalTrabalho()));
    e.setFlgCessaVinculo(ConfigurationUtils.parseFlag(dto.getCessaVinculo()));
    e.setFlgRegressaCarreira(ConfigurationUtils.parseFlag(dto.getRegressaCarreiraOrigem()));
    e.setTipoFalta(dto.getTipoFalta());
    e.setFlgRemuneracao(ConfigurationUtils.parseFlag(dto.getRemuneracao()));
    e.setFlgAfetaCarreira(ConfigurationUtils.parseFlag(dto.getCarreira()));
    e.setFlgContaTempServico(ConfigurationUtils.parseFlag(dto.getTempoServico()));
    e.setFlgCessaProgressao(ConfigurationUtils.parseFlag(dto.getSuspendeProgressaoPromocao()));
    e.setFlgEstadoContrato(dto.getEstadoContrato());
  }

  private void saveAssociations(List<SituacaoLaboralMotivoRequestDTO> associations, ParamSituacaoEntity saved) {
    if (!CollectionUtils.isEmpty(associations)) {
      var data = new ArrayList<ParamSituacaoDetalheEntity>();
      for (var association : associations) {
        final ParamSituacaoDetalheEntity obj;
        if (StringUtils.hasText(association.getAssociacaoId())) {
          obj = paramSituacaoDetalheEntityRepository.findByUuidOrThrow(UUID.fromString(association.getAssociacaoId()));
        } else {
          obj = new ParamSituacaoDetalheEntity();
          obj.setSituacaoId(saved);
          obj.setEstado(Estado.A);
          obj.setUuid(UuidCreator.getTimeOrderedEpoch());
        }
        obj.setMotivo(association.getMotivo());
        data.add(obj);
      }
      paramSituacaoDetalheEntityRepository.saveAll(data);
    }
  }

  @Override
  public Object read(String uuid) {

    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));

    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());
    var type = domainEntityRepository.getActiveDomainByCode(Domains.SITUACAO_LABORAL.name());
    var areaClassification = domainEntityRepository.getActiveDomainByCode(Domains.CLASSIFICACAO_SITUACAO.name());
    var contractStatus = domainEntityRepository.getActiveDomainByCode(Domains.ESTADO_CONTRATO.name());

    return buildResponse(e, domain, type, contractStatus, areaClassification);
  }

  @NotNull
  private Object buildResponse(ParamSituacaoEntity e, Map<String, String> domain, Map<String, String> type, Map<String, String> contractStatus, Map<String, String> areaClassification) {
    var response = new SituacaoLaboralResponseDTO();
    response.setId(e.getUuid().toString());
    response.setCodigo(e.getCodigo());
    response.setDescricao(e.getNome());
    response.setNome(e.getNome());
    response.setAreaClassificacao(e.getClassificacaoArea());
    response.setAreaClassificacaoDesc(areaClassification.getOrDefault(e.getClassificacaoArea(), e.getClassificacaoArea()));
    response.setTipoSituacaoLaboral(type.get(e.getTipoSituacao()));
    response.setTipoSituacao(e.getTipoSituacao());
    response.setRemuneracao(e.getFlgRemuneracao() != null ? e.getFlgRemuneracao().toString() : null);
    response.setRemuneracaoDesc(e.getFlgRemuneracao() != null ? domain.get(e.getFlgRemuneracao().toString()) : null);
    response.setCarreira(e.getFlgAfetaCarreira() != null ? e.getFlgAfetaCarreira().toString() : null);
    response.setCarreiraDesc(e.getFlgAfetaCarreira() != null ? domain.get(e.getFlgAfetaCarreira().toString()) : null);
    response.setTempoServico(e.getFlgContaTempServico() != null ? e.getFlgContaTempServico().toString() : null);
    response.setTempoServicoDesc(e.getFlgContaTempServico() != null ? domain.get(e.getFlgContaTempServico().toString()) : null);
    response.setSuspendeProgressaoPromocao(e.getFlgCessaProgressao() != null ? e.getFlgCessaProgressao().toString() : null);
    response.setSuspendeProgressaoPromocaoDesc(e.getFlgCessaProgressao() != null ? domain.get(e.getFlgCessaProgressao().toString()) : null);
    response.setEstadoContrato(e.getFlgEstadoContrato());
    response.setEstado(e.getEstado().getCode());
    response.setEstadoDescricao(e.getEstado().getDescription());
    response.setAfetaSituacaoLaboral(e.getFlgSituacaoLaboral() != null ? e.getFlgSituacaoLaboral().toString() : null);
    response.setAbonoBeneficio(e.getFlgAbonoBeneficio() != null ? e.getFlgAbonoBeneficio().toString() : null);
    response.setAbonoBeneficioDesc(e.getFlgAbonoBeneficio() != null ? domain.get(e.getFlgAbonoBeneficio().toString()) : null);
    response.setAusenciaLocalTrabalho(e.getFlgAusencia() != null ? e.getFlgAusencia().toString() : null);
    response.setAusenciaLocalTrabalhoDesc(e.getFlgAusencia() != null ? domain.get(e.getFlgAusencia().toString()) : null);
    response.setCessaVinculo(e.getFlgCessaVinculo() != null ? e.getFlgCessaVinculo().toString() : null);
    response.setCessaVinculoDesc(e.getFlgCessaVinculo() != null ? domain.get(e.getFlgCessaVinculo().toString()) : null);
    response.setRegressaCarreiraOrigem(e.getFlgRegressaCarreira() != null ? e.getFlgRegressaCarreira().toString() : null);
    response.setRegressaCarreiraOrigemDesc(e.getFlgRegressaCarreira() != null ? domain.get(e.getFlgRegressaCarreira().toString()) : null);
    response.setNumeroDias(e.getNumDiasAbonos());
    response.setNumeroDiasDescontado(e.getNumDiasDescontoRh());
    response.setNumeroDiasNaoDescontado(e.getNumDiasNdescontoRh());
    response.setTipoAusencia(e.getTipoAusencia());
    response.setFaltaDesc(domain.getOrDefault(e.getTipoFalta(), e.getTipoFalta()));
    response.setTipoFalta(e.getTipoFalta());
    response.setDescontoSalario(e.getFlgFaltaDecontoSal() != null ? e.getFlgFaltaDecontoSal().toString() : null);
    response.setTipoContagem(e.getTipoContagemDias());

    var data = paramSituacaoDetalheEntityRepository.findAllBySituacaoId_IdAndEstado(e.getId(), Estado.A).stream()
        .map(obj -> {
          var resp = new SituacaoLaboralMotivoRequestDTO();
          resp.setAssociacaoId(obj.getUuid().toString());
          resp.setMotivo(obj.getMotivo());
          return resp;
        })
        .toList();

    response.setAssociacao(data);

    return response;
  }


  @Override
  public List<Object> list(Map<String, String> filters) {

    var classificacaoArea = filters.get("classificacaoArea");
    var afetaSituacaoLaboral = filters.get("afetaSituacaoLaboral");
    var abonoBeneficio = filters.get("abonoBeneficio");
    var ausenciaLocalTrabalho = filters.get("ausenciaLocalTrabalho");
    var estado = filters.containsKey(SecaoEntity_.ESTADO)
        ? Estado.valueOf(filters.get(SecaoEntity_.ESTADO))
        : Estado.A;

    Specification<ParamSituacaoEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(SecaoEntity_.ESTADO), estado));

      if (StringUtils.hasText(classificacaoArea))
        predicates.add(cb.equal(root.get(ParamSituacaoEntity_.classificacaoArea), classificacaoArea));
      if (StringUtils.hasText(afetaSituacaoLaboral))
        predicates.add(cb.equal(root.get(ParamSituacaoEntity_.flgSituacaoLaboral), afetaSituacaoLaboral));
      if (StringUtils.hasText(abonoBeneficio))
        predicates.add(cb.equal(root.get(ParamSituacaoEntity_.flgAbonoBeneficio), abonoBeneficio));
      if (StringUtils.hasText(ausenciaLocalTrabalho))
        predicates.add(cb.equal(root.get(ParamSituacaoEntity_.flgAusencia), ausenciaLocalTrabalho));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);
    var data = repository.findAll(spec, pageable);
    if (data.isEmpty())
      return List.of();

    var domain = domainEntityRepository.getActiveDomainByCode(Domains.SIM_NAO_NUMBER.name());
    var type = domainEntityRepository.getActiveDomainByCode(Domains.SITUACAO_LABORAL.name());
    var contractStatus = domainEntityRepository.getActiveDomainByCode(Domains.ESTADO_CONTRATO.name());
    var areaClassification = domainEntityRepository.getActiveDomainByCode(Domains.CLASSIFICACAO_SITUACAO.name());

    return data.stream()
        .map(e -> buildResponse(e, domain, type, contractStatus, areaClassification))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(String uuid) {
    var e = repository.findByUuidOrThrow(UUID.fromString(uuid));
    e.setEstado(Estado.E);
    repository.save(e);
  }
}
