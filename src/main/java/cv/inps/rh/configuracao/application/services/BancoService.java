package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.configuracao.application.dto.BancoRequestDTO;
import cv.inps.rh.configuracao.application.dto.BancoResponseDTO;
import cv.inps.rh.configuracao.application.dto.ConfigurationResponseIdDTO;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.BancoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.EntidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.BancoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosBancariosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EntidadeEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Parametrização de bancos (RH_T_BANCO): listar, consultar, criar, editar e eliminar.
 * Só se elimina um banco sem dependências (dados bancários, definições de pagamento,
 * empréstimos ou processamentos salariais).
 */
@Transactional
@Service("banco_type")
public class BancoService extends ConfigurationProcess<BancoRequestDTO> {

  private final BancoEntityRepository bancoRepository;
  private final EntidadeEntityRepository entidadeRepository;
  private final DadosBancariosEntityRepository dadosBancariosRepository;
  private final DefPagamentoEntityRepository defPagamentoRepository;
  private final EmprestimoEntityRepository emprestimoRepository;

  protected BancoService(
      Validator validator, ObjectMapper jsonMapper,
      BancoEntityRepository bancoRepository,
      EntidadeEntityRepository entidadeRepository,
      DadosBancariosEntityRepository dadosBancariosRepository,
      DefPagamentoEntityRepository defPagamentoRepository,
      EmprestimoEntityRepository emprestimoRepository
  ) {
    super(validator, jsonMapper, BancoRequestDTO.class);
    this.bancoRepository = bancoRepository;
    this.entidadeRepository = entidadeRepository;
    this.dadosBancariosRepository = dadosBancariosRepository;
    this.defPagamentoRepository = defPagamentoRepository;
    this.emprestimoRepository = emprestimoRepository;
  }

  @Override
  public Object create(BancoRequestDTO dto) {
    var entity = new BancoEntity();
    fill(entity, dto, null);
    bancoRepository.save(entity);
    return new ConfigurationResponseIdDTO(entity.getId().toString());
  }

  @Override
  public Object update(String id, BancoRequestDTO dto) {
    var bancoId = parseId(id);
    var entity = bancoRepository.findByIdOrThrow(bancoId);
    fill(entity, dto, bancoId);
    bancoRepository.save(entity);
    return "";
  }

  @Override
  @Transactional(readOnly = true)
  public Object read(String id) {
    var entity = bancoRepository.findByIdOrThrow(parseId(id));
    return buildResponse(entity, entidadesPorId(List.of(entity)));
  }

  @Override
  @Transactional(readOnly = true)
  public Object list(Map<String, String> filters) {
    var base = ConfigurationUtils.buildDefaultPageRequest(filters);
    var pageable = PageRequest.of(base.getPageNumber(), base.getPageSize(), Sort.by("nmBanco", "id"));
    var nome = filters.get("nome");

    Specification<BancoEntity> spec = (root, _, cb) -> {
      if (!StringUtils.hasText(nome)) return cb.conjunction();
      var termo = "%" + nome.trim().toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("nmBanco")), termo),
          cb.like(cb.lower(root.get("sigla")), termo),
          cb.like(cb.lower(root.get("cdBanco")), termo));
    };

    var data = bancoRepository.findAll(spec, pageable);
    var entidades = entidadesPorId(data.getContent());

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent().stream()
        .map(e -> buildResponse(e, entidades))
        .toList());
    return response;
  }

  @Override
  public void delete(String id) {
    var entity = bancoRepository.findByIdOrThrow(parseId(id));
    var bancoId = entity.getId();

    if (dadosBancariosRepository.existsByRhbId_Id(bancoId)
        || defPagamentoRepository.existsByRhbId_Id(bancoId)
        || emprestimoRepository.existsByBanco_Id(bancoId))
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();

    // RH_T_PROC_FUNCIONARIOS.RHB_ID tem FK para RH_T_BANCO, mas no JPA está mapeado como dados
    // bancários; essa (e qualquer outra FK) fica garantida pela própria BD no flush.
    try {
      bancoRepository.delete(entity);
      bancoRepository.flush();
    } catch (DataIntegrityViolationException ex) {
      throw IgrpResponseStatusException.conflictByAnotherTableDependency();
    }
  }

  private void fill(BancoEntity entity, BancoRequestDTO dto, Long idAtual) {
    var codigo = dto.getCodigo().trim().toUpperCase();
    var sigla = StringUtils.hasText(dto.getSigla()) ? dto.getSigla().trim().toUpperCase() : null;

    var codigoRepetido = idAtual == null
        ? bancoRepository.existsByCdBanco(codigo)
        : bancoRepository.existsByCdBancoAndIdNot(codigo, idAtual);
    if (codigoRepetido)
      throw IgrpResponseStatusException.badRequest("Já existe um banco com o código " + codigo + ".");

    if (sigla != null) {
      var siglaRepetida = idAtual == null
          ? bancoRepository.existsBySigla(sigla)
          : bancoRepository.existsBySiglaAndIdNot(sigla, idAtual);
      if (siglaRepetida)
        throw IgrpResponseStatusException.badRequest("Já existe um banco com a sigla " + sigla + ".");
    }

    if (dto.getEntId() != null && !entidadeRepository.existsById(dto.getEntId()))
      throw IgrpResponseStatusException.badRequest("A entidade indicada não existe no SIGOF.");

    entity.setCdBanco(codigo);
    entity.setSigla(sigla);
    entity.setNmBanco(dto.getNome().trim());
    entity.setNuConta(dto.getNumeroConta());
    entity.setNib(ValidationUtil.sanitizeNib(dto.getNib()));
    entity.setEntId(dto.getEntId());
  }

  private Map<Long, String> entidadesPorId(Collection<BancoEntity> bancos) {
    var ids = bancos.stream().map(BancoEntity::getEntId).filter(Objects::nonNull).distinct().toList();
    if (ids.isEmpty()) return Map.of();
    return entidadeRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(EntidadeEntity::getId, EntidadeEntity::getNome, (a, _) -> a));
  }

  private BancoResponseDTO buildResponse(BancoEntity e, Map<Long, String> entidades) {
    var dto = new BancoResponseDTO();
    dto.setId(e.getId().toString());
    dto.setCodigo(e.getCdBanco());
    dto.setSigla(e.getSigla());
    dto.setNome(e.getNmBanco());
    dto.setNumeroConta(e.getNuConta());
    dto.setNib(e.getNib());
    dto.setEntId(e.getEntId());
    dto.setEntidadeNome(e.getEntId() != null ? entidades.get(e.getEntId()) : null);
    return dto;
  }

  private static Long parseId(String id) {
    try {
      return Long.valueOf(id);
    } catch (NumberFormatException ex) {
      throw IgrpResponseStatusException.of(HttpStatus.BAD_REQUEST, "Id de banco inválido: " + id);
    }
  }
}
