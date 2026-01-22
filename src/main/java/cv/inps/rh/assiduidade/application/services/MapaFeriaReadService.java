package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.DetalheMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.dto.PeriodoDTO;
import cv.inps.rh.assiduidade.application.dto.VerMapaContentDTO;
import cv.inps.rh.assiduidade.application.dto.VerMapaDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.queries.GetDetalheMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.ListaMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.VerMapaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasMapaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasMapaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapaFeriaReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final FeriasMapaEntityRepository feriasMapaEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasEntityRepository;

  public VerMapaDTO verMapa(VerMapaQuery query) {
    int ano = LocalDate.now().getYear();

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("estActAdm"), 1));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    List<TiposRelacionamentoEntity> colaboradores = tiposRelacionamentoEntityRepository.findAll(
        spec, Sort.by(Sort.Direction.ASC, "funId.nome"));

    List<VerMapaContentDTO> content = new ArrayList<>();

    for (TiposRelacionamentoEntity tr : colaboradores) {
      FuncionarioEntity fun = tr.getFunId();
      if (fun == null)
        continue;

      List<PeriodoDTO> agendadas = getFeriasAgendadas(fun, ano);
      List<PeriodoDTO> gozadas = getFeriasGozadas(fun, ano);

      if (agendadas.isEmpty() && gozadas.isEmpty())
        continue;

      VerMapaContentDTO row = new VerMapaContentDTO();
      row.setNomeColaborador(fun.getNome());
      row.setFeriasAgendadas(agendadas);
      row.setFeriasGozadas(gozadas);
      content.add(row);
    }

    var dto = new VerMapaDTO();
    dto.setContent(content);
    return dto;
  }

  public WrapperListaMapaFeriaDTO getListaMapaFeria(ListaMapaFeriaQuery query) {
    return null;
    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;
    int ano = query.getAnoReferente() != null ? query.getAnoReferente() : LocalDate.now().getYear();

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      var f = root.join("funId");
      var mob = root.join("mobId");

      if (query.getDirecao() != null) {
        predicates.add(cb.equal(mob.get("instidId").get("id"), query.getDirecao()));
      }
      if (query.getSeccao() != null) {
        predicates.add(cb.equal(mob.get("secaoId").get("id"), query.getSeccao()));
      }
      if (query.getIlha() != null) {
        predicates.add(cb.equal(mob.get("localTrabId").get("ilhaId").get("id"), query.getIlha()));
      }
      if (StringUtils.hasText(query.getEstado())) {
        try {
          predicates.add(cb.equal(f.get("estado"), Estado.fromCodeOrThrow(query.getEstado())));
        } catch (Exception ignored) {
        }
      }

      predicates.add(cb.equal(root.get("estActAdm"), 1));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    List<TiposRelacionamentoEntity> all = tiposRelacionamentoEntityRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "mobId.instidId.nome"));

    Map<Long, String> direcaoNames = new HashMap<>();
    Map<Long, List<FuncionarioEntity>> groupByDirecao = new HashMap<>();

    for (TiposRelacionamentoEntity tr : all) {
      var mob = tr.getMobId();
      if (mob == null || mob.getInstidId() == null) continue;
      Long direcaoId = mob.getInstidId().getId();
      String direcaoNome = mob.getInstidId().getNome();
      direcaoNames.put(direcaoId, direcaoNome);

      var fun = tr.getFunId();
      if (fun == null) continue;

      groupByDirecao.computeIfAbsent(direcaoId, k -> new ArrayList<>()).add(fun);
    }

    List<Long> allFunIds = all.stream()
        .map(TiposRelacionamentoEntity::getFunId)
        .filter(Objects::nonNull)
        .map(FuncionarioEntity::getId)
        .distinct()
        .toList();

    Specification<FeriasMapaEntity> feriasSpec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (!allFunIds.isEmpty()) {
        predicates.add(root.get("funId").get("id").in(allFunIds));
      } else {
        predicates.add(cb.equal(root.get("id"), -1));
      }
      predicates.add(cb.equal(root.get("anoId").get("ano"), String.valueOf(ano)));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var scheduled = feriasMapaEntityRepository.findAll(feriasSpec);
    var scheduledFunIds = scheduled.stream()
        .map(FeriasMapaEntity::getFunId)
        .filter(Objects::nonNull)
        .map(FuncionarioEntity::getId)
        .collect(Collectors.toSet());

    List<MapaFeriaListDTO> rows = new ArrayList<>();

    for (var entry : groupByDirecao.entrySet()) {
      Long dirId = entry.getKey();
      List<FuncionarioEntity> funcs = entry.getValue();
      int totalColab = (int) funcs.stream().map(FuncionarioEntity::getId).distinct().count();
      int totalAgendados = (int) funcs.stream().map(FuncionarioEntity::getId).distinct().filter(scheduledFunIds::contains).count();
      int totalPorAgendar = Math.max(totalColab - totalAgendados, 0);

      var dto = new MapaFeriaListDTO();
      dto.setId(dirId);
      dto.setUuid(null);
      dto.setAnoReferente(String.valueOf(ano));
      dto.setDirecao(direcaoNames.get(dirId));
      dto.setTotalColaborador(totalColab);
      dto.setTotalFeriasAgendadas(totalAgendados);
      dto.setTotalFeriasPorAgendar(totalPorAgendar);
      dto.setEstado(null);
      dto.setEstadoDesc(null);
      rows.add(dto);
    }

    rows.sort((a, b) -> {
      if (a.getDirecao() == null && b.getDirecao() == null) return 0;
      if (a.getDirecao() == null) return 1;
      if (b.getDirecao() == null) return -1;
      return a.getDirecao().compareToIgnoreCase(b.getDirecao());
    });

    int fromIndex = Math.min(pageNumber * pageSize, rows.size());
    int toIndex = Math.min(fromIndex + pageSize, rows.size());
    var pageContent = rows.subList(fromIndex, toIndex);

    var wrapper = new WrapperListaMapaFeriaDTO();
    wrapper.setContent(pageContent);
    wrapper.setPageNumber(pageNumber);
    wrapper.setPageSize(pageSize);
    wrapper.setTotalElements((long) rows.size());
    int totalPages = pageSize > 0 ? (int) Math.ceil(rows.size() / (double) pageSize) : 1;
    wrapper.setTotalPages(totalPages);
    wrapper.setFirst(pageNumber == 0);
    wrapper.setLast(pageNumber >= totalPages - 1);
    return wrapper;

  public DetalheMapaFeriaDTO getDetalheMapaFeria(GetDetalheMapaFeriaQuery query) {
    return null;
  }

  }

  private List<PeriodoDTO> getFeriasAgendadas(FuncionarioEntity fun, int anoReferente) {
    Specification<FeriasMapaEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("funId").get("id"), fun.getId()));
      predicates.add(cb.equal(root.get("anoId").get("ano"), String.valueOf(anoReferente)));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
    return feriasMapaEntityRepository.findAll(spec).stream()
        .map(e -> new PeriodoDTO(DateFormatter.localDateToString(e.getDataInicio()),
            DateFormatter.localDateToString(e.getDataFim())))
        .toList();
  }

  private List<PeriodoDTO> getFeriasGozadas(FuncionarioEntity fun, int anoReferente) {
    Specification<FeriasGozadasEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("funId").get("id"), fun.getId()));
      predicates.add(cb.equal(root.get("anoId").get("ano"), String.valueOf(anoReferente)));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
    return feriasGozadasEntityRepository.findAll(spec).stream()
        .map(e -> new PeriodoDTO(DateFormatter.localDateToString(e.getDataInicio()),
            DateFormatter.localDateToString(e.getDataFim())))
        .toList();
  }

}
