package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.RegimeDetalheDTO;
import cv.inps.rh.funcionario.application.dto.RegimeModalidadeDTO;
import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.queries.GetListRegimesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.RegimeTrabalhoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RegimeTrabalhoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RegimeTrabalhoEntityRepository;
import jakarta.persistence.criteria.Join;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegimeReadService {

  private final RegimeTrabalhoEntityRepository regimeTrabalhoEntityRepository;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;

  @Transactional(readOnly = true)
  public WrapperRegimeListDTO listRegime(GetListRegimesQuery query) {

    var pageNumber = query.getPageNumber()!=null ?  Integer.parseInt(query.getPageNumber()) : 0;
    var pageSize = query.getPageSize()!=null ? Integer.parseInt(query.getPageSize()) : 20;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<RegimeTrabalhoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      Join<RegimeTrabalhoEntity, FuncionarioEntity> fun = root.join("funId");
      predicates.add(cb.equal(fun.get("uuid"), idFuncionario));

      var estados = List.of(Estado.A, Estado.P, Estado.I);
      predicates.add(
          root.get("estado").in(estados)
      );

      if (StringUtils.hasText(query.getTipoRegime())) {
        predicates.add(cb.equal(root.get("tipoRegime"), query.getTipoRegime()));
      }

      if (StringUtils.hasText(query.getEstado())) {
        try {
          Estado estado = Estado.fromCodeOrThrow(query.getEstado());
          predicates.add(cb.equal(root.get("estado"), estado));
        } catch (Exception ignored) {}
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
    Page<RegimeTrabalhoEntity> page = regimeTrabalhoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(regimeTrabalhoMapper::toDTO)
        .toList();

    var wrapper = new WrapperRegimeListDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }

  @Transactional(readOnly = true)
  public RegimeDetalheDTO getById(String regimeId) {
    var uuid = IdentificadorUnico.from(regimeId).valor();
    var regime = regimeTrabalhoEntityRepository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Regime não encontrado: " + regimeId));

    var dto = new RegimeDetalheDTO();
    dto.setId(regime.getId());
    dto.setUuid(regime.getUuid() != null ? regime.getUuid().toString() : null);
    if (regime.getFunId() != null) {
      dto.setFuncionarioId(regime.getFunId().getId());
      dto.setFuncionarioUuid(regime.getFunId().getUuid() != null ? regime.getFunId().getUuid().toString() : null);
    }
    dto.setTipoRegime(regime.getTipoRegime());
    dto.setDataInicio(regime.getDataInicio());
    dto.setDataFim(regime.getDataFim());
    dto.setEstado(regime.getEstado() != null ? regime.getEstado().getCode() : null);
    dto.setEstadoDesc(regime.getEstado() != null ? regime.getEstado().getDescription() : null);
    dto.setObs(regime.getObs());

    if (regime.getModalidades() != null) {
      var mods = regime.getModalidades().stream()
          .filter(m -> m != null && m.getEstado() != Estado.I && m.getEstado() != Estado.E)
          .map(m -> {
            var md = new RegimeModalidadeDTO();
            md.setId(m.getId());
            md.setModalidade(m.getModalidade());
            md.setDiasSemana(m.getDiasSemana());
            md.setNumeroHoras(m.getNumHoras());
            return md;
          }).toList();
      dto.setRegimeModalidade(mods);
    }

    return dto;
  }
}
