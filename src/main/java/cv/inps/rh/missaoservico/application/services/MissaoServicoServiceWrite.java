package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.CancelarMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveAnaliseProcessoMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveMissaoServicoAutorizacaoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveMissaoServicoCabimentoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveMissaoServicoLogisticaCommand;
import cv.inps.rh.missaoservico.application.commands.SaveSubmissaoServicoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveSubmissaoServicoEmissaoRequisicaoCommand;
import cv.inps.rh.missaoservico.application.commands.SubmeterMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.AjudaCustoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.AlojamentoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.BilhetePassagemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoItemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoItemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCancelarRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoPrestadorDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoRequisicaoItemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.SeguroViagemRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoRequisicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MissaoServicoServiceWrite {

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_INATIVO = "I";
  private static final Integer DESTINO_NACIONAL = 1;
  private static final Integer DESTINO_ESTRANGEIRO = 2;
  private static final String ETAPA_1 = "ETAPA_1_SUBMISSAO_AUTORIZACAO";
  private static final String ETAPA_2 = "ETAPA_2_ANALISE_RH";
  private static final String ETAPA_3 = "ETAPA_3_EMISSAO_REQUISICAO";
  private static final String ETAPA_4 = "ETAPA_4_PROCESSAMENTO_LOGISTICO";
  private static final String ETAPA_5 = "ETAPA_5_CABIMENTACAO_SGAL";
  private static final String ETAPA_6 = "ETAPA_6_AUTORIZACAO_RH";
  private static final String ETAPA_7 = "ETAPA_7_PAGAMENTO_FINANCEIRO";

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

    validarAnalise(dto);

    var prestadoresPersistidos = syncPrestadores(missao, dto.getPrestadores());
    if (!prestadoresPersistidos.isEmpty()) {
      missaoPrestadorRepository.saveAll(prestadoresPersistidos);
    }

    persistirNotificacoesAnalise(missao, dto.getNotificacao(), dto.getPrestadores());

    missao.setEtapa(ETAPA_2);
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
    missao.setEtapa(ETAPA_1);

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

    validarEmissaoRequisicao(dto.getRequisicoes());

    var existentes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid);

    var desired = new HashSet<String>();
    var propostaByPrestador = new HashMap<Long, cv.inps.rh.shared.application.dto.AnexoReqDTO>();

    for (var item : dto.getRequisicoes()) {
      if (item == null)
        continue;
      if (item.getSelecionado() == null || !item.getSelecionado())
        continue;
      if (item.getMissaoPrestId() == null)
        continue;

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

    missao.setEtapa(ETAPA_3);
    if (dto.getProcessoEtapaAction() != null && dto.getProcessoEtapaAction().getCode().equals("NEXT")) {
      missao.setEtapa(ETAPA_4);
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
      var alimentacaoByColabId = new HashMap<Long, String>();
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

    if (dto.getProcessoEtapaAction() != null && "NEXT".equals(dto.getProcessoEtapaAction().getCode())) {
      missao.setEtapa(ETAPA_4);
      missaoServicoRepository.save(missao);
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

    var toSave = new ArrayList<MissaoLogisticaEntity>();

    for (var item : dto.getItens()) {
      if (item == null)
        continue;
      if (item.getSelecionado() == null || !item.getSelecionado())
        continue;
      if (item.getLogisticaId() == null)
        continue;

      if (item.getCabId() == null) {
        throw IgrpResponseStatusException.badRequest("cabId é obrigatório");
      }

      var log = missaoLogisticaRepository.findById(item.getLogisticaId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("logisticaId inválido: " + item.getLogisticaId()));

      if (log.getMissaoServId() == null || log.getMissaoServId().getUuid() == null
          || !log.getMissaoServId().getUuid().equals(missaoUuid)) {
        throw IgrpResponseStatusException.badRequest("logisticaId não pertence à missão: " + item.getLogisticaId());
      }

      log.setCabId(item.getCabId());
      log.setEstadoCabimento("CABIMENTADO");
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

    missao.setEtapa(ETAPA_5);
    if (dto.getProcessoEtapaAction() != null && "NEXT".equals(dto.getProcessoEtapaAction().getCode())) {
      missao.setEtapa(ETAPA_6);
    }
    missaoServicoRepository.save(missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
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

      if (log.getCabId() == null) {
        throw IgrpResponseStatusException.badRequest("Item sem cabimento: " + item.getLogisticaId());
      }

      log.setEstadoCabimento("AUTORIZADO");
      if (!ESTADO_ATIVO.equals(log.getEstado())) {
        log.setEstado(ESTADO_ATIVO);
      }
      toSave.add(log);
    }

    if (!toSave.isEmpty()) {
      missaoLogisticaRepository.saveAll(toSave);
    }

    missao.setEtapa(ETAPA_6);
    if (dto.getProcessoEtapaAction() != null && "NEXT".equals(dto.getProcessoEtapaAction().getCode())) {
      missao.setEtapa(ETAPA_7);
    }
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
      if (item.getCabId() == null) {
        throw IgrpResponseStatusException.badRequest("cabId é obrigatório");
      }
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
        colabs.add(missaoColaboradorRepository.findByUuidOrThrow(colabUuid));
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
        colabs.add(missaoColaboradorRepository.findByUuidOrThrow(colabUuid));
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

      var colab = missaoColaboradorRepository.findById(aloj.getColaboradorId())
          .orElseThrow(
              () -> IgrpResponseStatusException.badRequest("colaboradorId inválido: " + aloj.getColaboradorId()));

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
      Map<Long, String> alimentacaoByColabId,
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

      var colab = missaoColaboradorRepository.findByUuidOrThrow(ajuda.getColaboradorId());
      var prestador = derivePrestadorFromRequisicao(missao.getUuid(), List.of(colab), requisicoes);

      var baseValorDiario = ajuda.getValorDiario();
      var valorDiarioCalculado = calcularValorDiarioAjudaCusto(baseValorDiario, ajuda.getFlgAlojamento(),
          alimentacaoByColabId != null ? alimentacaoByColabId.get(colab.getId()) : null);
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

  private void persistirNotificacoesAnalise(
      MissaoServicoEntity missao,
      MissaoNotificacaoRequestDTO notificacao,
      List<MissaoPrestadorDTO> prestadores) {
    if (notificacao == null)
      return;
    if (!StringUtils.hasText(notificacao.getAssunto()) && !StringUtils.hasText(notificacao.getCorpoEmail()))
      return;
    if (CollectionUtils.isEmpty(prestadores))
      return;

    var toSave = new ArrayList<NotificacaoEntity>();
    for (var p : prestadores) {
      if (p == null || !StringUtils.hasText(p.getEmail()))
        continue;
      var n = new NotificacaoEntity();
      n.setUuid(UuidCreator.getTimeOrderedEpoch());
      n.setReferenciaId(missao.getId());
      n.setReferenciaName(TableName.RH_T_MISSAO_SERVICO.name());
      n.setReferenciaUuid(missao.getUuid());
      n.setAssunto(notificacao.getAssunto());
      n.setMessage(notificacao.getCorpoEmail());
      n.setEmail(p.getEmail());
      n.setNomeReceptor(p.getNome());
      toSave.add(n);
    }

    if (!toSave.isEmpty()) {
      notificacaoRepository.saveAll(toSave);
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
    var anteriores = notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getUuid());

    if (CollectionUtils.isEmpty(anteriores))
      return;

    var seen = new HashSet<String>();
    var toSave = new ArrayList<NotificacaoEntity>();

    String assunto = "Cancelamento de Missão Nº " + missao.getNrMissao();
    String message = buildMensagemCancelamento(missao, dto);

    for (var n0 : anteriores) {
      var email = n0 != null ? n0.getEmail() : null;
      if (!StringUtils.hasText(email))
        continue;
      if (!seen.add(email.trim().toLowerCase()))
        continue;

      var n = new NotificacaoEntity();
      n.setUuid(UuidCreator.getTimeOrderedEpoch());
      n.setReferenciaId(missao.getId());
      n.setReferenciaName(TableName.RH_T_MISSAO_SERVICO.name());
      n.setReferenciaUuid(missao.getUuid());
      n.setAssunto(assunto);
      n.setMessage(message);
      n.setEmail(email);
      n.setNomeReceptor(n0.getNomeReceptor());
      n.setEstado(ESTADO_ATIVO);
      toSave.add(n);
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
