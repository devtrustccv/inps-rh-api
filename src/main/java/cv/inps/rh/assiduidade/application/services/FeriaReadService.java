package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.WrapperListaFeriaDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaFeriaQuery;
import cv.inps.rh.assiduidade.application.dto.FeriasListDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasMapaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasMapaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriaReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final FeriasMapaEntityRepository feriasMapaEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaFeriaDTO getListaFeria(GetListaFeriaQuery query) {
    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<TiposRelacionamentoEntity> spec = buildColaboradorSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<TiposRelacionamentoEntity> page = tiposRelacionamentoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(tr -> toDTO(tr, query.getAnoReferente()))
        .toList();

    var wrapper = new WrapperListaFeriaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<TiposRelacionamentoEntity> buildColaboradorSpec(GetListaFeriaQuery query) {
    return (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      Join<TiposRelacionamentoEntity, FuncionarioEntity> f = root.join("funId", JoinType.INNER);

      if (StringUtils.hasText(query.getColaborador())) {
        String nome = query.getColaborador().toLowerCase();
        predicates.add(cb.like(cb.lower(f.get("nome")), "%" + nome + "%"));
      }

      Join<?, ?> mob = root.join("mobId", JoinType.LEFT);

      if (query.getDirecao() != null) {
        predicates.add(cb.equal(mob.get("instidId").get("id"), query.getDirecao()));
      }

      if (query.getSeccao() != null) {
        predicates.add(cb.equal(mob.get("secaoId").get("id"), query.getSeccao()));
      }

      if (query.getIlha() != null) {
        predicates.add(cb.equal(mob.get("localTrabId").get("ilhaId").get("id"), query.getIlha()));
      }

      predicates.add(cb.equal(root.get("estActAdm"), 1));

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private FeriasListDTO toDTO(TiposRelacionamentoEntity tr, Integer anoReferente) {
    var dto = new FeriasListDTO();

    var fun = tr.getFunId();
    var mob = tr.getMobId();

    dto.setId(fun != null ? fun.getId() : null);
    dto.setUuid(fun != null && fun.getUuid() != null ? fun.getUuid().toString() : null);
    dto.setNomeColaborador(fun != null ? fun.getNome() : null);
    dto.setDirecao(mob != null && mob.getInstidId() != null ? mob.getInstidId().getNome() : null);
    dto.setSecao(mob != null && mob.getSecaoId() != null ? mob.getSecaoId().getNome() : null);
    dto.setVinculo(tr.getContrVinculoId() != null && tr.getContrVinculoId().getVinculoId() != null
        ? tr.getContrVinculoId().getVinculoId().getNome()
        : null);
    dto.setCategoria(tr.getCargoId() != null ? tr.getCargoId().getNome() : null);

    int agendado = totalAgendado(fun, anoReferente);
    int gozado = totalGozado(fun, anoReferente);

    dto.setTotalAgendado(agendado);
    dto.setTotalGozado(gozado);
    dto.setTotalDireito(agendado + gozado);
    dto.setTotalDireitoAno(agendado + gozado);

    dto.setEstado(fun != null && fun.getEstado() != null ? fun.getEstado().name() : null);
    dto.setEstadoDesc(fun != null && fun.getEstado() != null ? fun.getEstado().getDescription() : null);
    return dto;
  }

  private int totalAgendado(FuncionarioEntity fun, Integer anoReferente) {
    if (fun == null || anoReferente == null)
      return 0;
    Specification<FeriasMapaEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("funId").get("id"), fun.getId()));
      predicates.add(cb.equal(root.get("anoId").get("ano"), String.valueOf(anoReferente)));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
    return feriasMapaEntityRepository.findAll(spec).stream()
        .mapToInt(e -> diffDays(e.getDataInicio(), e.getDataFim()))
        .sum();
  }

  private int totalGozado(FuncionarioEntity fun, Integer anoReferente) {
    if (fun == null || anoReferente == null)
      return 0;
    Specification<FeriasGozadasEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("funId").get("id"), fun.getId()));
      predicates.add(cb.equal(root.get("anoId").get("ano"), String.valueOf(anoReferente)));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
    return feriasGozadasEntityRepository.findAll(spec).stream()
        .mapToInt(e -> e.getNumDia() != null ? e.getNumDia() : 0)
        .sum();
  }

  private int diffDays(LocalDate inicio, LocalDate fim) {
    if (inicio == null || fim == null)
      return 0;
    long days = ChronoUnit.DAYS.between(inicio, fim);
    return (int) (days >= 0 ? days + 1 : 0);
  }
}
