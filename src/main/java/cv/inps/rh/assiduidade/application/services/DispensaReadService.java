package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaDispensaDTO;
import cv.inps.rh.assiduidade.application.dto.DispensaListDTO;
import cv.inps.rh.assiduidade.application.queries.GetDispensaByPedidoIdQuery;
import cv.inps.rh.assiduidade.application.queries.GetDispensaQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaDispensaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispensaReadService {

  private final DispensaEntityRepository dispensaRepository;

  @Transactional(readOnly = true)
  public WrapperListaDispensaDTO getListaDispensa(GetListaDispensaQuery query) {
    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<DispensaEntity> spec = buildSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "data"));
    Page<DispensaEntity> page = dispensaRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaDispensaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<DispensaEntity> buildSpec(GetListaDispensaQuery query) {
    return (root, cq, cb) -> {
      var predicates = new java.util.ArrayList<Predicate>();

      if (StringUtils.hasText(query.getColaborador())) {
        predicates.add(
            cb.like(
                cb.lower(root.get("pedidoId").get("funId").get("nome")),
                "%" + query.getColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        if (di != null) {
          predicates.add(cb.greaterThanOrEqualTo(root.get("data"), di));
        }
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        if (df != null) {
          predicates.add(cb.lessThanOrEqualTo(root.get("data"), df));
        }
      }

      if (StringUtils.hasText(query.getEstado())) {
        try {
          var estado = Estado.valueOf(query.getEstado());
          predicates.add(cb.equal(root.get("estado"), estado));
        } catch (IllegalArgumentException ignored) {
        }
      }

      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("tiprelId").get("mobId").get("instidId").get("id"), query.getDirecao()));
      }
      if (query.getSeccao() != null) {
        predicates.add(cb.equal(root.get("tiprelId").get("mobId").get("secaoId").get("id"), query.getSeccao()));
      }
      if (query.getIlha() != null) {
        predicates.add(
            cb.equal(root.get("tiprelId").get("mobId").get("localTrabId").get("ilhaId").get("id"), query.getIlha()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private DispensaListDTO toDTO(DispensaEntity e) {
    var dto = new DispensaListDTO();
    dto.setId(e.getId());
    dto.setUuid(e.getUuid() != null ? e.getUuid().toString() : null);
    dto.setPedidoId(e.getPedidoId() != null ? e.getPedidoId().getId() : null);
    dto.setPedidoUuid(e.getPedidoId() != null ? e.getPedidoId().getUuid().toString() : null);
    var mob = e.getTiprelId() != null ? e.getTiprelId().getMobId() : null;
    var inst = mob != null ? mob.getInstidId() : null;
    dto.setDirecao(inst != null ? inst.getNome() : null);
    dto.setDirecaoId(inst != null ? inst.getId() : null);
    var contr = e.getTiprelId() != null ? e.getTiprelId().getContrVinculoId() : null;
    var vinculo = contr != null ? contr.getVinculoId() : null;
    dto.setVinculo(vinculo != null ? vinculo.getNome() : null);
    dto.setVinculoId(vinculo != null ? vinculo.getId() : null);
    var cargo = e.getTiprelId() != null ? e.getTiprelId().getCargoId() : null;
    dto.setCategoria(cargo != null ? cargo.getNome() : null);
    dto.setCategoriaId(cargo != null ? cargo.getId() : null);
    dto.setDataPedido(
        e.getPedidoId() != null ? DateFormatter.localDateTimeToLocalDateString(e.getPedidoId().getCreatedDate())
            : null);
    dto.setDataDispensa(e.getData() != null ? DateFormatter.localDateToString(e.getData()) : null);
    var hi = e.getHoraInicio();
    var hf = e.getHoraFim();
    dto.setIntervaloHoras(
        (StringUtils.hasText(hi) ? hi : "") +
            (StringUtils.hasText(hi) || StringUtils.hasText(hf) ? " / " : "") +
            (StringUtils.hasText(hf) ? hf : ""));
    dto.setTotalHorasDireito(null);
    dto.setTotalHorasSolicitadas(diffMinutes(hi, hf));
    dto.setMotivoDispensa(StringUtils.hasText(e.getDescricaoMotivo()) ? e.getDescricaoMotivo() : e.getTipoDispensa());
    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);
    return dto;
  }

  private static Integer toMinutes(String s) {
    if (!StringUtils.hasText(s))
      return null;
    var parts = s.split(":");
    try {
      int h = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
      int m = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      int sec = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
      return h * 60 + m + (sec / 60);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private static Integer diffMinutes(String start, String end) {
    var s = toMinutes(start);
    var e = toMinutes(end);
    if (s == null || e == null)
      return null;
    var d = e - s;
    return Math.max(d, 0);
  }

  @Transactional(readOnly = true)
  public DispensaReqDTO getDispensa(GetDispensaQuery query) {
    if (query == null || !StringUtils.hasText(query.getDispensaId())) {
      return new DispensaReqDTO();
    }

    var e = dispensaRepository.findByUuid(UUID.fromString(query.getDispensaId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Dispensa nao encontrada",
            query.getDispensaId()));

    var dto = new DispensaReqDTO();
    dto.setColaborador(
        e.getPedidoId() != null && e.getPedidoId().getFunId() != null
            ? e.getPedidoId().getFunId().getUuid()
            : null);
    dto.setDataDispensa(e.getData());
    dto.setHoraSaida(intervalFormatToHHmm(e.getHoraInicio()));
    dto.setHoraEntrada(intervalFormatToHHmm(e.getHoraFim()));
    var mins = diffMinutes(e.getHoraInicio(), e.getHoraFim());
    dto.setTotalHoras(formatMinutes(mins));
    dto.setTipoMotivo(e.getTipoDispensa());
    dto.setMotivo(StringUtils.hasText(e.getDescricaoMotivo()) ? e.getDescricaoMotivo() : null);
    dto.setParecerResponsavel(e.getDecisaoResponsavel());
    dto.setObservacaoResponsavel(e.getObsResponsavel());
    dto.setObservacaoRh(e.getObsRh());

    if (dto.getColaborador() != null && dto.getDataDispensa() != null) {
      var inicioMes = dto.getDataDispensa().withDayOfMonth(1);
      var fimMes = dto.getDataDispensa().withDayOfMonth(dto.getDataDispensa().lengthOfMonth());
      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataBetween(
          dto.getColaborador(), inicioMes, fimMes);
      int totalMin = 0;
      for (var d : listaMes) {
        var minsItem = diffMinutes(d.getHoraInicio(), d.getHoraFim());
        if (minsItem != null) totalMin += minsItem;
      }
      dto.setHorasUsadasMes(formatMinutes(totalMin));
    }
    return dto;
  }

  @Transactional(readOnly = true)
  public DispensaReqDTO getDispensaByPedidoId(GetDispensaByPedidoIdQuery query) {
    if (query == null || !StringUtils.hasText(query.getPedidoId())) {
      return new DispensaReqDTO();
    }

    var e = dispensaRepository.findByPedidoId_Uuid(UUID.fromString(query.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Dispensa nao encontrada",
            query.getPedidoId()));

    var dto = new DispensaReqDTO();
    dto.setColaborador(
        e.getPedidoId() != null && e.getPedidoId().getFunId() != null
            ? e.getPedidoId().getFunId().getUuid()
            : null);
    dto.setDataDispensa(e.getData());
    dto.setHoraSaida(intervalFormatToHHmm(e.getHoraInicio()));
    dto.setHoraEntrada(intervalFormatToHHmm(e.getHoraFim()));
    var mins = diffMinutes(e.getHoraInicio(), e.getHoraFim());
    dto.setTotalHoras(formatMinutes(mins));
    dto.setTipoMotivo(e.getTipoDispensa());
    dto.setMotivo(StringUtils.hasText(e.getDescricaoMotivo()) ? e.getDescricaoMotivo() : null);
    dto.setParecerResponsavel(e.getDecisaoResponsavel());
    dto.setObservacaoResponsavel(e.getObsResponsavel());
    dto.setObservacaoRh(e.getObsRh());

    if (dto.getColaborador() != null && dto.getDataDispensa() != null) {
      var inicioMes = dto.getDataDispensa().withDayOfMonth(1);
      var fimMes = dto.getDataDispensa().withDayOfMonth(dto.getDataDispensa().lengthOfMonth());
      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataBetween(
          dto.getColaborador(), inicioMes, fimMes);
      int totalMin = 0;
      for (var d : listaMes) {
        var minsItem = diffMinutes(d.getHoraInicio(), d.getHoraFim());
        if (minsItem != null) totalMin += minsItem;
      }
      dto.setHorasUsadasMes(formatMinutes(totalMin));
    }
    return dto;
  }

  private static String formatMinutes(Integer minutes) {
    if (minutes == null)
      return null;
    int h = minutes / 60;
    int m = minutes % 60;
    String mm = (m < 10 ? "0" : "") + m;
    return h + ":" + mm;
  }

  private String intervalFormatToHHmm(String interval) {
    if (interval == null || interval.isBlank()) {
      return "00:00";
    }

    try {
      // Divide pelo espaço para pegar a parte do tempo
      String[] parts = interval.trim().split("\\s+");
      if (parts.length < 2) {
        throw new IllegalArgumentException("Formato inválido de interval: " + interval);
      }

      // Pega HH:MM:SS
      String timePart = parts[1];
      String[] hm = timePart.split(":");

      if (hm.length < 2) {
        throw new IllegalArgumentException("Formato inválido de tempo: " + timePart);
      }

      int hours = Integer.parseInt(hm[0]);
      int minutes = Integer.parseInt(hm[1]);

      return String.format("%02d:%02d", hours, minutes);

    } catch (Exception e) {
      throw new IllegalArgumentException("Erro ao converter interval para HH:MM: " + interval, e);
    }
  }

}
