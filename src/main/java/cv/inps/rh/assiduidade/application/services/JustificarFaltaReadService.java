package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.dto.ResumoFaltaMesDTO;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaByPedidoQuery;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.util.TimeUtils;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JustificarFaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoEntityRepository;

  private final AssiduidadeSinteseDiarioEntityRepository assiduidadeSinteseDiarioEntityRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final DocumentoMapper documentoMapper;

  /**
   * Rótulo do estado da falta para o resumo. {@code I} lê-se "Rejeitada" e não
   * "Inactiva" — neste ecrã o estado inactivo resulta de o RH ter recusado a
   * justificação.
   */
  private static final String ESTADO_POR_JUSTIFICAR = "Por justificar";

  private static String descreverEstadoFalta(Estado estado) {
    if (estado == null)
      return ESTADO_POR_JUSTIFICAR;
    return switch (estado) {
      case P -> "Pendente";
      case A -> "Justificada";
      case I -> "Rejeitada";
      default -> estado.getDescription();
    };
  }

  /**
   * Anexos do bloco "Justificar Faltas Selecionadas": pertencem ao PEDIDO, não a um dia
   * (ver JustificarFaltaWriteService). Os anexos de um dia continuam em RH_T_FALTA e são
   * devolvidos em {@code FaltaItemDTO.documento}.
   */
  private List<AnexoReqDTO> anexosDoPedido(PedidoEntity pedido) {
    if (pedido == null || pedido.getUuid() == null)
      return new java.util.ArrayList<>();
    return documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_PEDIDO.name(), pedido.getUuid())
        .stream()
        .map(doc -> {
          var anexo = new AnexoReqDTO();
          anexo.setId(doc.getId());
          anexo.setTipoDocumentoId(doc.getTpDocumentoId() != null ? doc.getTpDocumentoId().getId() : null);
          anexo.setDocumento(doc.getUrl());
          return anexo;
        })
        .collect(Collectors.toCollection(java.util.ArrayList::new));
  }

  /** Data de uma falta: a da síntese diária que a originou, ou a sua própria data de início. */
  private static LocalDate dataDaFalta(FaltaEntity f) {
    var sintese = f.getSinteseDiarioId();
    if (sintese != null && sintese.getData() != null)
      return sintese.getData();
    return f.getDataInicio() != null ? f.getDataInicio().toLocalDate() : null;
  }

  @Transactional(readOnly = true)
  public ResumoFaltaMesDTO getFaltaJustificadaResumo(GetJustificacaoFaltaQuery query) {

    UUID funcUuid;
    try {
      funcUuid = UUID.fromString(query.getFuncionarioId());
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest("Funcionario UUID inválido");
    }

    // Buscar funcionário
    FuncionarioEntity funcionario = funcionarioRepository.findByUuid(funcUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Funcionário não encontrado para UUID: " + funcUuid));

    // Calcular intervalo do mês
    LocalDate inicioMes = LocalDate.of(query.getAno(), query.getMes(), 1);
    LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

    // Dias por justificar do mês: só ausências (FALTA=1 ou horas de ausência > 0) e só as que
    // ainda não têm falta associada. O filtro é feito em SQL — ver findAusenciasPorJustificar.
    List<AssiduidadeSinteseDiarioEntity> sinteses = assiduidadeSinteseDiarioEntityRepository
        .findAusenciasPorJustificar(funcionario.getId(), inicioMes, fimMes);

    // Faltas já registadas no período, indexadas pela síntese que as originou.
    // Sem isto o resumo não conseguiria mostrar o estado de cada dia
    // (Pendente / Justificada / Rejeitada).
    Map<Long, FaltaEntity> faltaPorSintese = faltaRepository
        .findAllByFuncionarioAndPeriodo(funcUuid, inicioMes, fimMes)
        .stream()
        .filter(f -> f.getSinteseDiarioId() != null)
        // As eliminadas não contam para nada: nem aparecem no grupo, nem prendem o dia — que
        // volta a ficar por justificar (ver findAusenciasPorJustificar).
        .filter(f -> !Estado.E.equals(f.getEstado()))
        .sorted(Comparator.comparing(JustificarFaltaReadService::dataDaFalta,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .collect(Collectors.toMap(
            f -> f.getSinteseDiarioId().getId(),
            Function.identity(),
            (a, b) -> a,
            LinkedHashMap::new));

    // Os dias JÁ justificados não vêm soltos: vão agrupados no pedido a que pertencem
    // (dto.pedidos), cada grupo com o cabeçalho completo do formulário, para o Editar abrir
    // sem uma segunda chamada. Soltos ficam só os dias por justificar, que a consulta acima
    // já devolve filtrados.
    List<FaltaItemDTO> itensFalta = sinteses.stream()
        .map(sin -> {
          FaltaItemDTO item = new FaltaItemDTO();
          item.setId(sin.getId());
          item.setData(sin.getData().toString());
          // Normalizado para HH:MM — Oracle devolve o INTERVAL como "0 5:20:0.0" e o
          // frontend não tem de conhecer esse formato.
          item.setHorasAusencia(TimeUtils.intervalFormatToHHmm(sin.getHorasAusencia()));
          item.setEstadoDesc(ESTADO_POR_JUSTIFICAR);
          return item;
        })
        .toList();

    // Um grupo por pedido, na ordem do dia mais antigo. Só as faltas DESTE mês entram no
    // grupo: o painel é do mês consultado e um pedido pode atravessar dois meses — para o
    // pedido completo há o GET .../pedido/{pedidoUuid}.
    List<JustificarFaltaDTO> pedidos = faltaPorSintese.values().stream()
        .filter(f -> f.getPedidoId() != null)
        .collect(Collectors.groupingBy(
            f -> f.getPedidoId().getId(),
            LinkedHashMap::new,
            Collectors.toList()))
        .values().stream()
        .map(faltasDoPedido -> {
          var ordenadas = faltasDoPedido.stream()
              .sorted(Comparator.comparing(JustificarFaltaReadService::dataDaFalta,
                  Comparator.nullsLast(Comparator.naturalOrder())))
              .toList();
          return montarGrupo(ordenadas.getFirst().getPedidoId(), ordenadas);
        })
        .sorted(Comparator.comparing(
            g -> g.getItensFalta().isEmpty() ? null : g.getItensFalta().getFirst().getData(),
            Comparator.nullsLast(Comparator.naturalOrder())))
        .toList();

    var dto = new ResumoFaltaMesDTO();
    dto.setColaboradorId(funcionario.getUuid());
    dto.setNomeColaborador(funcionario.getNome());
    dto.setItensFalta(itensFalta);
    dto.setPedidos(pedidos);
    dto.setAno(query.getAno());
    dto.setMes(query.getMes());

    return dto;
  }

  @Transactional(readOnly = true)
  public JustificarFaltaDTO getFaltaJustificada(GetJustificacaoFaltaByPedidoQuery query) {

    if (query == null || !StringUtils.hasText(query.getPedidoId())) {
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");
    }
    // Converter UUID
    UUID pedidoUuid;
    try {
      pedidoUuid = UUID.fromString(query.getPedidoId());
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest("UUID do pedido inválido");
    }

    // Buscar pedido
    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Pedido não encontrado com UUID: " + pedidoUuid));

    // Buscar todas as faltas associadas ao pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoIdOrderByDataInicioAsc(pedido);

    var dto = montarGrupo(pedido, faltas);

    return dto;
  }

  /**
   * Um pedido de justificação como o formulário o mostra: o cabeçalho do bloco "Justificar
   * Faltas Selecionadas" (motivo, tipo, dedução, valores, parecer, responsável, anexos) mais
   * os dias que o compõem. É a mesma forma devolvida pelo GET .../pedido/{pedidoUuid} e por
   * cada elemento de {@code pedidos} no GET do mês, para o frontend ter um só formato.
   */
  private JustificarFaltaDTO montarGrupo(PedidoEntity pedido, List<FaltaEntity> faltas) {

    var funcionario = pedido.getFunId();

    List<FaltaItemDTO> itensFalta = faltas.stream().map(f -> {
      var item = new FaltaItemDTO();
      // Nem toda a falta nasce de uma síntese diária: as que a baixa médica gera a
      // partir de CALCULO_FALTA_LICENCA têm só datas, sem síntese associada. Sem esta
      // guarda a leitura do pedido rebentava com NPE.
      var sintese = f.getSinteseDiarioId();
      item.setId(sintese != null ? sintese.getId() : null);
      if (sintese != null && sintese.getData() != null)
        item.setData(sintese.getData().toString());
      else if (f.getDataInicio() != null)
        item.setData(f.getDataInicio().toLocalDate().toString());
      item.setTipoFalta(f.getParamSitId() != null ? f.getParamSitId().getNome() : null);
      item.setValorAusencia(f.getValor());
      item.setHorasAusencia(TimeUtils.intervalFormatToHHmm(f.getHorasAusencia()));
      item.setMotivo(f.getDescricaoMotivo());
      item.setComJustificativo(f.getFlgJustificativo());
      item.setEstado(f.getEstado() != null ? f.getEstado().getCode() : null);
      item.setEstadoDesc(descreverEstadoFalta(f.getEstado()));
      // Anexos não são por dia: vêm em dto.documentos, do pedido (ver anexosDoPedido).
      return item;
    }).toList();

    var dto = new JustificarFaltaDTO();
    dto.setColaboradorId(funcionario.getUuid());
    dto.setNomeColaborador(funcionario.getNome());
    dto.setItensFalta(itensFalta);
    dto.setPedidoId(pedido.getUuid());
    dto.setDocumentos(anexosDoPedido(pedido));
    // Estado do pedido, não das faltas: o ecrã precisa dele para saber se o grupo está à
    // espera de despacho (P) ou já fechado (A/I).
    dto.setEstado(pedido.getEstado());
    dto.setEstadoDesc(pedido.getEstado() != null
        ? Estado.fromCode(pedido.getEstado()).map(Estado::getDescription).orElse(null)
        : null);
    dto.setEtapa(pedido.getEtapa());

    // Cabeçalho do formulário: o pedido é gravado com os mesmos valores em todas as suas
    // faltas (ver JustificarFaltaWriteService), por isso lê-se da primeira — excepto o
    // valor total, que é a soma dos dias, tal como o POST o devolve.
    if (!faltas.isEmpty()) {
      var primeira = faltas.getFirst();
      dto.setParecerResponsavel(primeira.getDecisaoResponsavel());
      dto.setResponsavelId(primeira.getResponsavelId() != null ? primeira.getResponsavelId().getId() : null);
      dto.setObsResponsavel(primeira.getObsResponsavel());
      dto.setTipoJustificacao(primeira.getParamSitId() != null ? primeira.getParamSitId().getId() : null);
      dto.setMotivo(primeira.getDescricaoMotivo());
      dto.setComJustificativo(primeira.getFlgJustificativo());
      dto.setDeduzirFaltaEm(primeira.getFlgDescontoFalta());
      dto.setValorDiario(primeira.getValor());
      var valorTotal = faltas.stream()
          .map(FaltaEntity::getValor)
          .filter(Objects::nonNull)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      dto.setValorTotal(valorTotal);

      // valorTotal e o BRUTO da ausencia. Com deducao em ferias ou dispensa o saldo cobre parte
      // e so o resto vai ao vencimento: um pedido de 25 378,24 podia ter descontado 22 205,96 e
      // o ecra mostrava sempre o bruto. Ambos ficam a zero enquanto nao houver despacho.
      dto.setValorDescontado(FaltaDescontoService.valorDescontado(faltas));
      dto.setValorCoberto(FaltaDescontoService.valorCoberto(faltas));

      // Mês de referência: é por ele que o ecrã volta à lista depois de editar. Vem da
      // falta mais antiga do pedido (síntese diária, ou a data da falta quando não há
      // síntese — caso das faltas geradas pela baixa médica).
      faltas.stream()
          .map(JustificarFaltaReadService::dataDaFalta)
          .filter(Objects::nonNull)
          .min(LocalDate::compareTo)
          .ifPresent(d -> {
            dto.setAno(d.getYear());
            dto.setMes(d.getMonthValue());
          });
    }

    return dto;
  }

}