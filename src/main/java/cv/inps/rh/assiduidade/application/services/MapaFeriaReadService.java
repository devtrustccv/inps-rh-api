package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.*;
import cv.inps.rh.assiduidade.application.queries.GetDetalheMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.ListaMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.VerMapaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
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

  private final VMapaFeriasDetalheEntityRepository vMapaFeriasDetalheEntityRepository;
  private final VMapaFeriaEntityRepository vMapaFeriaEntityRepository;

  @Transactional(readOnly = true)
  public VerMapaDTO verMapa(VerMapaQuery query) {
    int ano = LocalDate.now().getYear();

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("estActAdm"), 1));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    List<TiposRelacionamentoEntity> colaboradores = tiposRelacionamentoEntityRepository.findAll(spec);

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

    content.sort((a, b) -> {
      if (a.getNomeColaborador() == null && b.getNomeColaborador() == null)
        return 0;
      if (a.getNomeColaborador() == null)
        return 1;
      if (b.getNomeColaborador() == null)
        return -1;
      return a.getNomeColaborador().compareToIgnoreCase(b.getNomeColaborador());
    });

    var dto = new VerMapaDTO();
    dto.setContent(content);
    return dto;
  }

  @Transactional(readOnly = true)
  public WrapperListaMapaFeriaDTO getListaMapaFeria(ListaMapaFeriaQuery query) {

    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;
    int ano = query.getAnoReferente() != null ? query.getAnoReferente() : LocalDate.now().getYear();

    // Construir a Specification com os filtros
    Specification<VMapaFeriaEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (query.getAnoReferente() != null) {
        predicates.add(cb.equal(root.get("anoReferente"), ano));
      }
      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("direcaoId"), query.getDirecao()));
      }
      if (query.getSeccao() != null) {
        predicates.add(cb.equal(root.get("secaoId"), query.getSeccao()));
      }
      if (query.getIlha() != null) {
        predicates.add(cb.equal(root.get("ilhaId"), query.getIlha()));
      }
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estado"), query.getEstado()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "direcao", "secao", "ilha"));
    Page<VMapaFeriaEntity> page = vMapaFeriaEntityRepository.findAll(spec, pageable);

    // Mapear para DTO
    List<MapaFeriaListDTO> content = page.getContent().stream()
        .map(e -> {
          var dto = new MapaFeriaListDTO();
          dto.setDirecao(e.getDirecao());
          dto.setDirecaoId(e.getDirecaoId());
          dto.setAnoReferente(e.getAnoReferente());
          dto.setTotalColaborador(e.getTotalColaborador());
          dto.setTotalFeriasAgendadas(e.getTotalFeriasAgendadas());
          dto.setTotalFeriasPorAgendar(e.getTotalFeriasPorAgendar());
          dto.setEstado(e.getEstado());
          dto.setEstadoDesc(e.getEstadoDesc());
          return dto;
        })
        .toList();

    // Preencher wrapper
    var wrapper = new WrapperListaMapaFeriaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);

    return wrapper;
  }


  @Transactional(readOnly = true)
  public DetalheMapaFeriaDTO getDetalheMapaFeria(GetDetalheMapaFeriaQuery query) {
    DetalheMapaFeriaDTO dto = new DetalheMapaFeriaDTO();

    // -----------------------------
    // Ferias Agendadas
    // -----------------------------
    var agendadas = vMapaFeriasDetalheEntityRepository.findFeriasAgendadas(query.getAno(), query.getDirecao());
    dto.setFeriasAgendadas(
        agendadas.stream()
            .map(f -> new FeriasAgendadasDTO(
                f.getNomeColaborador(),
                f.getTotalDireito(),
                f.getTotalDireitoAno(),
                f.getDataInicioMapa(),
                f.getDataFimMapa()
            ))
            .toList()
    );

    // -----------------------------
    // Ferias Por Agendar
    // -----------------------------
    var porAgendar = vMapaFeriasDetalheEntityRepository.findFeriasPorAgendar(query.getAno(), query.getDirecao());
    dto.setFeriasPorAgendar(
        porAgendar.stream()
            .map(f -> new FeriasPorAgendarDTO(
                f.getNomeColaborador(),
                f.getTotalDireito(),
                f.getTotalDireitoAno()
            ))
            .toList()
    );

    return dto;
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
