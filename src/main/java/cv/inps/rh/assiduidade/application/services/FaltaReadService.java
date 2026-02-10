package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.FaltaListDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;
import cv.inps.rh.assiduidade.application.queries.GetFaltaQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaFaltaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.VfaltaMensalEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VfaltaMensalEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import io.micrometer.core.instrument.binder.BaseUnits;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final PedidoEntityRepository pedidoEntityRepository;
  private final DocumentoEntityRepository documentoEntityRepository;

  private final VfaltaMensalEntityRepository vfaltaMensalRepository;

  @Transactional(readOnly = true)
  public WrapperListaFaltaDTO faltaReadService(GetListaFaltaQuery query) {

    int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<VfaltaMensalEntity> spec = buildSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<VfaltaMensalEntity> page = vfaltaMensalRepository.findAll(spec, pageable);

    List<FaltaListDTO> content = page.getContent().stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaFaltaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<VfaltaMensalEntity> buildSpec(GetListaFaltaQuery query) {
    return (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(query.getColaborador())) {
        predicates
            .add(cb.like(cb.lower(root.get("nomeFuncionario")), "%" + query.getColaborador().toLowerCase() + "%"));
      }
      if (query.getIlha() != null) {
        predicates.add(cb.equal(root.get("idIlha"), query.getIlha()));
      }
      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("idDirecao"), query.getDirecao()));
      }
      if (query.getSeccao() != null) {
        predicates.add(cb.equal(root.get("idSecao"), query.getSeccao()));
      }
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get("estMensal"), query.getEstado()));
      }
      if (StringUtils.hasText(query.getDataInicio())) {
        var dataInicio = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), dataInicio));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var dataFim = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), dataFim));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private FaltaListDTO toDTO(VfaltaMensalEntity e) {
    var dto = new FaltaListDTO();
    dto.setNomeColaborador(e.getNomeFuncionario());
    dto.setDirecao(e.getNomeDirecao());
    dto.setCategoria(e.getNomeCargo());
    dto.setDataInicio(e.getDataInicio() != null ? e.getDataInicio().toString() : null);
    dto.setDataFim(e.getDataFim() != null ? e.getDataFim().toString() : null);
    dto.setTotalHorasAusente(e.getTotHorAus() != null ? formatarHoras(e.getTotHorAus()) : null);
    dto.setNumFalta(e.getTotFaltas());
    dto.setValorADescontar(e.getTotValDesc());
    dto.setDescontoRenumeracao(Objects.equals("S", e.getFlgDescSal()));
    dto.setEstadoProcessamento(e.getEstProc());
    dto.setEstado(e.getEstMensal());
    dto.setEstadoDesc(e.getEstMensal());
    return dto;
  }

  private String formatarHoras(BigDecimal horas) {
    if (horas == null)
      return "00:00";
    int h = horas.intValue();
    int m = horas.subtract(new BigDecimal(h)).multiply(new BigDecimal(60)).intValue();
    return String.format("%02d:%02d", h, m);
  }

  // get falta quando esta por validar
  @Transactional(readOnly = true)
  public FaltaReqDTO getFalta(GetFaltaQuery query) {
    if (query == null || !StringUtils.hasText(query.getPedidoId())) {
      return new FaltaReqDTO();
    }
    UUID pedidoUuid = UUID.fromString(query.getPedidoId());

    // Buscar pedido
    var pedido = pedidoEntityRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Registo de marcacao de falta nao encontrada com: " + query.getPedidoId()));

    // Buscar todas as faltas desse pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);
    if (faltas.isEmpty()) {
      return new FaltaReqDTO();
    }

    // Usar a primeira falta para os campos comuns (horasAusencia, justificativa,
    // etc.)
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
    dto.setResponsavel(
        primeiraFalta.getResponsavelId() != null ? primeiraFalta.getResponsavelId().getFunId().getUuid() : null);
    dto.setResponsavelNome(
        primeiraFalta.getResponsavelId() != null ? primeiraFalta.getResponsavelId().getFunId().getNome() : null);
    dto.setTipoJustificacao(primeiraFalta.getParamSitId() != null ? primeiraFalta.getParamSitId().getId() : null);

    var documentos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(Referencia.JUSTIFICAR_FALTA.name(),pedido.getUuid());

    if (!CollectionUtils.isEmpty(documentos)) {
      dto.setDocumentos(documentos.stream().map(d -> {
        var anexo = new AnexoReqDTO();
        anexo.setId(d.getId() != null ? d.getId() : null);
        anexo.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
        anexo.setDocumento(d.getUrl());
        return anexo;
      }).collect(Collectors.toList()));
    }

    return dto;
  }

}
