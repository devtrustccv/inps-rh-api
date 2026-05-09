package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.AssiduidadeListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaMovimentosResumidosQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.VResumoAssiduidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.VResumoAssiduidadeEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MovimentoResumoService {

  private final VResumoAssiduidadeEntityRepository vResumoAssiduidadeEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaAssiduidadadeDTO getListaMovimentosResumidos(GetListaMovimentosResumidosQuery query) {
    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("nomeFuncionario").ascending());

    Specification<VResumoAssiduidadeEntity> spec = buildSpec(query);

    Page<VResumoAssiduidadeEntity> page = vResumoAssiduidadeEntityRepository.findAll(spec, pageable);

    WrapperListaAssiduidadadeDTO wrapper = new WrapperListaAssiduidadadeDTO();

    page.forEach(entity -> {
      AssiduidadeListDTO dto = new AssiduidadeListDTO();
      dto.setId(entity.getId());
      dto.setUuid(entity.getFuncionarioUuid());
      dto.setUuidFuncionairio(entity.getFuncionarioUuid());
      dto.setNomeColaborador(entity.getNomeFuncionario());
      dto.setDirecao(entity.getNomeDirecao());
      dto.setTotalDias(entity.getTotalDias());
      dto.setTotalFalta(entity.getTotalFaltas());

      dto.setTotalHorasTrabalhadas(formatarHoras(entity.getHorasTrabalhadas()));
      dto.setTotalHoraAlmoco(formatarHoras(entity.getHorasAlmoco()));
      dto.setTotalHoraExtra(formatarHoras(entity.getHorasExtras()));
      dto.setTotalHorasAusentes(formatarHoras(entity.getHorasAusencia()));

      dto.setEstado(entity.getEstado());
      dto.setEstadoDesc(entity.getEstado());
      dto.setMesReferencia(String.format("%02d/%d", entity.getMes(), entity.getAno()));
      dto.setMes(entity.getMes());
      dto.setAno(entity.getAno());

      wrapper.getContent().add(dto);
    });

    PageMapper.fillPagination(page, wrapper);

    return wrapper;

  }

  private Specification<VResumoAssiduidadeEntity> buildSpec(GetListaMovimentosResumidosQuery query) {
    return (root, cq, cb) -> {
      var predicates = cb.conjunction();

      Integer mes = query.getMes() != null ? query.getMes() : LocalDate.now().getMonthValue();
      Integer ano = query.getAno() != null ? query.getAno() : LocalDate.now().getYear();

      if (query.getAno() != null) {
        predicates = cb.and(predicates, cb.equal(root.get("ano"), ano));
      }

      if (query.getMes() != null) {
        predicates = cb.and(predicates, cb.equal(root.get("mes"), mes));
      }

      if (query.getColaborador() != null && !query.getColaborador().isBlank()) {
        predicates = cb.and(predicates,
            cb.like(cb.lower(root.get("nomeFuncionario")), "%" + query.getColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getFuncionarioUuid())) {
        try {
          var funcUuid = java.util.UUID.fromString(query.getFuncionarioUuid());
          predicates = cb.and(predicates, cb.equal(root.get("funcionarioUuid"), funcUuid));
        } catch (IllegalArgumentException ignored) {
          // Ignore invalid UUIDs
        }
      }

      if (query.getDirecao() != null) {
        predicates = cb.and(predicates, cb.equal(root.get("idDirecao"), query.getDirecao()));
      }

      if (query.getSeccao() != null) {
        predicates = cb.and(predicates, cb.equal(root.get("idSecao"), query.getSeccao()));
      }

      if (query.getIlha() != null) {
        predicates = cb.and(predicates, cb.equal(root.get("idIlha"), query.getIlha()));
      }

      if (query.getEstado() != null && !query.getEstado().isBlank()) {
        predicates = cb.and(predicates, cb.equal(root.get("estado"), query.getEstado()));
      }

      return predicates;
    };
  }

  private String formatarHoras(BigDecimal horas) {
    if (horas == null)
      return "00:00";
    int h = horas.intValue();
    int m = horas.subtract(new BigDecimal(h)).multiply(new BigDecimal(60)).intValue();
    return String.format("%02d:%02d", h, m);
  }

}
