package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.FaltaListDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;
import cv.inps.rh.assiduidade.application.queries.GetFaltaQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaFaltaQuery;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.application.constants.custom.TableName;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
      if (StringUtils.hasText(query.getFuncionarioUuid())) {
        try {
          var funcUuid = UUID.fromString(query.getFuncionarioUuid());
          predicates.add(cb.equal(root.get("funcionarioUuid"), funcUuid));
        } catch (IllegalArgumentException ignored) {
          // Ignore invalid UUIDs
        }
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
    UUID pedidoUuid = ValidationUtil.parseUuid(query.getPedidoId(), "Identificador do pedido");

    // Buscar pedido
    var pedido = pedidoEntityRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Registo de marcacao de falta nao encontrada com: " + query.getPedidoId()));

    // Buscar todas as faltas desse pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoIdOrderByDataInicioAsc(pedido);
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
            .filter(Objects::nonNull)
        .map(LocalDateTime::toLocalDate)
        .min(LocalDate::compareTo)
        .orElse(null);

    LocalDate dataFim = faltas.stream()
        .map(FaltaEntity::getDataFim)
            .filter(Objects::nonNull)
        .map(LocalDateTime::toLocalDate)
        .max(LocalDate::compareTo)
        .orElse(null);

    dto.setDataInicio(dataInicio);
    dto.setDataFim(dataFim);

    if (dataInicio != null && dataFim != null) {
      long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
      dto.setTotalDias((int) Math.max(dias, 1));
    }

    // Total do PERÍODO, em HH:MM — é o que o formulário mostra e o que o POST recebe
    // ("32:00" para 4 dias de 8h). Devolvia o intervalo Oracle cru de um só dia
    // ("0 8:0:0.0"), que nem era o total nem era reenviável: o round-trip do ecrã não fechava.
    int minutosTotais = faltas.stream()
        .map(FaltaEntity::getHorasAusencia)
        .filter(Objects::nonNull)
        .mapToInt(h -> TimeUtils.parseHorasFlexivel(TimeUtils.intervalFormatToHHmm(h)))
        .sum();
    dto.setTotalDeHorasAusentes(String.format("%02d:%02d", minutosTotais / 60, minutosTotais % 60));

    dto.setJustificar(primeiraFalta.getFlgJustificativo());
    dto.setMotivoAusencia(primeiraFalta.getDescricaoMotivo());
    dto.setParecer(primeiraFalta.getDecisaoResponsavel());
    dto.setObservacao(primeiraFalta.getObsResponsavel());
    // A guarda tem de cobrir também o funcionário do responsável: RH_T_RESPONSAVEL
    // pode existir sem FUN_ID preenchido.
    var funResponsavel = primeiraFalta.getResponsavelId() != null
        ? primeiraFalta.getResponsavelId().getFunId()
        : null;
    dto.setResponsavel(funResponsavel != null ? funResponsavel.getUuid() : null);
    dto.setResponsavelNome(funResponsavel != null ? funResponsavel.getNome() : null);
    dto.setTipoJustificacao(primeiraFalta.getParamSitId() != null ? primeiraFalta.getParamSitId().getId() : null);

    // "Deduzir Falta Em" era gravado e nunca lido. É o campo mais caro deste ecrã: o PUT de
    // despacho aplica o que o formulário enviar, e um combo carregado a null reenviava null —
    // a dedução em férias desaparecia no despacho e os dias iam todos a desconto no salário,
    // sem ninguém o ter pedido.
    dto.setDeduzirFaltaEm(primeiraFalta.getFlgDescontoFalta());

    // Valores: o POST já os devolve, o GET devolvia null e o ecrã de despacho abria sem eles.
    dto.setValorDiario(primeiraFalta.getValor());
    var valorTotal = faltas.stream()
        .map(FaltaEntity::getValor)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    dto.setValorTotal(valorTotal);

    // Bruto vs efectivo — ver FaltaDescontoService.valorDescontado.
    dto.setValorDescontado(FaltaDescontoService.valorDescontado(faltas));
    dto.setValorCoberto(FaltaDescontoService.valorCoberto(faltas));

    // Os anexos da marcação são gravados contra o PEDIDO (ver FaltaServiceWrite), por isso a
    // leitura tem de usar a mesma referência: com RH_T_FALTA o GET nunca devolvia nada.
    var documentos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_PEDIDO.name(), pedido.getUuid());

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
