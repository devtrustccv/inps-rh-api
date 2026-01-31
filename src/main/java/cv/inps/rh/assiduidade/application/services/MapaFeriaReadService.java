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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapaFeriaReadService {

  private final VMapaFeriasDetalheEntityRepository vMapaFeriasDetalheEntityRepository;
  private final VMapaFeriaEntityRepository vMapaFeriaEntityRepository;
  private final VFeriasDetalheColaboradorEntityRepository vFeriasDetalheColaboradorEntityRepository;

  @Transactional(readOnly = true)
  public VerMapaDTO verMapa(VerMapaQuery query) {
    // Ano default
    int ano = query.getAno() != null ? query.getAno() : LocalDate.now().getYear();

    // Specification dinâmica
    Specification<VFeriasDetalheColaboradorEntity> spec = (root, cq, cb) ->
        cb.equal(root.get("anoReferente"), ano);

    if (query.getDirecaoId() != null) {
      spec = spec.and((root, cq, cb) -> cb.equal(root.get("direcaoId"), query.getDirecaoId()));
    }

    if (StringUtils.hasText(query.getFuncionarioUuid())) {
      spec = spec.and((root, cq, cb) -> cb.equal(root.get("uuidFuncionario"), UUID.fromString(query.getFuncionarioUuid())));
    }

    // Buscar registros da view
    List<VFeriasDetalheColaboradorEntity> registros = vFeriasDetalheColaboradorEntityRepository.findAll(spec);

    // Agrupar por funcionário
    Map<Long, VerMapaContentDTO> mapa = new HashMap<>();
    for (VFeriasDetalheColaboradorEntity r : registros) {
      VerMapaContentDTO content = mapa.computeIfAbsent(
          r.getFuncionarioId(),
          k -> {
            VerMapaContentDTO v = new VerMapaContentDTO();
            v.setNomeColaborador(r.getNomeColaborador());
            return v;
          }
      );

      // Adicionar período de férias marcadas se existir
      if (r.getFeriasMarcadasInicio() != null && r.getFeriasMarcadasFim() != null) {
        PeriodoDTO periodoMarcadas = new PeriodoDTO();
        periodoMarcadas.setDataInicio(r.getFeriasMarcadasInicio().toString());
        periodoMarcadas.setDataFim(r.getFeriasMarcadasFim().toString());
        content.getFeriasAgendadas().add(periodoMarcadas);
      }

      // Adicionar período de férias gozadas se existir
      if (r.getFeriasGozadasInicio() != null && r.getFeriasGozadasFim() != null) {
        PeriodoDTO periodoGozadas = new PeriodoDTO();
        periodoGozadas.setDataInicio(r.getFeriasGozadasInicio().toString());
        periodoGozadas.setDataFim(r.getFeriasGozadasFim().toString());
        content.getFeriasGozadas().add(periodoGozadas);
      }
    }

    // Montar DTO final
    VerMapaDTO resultado = new VerMapaDTO();
    resultado.setContent(new ArrayList<>(mapa.values()));

    return resultado;
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

}
