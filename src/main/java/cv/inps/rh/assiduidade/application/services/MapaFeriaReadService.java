package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.DetalheMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.dto.PeriodoDTO;
import cv.inps.rh.assiduidade.application.dto.VerMapaContentDTO;
import cv.inps.rh.assiduidade.application.dto.VerMapaDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.queries.GetDetalheMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.ListaMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.VerMapaQuery;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
  }

  public DetalheMapaFeriaDTO getDetalheMapaFeria(GetDetalheMapaFeriaQuery query) {
    return null;
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
