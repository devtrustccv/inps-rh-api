package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVDossieEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioReadService {

  private final RhVDossieEntityRepository dossieRepository;

  @Transactional(readOnly = true)
  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    int pageNumber = Integer.parseInt(query.getPageNumber());
    int pageSize = Integer.parseInt(query.getPageSize());

    // Specification para filtros dinâmicos
    Specification<RhVDossieEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // filtro pelo nome
      if (StringUtils.hasText(query.getNome())) {
        String nome = query.getNome().toLowerCase();
        predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome + "%"));
      }

      // filtro direcção
      if (query.getDireccao() != null) {
        predicates.add(cb.equal(root.get("direcaoId"), query.getDireccao()));
      }

      // filtro secção
      if (query.getSeccao() != null) {
        predicates.add(cb.equal(root.get("seccaoId"), query.getSeccao()));
      }

      // filtro estado colaborador
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estadoColaborador"), query.getEstado()));
      }

      // filtro data inicio contrato
      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicioContrato"), di));
      }

      // filtro data fim contrato
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataFimContrato"), df));
      }

      // filtro tipo de vinculo laboral
      if (query.getTipoVinculoLaboral() != null) {
        predicates.add(cb.equal(root.get("vinculoId"), query.getTipoVinculoLaboral()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "funId"));
    Page<RhVDossieEntity> page = dossieRepository.findAll(spec, pageable);

    // mapear para DTO
    List<FuncionarioListDTO> content = page.getContent().stream().map(d -> {
      FuncionarioListDTO dto = new FuncionarioListDTO();
      dto.setId(d.getFunId());
      dto.setUuid(d.getFunUuid() != null ? d.getFunUuid().toString() : null);
      dto.setNome(d.getNome());
      dto.setCargo(d.getCargoDesc());
      dto.setDireccao(d.getDirecaoDesc());
      dto.setSeccao(d.getSeccaoDesc());
      dto.setCarreiraCategoria(
          (d.getCarreiraDesc() != null ? d.getCarreiraDesc() : "") + "/" +
              (d.getCategoriaDesc() != null ? d.getCategoriaDesc() : "")
      );
      dto.setDataInicio(d.getDataInicioContrato() != null ? d.getDataInicioContrato().toString() : null);
      dto.setVinculoId(d.getVinculoId());
      dto.setEstadoColaborador(d.getEstadoColaborador());
      dto.setEstadoColaboradorDesc(
          Estado.fromCode(d.getEstadoColaborador())
              .map(Estado::getDescription)
              .orElse("Desconhecido")
      );
      dto.setEstadoRegisto(null);
      dto.setEstadoRegistoDesc(null);
      return dto;
    }).toList();

    // wrapper de resposta
    WrapperListaFuncionarioDTO wrapper = new WrapperListaFuncionarioDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }
}
