package cv.inps.rh.transversal.application.service;

import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.transversal.application.dto.DossierColaboradorListDTO;
import cv.inps.rh.transversal.application.dto.DossierColaboradorRowDTO;
import cv.inps.rh.transversal.application.queries.RelatorioDossierColaboradorQuery;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioDossierService {

  private final TiposRelacionamentoEntityRepository repository;

  public DossierColaboradorListDTO get(RelatorioDossierColaboradorQuery request) {

    if(!request.isSearch())
      return new DossierColaboradorListDTO();

    int pageNumber = Integer.parseInt(request.getPageNumber());
    int pageSize = Integer.parseInt(request.getPageSize());

    Pageable pageable = PageRequest.of(pageNumber, pageSize);

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {

      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.equal(root.get("estActAdm"), 1));

      if (request.getDireccaoId() != null) {
        predicates.add(cb.equal(root.get("mobId").get("instidId").get("id"), request.getDireccaoId())
        );
      }

      if (request.getSeccaoId() != null) {
        predicates.add(cb.equal(root.get("mobId").get("secaoId").get("id"), request.getSeccaoId()
            )
        );
      }

      if (request.getCargoId() != null) {
        predicates.add(cb.equal(root.get("cargoId").get("id"), request.getCargoId()
            )
        );
      }

      if (request.getIdade() != null) {
        LocalDate today = LocalDate.now();
        LocalDate minBirthDate = today.minusYears(request.getIdade() + 1).plusDays(1);
        LocalDate maxBirthDate = today.minusYears(request.getIdade());
        predicates.add(cb.between(root.get("funId").get("dataNascimento"), minBirthDate, maxBirthDate));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Page<TiposRelacionamentoEntity> pageResult = repository.findAll(spec, pageable);

    List<DossierColaboradorRowDTO> rows = pageResult.getContent()
        .stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());

    DossierColaboradorListDTO response = new DossierColaboradorListDTO();
    response.setContent(rows);
    response.setTotalElements(pageResult.getTotalElements());
    response.setTotalPages(pageResult.getTotalPages());
    response.setPageNumber(pageResult.getNumber());
    response.setPageSize(pageResult.getSize());
    response.setFirst(pageResult.isFirst());
    response.setLast(pageResult.isLast());

    return response;
  }

  private DossierColaboradorRowDTO mapToDTO(TiposRelacionamentoEntity entity) {
    DossierColaboradorRowDTO dto = new DossierColaboradorRowDTO();


    if (entity.getMobId() != null) {
      if (entity.getMobId().getInstidId() != null) {
        dto.setDireccao(entity.getMobId().getInstidId().getNome());
      }
      if (entity.getMobId().getSecaoId() != null) {
        dto.setSeccao(entity.getMobId().getSecaoId().getNome());
      }
      if (entity.getMobId().getLocalTrabId() != null) {
        dto.setLocalTrabalho(entity.getMobId().getLocalTrabId().getNome());
      }
      dto.setMobilidade(entity.getTipoSituacao());
    }

    if (entity.getCarreiraId() != null) {
      if (entity.getCarreiraId().getCargoId() != null) {
        dto.setCargo(entity.getCarreiraId().getCargoId().getNome());
      }
      if (entity.getCarreiraId().getCarrPccsId() != null) {
        dto.setCarreira(entity.getCarreiraId().getCarrPccsId().getNome());
      }
      if (entity.getCarreiraId().getCategoriaId() != null) {
        dto.setCategoria(entity.getCarreiraId().getCategoriaId().getNome());
      }
      if (entity.getCarreiraId().getEscalaoId() != null) {
        dto.setEscalao(entity.getCarreiraId().getEscalaoId().getEscalao());
      }
    }

    if (entity.getContrVinculoId() != null && entity.getContrVinculoId().getVinculoId() != null) {
      dto.setVinculo(entity.getContrVinculoId().getVinculoId().getNome());
    }

    if (entity.getSituacLaboralId() != null && entity.getSituacLaboralId().getSituacaoLaboralId() != null) {
      dto.setSituacaoLaboral(entity.getSituacLaboralId().getSituacaoLaboralId().getNome());
    }


    if (entity.getFunId() != null && entity.getFunId().getDataNascimento() != null) {
      java.time.Period period = java.time.Period.between(entity.getFunId().getDataNascimento(),
          java.time.LocalDate.now());
      dto.setIdade(period.getYears());
      dto.setGenero(entity.getFunId().getSexo());

      int idade = period.getYears();
      int faixaInicio = (idade / 10) * 10;
      int faixaFim = faixaInicio + 9;
      dto.setFaixaEtaria(faixaInicio + " - " + faixaFim);
    }

    return dto;
  }
}
