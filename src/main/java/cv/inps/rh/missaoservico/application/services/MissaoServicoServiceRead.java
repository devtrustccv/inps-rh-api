package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.missaoservico.application.queries.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.missaoservico.application.dto.NotificacaoMissaoResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MissaoServicoServiceRead {

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_INATIVO = "I";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final DocumentoMapper documentoMapper;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;
  private final MissaoProcessoEntityRepository missaoProcessoRepository;

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoPagamentoResponseDTO> getPagamento(GetMissaoServicoPagamentoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var response = new MissaoPagamentoResponseDTO();
    response.setMissaoId(missao.getId());
    // No modelo por processo a etapa da missão fica em SUBMISSAO: mostrar a do processo activo
    // mais atrasado, como na lista, em vez de um valor que não diz nada ao ecrã.
    var processos = missaoProcessoRepository.findAllByMissaoServId_UuidOrderByIdAsc(missaoUuid);
    var etapa = processoMaisAtrasado(processos).map(MissaoProcessoEntity::getEtapa).orElse(missao.getEtapa());
    response.setEtapaAtual(etapa);
    var etapaEnum = EtapaProcesso.fromCode(etapa);
    response.setEtapaAtualDesc(etapaEnum != null ? etapaEnum.getDescricao() : resolveEtapaDesc(etapa));
    response.setEstado(missao.getEstado());
    response.setReferenciaPagamento(missao.getReferenciaPagamento());
    response.setDataPagamento(missao.getDataPagamento());
    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoSubmissaoResponseDTO> getSubmissao(GetSubmissaoServicoProcessQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var colaboradores = colaboradoresDaMissao(missaoUuid);

    var docs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_SERVICO.name(),
        missaoUuid);
    var documentos = docs == null
        ? List.<AnexoRespDTO>of()
        : docs.stream()
        .filter(d -> d != null && d.getEstado() != Estado.E)
        .sorted(Comparator.comparing(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity::getId,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(documentoMapper::toRespDto)
        .filter(Objects::nonNull)
        .toList();

    var response = new MissaoSubmissaoResponseDTO();
    response.setId(missao.getId());
    response.setUuid(missao.getUuid());
    response.setNrMissao(missao.getNrMissao());
    response.setAno(missao.getAno());
    response.setNrMissaoFormatado(formatarNrMissao(missao));
    response.setEtapaAtual(missao.getEtapa());
    response.setEtapaAtualDesc(resolveEtapaDesc(missao.getEtapa()));
    response.setPaisDestinoId(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getId() : null);
    response.setPaisDestinoNome(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getNome() : null);
    response.setIlhaId(missao.getIlhaId() != null ? missao.getIlhaId().getId() : null);
    response.setIlhaNome(missao.getIlhaId() != null ? missao.getIlhaId().getNome() : null);
    response.setConcelhoId(missao.getConcelhoId() != null ? missao.getConcelhoId().getId() : null);
    response.setConcelhoNome(missao.getConcelhoId() != null ? missao.getConcelhoId().getNome() : null);
    response.setFlgDestino(missao.getFlgDestino());
    response.setDescricaoDestino(missao.getDescricaoDestino());
    response.setAmbitoMissao(missao.getAmbitoMissao());
    response.setTipoDestino(resolveAmbitoMissao(missao.getFlgDestino()));
    response.setDataInicio(missao.getDataInicio());
    response.setDataFim(missao.getDataFim());
    response.setNrDias(missao.getNrDias());
    response.setAutorizadoPor(missao.getAutorizadoPor());
    response.setDataAutorizacao(missao.getDataAutorizacao());
    response.setEtapa(missao.getEtapa());
    response.setEstado(missao.getEstado());
    response.setColaboradores(colaboradores);
    response.setDocumentos(documentos);
    var processos = processosDaMissao(missaoUuid);
    response.setProcessos(processos);
    response.setAlojamento(processos.stream()
        .anyMatch(p -> TipoProcesso.ALOJAMENTO.name().equals(p.getTipoProcesso()) && ESTADO_ATIVO.equals(p.getEstado())));

    response.setDataRegisto(toLocalDate(missao.getCreatedDate()));
    response.setUserRegistoId(missao.getCreatedById());
    response.setUserRegistoName(missao.getCreatedBy());
    response.setUserAlteracaoId(missao.getLastModifiedById());
    response.setUserAlteracaoName(missao.getLastModifiedBy());
    response.setDataAlteracao(toLocalDate(missao.getLastModifiedDate()));

    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoServicoResponseDTO> getDetalhe(GetDetalheMissaoServicoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var response = new MissaoServicoResponseDTO();
    response.setId(missao.getId());
    response.setUuid(missao.getUuid());
    response.setNrMissao(missao.getNrMissao());
    response.setAno(missao.getAno());
    response.setNrMissaoFormatado(formatarNrMissao(missao));
    response.setPaisDestinoId(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getId() : null);
    response.setPaisDestinoNome(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getNome() : null);
    response.setFlgDestino(missao.getFlgDestino());
    response.setDescricaoDestino(missao.getDescricaoDestino());
    response.setAmbitoMissao(missao.getAmbitoMissao());
    response.setTipoDestino(resolveAmbitoMissao(missao.getFlgDestino()));
    response.setDataInicio(missao.getDataInicio());
    response.setDataFim(missao.getDataFim());
    response.setNrDias(missao.getNrDias());
    response.setAutorizadoPor(missao.getAutorizadoPor());
    response.setDataAutorizacao(missao.getDataAutorizacao());
    response.setEtapa(missao.getEtapa());
    response.setEstado(missao.getEstado());
    response.setMotivoCancelamento(missao.getMotivoCancelamento());

    response.setDataRegisto(toLocalDate(missao.getCreatedDate()));
    response.setUserRegistoId(missao.getCreatedById());
    response.setUserRegistoName(missao.getCreatedBy());
    response.setUserAlteracaoId(missao.getLastModifiedById());
    response.setUserAlteracaoName(missao.getLastModifiedBy());
    response.setDataAlteracao(toLocalDate(missao.getLastModifiedDate()));

    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<WrapperListMissaoServicoDTO> getLista(GetListaMissaoServicoQuery query) {

    // Aceita "12" (nº em qualquer ano) ou "12/2026" (nº daquele ano), tal como é apresentado.
    var nrMissaoRaw = query != null ? query.getNrMissao() : null;
    Long nrMissao;
    Integer anoFiltro = null;
    if (StringUtils.hasText(nrMissaoRaw) && nrMissaoRaw.contains("/")) {
      var partes = nrMissaoRaw.split("/", 2);
      nrMissao = parseLongSafe(partes[0].trim());
      anoFiltro = parseIntOrNull(partes[1].trim());
    } else {
      nrMissao = parseLongSafe(nrMissaoRaw);
    }

    var periodoDe = parseDateSafe(query != null ? query.getPeriodoDe() : null);
    var periodoAte = parseDateSafe(query != null ? query.getPeriodoAte() : null);
    int pageNumber = parseIntSafe(query != null ? query.getPageNumber() : null, 0);
    int pageSize = parseIntSafe(query != null ? query.getPageSize() : null, 10);

    // Ordena por ano e depois por nº: com a numeração a reiniciar todos os anos, ordenar só pelo
    // nº misturaria os anos (o 1/2027 apareceria depois do 9/2026).
    var pageable = PageRequest.of(pageNumber, pageSize,
        Sort.by(Sort.Direction.DESC, "ano").and(Sort.by(Sort.Direction.DESC, "nrMissao")));

    final var ano = anoFiltro;
    Specification<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity> spec = (root, q, cb) -> {
      var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
      if (nrMissao != null) {
        predicates.add(cb.equal(root.get("nrMissao"), nrMissao));
      }
      if (ano != null) {
        predicates.add(cb.equal(root.get("ano"), ano));
      }
      if (periodoDe != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), periodoDe));
      }
      if (periodoAte != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), periodoAte));
      }
      return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
    };

    var page = missaoServicoRepository.findAll(spec, pageable);
    page.getContent();
    var missoes = page.getContent();

    var missaoById = new HashMap<Long, cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity>();
    var missaoIds = new ArrayList<Long>();
    for (var m : missoes) {
      if (m == null || m.getId() == null)
        continue;
      missaoById.put(m.getId(), m);
      missaoIds.add(m.getId());
    }

    var totalsByMissao = new HashMap<Long, Map<String, BigDecimal>>();
    var totaisPorProcesso = new HashMap<Long, BigDecimal>();
    var processosPorMissao = new HashMap<Long, List<MissaoProcessoEntity>>();
    if (!missaoIds.isEmpty()) {
      for (var p : missaoProcessoRepository.findAllByMissaoServId_IdIn(missaoIds)) {
        processosPorMissao.computeIfAbsent(p.getMissaoServId().getId(), _ -> new ArrayList<>()).add(p);
      }
    }
    if (!missaoIds.isEmpty()) {
      Specification<MissaoLogisticaEntity> logSpec = (root, q, cb) -> cb.and(
          root.get("missaoServId").get("id").in(missaoIds),
          cb.equal(root.get("estado"), ESTADO_ATIVO));
      var logs = missaoLogisticaRepository.findAll(logSpec);
      if (!CollectionUtils.isEmpty(logs)) {
        for (var l : logs) {
          if (l == null || l.getMissaoServId() == null || l.getMissaoServId().getId() == null)
            continue;
          var mid = l.getMissaoServId().getId();
          var ref = l.getReferencia();
          if (!StringUtils.hasText(ref))
            continue;
          var v = l.getValorTotal();
          if (v == null)
            continue;
          totalsByMissao
              .computeIfAbsent(mid, _ -> new HashMap<>())
              .merge(ref.toUpperCase(), v, BigDecimal::add);
          if (l.getMissaoProcessoId() != null) {
            totaisPorProcesso.merge(l.getMissaoProcessoId().getId(), v, BigDecimal::add);
          }
        }
      }
    }

    var content = new ArrayList<MissaoServicoResumoDTO>();
    for (var m : missoes) {
      if (m == null)
        continue;

      var estado = resolveEstadoMissao(m);
      var processosDaMissao = processosPorMissao.getOrDefault(m.getId(), List.of());
      var situacao = processosDaMissao.isEmpty() ? resolveSituacaoLista(m) : situacaoPorProcessos(m, processosDaMissao);
      var etapaAtrasada = processoMaisAtrasado(processosDaMissao).map(MissaoProcessoEntity::getEtapa).orElse(m.getEtapa());

      var sums = totalsByMissao.getOrDefault(m.getId(), java.util.Map.of());
      var dto = new MissaoServicoResumoDTO();
      dto.setId(m.getId());
      dto.setUuid(m.getUuid());
      dto.setNrMissao(m.getNrMissao());
      dto.setAno(m.getAno());
      dto.setNrMissaoFormatado(formatarNrMissao(m));
      dto.setDestino(m.getDescricaoDestino());
      dto.setNacionalInternacional(resolveNacionalInternacional(m.getFlgDestino()));
      dto.setDataMissao(m.getDataInicio());
      // Etapa da missão = a do processo activo mais atrasado (a etapa da missão fica em SUBMISSAO)
      dto.setEtapa(etapaAtrasada);
      var etapaEnum = EtapaProcesso.fromCode(etapaAtrasada);
      dto.setEtapaDesc(etapaEnum != null ? etapaEnum.getDescricao() : resolveEtapaLista(etapaAtrasada));
      dto.setEstado(estado.estado());
      dto.setEstadoDesc(estado.estadoDesc());
      dto.setSituacao(situacao.estado());
      dto.setSituacaoDesc(situacao.estadoDesc());
      dto.setValorAC(sums.get("AJUDA_CUSTO"));
      dto.setValorBP(sums.get("BILHETE_PASSAGEM"));
      dto.setValorAlojamento(sums.get("ALOJAMENTO"));
      dto.setValorSeguro(sums.get("SEGURO_VIAGEM"));
      dto.setProcessos(processosDaMissao.stream()
          .sorted(Comparator.comparing(MissaoProcessoEntity::getId))
          .map(p -> {
            var pd = new MissaoProcessoResumoDTO();
            pd.setUuid(p.getUuid());
            pd.setTipoProcesso(p.getTipoProcesso());
            pd.setTipoProcessoDesc(TipoProcesso.fromCodeOrThrow(p.getTipoProcesso()).getDescricao());
            pd.setEtapa(p.getEtapa());
            var e = EtapaProcesso.fromCode(p.getEtapa());
            pd.setEtapaDesc(e != null ? e.getDescricao() : p.getEtapa());
            pd.setEstado(p.getEstado());
            pd.setValorTotal(totaisPorProcesso.get(p.getId()));
            return pd;
          })
          .toList());
      content.add(dto);
    }

    var wrapper = new WrapperListMissaoServicoDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return ResponseEntity.ok(wrapper);
  }

  /** Colaboradores ativos afetos à missão — usado pelos ecrãs que precisam de popular multiselects. */
  private List<MissaoColaboradorResponseDTO> colaboradoresDaMissao(UUID missaoUuid) {
    return missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(c -> c != null && ESTADO_ATIVO.equals(c.getEstado()))
        .map(this::toColaboradorDto)
        .toList();
  }

  private MissaoColaboradorResponseDTO toColaboradorDto(MissaoColaboradorEntity c) {
    if (c == null)
      return null;
    var dto = new MissaoColaboradorResponseDTO();
    dto.setId(c.getId());
    dto.setUuid(c.getUuid());
    dto.setEstado(c.getEstado());
    // O nº de documento gravado na missão manda: é editável no ecrã e representa o documento
    // usado naquela missão (ex.: passaporte, quando o cadastro tem BI). Sem valor gravado —
    // registos antigos — cai para o do funcionário.
    var numDocumentoFuncionario = c.getFunId() != null ? c.getFunId().getNumDocumento() : null;
    dto.setNumDocumento(StringUtils.hasText(c.getNumDocumento())
        ? c.getNumDocumento()
        : numDocumentoFuncionario);
    dto.setFunId(c.getFunId() != null ? c.getFunId().getId() : null);
    dto.setFunUuid(c.getFunId() != null ? c.getFunId().getUuid() : null);
    dto.setNomeColaborador(c.getFunId() != null ? c.getFunId().getNome() : null);
    return dto;
  }

  /** Os processos da missão, pela ordem de criação, com as descrições de tipo e etapa. */
  private List<MissaoProcessoResponseDTO> processosDaMissao(UUID missaoUuid) {
    return missaoProcessoRepository.findAllByMissaoServId_UuidOrderByIdAsc(missaoUuid)
        .stream()
        .map(p -> {
          var dto = new MissaoProcessoResponseDTO();
          dto.setId(p.getId());
          dto.setUuid(p.getUuid());
          dto.setTipoProcesso(p.getTipoProcesso());
          dto.setTipoProcessoDesc(TipoProcesso.fromCodeOrThrow(p.getTipoProcesso()).getDescricao());
          dto.setEtapa(p.getEtapa());
          var etapa = EtapaProcesso.fromCode(p.getEtapa());
          dto.setEtapaDesc(etapa != null ? etapa.getDescricao() : p.getEtapa());
          dto.setEstado(p.getEstado());
          return dto;
        })
        .toList();
  }

  private String resolveAmbitoMissao(Integer flgDestino) {
    if (flgDestino == null)
      return null;
    if (Integer.valueOf(1).equals(flgDestino))
      return "NACIONAL";
    if (Integer.valueOf(2).equals(flgDestino))
      return "INTERNACIONAL";
    return null;
  }

  private String resolveNacionalInternacional(Integer flgDestino) {
    if (flgDestino == null)
      return null;
    if (Integer.valueOf(1).equals(flgDestino))
      return "Nacional";
    if (Integer.valueOf(2).equals(flgDestino))
      return "Internacional";
    return null;
  }

  /**
   * Estado da missão — o registo em si: activa ou cancelada (RH_T_MISSAO_SERVICO.ESTADO = 'A'/'I').
   * Não confundir com a etapa (onde o processo vai) nem com a situação (o que falta fazer).
   */
  private EstadoDesc resolveEstadoMissao(MissaoServicoEntity missao) {
    if (missao == null || !StringUtils.hasText(missao.getEstado())) {
      return new EstadoDesc("", "");
    }
    if (ESTADO_INATIVO.equals(missao.getEstado()))
      return new EstadoDesc(ESTADO_INATIVO, "Cancelado");
    if ("FINALIZADO".equals(missao.getEstado()))
      return new EstadoDesc("FINALIZADO", "Finalizado");
    return new EstadoDesc(ESTADO_ATIVO, "Activo");
  }

  /** Processo activo mais atrasado — define a etapa e a situação da missão na lista. */
  private Optional<MissaoProcessoEntity> processoMaisAtrasado(List<MissaoProcessoEntity> processos) {
    return processos.stream()
        .filter(p -> ESTADO_ATIVO.equals(p.getEstado()))
        .min(Comparator.comparing(p -> {
          var e = EtapaProcesso.fromCode(p.getEtapa());
          return e != null ? e.ordinal() : -1;
        }));
  }

  /** Situação da missão no modelo por processo: o que falta ao processo mais atrasado. */
  private EstadoDesc situacaoPorProcessos(MissaoServicoEntity missao, List<MissaoProcessoEntity> processos) {
    if ("FINALIZADO".equals(missao.getEstado()))
      return missao.getReferenciaPagamento() != null || missao.getDataPagamento() != null
          ? new EstadoDesc("PAGO", "Pago")
          : new EstadoDesc("POR_PAGAR", "Autorizado — por pagar");
    var etapa = processoMaisAtrasado(processos).map(p -> EtapaProcesso.fromCode(p.getEtapa())).orElse(null);
    if (etapa == null)
      return new EstadoDesc("", "");
    return switch (etapa) {
      case PRESTADOR_SERVICO, EMISSAO_REQUISICAO -> new EstadoDesc("PENDENTE_REQUISICAO", "Pendente de Requisição");
      case LOGISTICA -> new EstadoDesc("PENDENTE_FATURA", "Pendente de Fatura");
      case VALIDACAO_UGAL, APROVACAO_RH -> new EstadoDesc("EM_VALIDACAO", "Em Validação");
      case CABIMENTO, AUTORIZACAO -> new EstadoDesc("POR_PAGAR", "Por pagar");
      case PAGAMENTO -> new EstadoDesc("PAGO", "Pago");
    };
  }

  /**
   * Situação do processo — derivada da etapa, indica o que falta. Não vem da especificação;
   * é o badge que a listagem já apresentava no campo "estado", agora com nome próprio.
   */
  private EstadoDesc resolveSituacaoLista(MissaoServicoEntity missao) {
    if (missao == null || !StringUtils.hasText(missao.getEtapa())) {
      return new EstadoDesc("", "");
    }

    var etapa = missao.getEtapa();

    if ("PAGAMENTO".equals(etapa)) {
      return missao.getReferenciaPagamento() != null || missao.getDataPagamento() != null
          ? new EstadoDesc("PAGO", "Pago")
          : new EstadoDesc("POR_PAGAR", "Por pagar");
    }

    if ("CABIMENTO".equals(etapa)) {
      return new EstadoDesc("POR_PAGAR", "Por pagar");
    }

    if ("LOGISTICA".equals(etapa)) {
      return new EstadoDesc("PENDENTE_FATURA", "Pendente de Fatura");
    }

    return new EstadoDesc("PENDENTE_REQUISICAO", "Pendente de Requisição");
  }

  /** Descrição legível da etapa — os mesmos rótulos dos separadores do processo. */
  private String resolveEtapaDesc(String etapa) {
    if (!StringUtils.hasText(etapa))
      return "";
    return switch (etapa) {
      case "SUBMISSAO" -> "Submissão e Autorização";
      case "ANALISE" -> "Análise / Verificação";
      case "EMISSAO_REQUISICAO" -> "Emissão de Requisição";
      case "LOGISTICA" -> "Processamento Logístico";
      case "CABIMENTO" -> "Cabimento";
      case "PAGAMENTO" -> "Pagamento";
      default -> etapa;
    };
  }

  /** Nº da missão como "nr/ano" — ex.: "1/2026". Sem ano (registos antigos), devolve só o número. */
  private String formatarNrMissao(MissaoServicoEntity missao) {
    if (missao == null || missao.getNrMissao() == null)
      return null;
    return missao.getAno() != null
        ? missao.getNrMissao() + "/" + missao.getAno()
        : String.valueOf(missao.getNrMissao());
  }

  /**
   * Descrição da etapa na listagem — delega em {@link #resolveEtapaDesc} para a coluna Etapa
   * mostrar o mesmo texto que os ecrãs do processo. Antes só traduzia três etapas e devolvia o
   * código cru nas restantes, pelo que a lista mostrava "SUBMISSAO", "ANALISE" e
   * "EMISSAO_REQUISICAO" ao utilizador.
   */
  private String resolveEtapaLista(String etapa) {
    return resolveEtapaDesc(etapa);
  }

  private record EstadoDesc(String estado, String estadoDesc) {
  }

  private Long parseLongSafe(String raw) {
    if (!StringUtils.hasText(raw))
      return null;
    try {
      return Long.valueOf(raw.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private Integer parseIntOrNull(String raw) {
    if (!StringUtils.hasText(raw))
      return null;
    try {
      return Integer.valueOf(raw.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private int parseIntSafe(String raw, int defaultValue) {
    if (!StringUtils.hasText(raw))
      return defaultValue;
    try {
      return Integer.parseInt(raw.trim());
    } catch (Exception e) {
      return defaultValue;
    }
  }

  private LocalDate parseDateSafe(String raw) {
    if (!StringUtils.hasText(raw))
      return null;
    try {
      return LocalDate.parse(raw.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private java.time.LocalDate toLocalDate(LocalDateTime dt) {
    return dt != null ? dt.toLocalDate() : null;
  }

  /**
   * Todas as notificações emitidas no âmbito de uma missão — ecrã "Ver Notificação" da Lista Missão.
   *
   * <p>Ficam espalhadas por quatro referências: a missão (cancelamento), o prestador (pedido de
   * proposta, um registo por email), a requisição (envio ao prestador) e o colaborador (aviso de
   * logística). Este método junta-as e ordena da mais recente para a mais antiga.
   */
  @Transactional(readOnly = true)
  public ResponseEntity<List<NotificacaoMissaoResponseDTO>> listarNotificacoes(UUID missaoUuid) {
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var encontradas = new java.util.LinkedHashMap<Long, NotificacaoMissaoResponseDTO>();
    java.util.function.BiConsumer<String, UUID> recolher = (referencia, uuid) -> {
      if (uuid == null)
        return;
      for (var n : notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(referencia, uuid)) {
        encontradas.putIfAbsent(n.getId(), toNotificacaoDto(n, referencia));
      }
    };

    recolher.accept(TableName.RH_T_MISSAO_SERVICO.name(), missao.getUuid());
    for (var p : missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid))
      recolher.accept(TableName.RH_T_MISSAO_PRESTADOR.name(), p.getUuid());
    for (var r : missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid))
      recolher.accept(TableName.RH_T_MISSAO_REQUISICAO.name(), r.getUuid());
    for (var c : missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid))
      recolher.accept(TableName.RH_T_MISSAO_COLABORADOR.name(), c.getUuid());

    var out = new java.util.ArrayList<>(encontradas.values());
    out.sort(java.util.Comparator.comparing(NotificacaoMissaoResponseDTO::getDataEnvio,
        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())));
    return ResponseEntity.ok(out);
  }

  private NotificacaoMissaoResponseDTO toNotificacaoDto(NotificacaoEntity n, String origem) {
    var dto = new NotificacaoMissaoResponseDTO();
    dto.setUuid(n.getUuid());
    dto.setTipoNotificacao(n.getTipoNotificacao());
    dto.setAssunto(n.getAssunto());
    dto.setMensagem(n.getMessage());
    dto.setEmail(n.getEmail());
    dto.setNomeReceptor(n.getNomeReceptor());
    dto.setDataEnvio(n.getDataEnvio());
    dto.setEstado(n.getEstado());
    dto.setOrigem(origem);
    return dto;
  }
}
