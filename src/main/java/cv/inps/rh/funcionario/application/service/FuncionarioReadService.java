package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVDossieEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVDossieEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVDossieEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FuncionarioReadService {

  private final RhVDossieEntityRepository dossieRepository;

  @Transactional(readOnly = true)
  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    int pageNumber = Integer.parseInt(query.getPageNumber());
    int pageSize = Integer.parseInt(query.getPageSize());

    // Specification para filtros dinâmicos
    Specification<RhVDossieEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(RhVDossieEntity_.ultimoVinculo), 1));

      if (StringUtils.hasText(query.getNome())) {
        var nome = query.getNome().toLowerCase();
        predicates.add(cb.like(cb.lower(root.get(RhVDossieEntity_.nome)), "%" + nome + "%"));
      }

      if (query.getDireccao() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.direcaoId), query.getDireccao()));

      if (query.getSeccao() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.seccaoId), query.getSeccao()));

      if (query.getLocal() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.localTrabalhoId), query.getLocal()));

      if (query.getCargo() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.cargoId), query.getCargo()));

      if (query.getCarreira() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.carreiraId), query.getCarreira()));

      if (StringUtils.hasText(query.getEstado()))
        predicates.add(cb.equal(root.get(RhVDossieEntity_.estadoColaborador), query.getEstado()));

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get(RhVDossieEntity_.dataInicioContrato), di));
      }

      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get(RhVDossieEntity_.dataFimContrato), df));
      }

      if (query.getTipoVinculoLaboral() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.vinculoId), query.getTipoVinculoLaboral()));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    var page = dossieRepository.findAll(spec, pageable);

    //mapear para DTO
    List<FuncionarioListDTO> content = page.getContent()
        .stream()
        .map(d -> {
          FuncionarioListDTO dto = new FuncionarioListDTO();
          dto.setId(d.getFunId());
          dto.setUuid(d.getFunUuid() != null ? d.getFunUuid().toString() : null);
          dto.setNome(d.getNome());
          dto.setNumColaborador(d.getIdColaborador() + "");
          dto.setCargo(d.getCargoDesc());
          dto.setDireccao(d.getDirecaoDesc());
          dto.setSeccao(d.getSeccaoDesc());
          String carreiraCategoria = Stream.of(d.getCarreiraDesc(), d.getCategoriaDesc())
              .filter(Objects::nonNull)
              .filter(s -> !s.isBlank())
              .collect(Collectors.joining("/"));

          dto.setCarreiraCategoria(
              carreiraCategoria.isEmpty() ? null : carreiraCategoria
          );
          dto.setDataInicio(d.getDataInicioContrato() != null ? d.getDataInicioContrato().toString() : null);
          dto.setVinculoId(d.getVinculoId());
          dto.setEstadoColaborador(d.getEstadoColaborador());
          dto.setEstadoColaboradorDesc(
              Estado.fromCode(d.getEstadoColaborador())
                  .map(Estado::getDescription)
                  .orElse("Desconhecido")
          );
          dto.setEstadoRegisto(d.getEstadoColaborador());
          dto.setEstadoRegistoDesc(Estado.fromCode(d.getEstadoColaborador())
              .map(Estado::getDescription)
              .orElse("Desconhecido"));
          return dto;
        }).toList();

    var wrapper = new WrapperListaFuncionarioDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);

    return wrapper;
  }
}
