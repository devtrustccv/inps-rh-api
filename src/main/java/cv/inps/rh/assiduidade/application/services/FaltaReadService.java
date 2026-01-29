package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.FaltaListDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;
import cv.inps.rh.assiduidade.application.queries.GetFaltaQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaFaltaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final PedidoEntityRepository pedidoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaFaltaDTO faltaReadService(GetListaFaltaQuery query) {
    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<FaltaEntity> spec = buildSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<FaltaEntity> page = faltaRepository.findAll(spec, pageable);

    List<FaltaListDTO> content = page.getContent().stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaFaltaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<FaltaEntity> buildSpec(GetListaFaltaQuery query) {
    return (root, cq, cb) -> {
      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getColaborador())) {
        predicates.add(
            cb.like(
                cb.lower(root.get("pedidoId").get("funId").get("nome")),
                "%" + query.getColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        if (di != null) {
          predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), di.atStartOfDay()));
        }
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        if (df != null) {
          predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), df.atTime(23, 59, 59)));
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

  private FaltaListDTO toDTO(FaltaEntity e) {
    var dto = new FaltaListDTO();
    dto.setId(e.getId());
    dto.setUuid(e.getUuid() != null ? e.getUuid().toString() : null);
    dto.setNomeColaborador(
        e.getPedidoId() != null && e.getPedidoId().getFunId() != null ? e.getPedidoId().getFunId().getNome() : null);
    dto.setDirecao(
        e.getTiprelId() != null &&
            e.getTiprelId().getMobId() != null &&
            e.getTiprelId().getMobId().getInstidId() != null ? e.getTiprelId().getMobId().getInstidId().getNome()
                : null);
    dto.setCategoria(
        e.getTiprelId() != null &&
            e.getTiprelId().getCargoId() != null ? e.getTiprelId().getCargoId().getNome() : null);
    var di = e.getDataInicio() != null ? DateFormatter.localDateTimeToLocalDateString(e.getDataInicio()) : "";
    var df = e.getDataFim() != null ? DateFormatter.localDateTimeToLocalDateString(e.getDataFim()) : "";
    dto.setDataIntervalo(di + " / " + df);
    dto.setMotivo(e.getDescricaoMotivo());
    dto.setTotalHorasAusente(parseToMinutes(e.getHorasAusencia()));
    dto.setNumFalta(1);
    dto.setValorADescontar(
        e.getDefRemId() != null && e.getDefRemId().getValor() != null ? e.getDefRemId().getValor() : null);
    dto.setDescontoRenumeracao(Objects.equals(e.getFlgDescontoSal(), 1));
    dto.setEstadoProcessamento(e.getPedidoId() != null ? e.getPedidoId().getEtapa() : null);
    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);
    return dto;
  }

  private static Integer parseToMinutes(String s) {
    if (!StringUtils.hasText(s))
      return 0;
    var parts = s.split(":");
    try {
      if (parts.length == 3) {
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        int sec = Integer.parseInt(parts[2]);
        return h * 60 + m + (sec / 60);
      } else if (parts.length == 2) {
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        return h * 60 + m;
      } else {
        return Integer.parseInt(s);
      }
    } catch (NumberFormatException ex) {
      return 0;
    }
  }



  @Transactional(readOnly = true)
  public FaltaReqDTO getFalta(GetFaltaQuery query) {
    if (query == null || !StringUtils.hasText(query.getPedidoId())) {
      return new FaltaReqDTO();
    }
    UUID pedidoUuid = UUID.fromString(query.getPedidoId());

    // Buscar pedido
    var pedido = pedidoEntityRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Registo de marcacao de falta nao encontrada com: " + query.getPedidoId())
        );

    // Buscar todas as faltas desse pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);
    if (faltas.isEmpty()) {
      return new FaltaReqDTO();
    }

    // Usar a primeira falta para os campos comuns (horasAusencia, justificativa, etc.)
    var primeiraFalta = faltas.getFirst();

    // Montar DTO
    var dto = new FaltaReqDTO();
    var funcionario = pedido.getFunId();

    dto.setColaboradorId(funcionario != null ? funcionario.getUuid() : null);
    dto.setColaboradorNome(funcionario != null ? funcionario.getNome() : null);

    // Determinar período
    LocalDate dataInicio = faltas.stream()
        .map(FaltaEntity::getDataInicio)
        .filter(d -> d != null)
        .map(LocalDateTime::toLocalDate)
        .min(LocalDate::compareTo)
        .orElse(null);

    LocalDate dataFim = faltas.stream()
        .map(FaltaEntity::getDataFim)
        .filter(d -> d != null)
        .map(LocalDateTime::toLocalDate)
        .max(LocalDate::compareTo)
        .orElse(null);

    dto.setDataInicio(dataInicio);
    dto.setDataFim(dataFim);

    if (dataInicio != null && dataFim != null) {
      long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
      dto.setTotalDias((int) Math.max(dias, 1));
    }

    dto.setTotalDeHorasAusentes(primeiraFalta.getHorasAusencia());
    dto.setJustificar(primeiraFalta.getFlgJustificativo());
    dto.setMotivoAusencia(primeiraFalta.getDescricaoMotivo());
    dto.setParecer(primeiraFalta.getDecisaoResponsavel());
    dto.setObservacao(primeiraFalta.getObsResponsavel());
    dto.setDespachoRh(primeiraFalta.getDespachoRh());
    dto.setTipoJustificacao(primeiraFalta.getParamSitId() != null ? primeiraFalta.getParamSitId().getId() : null);

    return dto;
  }

}
