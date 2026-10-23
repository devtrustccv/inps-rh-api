package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.DocOutputRequestDTO;
import cv.inps.rh.configuracao.application.dto.WrapperDocOutputListDTO;
import cv.inps.rh.configuracao.infrastructure.mappers.DocOutputMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocOutputService {

  private final ParamDocOutputEntityRepository repository;
  private final ResponsavelEntityRepository responsavelRepository;
  private final DocOutputMapper mapper;

  public WrapperDocOutputListDTO findAll(String tipoDocumento, int pageNumber, int pageSize) {
    PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

    Specification<ParamDocOutputEntity> spec = (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("estado"), "A")); // Regra: Listar apenas ativos

      if (StringUtils.hasText(tipoDocumento)) {
        predicates.add(cb.equal(root.get("tipoDocumento"), tipoDocumento));
      }
      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };

    Page<ParamDocOutputEntity> page = repository.findAll(spec, pageRequest);
    return mapper.toWrapper(page);
  }

  public ParamDocOutputEntity findById(String id) {
    var uuid = UUID.fromString(id);
    return repository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Documento não encontrado com o id: " + id));
  }

  @Transactional
  public ParamDocOutputEntity create(DocOutputRequestDTO request) {

    ParamDocOutputEntity entity = mapper.toEntity(request);
    exigirTipoLivre(entity.getTipoDocumento(), null);
    entity.setUuid(UuidCreator.getTimeOrderedEpoch());
    entity.setEstado("A");

    if (request.getResponsavelId() != null)
      entity.setResponsavel(responsavel(request.getResponsavelId()));

    return repository.save(entity);
  }

  @Transactional
  public ParamDocOutputEntity update(String id, DocOutputRequestDTO request) {
    // Spec: editar = inativar o registo atual e criar um novo.
    ParamDocOutputEntity oldEntity = findById(id);

    ParamDocOutputEntity newEntity = mapper.toEntity(request);
    exigirTipoLivre(newEntity.getTipoDocumento(), oldEntity.getId());
    newEntity.setUuid(UuidCreator.getTimeOrderedEpoch());
    newEntity.setEstado("A");
    // QA TR-04: sem responsável no pedido mantém-se o anterior (antes perdia-se em silêncio).
    newEntity.setResponsavel(request.getResponsavelId() != null
        ? responsavel(request.getResponsavelId())
        : oldEntity.getResponsavel());

    oldEntity.setEstado("I");
    repository.save(oldEntity);
    return repository.save(newEntity);
  }

  /**
   * Responsável pelo id de RH_T_RESPONSAVEL (spec: RESPONSAVEL_ID). Antes era o UUID do funcionário, e quem
   * é responsável da direção e de uma secção ficava sempre com a primeira linha (QA TR-04).
   */
  private ResponsavelEntity responsavel(Long responsavelId) {
    var r = responsavelRepository.findById(responsavelId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Responsável não encontrado: " + responsavelId));
    if (!"A".equals(r.getEstado()))
      throw IgrpResponseStatusException.badRequest("O responsável " + responsavelId + " não está ativo.");
    return r;
  }

  /**
   * Um só documento ativo por tipo: a Ordem de Serviço e a declaração leem-no com findByTipoDocumentoAndEstado,
   * que rebenta com dois ativos (havia dois de TRANSFERENCIA depois de uma edição trocar o tipo).
   */
  private void exigirTipoLivre(String tipoDocumento, Long idEmEdicao) {
    if (tipoDocumento == null)
      throw IgrpResponseStatusException.badRequest("Indique o Tipo de Documento.");
    var outro = repository.findAllByTipoDocumentoAndEstado(tipoDocumento, "A").stream()
        .anyMatch(d -> !d.getId().equals(idEmEdicao));
    if (outro)
      throw IgrpResponseStatusException.badRequest(
          "Já existe um documento ativo para o tipo " + tipoDocumento + ". Edite esse documento.");
  }
}
