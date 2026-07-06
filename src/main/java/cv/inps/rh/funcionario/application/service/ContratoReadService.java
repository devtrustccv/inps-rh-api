package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.queries.GetListContratosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVContratoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoReadService {

  private final ContratoMapper contratoMapper;
  private final RhVContratoEntityRepository rhVContratoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListContratoDTO listaContratos(GetListContratosQuery query) {

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<RhVContratoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.equal(root.get("funUuid"), idFuncionario));

      // Mostra activos e pendentes; não mostra inactivos (I)
      var estados = List.of(Estado.A.getCode(), Estado.P.getCode());
      predicates.add(root.get("estado").in(estados));

      if (query.getVinculo() != null) {
        predicates.add(cb.equal(root.get("vinculoId"), query.getVinculo()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<RhVContratoEntity> page = rhVContratoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(contratoMapper::toDTO)
        .toList();

    var wrapper = new WrapperListContratoDTO();
    wrapper.setContent(content);
    PageMapper.fillPagination(page, wrapper);

    return wrapper;
  }
}
