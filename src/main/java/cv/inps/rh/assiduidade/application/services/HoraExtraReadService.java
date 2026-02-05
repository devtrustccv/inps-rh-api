package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;
import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaHoraExtraDTO;
import cv.inps.rh.assiduidade.application.queries.GetHoraExtraQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaHoraExtraQuery;
import cv.inps.rh.assiduidade.application.dto.HorExtraListDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.HoraExtraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.VHoraExtraMensalEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VHoraExtraMensalEntityRepository;
import cv.inps.rh.shared.application.constants.custom.Referencia;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HoraExtraReadService {

  private final HoraExtraEntityRepository horaExtraRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final VHoraExtraMensalEntityRepository vHoraExtraMensalRepository;

  @Transactional(readOnly = true)
  public WrapperListaHoraExtraDTO getListaHoraExtra(GetListaHoraExtraQuery query) {

    int pageNumber = StringUtils.hasText(query.getPageNumber())
        ? Integer.parseInt(query.getPageNumber())
        : 0;

    int pageSize = StringUtils.hasText(query.getPageSize())
        ? Integer.parseInt(query.getPageSize())
        : 20;

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));

    Page<VHoraExtraMensalEntity> page = vHoraExtraMensalRepository.findAll(buildSpec(query), pageable);

    var content = page.getContent()
        .stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaHoraExtraDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<VHoraExtraMensalEntity> buildSpec(GetListaHoraExtraQuery query) {
    return (root, cq, cb) -> {

      var predicates = new ArrayList<Predicate>();

      if (query.getIlha() != null) {
        predicates.add(cb.equal(root.get("ilhaId"), query.getIlha()));
      }

      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("direcaoId"), query.getDirecao()));
      }

      if (query.getSeccao() != null) {
        predicates.add(cb.equal(root.get("seccaoId"), query.getSeccao()));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var dtIni = LocalDate.parse(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), dtIni));
      }

      if (StringUtils.hasText(query.getDataFim())) {
        var dtFim = LocalDate.parse(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), dtFim));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private HorExtraListDTO toDTO(VHoraExtraMensalEntity e) {

    var dto = new HorExtraListDTO();

    dto.setFuncionarioUuid(e.getFuncionarioUuid().toString());
    dto.setPedidoId(e.getPedidoId());

    dto.setDirecao(e.getNomeDirecao());
    dto.setDirecaoId(e.getIdDirecao());

    dto.setSeccao(e.getNomeSecao());
    dto.setSeccaoId(e.getIdSecao());

    dto.setNomeColaborador(e.getNomeFuncionario());

    dto.setDataInicio(
        e.getDataInicio() != null ? e.getDataInicio().toString() : null);
    dto.setDataFim(
        e.getDataFim() != null ? e.getDataFim().toString() : null);

    dto.setHorasContratato(
        e.getHorasContratadoDiario() != null
            ? e.getHorasContratadoDiario() + " / " + e.getHorasContratadoMensal()
            : null);

    dto.setHorasTrabalho(
        e.getHorasTrabalho() != null ? e.getHorasTrabalho().toPlainString() : null);

    dto.setSalarioMensal(e.getSalarioMensal());
    dto.setValorHorasMensal(e.getValorHorasMensal());
    dto.setValorHorasDiario(e.getValorHorasDiario());

    dto.setPercentagem(e.getPercentagem());

    dto.setEstado(e.getEstado());
    dto.setEstadoDesc(e.getEstadoDesc());

    return dto;
  }

  private String formatHorasExtra(String s) {
    if (!StringUtils.hasText(s))
      return null;

    var p = s.split(":");
    if (p.length >= 2) {
      return p[0] + ":" + p[1];
    }
    return s;
  }

  @Transactional(readOnly = true)
  public HoraExtraReqDTO getHoraExtra(GetHoraExtraQuery query) {

    HoraExtraReqDTO dto = new HoraExtraReqDTO();

    if (query == null || !StringUtils.hasText(query.getPedidoId())) {
      return dto;
    }

    UUID pedidoUuid;
    try {
      pedidoUuid = UUID.fromString(query.getPedidoId());
    } catch (IllegalArgumentException e) {
      return dto;
    }

    var horasExtra = horaExtraRepository.findAllByPedidoId_Uuid(pedidoUuid);

    if (horasExtra == null || horasExtra.isEmpty()) {
      return dto;
    }

    for (HoraExtraEntity e : horasExtra) {
      HoraExtraDTO item = new HoraExtraDTO();
      item.setId(e.getId());
      item.setColaborador(
          e.getTiprelId() != null && e.getTiprelId().getFunId() != null
              ? e.getTiprelId().getFunId().getUuid()
              : null);
      item.setDataInicio(e.getDataInicio());
      item.setDataFim(e.getDataFim());
      item.setHorasDiaria(e.getHorasDiarias());
      item.setPercentagemHora(e.getPercentagem());
      item.setValorDiario(e.getValorDiario());

      dto.getHoraExtra().add(item);
    }

    var anexos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(Referencia.HORA_EXTRA.name(), pedidoUuid);
    if (anexos != null && !anexos.isEmpty()) {
      var list = new java.util.ArrayList<cv.inps.rh.shared.application.dto.AnexoReqDTO>();
      for (var d : anexos) {
        var ar = new cv.inps.rh.shared.application.dto.AnexoReqDTO();
        ar.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
        ar.setDocumento(d.getUrl());
        list.add(ar);
      }
      dto.setDocumentos(list);
    }

    return dto;
  }

}
