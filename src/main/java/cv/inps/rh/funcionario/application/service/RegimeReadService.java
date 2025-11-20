package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.queries.GetListRegimesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.RegimeTrabalhoMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeTrabalhoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import jakarta.persistence.criteria.Predicate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import cv.inps.rh.shared.application.constants.Estado;

@Service
@RequiredArgsConstructor
public class RegimeReadService {

  private final RegimeTrabalhoEntityRepository regimeTrabalhoEntityRepository;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;

  @Transactional(readOnly = true)
  public WrapperRegimeListDTO listRegime(GetListRegimesQuery query) {

    var pageNumber = query.getPageNumber()!=null ?  Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = query.getPageSize()!=null ? Integer.parseInt(query.getPageSize()) : 20;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).getValor();

    Specification<RegimeTrabalhoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      if (StringUtils.hasText(query.getTipoRegime())) {
        predicates.add(cb.equal(root.get("tipoRegime"), query.getTipoRegime()));
      }

      if (StringUtils.hasText(query.getEstado())) {
        try {
          Estado estado = Estado.fromCodeOrThrow(query.getEstado());
          predicates.add(cb.equal(root.get("estado"), estado));
        } catch (Exception ignored) {}
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
    Page<RegimeTrabalhoEntity> page = regimeTrabalhoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(regimeTrabalhoMapper::toDTO)
        .toList();

    var wrapper = new WrapperRegimeListDTO();
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
