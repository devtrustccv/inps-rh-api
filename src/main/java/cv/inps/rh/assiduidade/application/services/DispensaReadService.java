package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaDispensaDTO;
import cv.inps.rh.assiduidade.application.dto.DispensaListDTO;
import cv.inps.rh.assiduidade.application.queries.GetDispensaByPedidoIdQuery;
import cv.inps.rh.assiduidade.application.queries.GetDispensaQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaDispensaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DispensaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.util.TimeUtils;
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

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispensaReadService {

  private final DispensaEntityRepository dispensaRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final ResponsavelEntityRepository responsavelEntityRepository;

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

      if (StringUtils.hasText(query.getFuncionarioUuid())) {
        try {
          var funcUuid = UUID.fromString(query.getFuncionarioUuid());
          predicates.add(cb.equal(root.get("pedidoId").get("funId").get("uuid"), funcUuid));
        } catch (IllegalArgumentException ignored) {
          // Ignore invalid UUIDs
        }
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
    dto.setTotalHorasSolicitadas(TimeUtils.diffMinutes(hi, hf));
    dto.setMotivoDispensa(StringUtils.hasText(e.getDescricaoMotivo()) ? e.getDescricaoMotivo() : e.getTipoDispensa());
    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);
    return dto;
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
    dto.setColaboradorNome(
        e.getPedidoId() != null && e.getPedidoId().getFunId() != null
            ? e.getPedidoId().getFunId().getNome()
            : null);
    dto.setDataDispensa(e.getData());
    dto.setHoraSaida(TimeUtils.intervalFormatToHHmm((e.getHoraInicio())));
    dto.setHoraEntrada(TimeUtils.intervalFormatToHHmm((e.getHoraInicio())));
    var mins = TimeUtils.diffMinutes(e.getHoraInicio(), e.getHoraFim());
    dto.setTotalHoras(TimeUtils.formatMinutesToHHmm(mins));
    dto.setTipoMotivo(e.getTipoDispensa());
    dto.setMotivo(StringUtils.hasText(e.getDescricaoMotivo()) ? e.getDescricaoMotivo() : null);
    dto.setParecerResponsavel(e.getDecisaoResponsavel());

    if (e.getResponsavelId() != null) {
      responsavelEntityRepository.findById(e.getResponsavelId())
          .ifPresent(responsavel -> {
            dto.setResponsavel(responsavel.getFunId().getUuid());
            dto.setResponsavelNome(responsavel.getFunId().getNome());
          });
    };

    dto.setObservacaoResponsavel(e.getObsResponsavel());
    dto.setObservacaoRh(e.getObsRh());

    if (dto.getColaborador() != null && dto.getDataDispensa() != null) {
      var inicioMes = dto.getDataDispensa().withDayOfMonth(1);
      var fimMes = dto.getDataDispensa().withDayOfMonth(dto.getDataDispensa().lengthOfMonth());
      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataBetween(
          dto.getColaborador(), inicioMes, fimMes);
      int totalMin = 0;
      for (var d : listaMes) {
        var minsItem = TimeUtils.diffMinutes(d.getHoraInicio(), d.getHoraFim());
        totalMin += minsItem;
      }
      dto.setHorasUsadasMes(TimeUtils.formatMinutesToHHmm(totalMin));
    }

    var documentos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_DISPENSA.name(), e.getUuid());

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
    dto.setColaboradorNome(
        e.getPedidoId() != null && e.getPedidoId().getFunId() != null
            ? e.getPedidoId().getFunId().getNome()
            : null);
    dto.setDataDispensa(e.getData());
    dto.setHoraSaida(TimeUtils.intervalFormatToHHmm((e.getHoraInicio())));
    dto.setHoraEntrada(TimeUtils.intervalFormatToHHmm((e.getHoraInicio())));
    var mins = TimeUtils.diffMinutes(e.getHoraInicio(), e.getHoraFim());
    dto.setTotalHoras(TimeUtils.formatMinutesToHHmm(mins));
    dto.setTipoMotivo(e.getTipoDispensa());
    dto.setMotivo(StringUtils.hasText(e.getDescricaoMotivo()) ? e.getDescricaoMotivo() : null);
    dto.setParecerResponsavel(e.getDecisaoResponsavel());
    dto.setObservacaoResponsavel(e.getObsResponsavel());
    dto.setObservacaoRh(e.getObsRh());

    if (e.getResponsavelId() != null) {
      responsavelEntityRepository.findById(e.getResponsavelId())
          .ifPresent(responsavel -> {
            dto.setResponsavel(responsavel.getFunId().getUuid());
            dto.setResponsavelNome(responsavel.getFunId().getNome());
          });
    };

    if (dto.getColaborador() != null && dto.getDataDispensa() != null) {

      var inicioMes = dto.getDataDispensa().withDayOfMonth(1);
      var fimMes = dto.getDataDispensa().withDayOfMonth(dto.getDataDispensa().lengthOfMonth());

      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataBetween(
          dto.getColaborador(), inicioMes, fimMes);
      int totalMin = 0;
      for (var d : listaMes) {
        var minsItem = TimeUtils.diffMinutes(d.getHoraInicio(), d.getHoraFim());
        totalMin += minsItem;
      }
      dto.setHorasUsadasMes(TimeUtils.formatMinutesToHHmm(totalMin));
    }

    var documentos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_DISPENSA.name(), e.getUuid());

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
