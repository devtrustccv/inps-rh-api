package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.*;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.NotificacaoDispatchService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MissaoServicoServiceWrite {

  private static final Logger LOGGER = LoggerFactory.getLogger(MissaoServicoServiceWrite.class);

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_INATIVO = "I";
  private static final Integer DESTINO_NACIONAL = 1;
  private static final Integer DESTINO_ESTRANGEIRO = 2;
  private static final String ETAPA_1 = "SUBMISSAO";
  private static final String ETAPA_2 = "ANALISE";
  private static final String ETAPA_3 = "EMISSAO_REQUISICAO";
  private static final String ETAPA_4 = "LOGISTICA";
  private static final String ETAPA_5 = "CABIMENTO";
  private static final String ETAPA_7 = "PAGAMENTO";

  private static final String ACTION_NEXT = "NEXT";
  private static final String ESTADO_CABIMENTO_CABIMENTADO = "CABIMENTADO";
  private static final String ESTADO_CABIMENTO_AUTORIZADO = "AUTORIZADO";

  /** Ordem das etapas do processo — usada para nunca retroceder a etapa (ver avancarEtapa). */
  private static final List<String> ORDEM_ETAPAS = List.of(
      ETAPA_1, ETAPA_2, ETAPA_3, ETAPA_4, ETAPA_5, ETAPA_7);

  private static final int MAX_PRESTADORES = 3;

  private static final String TIPO_NOTIF_PEDIDO_PROPOSTA    = "MISSAO_PEDIDO_PROPOSTA";
  private static final String TIPO_NOTIF_EMISSAO_REQUISICAO = "MISSAO_EMISSAO_REQUISICAO";
  private static final String TIPO_NOTIF_LOGISTICA_COLAB    = "MISSAO_LOGISTICA_COLABORADOR";
  private static final String TIPO_NOTIF_CANCELAMENTO       = "MISSAO_CANCELAMENTO";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;
  private final GeografiaEntityRepository geografiaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final DocumentoMapper documentoMapper;
  private final EmailService emailService;
  private final NotificacaoDispatchService notificacaoDispatchService;

  @Transactional
  public ResponseEntity<Map<String, ?>> submeter(SubmeterMissaoServicoCommand command) {
    var dto = command != null ? command.getMissaosubmissaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarSubmissao(dto);

    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisDestinoId());

    var missao = new MissaoServicoEntity();
    missao.setUuid(UuidCreator.getTimeOrderedEpoch());
    missao.setNrMissao(nextNrMissao());
    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(isCaboVerde(pais) ? DESTINO_NACIONAL : DESTINO_ESTRANGEIRO);
    missao.setDescricaoDestino(dto.getDescricaoDestino());
    missao.setDataInicio(dto.getDataInicio());
    missao.setDataFim(dto.getDataFim());
    missao.setNrDias(calcularNrDias(dto.getDataInicio(), dto.getDataFim()));
    missao.setAutorizadoPor(dto.getAutorizadoPor());
    missao.setDataAutorizacao(dto.getDataAutorizacao());
    missao.setEtapa(ETAPA_1);
    missao.setEstado(StringUtils.hasText(dto.getEstado()) ? dto.getEstado() : ESTADO_ATIVO);

    missao = missaoServicoRepository.save(missao);

    var colaboradores = persistirColaboradores(dto.getColaboradores(), missao);
    if (!colaboradores.isEmpty()) {
      missaoColaboradorRepository.saveAll(colaboradores);
    }

    persistirDocumentos(dto, missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    resp.put("nrMissao", missao.getNrMissao());
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarAnalise(SaveAnaliseProcessoMissaoServicoCommand command) {
    var uuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaoanaliserequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var missao = missaoServicoRepository.findByUuidOrThrow(uuid);
    var avancar = isNext(dto.getProcessoEtapaAction());
    exigirEtapaMinima(missao, ETAPA_2, avancar);

    validarAnalise(dto);

    var prestadoresPersistidos = syncPrestadores(missao, dto.getPrestadores());
    var prestadoresSalvos = new ArrayList<MissaoPrestadorEntity>();
    if (!prestadoresPersistidos.isEmpty()) {
      prestadoresSalvos = new ArrayList<>(missaoPrestadorRepository.saveAll(prestadoresPersistidos));
    }

    if (avancar) {
      avancarEtapa(missao, ETAPA_3);
      enviarNotificacoesPedidoSimulacao(missao, prestadoresSalvos, dto.getNotificacao());
    }
    missaoServicoRepository.save(missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarSubmissao(SaveSubmissaoServicoCommand command) {
    var uuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaosubmissaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarSubmissao(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(uuid);
    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisDestinoId());

    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(isCaboVerde(pais) ? DESTINO_NACIONAL : DESTINO_ESTRANGEIRO);
    missao.setDescricaoDestino(dto.getDescricaoDestino());
    missao.setDataInicio(dto.getDataInicio());
    missao.setDataFim(dto.getDataFim());
    missao.setNrDias(calcularNrDias(dto.getDataInicio(), dto.getDataFim()));
    missao.setAutorizadoPor(dto.getAutorizadoPor());
    missao.setDataAutorizacao(dto.getDataAutorizacao());
    if (StringUtils.hasText(dto.getEstado())) {
      missao.setEstado(dto.getEstado());
    }
    if (isNext(dto.getProcessoEtapaAction())) {
      avancarEtapa(missao, ETAPA_2);
    }
    missao = missaoServicoRepository.save(missao);

    var colaboradores = syncColaboradores(missao, dto.getColaboradores());
    if (!colaboradores.isEmpty()) {
      missaoColaboradorRepository.saveAll(colaboradores);
    }

    persistirDocumentos(dto, missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    resp.put("nrMissao", missao.getNrMissao());
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarEmissaoRequisicao(SaveSubmissaoServicoEmissaoRequisicaoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getUui() : null, "uui");
    var dto = command != null ? command.getMissaoemissaorequisicaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);
    var avancar = isNext(dto.getProcessoEtapaAction());
    exigirEtapaMinima(missao, ETAPA_3, avancar);

    validarEmissaoRequisicao(dto.getRequisicoes());

    var existentes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid);

    var desired = new HashSet<String>();
    var propostaByPrestador = new HashMap<Long, cv.inps.rh.shared.application.dto.AnexoReqDTO>();
    var selectedPrestIds = new LinkedHashSet<Long>();

    for (var item : dto.getRequisicoes()) {
      if (item == null)
        continue;
      if (item.getSelecionado() == null || !item.getSelecionado())
        continue;
      if (item.getMissaoPrestId() == null)
        continue;

      selectedPrestIds.add(item.getMissaoPrestId());
      propostaByPrestador.putIfAbsent(item.getMissaoPrestId(), item.getDocumentoProposta());

      var prest = missaoPrestadorRepository.findById(item.getMissaoPrestId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Prestador inválido: " + item.getMissaoPrestId()));
      if (prest.getMissaoServId() == null || prest.getMissaoServId().getUuid() == null
          || !prest.getMissaoServId().getUuid().equals(missaoUuid)) {
        throw IgrpResponseStatusException.badRequest("Prestador não pertence à missão: " + item.getMissaoPrestId());
      }

      for (var funUuid : item.getMissaoColabIds()) {
        if (funUuid == null)
          continue;
        var colab = missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missaoUuid, funUuid)
            .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador inválido: " + funUuid));
        desired.add(key(item.getMissaoPrestId(), colab.getId()));
      }
    }

    var toSave = new ArrayList<MissaoRequisicaoEntity>();
    var byKey = new HashMap<String, MissaoRequisicaoEntity>();
    if (!CollectionUtils.isEmpty(existentes)) {
      for (var e : existentes) {
        if (e == null || e.getMissaoPrestId() == null || e.getMissaoColabId() == null)
          continue;
        byKey.put(key(e.getMissaoPrestId().getId(), e.getMissaoColabId().getId()), e);
      }
    }

    for (var entry : byKey.entrySet()) {
      var req = entry.getValue();
      if (desired.contains(entry.getKey())) {
        req.setEstado(ESTADO_ATIVO);
      } else {
        req.setEstado(ESTADO_INATIVO);
      }
      toSave.add(req);
    }

    for (var k : desired) {
      if (byKey.containsKey(k))
        continue;
      var parts = k.split(":");
      var prestId = Long.parseLong(parts[0]);
      var colabId = Long.parseLong(parts[1]);

      var prest = missaoPrestadorRepository.findById(prestId)
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Prestador inválido: " + prestId));
      var colab = missaoColaboradorRepository.findById(colabId)
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador inválido: " + colabId));

      var req = new MissaoRequisicaoEntity();
      req.setUuid(UuidCreator.getTimeOrderedEpoch());
      req.setEstado(ESTADO_ATIVO);
      req.setMissaoPrestId(prest);
      req.setMissaoColabId(colab);
      toSave.add(req);
    }

    if (!toSave.isEmpty()) {
      toSave = new ArrayList<>(missaoRequisicaoRepository.saveAll(toSave));
    }

    for (var req : toSave) {
      if (req == null || req.getUuid() == null || req.getMissaoPrestId() == null)
        continue;
      if (!ESTADO_ATIVO.equals(req.getEstado()))
        continue;

      var proposta = propostaByPrestador.get(req.getMissaoPrestId().getId());
      List<cv.inps.rh.shared.application.dto.AnexoReqDTO> novos = proposta != null ? List.of(proposta) : List.of();

      var existentesDocs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
          TableName.RH_T_MISSAO_REQUISICAO.name(),
          req.getUuid());

      var sync = documentoMapper.syncDocumentos(
          existentesDocs != null ? existentesDocs : new ArrayList<>(),
          novos,
          TableName.RH_T_MISSAO_REQUISICAO.name(),
          req.getId(),
          req.getUuid(),
          1L,
          null);

      if (sync != null && !sync.isEmpty()) {
        sync.forEach(d -> {
          if (d.getUuid() == null)
            d.setUuid(UuidCreator.getTimeOrderedEpoch());
          if (d.getEstado() == null)
            d.setEstado(Estado.A);
        });
        documentoRepository.saveAll(sync);
      }
    }

    if (avancar) {
      avancarEtapa(missao, ETAPA_4);
      enviarNotificacoesEmissaoRequisicao(missao, selectedPrestIds);
    }
    missaoServicoRepository.save(missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarLogistica(SaveMissaoServicoLogisticaCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaologisticarequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);
    var avancar = isNext(dto.getProcessoEtapaAction());
    exigirEtapaMinima(missao, ETAPA_4, avancar);

    var requisicoes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid);
    var logisticasExistentes = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid);

    if (dto.getBilhetesPassagem() != null) {
      syncLogisticaBilhete(missao, dto.getBilhetesPassagem(), logisticasExistentes, requisicoes);
    }
    if (dto.getSegurosViagem() != null) {
      syncLogisticaSeguro(missao, dto.getSegurosViagem(), logisticasExistentes, requisicoes);
    }
    if (dto.getAlojamentos() != null) {
      syncLogisticaAlojamento(missao, dto.getAlojamentos(), logisticasExistentes, requisicoes);
    }
    if (dto.getAjudasCusto() != null) {
      var alimentacaoByColabId = new HashMap<UUID, String>();
      if (dto.getAlojamentos() != null) {
        for (var a : dto.getAlojamentos()) {
          if (a == null || a.getColaboradorId() == null)
            continue;
          if (StringUtils.hasText(a.getFlgAlimentacao())) {
            alimentacaoByColabId.putIfAbsent(a.getColaboradorId(), a.getFlgAlimentacao());
          }
        }
      }
      syncLogisticaAjudaCusto(missao, dto.getAjudasCusto(), alimentacaoByColabId, logisticasExistentes, requisicoes);
    }
    if (avancar) {
      avancarEtapa(missao, ETAPA_5);
    }
    missaoServicoRepository.save(missao);
    if (ETAPA_5.equals(missao.getEtapa())) {
      enviarNotificacoesLogisticaColaborador(missao, dto.getNotificacao());
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarCabimento(SaveMissaoServicoCabimentoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaocabimentorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarCabimento(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    // "Gravar" apenas persiste os anexos/seleção; "Cabimentar" (NEXT) é que gera o
    // cabimento, marca ESTADO_CABIMENTO e avança a etapa.
    var cabimentar = isNext(dto.getProcessoEtapaAction());
    exigirEtapaMinima(missao, ETAPA_5, cabimentar);

    var toSave = new ArrayList<MissaoLogisticaEntity>();

    for (var item : dto.getItens()) {
      if (item == null)
        continue;
      if (item.getSelecionado() == null || !item.getSelecionado())
        continue;
      if (item.getLogisticaId() == null)
        continue;

      var log = missaoLogisticaRepository.findById(item.getLogisticaId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("logisticaId inválido: " + item.getLogisticaId()));

      if (log.getMissaoServId() == null || log.getMissaoServId().getUuid() == null
          || !log.getMissaoServId().getUuid().equals(missaoUuid)) {
        throw IgrpResponseStatusException.badRequest("logisticaId não pertence à missão: " + item.getLogisticaId());
      }

      // Cabimento manual/internacional: o financeiro pode enviar o nr de cabimento.
      if (item.getCabId() != null) {
        log.setCabId(item.getCabId());
      }

      if (cabimentar) {
        if (log.getCabId() == null) {
          var cabId = gerarCabimentoSgal(log);
          if (cabId != null) {
            log.setCabId(cabId);
          }
        }
        log.setEstadoCabimento(ESTADO_CABIMENTO_CABIMENTADO);
      }

      if (!ESTADO_ATIVO.equals(log.getEstado())) {
        log.setEstado(ESTADO_ATIVO);
      }
      toSave.add(log);

      if (item.getAnexo() != null && log.getUuid() != null) {
        var existentesDocs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_MISSAO_LOGISTICA.name(),
            log.getUuid());

        var sync = documentoMapper.syncDocumentos(
            existentesDocs != null ? existentesDocs : new ArrayList<>(),
            List.of(item.getAnexo()),
            TableName.RH_T_MISSAO_LOGISTICA.name(),
            log.getId(),
            log.getUuid(),
            1L,
            null);

        if (sync != null && !sync.isEmpty()) {
          sync.forEach(d -> {
            if (d.getUuid() == null)
              d.setUuid(UuidCreator.getTimeOrderedEpoch());
            if (d.getEstado() == null)
              d.setEstado(Estado.A);
          });
          documentoRepository.saveAll(sync);
        }
      }
    }

    if (!toSave.isEmpty()) {
      missaoLogisticaRepository.saveAll(toSave);
    }

    if (cabimentar) {
      avancarEtapa(missao, ETAPA_5);
      missaoServicoRepository.save(missao);
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  /**
   * Gera o cabimento no SGAL para uma linha de logística e devolve o nr de cabimento (CAB_ID).
   *
   * <p>TODO: integrar com o serviço financeiro do SGAL. A integração está bloqueada por falta de
   * contrato — a Especificação Técnica Funcional da Missão de Serviço apenas menciona que "é gerado
   * um cabimento para cada tipo de serviço" e que o SGAL pode cabimentar "diretamente na plataforma
   * ou por exportação para o SIPS FUN", sem indicar endpoint, payload nem onde vem o número
   * devolvido. Antes de implementar, obter do financeiro/SGAL:
   * <ul>
   *   <li>endpoint de cabimento aplicável a uma linha de RH_T_MISSAO_LOGISTICA;</li>
   *   <li>contrato do payload — 1 cabimento por tipo de serviço e 1 individual por colaborador na
   *       ajuda de custo;</li>
   *   <li>em que campo da resposta vem o CAB_ID;</li>
   *   <li>confirmar a direção: somos nós a chamar o SGAL ou é o SGAL a escrever o CAB_ID aqui.</li>
   * </ul>
   *
   * <p>O único precedente no projeto é {@code ProcessarSalarioApi#processarCabimento}
   * ({@code /processa_cabimento}), mas recebe {@code p_proc_sal_id} — não serve para logística de
   * missão — devolve um {@code OperationOutcomeResponse} sem nr de cabimento, e a chamada está
   * comentada em {@code ProcessamentoSalarialWriteService#cabimentar}.
   *
   * <p>Enquanto a integração não existir devolve null: a linha fica CABIMENTADO sem cabId, e por
   * isso {@code salvarAutorizacao} valida ESTADO_CABIMENTO em vez de exigir cabId. Cabimentos
   * manuais/internacionais continuam a poder enviar o cabId no payload.
   */
  private Long gerarCabimentoSgal(MissaoLogisticaEntity log) {
    LOGGER.warn("Integração SGAL pendente: cabimento não gerado para logistica id={}", log.getId());
    return null;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarAutorizacao(SaveMissaoServicoAutorizacaoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaoautorizacaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarAutorizacao(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    // "Gravar" apenas valida/persiste a seleção; só "Autorizar" (NEXT) põe os cabimentos
    // gerados em estado AUTORIZADO e avança para PAGAMENTO.
    var autorizar = isNext(dto.getProcessoEtapaAction());
    exigirEtapaMinima(missao, ETAPA_5, autorizar);

    var toSave = new ArrayList<MissaoLogisticaEntity>();

    for (var item : dto.getItens()) {
      if (item == null)
        continue;
      if (item.getAutorizado() == null || !item.getAutorizado())
        continue;
      if (item.getLogisticaId() == null)
        continue;

      var log = missaoLogisticaRepository.findById(item.getLogisticaId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("logisticaId inválido: " + item.getLogisticaId()));

      if (log.getMissaoServId() == null || log.getMissaoServId().getUuid() == null
          || !log.getMissaoServId().getUuid().equals(missaoUuid)) {
        throw IgrpResponseStatusException.badRequest("logisticaId não pertence à missão: " + item.getLogisticaId());
      }

      // Só se autoriza o que já foi cabimentado (o cabId pode ainda vir do SGAL).
      // AUTORIZADO é aceite para a gravação ser idempotente.
      if (!ESTADO_CABIMENTO_CABIMENTADO.equals(log.getEstadoCabimento())
          && !ESTADO_CABIMENTO_AUTORIZADO.equals(log.getEstadoCabimento())) {
        throw IgrpResponseStatusException.badRequest("Item sem cabimento: " + item.getLogisticaId());
      }

      if (autorizar) {
        log.setEstadoCabimento(ESTADO_CABIMENTO_AUTORIZADO);
      }
      if (!ESTADO_ATIVO.equals(log.getEstado())) {
        log.setEstado(ESTADO_ATIVO);
      }
      toSave.add(log);
    }

    if (!toSave.isEmpty()) {
      missaoLogisticaRepository.saveAll(toSave);
    }

    if (autorizar) {
      avancarEtapa(missao, ETAPA_7);
      missaoServicoRepository.save(missao);
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarPagamento(SaveMissaoServicoPagamentoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaopagamentorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarPagamento(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);
    // O pagamento é registado pelo sistema financeiro — exige sempre a etapa atingida.
    exigirEtapaMinima(missao, ETAPA_7, true);

    missao.setReferenciaPagamento(dto.getReferenciaPagamento());
    missao.setDataPagamento(dto.getDataPagamento());
    avancarEtapa(missao, ETAPA_7);
    missaoServicoRepository.save(missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<String> cancelar(CancelarMissaoServicoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getId() : null, "id");
    var dto = command != null ? command.getMissaocancelarrequest() : null;

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    missao.setMotivoCancelamento(dto != null ? dto.getMotivoCancelamento() : null);
    missao.setEstado(ESTADO_INATIVO);
    missaoServicoRepository.save(missao);

    var colaboradores = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid);
    if (!CollectionUtils.isEmpty(colaboradores)) {
      colaboradores.forEach(c -> c.setEstado(ESTADO_INATIVO));
      missaoColaboradorRepository.saveAll(colaboradores);
    }

    var prestadores = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid);
    if (!CollectionUtils.isEmpty(prestadores)) {
      prestadores.forEach(p -> p.setEstado(ESTADO_INATIVO));
      missaoPrestadorRepository.saveAll(prestadores);
    }

    var logistica = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid);
    if (!CollectionUtils.isEmpty(logistica)) {
      logistica.forEach(l -> l.setEstado(ESTADO_INATIVO));
      missaoLogisticaRepository.saveAll(logistica);
    }

    var logisticaDet = missaoLogisticaDetRepository.findAllByMissaoLogistId_MissaoServId_Uuid(missaoUuid);
    if (!CollectionUtils.isEmpty(logisticaDet)) {
      logisticaDet.forEach(d -> d.setEstado(ESTADO_INATIVO));
      missaoLogisticaDetRepository.saveAll(logisticaDet);
    }

    var requisicoes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid);
    if (!CollectionUtils.isEmpty(requisicoes)) {
      requisicoes.forEach(r -> r.setEstado(ESTADO_INATIVO));
      missaoRequisicaoRepository.saveAll(requisicoes);
    }

    var documentos = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_SERVICO.name(),
        missaoUuid);
    if (!CollectionUtils.isEmpty(documentos)) {
      documentos.forEach(d -> d.setEstado(Estado.I));
      documentoRepository.saveAll(documentos);
    }

    if (deveNotificarCancelamento(missao)) {
      persistirNotificacaoCancelamento(missao, dto);
    }

    return ResponseEntity.ok().build();
  }

  private void validarEmissaoRequisicao(List<MissaoRequisicaoItemRequestDTO> requisicoes) {
    if (CollectionUtils.isEmpty(requisicoes)) {
      throw IgrpResponseStatusException.badRequest("requisicoes é obrigatório");
    }

    boolean anySelected = false;
    for (var item : requisicoes) {
      if (item == null)
        continue;
      if (item.getSelecionado() == null || !item.getSelecionado())
        continue;
      anySelected = true;

      if (item.getMissaoPrestId() == null) {
        throw IgrpResponseStatusException.badRequest("missaoPrestId é obrigatório");
      }
      if (CollectionUtils.isEmpty(item.getMissaoColabIds())) {
        throw IgrpResponseStatusException.badRequest("missaoColabIds é obrigatório");
      }
    }

    if (!anySelected) {
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um prestador");
    }
  }

  private String key(Long prestId, Long colabId) {
    return prestId + ":" + colabId;
  }

  private void validarCabimento(MissaoCabimentoRequestDTO dto) {
    if (dto == null || CollectionUtils.isEmpty(dto.getItens())) {
      throw IgrpResponseStatusException.badRequest("itens é obrigatório");
    }

    boolean anySelected = false;
    for (var item : dto.getItens()) {
      if (item == null)
        continue;
      if (item.getSelecionado() == null || !item.getSelecionado())
        continue;
      anySelected = true;

      if (item.getLogisticaId() == null) {
        throw IgrpResponseStatusException.badRequest("logisticaId é obrigatório");
      }
      // cabId não é preenchido pelo utilizador: é gerado ao cabimentar (SGAL).
      // Só vem no payload no caso dos cabimentos manuais/internacionais.
    }

    if (!anySelected) {
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um item");
    }
  }

  private void validarAutorizacao(MissaoAutorizacaoRequestDTO dto) {
    if (dto == null || CollectionUtils.isEmpty(dto.getItens())) {
      throw IgrpResponseStatusException.badRequest("itens é obrigatório");
    }

    boolean anySelected = false;
    for (var item : dto.getItens()) {
      if (item == null)
        continue;
      if (item.getAutorizado() == null || !item.getAutorizado())
        continue;
      anySelected = true;
      if (item.getLogisticaId() == null) {
        throw IgrpResponseStatusException.badRequest("logisticaId é obrigatório");
      }
    }

    if (!anySelected) {
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um item");
    }
  }

  private void validarPagamento(MissaoPagamentoRequestDTO dto) {
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }
    if (!StringUtils.hasText(dto.getReferenciaPagamento())) {
      throw IgrpResponseStatusException.badRequest("referenciaPagamento é obrigatório");
    }
    if (dto.getDataPagamento() == null) {
      throw IgrpResponseStatusException.badRequest("dataPagamento é obrigatório");
    }
  }

  private void syncLogisticaBilhete(
      MissaoServicoEntity missao,
      List<BilhetePassagemRequestDTO> items,
      List<MissaoLogisticaEntity> existentes,
      List<MissaoRequisicaoEntity> requisicoes) {
    syncLogisticaGenerico(missao, "BILHETE_PASSAGEM", existentes, items, (bilhete) -> {
      if (bilhete == null)
        return null;
      if (CollectionUtils.isEmpty(bilhete.getColaboradorIds())) {
        throw IgrpResponseStatusException.badRequest("colaboradorIds é obrigatório");
      }
      if (bilhete.getValor() == null) {
        throw IgrpResponseStatusException.badRequest("valor é obrigatório");
      }

      var colabs = new ArrayList<MissaoColaboradorEntity>();
      for (var colabUuid : bilhete.getColaboradorIds()) {
        if (colabUuid == null)
          continue;
        colabs.add(missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missao.getUuid(), colabUuid)
            .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador inválido: " + colabUuid)));
      }
      if (colabs.isEmpty()) {
        throw IgrpResponseStatusException.badRequest("colaboradorIds inválido");
      }

      var prestador = derivePrestadorFromRequisicao(missao.getUuid(), colabs, requisicoes);

      var log = new MissaoLogisticaEntity();
      log.setUuid(UuidCreator.getTimeOrderedEpoch());
      log.setEstado(ESTADO_ATIVO);
      log.setMissaoServId(missao);
      log.setPrestadorServId(prestador);
      log.setReferencia("BILHETE_PASSAGEM");
      log.setMoeda("CVE");
      log.setValorTotal(bilhete.getValor());
      log.setDataInicio(missao.getDataInicio());
      log.setDataFim(missao.getDataFim());
      log.setNrDias(missao.getNrDias());

      return new LogisticaPersist(log, colabs, bilhete.getAnexo());
    });
  }

  private void syncLogisticaSeguro(
      MissaoServicoEntity missao,
      List<SeguroViagemRequestDTO> items,
      List<MissaoLogisticaEntity> existentes,
      List<MissaoRequisicaoEntity> requisicoes) {
    syncLogisticaGenerico(missao, "SEGURO_VIAGEM", existentes, items, (seguro) -> {
      if (seguro == null)
        return null;
      if (CollectionUtils.isEmpty(seguro.getColaboradorIds())) {
        throw IgrpResponseStatusException.badRequest("colaboradorIds é obrigatório");
      }
      if (seguro.getValor() == null) {
        throw IgrpResponseStatusException.badRequest("valor é obrigatório");
      }
      if (seguro.getEntId() == null) {
        throw IgrpResponseStatusException.badRequest("entId é obrigatório");
      }
      if (!StringUtils.hasText(seguro.getNomeSeguradora())) {
        throw IgrpResponseStatusException.badRequest("nomeSeguradora é obrigatório");
      }

      var colabs = new ArrayList<MissaoColaboradorEntity>();
      for (var colabUuid : seguro.getColaboradorIds()) {
        if (colabUuid == null)
          continue;
        colabs.add(missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missao.getUuid(), colabUuid)
            .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador inválido: " + colabUuid)));
      }
      if (colabs.isEmpty()) {
        throw IgrpResponseStatusException.badRequest("colaboradorIds inválido");
      }

      var prestador = derivePrestadorFromRequisicao(missao.getUuid(), colabs, requisicoes);

      var log = new MissaoLogisticaEntity();
      log.setUuid(UuidCreator.getTimeOrderedEpoch());
      log.setEstado(ESTADO_ATIVO);
      log.setMissaoServId(missao);
      log.setPrestadorServId(prestador);
      log.setReferencia("SEGURO_VIAGEM");
      log.setMoeda("CVE");
      log.setValorTotal(seguro.getValor());
      log.setDataInicio(missao.getDataInicio());
      log.setDataFim(missao.getDataFim());
      log.setNrDias(missao.getNrDias());
      log.setEntId(seguro.getEntId());
      log.setNomeSeguradora(seguro.getNomeSeguradora());

      return new LogisticaPersist(log, colabs, seguro.getAnexo());
    });
  }

  private void syncLogisticaAlojamento(
      MissaoServicoEntity missao,
      List<AlojamentoRequestDTO> items,
      List<MissaoLogisticaEntity> existentes,
      List<MissaoRequisicaoEntity> requisicoes) {
    syncLogisticaGenerico(missao, "ALOJAMENTO", existentes, items, (aloj) -> {
      if (aloj == null)
        return null;
      if (aloj.getColaboradorId() == null) {
        throw IgrpResponseStatusException.badRequest("colaboradorId é obrigatório");
      }
      if (!StringUtils.hasText(aloj.getLugarHospedagem())) {
        throw IgrpResponseStatusException.badRequest("lugarHospedagem é obrigatório");
      }
      if (aloj.getValorTotal() == null) {
        throw IgrpResponseStatusException.badRequest("valorTotal é obrigatório");
      }
      if (aloj.getValorDiario() == null) {
        throw IgrpResponseStatusException.badRequest("valorDiario é obrigatório");
      }
      if (aloj.getDataInicio() == null) {
        throw IgrpResponseStatusException.badRequest("dataInicio é obrigatório");
      }
      if (aloj.getDataFim() == null) {
        throw IgrpResponseStatusException.badRequest("dataFim é obrigatório");
      }
      if (aloj.getDataFim().isBefore(aloj.getDataInicio())) {
        throw IgrpResponseStatusException.badRequest("dataFim não pode ser anterior a dataInicio");
      }
      if (!StringUtils.hasText(aloj.getFlgAlimentacao())) {
        throw IgrpResponseStatusException.badRequest("flgAlimentacao é obrigatório");
      }

      var colab = missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missao.getUuid(), aloj.getColaboradorId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador inválido: " + aloj.getColaboradorId()));

      var prestador = derivePrestadorFromRequisicao(missao.getUuid(), List.of(colab), requisicoes);

      var log = new MissaoLogisticaEntity();
      log.setUuid(UuidCreator.getTimeOrderedEpoch());
      log.setEstado(ESTADO_ATIVO);
      log.setMissaoServId(missao);
      log.setPrestadorServId(prestador);
      log.setReferencia("ALOJAMENTO");
      log.setMoeda(StringUtils.hasText(aloj.getMoeda()) ? aloj.getMoeda() : "CVE");
      log.setValorDiario(aloj.getValorDiario());
      log.setValorTotal(aloj.getValorTotal());
      log.setLugarHospedagem(aloj.getLugarHospedagem());
      log.setDataInicio(aloj.getDataInicio());
      log.setDataFim(aloj.getDataFim());
      log.setNrDias(calcularNrDias(aloj.getDataInicio(), aloj.getDataFim()));
      log.setFlgAlimentacao(aloj.getFlgAlimentacao());

      return new LogisticaPersist(log, List.of(colab), aloj.getAnexo());
    });
  }

  private void syncLogisticaAjudaCusto(
      MissaoServicoEntity missao,
      List<AjudaCustoRequestDTO> items,
      Map<UUID, String> alimentacaoByColabId,
      List<MissaoLogisticaEntity> existentes,
      List<MissaoRequisicaoEntity> requisicoes) {
    syncLogisticaGenerico(missao, "AJUDA_CUSTO", existentes, items, (ajuda) -> {
      if (ajuda == null)
        return null;
      if (ajuda.getColaboradorId() == null) {
        throw IgrpResponseStatusException.badRequest("colaboradorId é obrigatório");
      }
      if (ajuda.getFlgAlojamento() == null) {
        throw IgrpResponseStatusException.badRequest("flgAlojamento é obrigatório");
      }
      if (ajuda.getNumeroDiasAlojamento() == null) {
        throw IgrpResponseStatusException.badRequest("numeroDiasAlojamento é obrigatório");
      }
      if (ajuda.getValorDiario() == null) {
        throw IgrpResponseStatusException.badRequest("valorDiario é obrigatório");
      }

      var colab = missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missao.getUuid(), ajuda.getColaboradorId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador inválido: " + ajuda.getColaboradorId()));
      var prestador = derivePrestadorFromRequisicao(missao.getUuid(), List.of(colab), requisicoes);

      var baseValorDiario = ajuda.getValorDiario();
      // alimentacaoByColabId é indexado por UUID do funcionário (ajuda.getColaboradorId()),
      // por isso o lookup usa colab.getFunId().getUuid() e não colab.getUuid() (missaoColab UUID)
      UUID funUuidParaLookup = colab.getFunId() != null ? colab.getFunId().getUuid() : null;
      var valorDiarioCalculado = calcularValorDiarioAjudaCusto(baseValorDiario, ajuda.getFlgAlojamento(),
          alimentacaoByColabId != null ? alimentacaoByColabId.get(funUuidParaLookup) : null);
      var valorTotal = valorDiarioCalculado.multiply(java.math.BigDecimal.valueOf(ajuda.getNumeroDiasAlojamento()));

      var log = new MissaoLogisticaEntity();
      log.setUuid(UuidCreator.getTimeOrderedEpoch());
      log.setEstado(ESTADO_ATIVO);
      log.setMissaoServId(missao);
      log.setPrestadorServId(prestador);
      log.setReferencia("AJUDA_CUSTO");
      log.setMoeda("CVE");
      log.setNrDias(ajuda.getNumeroDiasAlojamento());
      log.setFlgAlojamento(ajuda.getFlgAlojamento() ? "SIM" : "NAO");
      log.setValorDiario(valorDiarioCalculado);
      log.setValorTotal(valorTotal);
      log.setDataInicio(missao.getDataInicio());
      log.setDataFim(missao.getDataFim());

      return new LogisticaPersist(log, List.of(colab), null);
    });
  }

  private java.math.BigDecimal calcularValorDiarioAjudaCusto(
      java.math.BigDecimal baseValorDiario,
      boolean incluiAlojamento,
      String flgAlimentacao) {
    if (baseValorDiario == null)
      return null;
    if (!incluiAlojamento)
      return baseValorDiario;

    if ("SIM".equalsIgnoreCase(flgAlimentacao)) {
      return baseValorDiario.multiply(java.math.BigDecimal.ONE)
          .divide(java.math.BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
    }

    return baseValorDiario.multiply(java.math.BigDecimal.valueOf(2))
        .divide(java.math.BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
  }

  private <T> void syncLogisticaGenerico(
      MissaoServicoEntity missao,
      String referencia,
      List<MissaoLogisticaEntity> existentes,
      List<T> items,
      java.util.function.Function<T, LogisticaPersist> mapper) {
    var existentesRef = new ArrayList<MissaoLogisticaEntity>();
    if (!CollectionUtils.isEmpty(existentes)) {
      for (var e : existentes) {
        if (e != null && referencia.equals(e.getReferencia()) && ESTADO_ATIVO.equals(e.getEstado())) {
          existentesRef.add(e);
        }
      }
    }

    if (!existentesRef.isEmpty()) {
      existentesRef.forEach(e -> e.setEstado(ESTADO_INATIVO));
      missaoLogisticaRepository.saveAll(existentesRef);

      var ids = existentesRef.stream().map(MissaoLogisticaEntity::getId).filter(java.util.Objects::nonNull).toList();
      if (!ids.isEmpty()) {
        var dets = missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids);
        if (!CollectionUtils.isEmpty(dets)) {
          dets.forEach(d -> d.setEstado(ESTADO_INATIVO));
          missaoLogisticaDetRepository.saveAll(dets);
        }
      }

      for (var e : existentesRef) {
        if (e == null || e.getUuid() == null)
          continue;
        var docs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_MISSAO_LOGISTICA.name(),
            e.getUuid());
        if (!CollectionUtils.isEmpty(docs)) {
          docs.forEach(d -> d.setEstado(Estado.I));
          documentoRepository.saveAll(docs);
        }
      }
    }

    if (CollectionUtils.isEmpty(items)) {
      return;
    }

    var novosLogs = new ArrayList<MissaoLogisticaEntity>();
    var novos = new ArrayList<LogisticaPersist>();
    for (var raw : items) {
      var p = mapper.apply(raw);
      if (p == null)
        continue;
      novos.add(p);
      novosLogs.add(p.logistica);
    }

    if (novosLogs.isEmpty())
      return;

    novosLogs = new ArrayList<>(missaoLogisticaRepository.saveAll(novosLogs));

    var detsToSave = new ArrayList<MissaoLogisticaDetEntity>();

    for (int i = 0; i < novos.size(); i++) {
      var p = novos.get(i);
      var log = novosLogs.get(i);

      for (var colab : p.colaboradores) {
        var det = new MissaoLogisticaDetEntity();
        det.setEstado(ESTADO_ATIVO);
        det.setMissaoLogistId(log);
        det.setMissaoColabId(colab);
        detsToSave.add(det);
      }

      if (p.anexo != null) {
        var existentesDocs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_MISSAO_LOGISTICA.name(),
            log.getUuid());
        var sync = documentoMapper.syncDocumentos(
            existentesDocs != null ? existentesDocs : new ArrayList<>(),
            List.of(p.anexo),
            TableName.RH_T_MISSAO_LOGISTICA.name(),
            log.getId(),
            log.getUuid(),
            1L,
            null);

        if (sync != null && !sync.isEmpty()) {
          sync.forEach(d -> {
            if (d.getUuid() == null)
              d.setUuid(UuidCreator.getTimeOrderedEpoch());
            if (d.getEstado() == null)
              d.setEstado(Estado.A);
          });
          documentoRepository.saveAll(sync);
        }
      }
    }

    if (!detsToSave.isEmpty()) {
      missaoLogisticaDetRepository.saveAll(detsToSave);
    }
  }

  private MissaoPrestadorEntity derivePrestadorFromRequisicao(
      UUID missaoUuid,
      List<MissaoColaboradorEntity> colabs,
      List<MissaoRequisicaoEntity> requisicoes) {
    var prestadores = new HashSet<Long>();
    Long lastPrestId = null;

    for (var colab : colabs) {
      if (colab == null || colab.getId() == null)
        continue;

      Long prestId = null;
      if (!CollectionUtils.isEmpty(requisicoes)) {
        for (var req : requisicoes) {
          if (req == null)
            continue;
          if (!ESTADO_ATIVO.equals(req.getEstado()))
            continue;
          if (req.getMissaoPrestId() == null || req.getMissaoPrestId().getId() == null)
            continue;
          if (req.getMissaoColabId() == null || req.getMissaoColabId().getId() == null)
            continue;
          if (!colab.getId().equals(req.getMissaoColabId().getId()))
            continue;
          prestId = req.getMissaoPrestId().getId();
          break;
        }
      }

      if (prestId == null) {
        throw IgrpResponseStatusException.badRequest("Requisição não encontrada para colaborador: " + colab.getUuid());
      }
      prestadores.add(prestId);
      lastPrestId = prestId;
    }

    if (prestadores.size() != 1 || lastPrestId == null) {
      throw IgrpResponseStatusException.badRequest("Prestador inconsistente para os colaboradores selecionados");
    }

    var prestOpt = missaoPrestadorRepository.findById(lastPrestId);
    if (prestOpt.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Prestador inválido: " + lastPrestId);
    }
    var prest = prestOpt.get();
    if (prest.getMissaoServId() == null || prest.getMissaoServId().getUuid() == null
        || !prest.getMissaoServId().getUuid().equals(missaoUuid)) {
      throw IgrpResponseStatusException.badRequest("Prestador não pertence à missão: " + lastPrestId);
    }
    return prest;
  }

  private static final class LogisticaPersist {
    private final MissaoLogisticaEntity logistica;
    private final List<MissaoColaboradorEntity> colaboradores;
    private final cv.inps.rh.shared.application.dto.AnexoReqDTO anexo;

    private LogisticaPersist(
        MissaoLogisticaEntity logistica,
        List<MissaoColaboradorEntity> colaboradores,
        cv.inps.rh.shared.application.dto.AnexoReqDTO anexo) {
      this.logistica = logistica;
      this.colaboradores = colaboradores;
      this.anexo = anexo;
    }
  }

  private void validarSubmissao(MissaoSubmissaoRequestDTO dto) {
    if (dto.getPaisDestinoId() == null) {
      throw IgrpResponseStatusException.badRequest("paisDestinoId é obrigatório");
    }
    if (!StringUtils.hasText(dto.getDescricaoDestino())) {
      throw IgrpResponseStatusException.badRequest("descricaoDestino é obrigatório");
    }
    if (dto.getDataInicio() == null) {
      throw IgrpResponseStatusException.badRequest("dataInicio é obrigatório");
    }
    if (dto.getDataFim() == null) {
      throw IgrpResponseStatusException.badRequest("dataFim é obrigatório");
    }
    if (dto.getDataFim().isBefore(dto.getDataInicio())) {
      throw IgrpResponseStatusException.badRequest("dataFim não pode ser anterior a dataInicio");
    }
    if (!StringUtils.hasText(dto.getAutorizadoPor())) {
      throw IgrpResponseStatusException.badRequest("autorizadoPor é obrigatório");
    }
    if (dto.getDataAutorizacao() == null) {
      throw IgrpResponseStatusException.badRequest("dataAutorizacao é obrigatório");
    }
    if (CollectionUtils.isEmpty(dto.getColaboradores())) {
      throw IgrpResponseStatusException.badRequest("colaboradores é obrigatório");
    }
  }

  private void validarAnalise(MissaoAnaliseRequestDTO dto) {
    if (CollectionUtils.isEmpty(dto.getPrestadores())) {
      throw IgrpResponseStatusException.badRequest("prestadores é obrigatório");
    }
    if (dto.getPrestadores().size() > MAX_PRESTADORES) {
      throw IgrpResponseStatusException.badRequest(
          "Máximo de " + MAX_PRESTADORES + " prestadores permitidos por missão");
    }
  }

  private ArrayList<MissaoPrestadorEntity> syncPrestadores(MissaoServicoEntity missao, List<MissaoPrestadorDTO> dtos) {
    var existentes = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missao.getUuid());
    var toSave = new ArrayList<MissaoPrestadorEntity>();

    var incomingByEntId = new HashMap<Long, MissaoPrestadorDTO>();
    for (var p : dtos) {
      if (p == null || p.getEntId() == null)
        continue;
      incomingByEntId.putIfAbsent(p.getEntId(), p);
    }

    if (incomingByEntId.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("prestadores inválido");
    }

    if (!CollectionUtils.isEmpty(existentes)) {
      for (var e : existentes) {
        var dto = e != null ? incomingByEntId.remove(e.getEntId()) : null;
        if (dto != null) {
          e.setNome(dto.getNome());
          e.setEmail(dto.getEmail());
          e.setEstado(ESTADO_ATIVO);
          toSave.add(e);
        } else if (e != null) {
          e.setEstado(ESTADO_INATIVO);
          toSave.add(e);
        }
      }
    }

    for (var dto : incomingByEntId.values()) {
      if (dto == null)
        continue;
      if (!StringUtils.hasText(dto.getNome())) {
        throw IgrpResponseStatusException.badRequest("nome do prestador é obrigatório");
      }
      if (!StringUtils.hasText(dto.getEmail())) {
        throw IgrpResponseStatusException.badRequest("email do prestador é obrigatório");
      }

      var e = new MissaoPrestadorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEntId(dto.getEntId());
      e.setNome(dto.getNome());
      e.setEmail(dto.getEmail());
      e.setMissaoServId(missao);
      e.setEstado(ESTADO_ATIVO);
      toSave.add(e);
    }

    return toSave;
  }

  private void enviarNotificacoesPedidoSimulacao(
      MissaoServicoEntity missao,
      List<MissaoPrestadorEntity> prestadores,
      MissaoNotificacaoRequestDTO notifReq) {
    if (CollectionUtils.isEmpty(prestadores))
      return;

    var vars = Map.of(
        "nrMissao", String.valueOf(missao.getNrMissao()),
        "destino", missao.getDescricaoDestino() != null ? missao.getDescricaoDestino() : "",
        "dataInicio", missao.getDataInicio() != null ? missao.getDataInicio().toString() : "",
        "dataFim", missao.getDataFim() != null ? missao.getDataFim().toString() : "",
        "nrDias", String.valueOf(missao.getNrDias())
    );

    // Assunto e corpo: vêm do request (editável pelo RH) com fallback para template
    String assuntoOverride = notifReq != null ? notifReq.getAssunto() : null;
    String corpoOverride   = notifReq != null ? notifReq.getCorpoEmail() : null;

    for (var prest : prestadores) {
      if (prest == null || !StringUtils.hasText(prest.getEmail()) || !ESTADO_ATIVO.equals(prest.getEstado()))
        continue;

      String assunto = StringUtils.hasText(assuntoOverride)
          ? assuntoOverride
          : "Pedido de Proposta - Missão Nº " + missao.getNrMissao();
      String corpo = StringUtils.hasText(corpoOverride)
          ? corpoOverride
          : buildCorpoSimulacao(missao, vars);

      String estado = "Enviado";
      try {
        emailService.sendEmail(prest.getEmail(), assunto, corpo);
      } catch (Exception e) {
        LOGGER.warn("Erro ao enviar email de proposta para {}: {}", prest.getEmail(), e.getMessage());
        estado = "Erro";
      }

      var n = new NotificacaoEntity();
      n.setUuid(UuidCreator.getTimeOrderedEpoch());
      n.setTipoNotificacao(TIPO_NOTIF_PEDIDO_PROPOSTA);
      n.setReferenciaId(prest.getId());
      n.setReferenciaName(TableName.RH_T_MISSAO_PRESTADOR.name());
      n.setReferenciaUuid(prest.getUuid());
      n.setAssunto(assunto);
      n.setMessage(corpo);
      n.setEmail(prest.getEmail());
      n.setNomeReceptor(prest.getNome());
      n.setDataEnvio(LocalDate.now());
      n.setEstado(estado);
      notificacaoRepository.save(n);
    }
  }

  private void enviarNotificacoesEmissaoRequisicao(
      MissaoServicoEntity missao,
      Set<Long> selectedPrestIds) {
    if (CollectionUtils.isEmpty(selectedPrestIds))
      return;

    var vars = Map.of(
        "nrMissao", String.valueOf(missao.getNrMissao()),
        "destino", missao.getDescricaoDestino() != null ? missao.getDescricaoDestino() : "",
        "dataInicio", missao.getDataInicio() != null ? missao.getDataInicio().toString() : "",
        "dataFim", missao.getDataFim() != null ? missao.getDataFim().toString() : "",
        "nrDias", String.valueOf(missao.getNrDias())
    );

    for (var prestId : selectedPrestIds) {
      var prest = missaoPrestadorRepository.findById(prestId).orElse(null);
      if (prest == null || !StringUtils.hasText(prest.getEmail()))
        continue;

      notificacaoDispatchService.enviar(
          TIPO_NOTIF_EMISSAO_REQUISICAO,
          prest.getEmail(),
          prest.getNome(),
          prest.getId(),
          TableName.RH_T_MISSAO_PRESTADOR.name(),
          prest.getUuid(),
          null,
          vars
      );
    }
  }

  private void enviarNotificacoesLogisticaColaborador(
      MissaoServicoEntity missao,
      MissaoNotificacaoRequestDTO notifReq) {
    var colaboradores = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missao.getUuid());
    if (CollectionUtils.isEmpty(colaboradores))
      return;

    String assuntoOverride = notifReq != null ? notifReq.getAssunto() : null;
    String corpoOverride   = notifReq != null ? notifReq.getCorpoEmail() : null;

    String assunto = StringUtils.hasText(assuntoOverride)
        ? assuntoOverride
        : "Detalhes da sua Missão Nº " + missao.getNrMissao();
    String corpo = StringUtils.hasText(corpoOverride)
        ? corpoOverride
        : buildCorpoLogistica(missao);

    for (var colab : colaboradores) {
      if (colab == null || !ESTADO_ATIVO.equals(colab.getEstado()) || colab.getFunId() == null)
        continue;

      var n = new NotificacaoEntity();
      n.setUuid(UuidCreator.getTimeOrderedEpoch());
      n.setTipoNotificacao(TIPO_NOTIF_LOGISTICA_COLAB);
      n.setReferenciaId(missao.getId());
      n.setReferenciaName(TableName.RH_T_MISSAO_COLABORADOR.name());
      n.setReferenciaUuid(colab.getUuid());
      n.setAssunto(assunto);
      n.setMessage(corpo);
      n.setNomeReceptor(colab.getFunId().getNome());
      n.setFunId(colab.getFunId());
      n.setDataEnvio(LocalDate.now());
      n.setEstado("Pendente");
      notificacaoRepository.save(n);
    }
  }

  private String buildCorpoSimulacao(MissaoServicoEntity missao, Map<String, String> vars) {
    return "Exmo(a) Sr(a),\n\n" +
        "Solicita-se envio de proposta (fatura proforma) para missão de serviço com os seguintes dados:\n" +
        "- Nº Missão: " + vars.get("nrMissao") + "\n" +
        "- Destino: " + vars.get("destino") + "\n" +
        "- Data Início: " + vars.get("dataInicio") + "\n" +
        "- Data Fim: " + vars.get("dataFim") + "\n" +
        "- Duração: " + vars.get("nrDias") + " dia(s)\n\n" +
        "Aguardamos a vossa proposta.\n\nCom os melhores cumprimentos,\nINPS - Recursos Humanos";
  }

  private String buildCorpoLogistica(MissaoServicoEntity missao) {
    return "Exmo(a) Colaborador(a),\n\n" +
        "Informamos que os arranjos logísticos para a sua Missão Nº " + missao.getNrMissao() +
        " estão confirmados.\n" +
        "- Destino: " + (missao.getDescricaoDestino() != null ? missao.getDescricaoDestino() : "") + "\n" +
        "- Data Início: " + (missao.getDataInicio() != null ? missao.getDataInicio().toString() : "") + "\n" +
        "- Data Fim: " + (missao.getDataFim() != null ? missao.getDataFim().toString() : "") + "\n\n" +
        "Para detalhes sobre bilhete, alojamento, seguro e ajuda de custo, consulte o portal RH.\n\n" +
        "Com os melhores cumprimentos,\nINPS - Recursos Humanos";
  }

  /**
   * Avança a etapa da missão para {@code etapaAlvo}, nunca retrocedendo: gravar num ecrã de uma
   * etapa já ultrapassada não deve puxar o processo para trás. Também torna a gravação idempotente.
   */
  private boolean isNext(ProcessStepAction action) {
    return action != null && ACTION_NEXT.equals(action.getCode());
  }

  /**
   * Impede saltar etapas: o processo só avança (NEXT) se a missão já tiver atingido
   * {@code etapaMinima}. Gravar (SAVE) fora de ordem é permitido — não altera o estado do processo,
   * só escreve dados de formulário, e bloquear faria o utilizador perder o que preencheu; fica
   * apenas registado em log. Gravar numa etapa já ultrapassada é sempre permitido (correções).
   * Missões com etapa desconhecida/nula (dados legados) não são bloqueadas.
   */
  private void exigirEtapaMinima(MissaoServicoEntity missao, String etapaMinima, boolean avancando) {
    var atual = ORDEM_ETAPAS.indexOf(missao.getEtapa());
    if (atual < 0) {
      LOGGER.warn("Missão {} com etapa desconhecida ({}) — guarda de etapa ignorada",
          missao.getUuid(), missao.getEtapa());
      return;
    }
    if (atual >= ORDEM_ETAPAS.indexOf(etapaMinima)) {
      return;
    }
    if (avancando) {
      throw IgrpResponseStatusException.badRequest(
          "A missão encontra-se na etapa '" + missao.getEtapa()
              + "' — esta operação exige que já tenha atingido a etapa '" + etapaMinima + "'");
    }
    LOGGER.warn("Gravação fora de ordem na missão {}: etapa atual '{}', etapa do ecrã '{}'",
        missao.getUuid(), missao.getEtapa(), etapaMinima);
  }

  private void avancarEtapa(MissaoServicoEntity missao, String etapaAlvo) {
    var atual = ORDEM_ETAPAS.indexOf(missao.getEtapa());
    var alvo = ORDEM_ETAPAS.indexOf(etapaAlvo);
    if (alvo > atual) {
      missao.setEtapa(etapaAlvo);
    }
  }

  private Long nextNrMissao() {
    var max = missaoServicoRepository.findMaxNrMissao();
    return (max != null ? max : 0L) + 1L;
  }

  private int calcularNrDias(java.time.LocalDate inicio, java.time.LocalDate fim) {
    long diff = ChronoUnit.DAYS.between(inicio, fim);
    return (int) diff + 1;
  }

  private boolean isCaboVerde(cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity pais) {
    if (pais == null)
      return false;
    var nome = pais.getNome();
    var nomeOficial = pais.getNomeOficial();
    return (StringUtils.hasText(nome) && "cabo verde".equalsIgnoreCase(nome.trim()))
        || (StringUtils.hasText(nomeOficial) && "cabo verde".equalsIgnoreCase(nomeOficial.trim()));
  }

  private ArrayList<MissaoColaboradorEntity> persistirColaboradores(
      java.util.List<MissaoColaboradorRequestDTO> colaboradoresDto,
      MissaoServicoEntity missao) {
    var result = new ArrayList<MissaoColaboradorEntity>();
    var seen = new HashSet<UUID>();

    for (var c : colaboradoresDto) {
      if (c == null || c.getColaboradorId() == null)
        continue;
      if (!seen.add(c.getColaboradorId()))
        continue;

      var fun = funcionarioRepository.findByUuidOrThrow(c.getColaboradorId());

      var e = new MissaoColaboradorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setFunId(fun);
      e.setMissaoServId(missao);
      e.setNumDocumento(parseLong(fun.getNumDocumento()));
      result.add(e);
    }

    if (result.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("colaboradores inválido");
    }

    return result;
  }

  private ArrayList<MissaoColaboradorEntity> syncColaboradores(
      MissaoServicoEntity missao,
      java.util.List<MissaoColaboradorRequestDTO> colaboradoresDto) {
    var existentes = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missao.getUuid());
    var toSave = new ArrayList<MissaoColaboradorEntity>();

    var incoming = new HashMap<UUID, MissaoColaboradorRequestDTO>();
    for (var c : colaboradoresDto) {
      if (c == null || c.getColaboradorId() == null)
        continue;
      incoming.putIfAbsent(c.getColaboradorId(), c);
    }

    if (incoming.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("colaboradores inválido");
    }

    if (!CollectionUtils.isEmpty(existentes)) {
      for (var e : existentes) {
        var funUuid = e != null && e.getFunId() != null ? e.getFunId().getUuid() : null;
        var dto = funUuid != null ? incoming.remove(funUuid) : null;
        if (dto != null) {
          e.setEstado(ESTADO_ATIVO);
          var fun = funcionarioRepository.findByUuidOrThrow(funUuid);
          e.setNumDocumento(parseLong(fun.getNumDocumento()));
          toSave.add(e);
        } else if (e != null) {
          e.setEstado(ESTADO_INATIVO);
          toSave.add(e);
        }
      }
    }

    for (var funUuid : incoming.keySet()) {
      var fun = funcionarioRepository.findByUuidOrThrow(funUuid);
      var e = new MissaoColaboradorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setFunId(fun);
      e.setMissaoServId(missao);
      e.setNumDocumento(parseLong(fun.getNumDocumento()));
      toSave.add(e);
    }

    return toSave;
  }

  private boolean deveNotificarCancelamento(MissaoServicoEntity missao) {
    if (missao == null)
      return false;
    if (!StringUtils.hasText(missao.getEtapa()))
      return false;
    return !ETAPA_1.equals(missao.getEtapa()) && !ETAPA_2.equals(missao.getEtapa());
  }

  private void persistirNotificacaoCancelamento(MissaoServicoEntity missao, MissaoCancelarRequestDTO dto) {
    // Recolher todos os destinatários que já receberam notificação relativa a esta missão.
    // As notificações de análise ficam com referencia = RH_T_MISSAO_PRESTADOR (por prestador UUID),
    // por isso buscamos directamente nos prestadores activos da missão em vez do histórico.
    var prestadores = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missao.getUuid());

    // Complementar com emails do histórico de notificações gravadas com referencia da missão
    // (compatibilidade com notificações criadas noutros pontos do fluxo).
    var anteriores = notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getUuid());

    var seen = new HashSet<String>();
    var toSave = new ArrayList<NotificacaoEntity>();

    String assunto = "Cancelamento de Missão Nº " + missao.getNrMissao();
    String message = buildMensagemCancelamento(missao, dto);

    // 1. Prestadores activos da missão
    if (!CollectionUtils.isEmpty(prestadores)) {
      for (var prest : prestadores) {
        if (prest == null || !StringUtils.hasText(prest.getEmail()))
          continue;
        var email = prest.getEmail().trim().toLowerCase();
        if (!seen.add(email))
          continue;

        String estado = "Enviado";
        try {
          emailService.sendEmail(prest.getEmail(), assunto, message);
        } catch (Exception e) {
          LOGGER.warn("Erro ao enviar email de cancelamento para {}: {}", prest.getEmail(), e.getMessage());
          estado = "Erro";
        }

        var n = new NotificacaoEntity();
        n.setUuid(UuidCreator.getTimeOrderedEpoch());
        n.setTipoNotificacao(TIPO_NOTIF_CANCELAMENTO);
        n.setReferenciaId(missao.getId());
        n.setReferenciaName(TableName.RH_T_MISSAO_SERVICO.name());
        n.setReferenciaUuid(missao.getUuid());
        n.setAssunto(assunto);
        n.setMessage(message);
        n.setEmail(prest.getEmail());
        n.setNomeReceptor(prest.getNome());
        n.setDataEnvio(LocalDate.now());
        n.setEstado(estado);
        toSave.add(n);
      }
    }

    // 2. Outros destinatários do histórico (emails não cobertos pelos prestadores)
    if (!CollectionUtils.isEmpty(anteriores)) {
      for (var n0 : anteriores) {
        var email = n0 != null ? n0.getEmail() : null;
        if (!StringUtils.hasText(email))
          continue;
        if (!seen.add(email.trim().toLowerCase()))
          continue;

        String estado = "Enviado";
        try {
          emailService.sendEmail(email, assunto, message);
        } catch (Exception e) {
          LOGGER.warn("Erro ao enviar email de cancelamento para {}: {}", email, e.getMessage());
          estado = "Erro";
        }

        var n = new NotificacaoEntity();
        n.setUuid(UuidCreator.getTimeOrderedEpoch());
        n.setTipoNotificacao(TIPO_NOTIF_CANCELAMENTO);
        n.setReferenciaId(missao.getId());
        n.setReferenciaName(TableName.RH_T_MISSAO_SERVICO.name());
        n.setReferenciaUuid(missao.getUuid());
        n.setAssunto(assunto);
        n.setMessage(message);
        n.setEmail(email);
        n.setNomeReceptor(n0.getNomeReceptor());
        n.setDataEnvio(LocalDate.now());
        n.setEstado(estado);
        toSave.add(n);
      }
    }

    if (!toSave.isEmpty()) {
      notificacaoRepository.saveAll(toSave);
    }
  }

  private String buildMensagemCancelamento(MissaoServicoEntity missao, MissaoCancelarRequestDTO dto) {
    var motivo = dto != null ? dto.getMotivoCancelamento() : null;
    if (StringUtils.hasText(motivo)) {
      return "A missão Nº " + missao.getNrMissao() + " foi cancelada. Motivo: " + motivo;
    }
    return "A missão Nº " + missao.getNrMissao() + " foi cancelada.";
  }

  private Long parseLong(String raw) {
    if (!StringUtils.hasText(raw))
      return null;
    try {
      return Long.valueOf(raw.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private void persistirDocumentos(MissaoSubmissaoRequestDTO dto, MissaoServicoEntity missao) {
    if (dto.getDocumentos() == null)
      return;

    var existentes = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getUuid());

    var lista = documentoMapper.syncDocumentos(
        existentes != null ? existentes : new ArrayList<>(),
        dto.getDocumentos(),
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getId(),
        missao.getUuid(),
        1L,
        null);

    if (lista != null && !lista.isEmpty()) {
      lista.forEach(d -> {
        if (d.getUuid() == null)
          d.setUuid(UuidCreator.getTimeOrderedEpoch());
        if (d.getEstado() == null)
          d.setEstado(Estado.A);
      });
      documentoRepository.saveAll(lista);
    }
  }

  private UUID parseUuid(String raw, String field) {
    try {
      return UUID.fromString(raw);
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("UUID inválido para " + field + ": " + raw);
    }
  }
}
