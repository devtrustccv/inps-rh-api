package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.queries.GetListContratosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ContratoHistoricoEntityRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContratoReadService {

  private final ContratoMapper contratoMapper;
  private final ContratoEntityRepository contratoEntityRepository;
  private final ContratoHistoricoEntityRepository contratoHistoricoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListContratoDTO listaContratos(GetListContratosQuery query) {

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 10;

    Specification<ContratoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      Join<ContratoEntity, FuncionarioEntity> fun = root.join("funId");
      predicates.add(cb.equal(fun.get("uuid"), idFuncionario));

      var estados = List.of(Estado.A, Estado.P, Estado.I);
      predicates.add(
          root.get("estado").in(estados)
      );

      if (query.getVinculo() != null) {
        Join<ContratoEntity, ParamVinculoEntity> vinc = root.join("vinculoId");
        predicates.add(cb.equal(vinc.get("id"), query.getVinculo()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<ContratoEntity> page = contratoEntityRepository.findAll(spec, pageable);

    var contratoIds = page.getContent().stream().map(ContratoEntity::getId).toList();
    Map<Long, List<ContratoHistoricoEntity>> historicosPorContrato = contratoIds.isEmpty()
        ? Map.of()
        : contratoHistoricoEntityRepository
            .findByContratoId_IdInOrderByContratoId_IdAscVersaoDesc(contratoIds)
            .stream()
            .collect(Collectors.groupingBy(h -> h.getContratoId().getId()));

    var content = page.getContent().stream()
        .map(c -> contratoMapper.toDTO(c, historicosPorContrato.getOrDefault(c.getId(), List.of())))
        .toList();

    var wrapper = new WrapperListContratoDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;

  }
}
