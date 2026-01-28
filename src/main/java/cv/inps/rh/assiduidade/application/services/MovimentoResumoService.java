package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.AssiduidadeListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaMovimentosResumidosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
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

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovimentoResumoService {

  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaAssiduidadadeDTO getListaMovimentosResumidos(GetListaMovimentosResumidosQuery query) {

    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());

    Specification<AssiduidadeSinteseDiarioEntity> spec = buildSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "data"));
    Page<AssiduidadeSinteseDiarioEntity> page = sinteseRepository.findAll(spec, pageable);

    var uuids = page.getContent().stream()
        .map(AssiduidadeSinteseDiarioEntity::getFuncionarioId)
        .filter(java.util.Objects::nonNull)
        .map(FuncionarioEntity::getUuid)
        .filter(java.util.Objects::nonNull)
        .distinct()
        .toList();

    var atuais = tiposRelacionamentoEntityRepository.findAtuaisByFuncionarioUuids(uuids);
    var direcaoPorUuid = new java.util.HashMap<java.util.UUID, String>();
    for (var tr : atuais) {
      var funUuid = tr.getFunId() != null ? tr.getFunId().getUuid() : null;
      var nome = tr.getMobId() != null && tr.getMobId().getInstidId() != null ? tr.getMobId().getInstidId().getNome()
          : null;
      if (funUuid != null && !direcaoPorUuid.containsKey(funUuid)) {
        direcaoPorUuid.put(funUuid, nome);
      }
    }

    Map<Long, Aggregator> map = new LinkedHashMap<>();
    for (var e : page.getContent()) {
      var fun = e.getFuncionarioId();
      if (fun == null)
        continue;
      var key = fun.getId();
      var agg = map.computeIfAbsent(key, k -> Aggregator.init(fun, direcaoPorUuid.get(fun.getUuid())));
      agg.add(e);
    }

    var content = map.values().stream().map(Aggregator::toDTO).toList();

    var wrapper = new WrapperListaAssiduidadadeDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<AssiduidadeSinteseDiarioEntity> buildSpec(GetListaMovimentosResumidosQuery query) {
    return (root, cq, cb) -> {
      var predicates = new java.util.ArrayList<Predicate>();

      if (StringUtils.hasText(query.getColaborador())) {
        predicates.add(
            cb.like(cb.lower(root.get("funcionarioId").get("nome")), "%" + query.getColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = cv.inps.rh.shared.util.DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("data"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = cv.inps.rh.shared.util.DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("data"), df));
      }

      if (StringUtils.hasText(query.getEstado())) {
        try {
          var estado = Estado.valueOf(query.getEstado());
          predicates.add(cb.equal(root.get("estado"), estado));
        } catch (IllegalArgumentException ignored) {
        }
      }

      if (query.getDirecao() != null || query.getSeccao() != null || query.getIlha() != null) {
        Subquery<TiposRelacionamentoEntity> sub = cq.subquery(TiposRelacionamentoEntity.class);
        var tr = sub.from(TiposRelacionamentoEntity.class);
        var conds = new java.util.ArrayList<Predicate>();
        conds.add(cb.equal(tr.get("funId").get("id"), root.get("funcionarioId").get("id")));
        conds.add(cb.equal(tr.get("estActAdm"), 1));
        conds.add(cb.lessThanOrEqualTo(tr.get("dataInicio"), root.get("data")));
        conds.add(cb.or(cb.isNull(tr.get("dataFim")), cb.greaterThanOrEqualTo(tr.get("dataFim"), root.get("data"))));
        if (query.getDirecao() != null) {
          conds.add(cb.equal(tr.get("mobId").get("instidId").get("id"), query.getDirecao()));
        }
        if (query.getSeccao() != null) {
          conds.add(cb.equal(tr.get("mobId").get("secaoId").get("id"), query.getSeccao()));
        }
        if (query.getIlha() != null) {
          Join<TiposRelacionamentoEntity, ParamLocalTrabEntity> lt = tr.join("localTrabId");
          conds.add(cb.equal(lt.get("ilhaId").get("id"), query.getIlha()));
        }
        sub.select(tr).where(cb.and(conds.toArray(new Predicate[0])));
        predicates.add(cb.exists(sub));
      }

      Subquery<FaltaEntity> faltaJustificada = cq.subquery(FaltaEntity.class);
      var f = faltaJustificada.from(FaltaEntity.class);
      var fj = new java.util.ArrayList<Predicate>();
      fj.add(cb.equal(f.get("sinteseDiarioId").get("id"), root.get("id")));
      fj.add(cb.equal(cb.lower(f.get("flgJustificativo")), "sim"));
      faltaJustificada.select(f).where(cb.and(fj.toArray(new Predicate[0])));
      predicates.add(cb.not(cb.exists(faltaJustificada)));

      Subquery<FeriasGozadasEntity> ferias = cq.subquery(FeriasGozadasEntity.class);
      var fg = ferias.from(FeriasGozadasEntity.class);
      var pf = new java.util.ArrayList<Predicate>();
      pf.add(cb.equal(fg.get("funId").get("id"), root.get("funcionarioId").get("id")));
      pf.add(cb.lessThanOrEqualTo(fg.get("dataInicio"), root.get("data")));
      pf.add(cb.greaterThanOrEqualTo(fg.get("dataFim"), root.get("data")));
      ferias.select(fg).where(cb.and(pf.toArray(new Predicate[0])));
      predicates.add(cb.not(cb.exists(ferias)));

      Subquery<DispensaEntity> dispensa = cq.subquery(DispensaEntity.class);
      var d = dispensa.from(DispensaEntity.class);
      var pd = new java.util.ArrayList<Predicate>();
      pd.add(cb.equal(d.get("data"), root.get("data")));
      pd.add(cb.equal(d.get("estado"), Estado.A));
      Subquery<TiposRelacionamentoEntity> trDisp = cq.subquery(TiposRelacionamentoEntity.class);
      var trd = trDisp.from(TiposRelacionamentoEntity.class);
      var ctd = new java.util.ArrayList<Predicate>();
      ctd.add(cb.equal(trd.get("id"), d.get("tiprelId").get("id")));
      ctd.add(cb.equal(trd.get("funId").get("id"), root.get("funcionarioId").get("id")));
      trDisp.select(trd).where(cb.and(ctd.toArray(new Predicate[0])));
      pd.add(cb.exists(trDisp));
      dispensa.select(d).where(cb.and(pd.toArray(new Predicate[0])));
      predicates.add(cb.not(cb.exists(dispensa)));

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static int parseToMinutes(String s) {
    if (!StringUtils.hasText(s))
      return 0;
    var parts = s.split(":");
    try {
      if (parts.length == 3) {
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        int sec = Integer.parseInt(parts[2]);
        return h * 60 + m + (sec / 60);
      } else if (parts.length == 2) {
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h * 60 + m;
      } else {
        return Integer.parseInt(s);
      }
    } catch (NumberFormatException ex) {
      return 0;
    }
  }

  private static class Aggregator {
    private final FuncionarioEntity fun;
    private final String direcao;
    private int totalFalta;
    private int totalDias;
    private int trabMin;
    private int ausMin;
    private int extraMin;
    private int almocoMin;
    private Estado estado;

    private Aggregator(FuncionarioEntity fun, String direcao) {
      this.fun = fun;
      this.direcao = direcao;
    }

    static Aggregator init(FuncionarioEntity fun, String direcao) {
      return new Aggregator(fun, direcao);
    }

    void add(AssiduidadeSinteseDiarioEntity e) {
      if (StringUtils.hasText(e.getFalta()) && e.getFalta().equalsIgnoreCase("SIM")) {
        totalFalta += 1;
        totalDias += 1;
      }
      trabMin += parseToMinutes(e.getHorasTrabalhadas());
      ausMin += parseToMinutes(e.getHorasAusencia());
      extraMin += parseToMinutes(e.getHorasExtras());
      almocoMin += parseToMinutes(e.getHorasAlmoco());
      estado = e.getEstado();
    }

    AssiduidadeListDTO toDTO() {
      var dto = new AssiduidadeListDTO();
      dto.setId(fun.getId() != null ? fun.getId() : null);
      dto.setUuid(fun.getUuid() != null ? fun.getUuid() : null);
      dto.setNomeColaborador(fun.getNome());
      dto.setDirecao(direcao);
      dto.setTotalFalta(totalFalta);
      dto.setTotalDias(totalDias);
      dto.setTotalHorasTrabalhadas(trabMin / 60);
      dto.setTotalHorasAusentes(ausMin / 60);
      dto.setTotalHoraExtra(extraMin / 60);
      dto.setTotalHoraAlmoco(almocoMin / 60);
      dto.setEstado(estado != null ? estado.name() : null);
      dto.setEstadoDesc(estado != null ? estado.getDescription() : null);
      return dto;
    }
  }
}
