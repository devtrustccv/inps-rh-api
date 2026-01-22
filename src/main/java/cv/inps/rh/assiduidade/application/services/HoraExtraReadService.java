package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaHoraExtraDTO;
import cv.inps.rh.assiduidade.application.queries.GetHoraExtraQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaHoraExtraQuery;
import cv.inps.rh.assiduidade.application.dto.HorExtraListDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.HoraExtraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class HoraExtraReadService {

  private final HoraExtraEntityRepository horaExtraRepository;

  @Transactional(readOnly = true)
  public WrapperListaHoraExtraDTO getListaHoraExtra(GetListaHoraExtraQuery query) {

    int pageNumber = StringUtils.hasText(query.getPageNumber())
        ? Integer.parseInt(query.getPageNumber())
        : 0;

    int pageSize = StringUtils.hasText(query.getPageSize())
        ? Integer.parseInt(query.getPageSize())
        : 20;

    Pageable pageable =
        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));

    Page<HoraExtraEntity> page =
        horaExtraRepository.findAll(buildSpec(query), pageable);

    var content = page.getContent()
        .stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaHoraExtraDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<HoraExtraEntity> buildSpec(GetListaHoraExtraQuery query) {
    return (root, cq, cb) -> {

      var predicates = new java.util.ArrayList<Predicate>();

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        if (di != null) {
          predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), di));
        }
      }

      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        if (df != null) {
          predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), df));
        }
      }

      if (query.getDirecao() != null) {
        predicates.add(
            cb.equal(root.get("tiprelId").get("mobId").get("instidId").get("id"),
                query.getDirecao()));
      }

      if (query.getSeccao() != null) {
        predicates.add(
            cb.equal(root.get("tiprelId").get("mobId").get("secaoId").get("id"),
                query.getSeccao()));
      }

      if (query.getIlha() != null) {
        predicates.add(
            cb.equal(
                root.get("tiprelId")
                    .get("mobId")
                    .get("localTrabId")
                    .get("ilhaId")
                    .get("id"),
                query.getIlha()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private HorExtraListDTO toDTO(HoraExtraEntity e) {

    var dto = new HorExtraListDTO();

    dto.setId(e.getId());
    dto.setUuid(e.getUuid() != null ? e.getUuid().toString() : null);

    var tiprel = e.getTiprelId();
    var mob = tiprel != null ? tiprel.getMobId() : null;
    var inst = mob != null ? mob.getInstidId() : null;

    dto.setDirecao(inst != null ? inst.getNome() : null);
    dto.setDirecaoId(inst != null ? inst.getId() : null);

    dto.setNomeColaborador(
        tiprel != null && tiprel.getFunId() != null
            ? tiprel.getFunId().getNome()
            : null
    );

    var cargo = tiprel != null ? tiprel.getCargoId() : null;
    dto.setCategoria(cargo != null ? cargo.getNome() : null);
    dto.setCategoriaId(cargo != null ? cargo.getId() : null);

    var vinc =
        tiprel != null && tiprel.getContrVinculoId() != null
            ? tiprel.getContrVinculoId().getVinculoId()
            : null;

    dto.setVinculo(vinc != null ? vinc.getNome() : null);
    dto.setVinculoId(vinc != null ? vinc.getId() : null);

    var horasExtras =
        e.getSinteseDiarioId() != null
            ? e.getSinteseDiarioId().getHorasExtras()
            : null;

    dto.setHorasContratato(
        e.getHorasDiarias() != null ? e.getHorasDiarias().toString() : null);

    dto.setHorasTrabalho(formatHorasExtra(horasExtras));

    dto.setSalarioMensal(
        tiprel != null && tiprel.getSalario() != null
            ? tiprel.getSalario().toPlainString()
            : null
    );

    dto.setValorHorasMensal(null);
    dto.setValorHorasDiario(
        e.getValorDiario() != null ? e.getValorDiario().toString() : null);

    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);

    return dto;
  }

  private String formatHorasExtra(String s) {
    if (!StringUtils.hasText(s)) return null;

    var p = s.split(":");
    if (p.length >= 2) {
      return p[0] + ":" + p[1];
    }
    return s;
  }

  public HoraExtraReqDTO getHoraExtra(GetHoraExtraQuery query){
    return new HoraExtraReqDTO();
  }
}
