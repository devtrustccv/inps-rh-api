package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;
import cv.inps.rh.funcionario.application.queries.GetListPagamentosDescontoQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.VDefPagamentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.VDefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.VDefPagamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentosDescontoReadService {

  private final VDefPagamentoEntityRepository vDefPagamentoEntityRepository;
  private final VDefPagamentoMapper vDefPagamentoMapper;

  @Transactional(readOnly = true)
  public WrapperListPagamentosDescontoDTO getListPagamentosDesconto(GetListPagamentosDescontoQuery query) {

    int pageNumber = query.getPageNumber() != null  ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<VDefPagamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      predicates.add(cb.equal(root.get("funUuid"), idFuncionario));
      predicates.add(cb.equal(root.get("estActAdm"), 1));

      var estadosPermitidos = EnumSet.of(Estado.A, Estado.I);

      if (StringUtils.hasText(query.getEstado())) {
        try {
          Estado estadoRequest = Estado.fromCodeOrThrow(query.getEstado());
          if (estadosPermitidos.contains(estadoRequest)) {
            estadosPermitidos = EnumSet.of(estadoRequest);
          }
        } catch (Exception ignored) {}
      }

      predicates.add(
          root.get("estado").in(estadosPermitidos)
      );

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), df));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<VDefPagamentoEntity> page = vDefPagamentoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(vDefPagamentoMapper::toDTO)
        .toList();

    var wrapper = new WrapperListPagamentosDescontoDTO();
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
