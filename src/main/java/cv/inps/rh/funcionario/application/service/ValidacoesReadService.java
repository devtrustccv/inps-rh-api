package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;
import cv.inps.rh.funcionario.application.queries.GetValicoesUtilizadoresQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ValidacaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity_;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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

@Service
@RequiredArgsConstructor
public class ValidacoesReadService {

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ValidacaoMapper validacaoMapper;

  @Transactional(readOnly = true)
  public WrapperListaValidacoesDTO listaValidacoes(GetValicoesUtilizadoresQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<ValidacaoEntity> spec = (root, cq, cb) -> {
      java.util.List<Predicate> predicates = new java.util.ArrayList<>();

      if (StringUtils.hasText(query.getNomeColaborador())) {
        Join<ValidacaoEntity, FuncionarioEntity> fun = root.join(ValidacaoEntity_.FUN_ID, JoinType.LEFT);
        predicates.add(cb.like(cb.lower(fun.get(FuncionarioEntity_.NOME)), "%" + query.getNomeColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getTipoOperacao())) {
        predicates.add(cb.equal(root.get(ValidacaoEntity_.TIPO_ACCAO), query.getTipoOperacao()));
      }

      if (StringUtils.hasText(query.getReferenciaName())) {
        predicates.add(cb.equal(root.get(ValidacaoEntity_.REFERENCIA_NAME), query.getReferenciaName()));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDateTime(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get(ValidacaoEntity_.CREATED_DATE), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDateTime(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get(ValidacaoEntity_.CREATED_DATE), df));
      }

      predicates.add(cb.equal(root.get(ValidacaoEntity_.ESTADO), Estado.P));

      if (cq != null) { cq.distinct(true); }
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
    Page<ValidacaoEntity> page = validacaoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(validacaoMapper::toDto)
        .toList();

    var wrapper = new WrapperListaValidacoesDTO();
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
