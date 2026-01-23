package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.PicagemListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaPicagemQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.MovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MovimentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
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

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

@Service
@RequiredArgsConstructor
public class PicagemService {

  private final MovimentoEntityRepository movimentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaPicagemDTO getListaPicagem(GetListaPicagemQuery query) {
    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());

    Specification<MovimentoEntity> spec = (root, cq, cb) -> {
      var predicates = new java.util.ArrayList<Predicate>();

      if (StringUtils.hasText(query.getNomeColaborador())) {
        var like = "%" + query.getNomeColaborador().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get("nomeColaborador")), like));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dtMovimento"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dtMovimento"), df));
      }

      if (query.getDirecao() != null || query.getSeccao() != null || query.getUps() != null) {
        Subquery<TiposRelacionamentoEntity> sub = cq.subquery(TiposRelacionamentoEntity.class);
        var tr = sub.from(TiposRelacionamentoEntity.class);
        var conds = new java.util.ArrayList<Predicate>();
        conds.add(cb.equal(tr.get("funId").get("id"), root.get("idColaborador")));
        conds.add(cb.equal(tr.get("estActAdm"), 1));
        conds.add(cb.lessThanOrEqualTo(tr.get("dataInicio"), root.get("dtMovimento")));
        conds.add(
            cb.or(cb.isNull(tr.get("dataFim")), cb.greaterThanOrEqualTo(tr.get("dataFim"), root.get("dtMovimento"))));

        if (query.getDirecao() != null) {
          conds.add(cb.equal(tr.get("mobId").get("instidId").get("id"), query.getDirecao()));
        }
        if (query.getSeccao() != null) {
          conds.add(cb.equal(tr.get("mobId").get("secaoId").get("id"), query.getSeccao()));
        }
        if (query.getUps() != null) {
          conds.add(cb.equal(tr.get("mobId").get("localTrabId").get("upsId"), query.getUps()));
        }

        sub.select(tr).where(cb.and(conds.toArray(new Predicate[0])));
        predicates.add(cb.exists(sub));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dtMovimento"));
    Page<MovimentoEntity> page = movimentoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream().map(this::toDTO).toList();

    var wrapper = new WrapperListaPicagemDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private PicagemListDTO toDTO(MovimentoEntity e) {
    var dto = new PicagemListDTO();
    dto.setId(e.getId() != null ? e.getId().toString() : null);
    dto.setNomeColaborador(e.getNomeColaborador());
    dto.setData(DateFormatter.localDateToString(e.getDtMovimento()));

    var entrada = isEntrada(e);
    var saida = isSaida(e);
    dto.setHoraEntrada(entrada ? e.getHoraMovimento() : null);
    dto.setHoraSaida(saida ? e.getHoraMovimento() : null);

    return dto;
  }

  private boolean isEntrada(MovimentoEntity e) {
    var t = e.getTpMovimento();
    var d = e.getTpMovimentoDesc();
    var mode = e.getInOutMode();
    return (t != null && t.equalsIgnoreCase("ENTRADA"))
        || (d != null && d.equalsIgnoreCase("Entrada"))
        || (mode != null && mode == 0);
  }

  private boolean isSaida(MovimentoEntity e) {
    var t = e.getTpMovimento();
    var d = e.getTpMovimentoDesc();
    var mode = e.getInOutMode();
    return (t != null && t.equalsIgnoreCase("SAIDA"))
        || (d != null && d.equalsIgnoreCase("Saída"))
        || (mode != null && mode == 1);
  }
}
