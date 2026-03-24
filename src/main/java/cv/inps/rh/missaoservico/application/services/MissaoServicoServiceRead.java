package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.dto.AjudaCustoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.AlojamentoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.BilhetePassagemResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoItemResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoItemResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaDetResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoPagamentoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoPrestadorResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoReqResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoReqItemResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoServicoResumoDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoServicoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.SeguroViagemResponseDTO;
import cv.inps.rh.missaoservico.application.queries.GetAnaliseProcessoMissaoServicoQuery;
import cv.inps.rh.missaoservico.application.queries.GetDetalheMissaoServicoQuery;
import cv.inps.rh.missaoservico.application.queries.GetListaMissaoServicoQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoCabimentoQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoAutorizacaoQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoLogisticaQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoPagamentoQuery;
import cv.inps.rh.missaoservico.application.queries.GetSubmissaoServicoProcessQuery;
import cv.inps.rh.missaoservico.application.queries.GetSubmissaoServicoEmissaoRequisicaoQuery;
import cv.inps.rh.missaoservico.application.dto.WrapperListMissaoServicoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoRequisicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MissaoServicoServiceRead {

  private static final String ESTADO_ATIVO = "A";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final DocumentoMapper documentoMapper;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoCabimentoResponseDTO> getCabimento(GetMissaoServicoCabimentoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var logistica = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(e -> e != null && ESTADO_ATIVO.equals(e.getEstado()))
        .toList();

    var detByLogId = new HashMap<Long, List<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity>>();
    var ids = logistica.stream()
        .map(MissaoLogisticaEntity::getId)
        .filter(java.util.Objects::nonNull)
        .toList();
    if (!ids.isEmpty()) {
      var dets = missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids);
      if (!CollectionUtils.isEmpty(dets)) {
        for (var d : dets) {
          if (d == null || d.getMissaoLogistId() == null || d.getMissaoLogistId().getId() == null)
            continue;
          detByLogId.computeIfAbsent(d.getMissaoLogistId().getId(), _ -> new ArrayList<>()).add(d);
        }
      }
    }

    var itens = new ArrayList<MissaoCabimentoItemResponseDTO>();
    for (var l : logistica) {
      if (l == null)
        continue;

      var nome = resolveNome(l, detByLogId.get(l.getId()));

      var docs = l.getUuid() != null
          ? documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_LOGISTICA.name(),
              l.getUuid())
          : List.<cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity>of();
      var fatura = docs == null
          ? null
          : docs.stream()
              .filter(d -> d != null && d.getEstado() != Estado.E)
              .max(Comparator.comparing(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity::getId,
                  Comparator.nullsLast(Comparator.naturalOrder())))
              .map(documentoMapper::toRespDto)
              .orElse(null);

      var item = new MissaoCabimentoItemResponseDTO();
      item.setLogisticaId(l.getId());
      item.setLogisticaUuid(l.getUuid());
      item.setReferencia(l.getReferencia());
      item.setNome(nome);
      item.setValorTotal(l.getValorTotal());
      item.setCabId(l.getCabId());
      item.setEstadoCabimento(l.getEstadoCabimento());
      item.setFatura(fatura);
      itens.add(item);
    }

    var response = new MissaoCabimentoResponseDTO();
    response.setMissaoId(missao.getId());
    response.setEtapaAtual(missao.getEtapa());
    response.setItens(itens);

    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoAutorizacaoResponseDTO> getAutorizacao(GetMissaoServicoAutorizacaoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var logistica = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(e -> e != null && ESTADO_ATIVO.equals(e.getEstado()))
        .toList();

    var detByLogId = new HashMap<Long, List<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity>>();
    var ids = logistica.stream()
        .map(MissaoLogisticaEntity::getId)
        .filter(java.util.Objects::nonNull)
        .toList();
    if (!ids.isEmpty()) {
      var dets = missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids);
      if (!CollectionUtils.isEmpty(dets)) {
        for (var d : dets) {
          if (d == null || d.getMissaoLogistId() == null || d.getMissaoLogistId().getId() == null)
            continue;
          detByLogId.computeIfAbsent(d.getMissaoLogistId().getId(), _ -> new ArrayList<>()).add(d);
        }
      }
    }

    var itens = new ArrayList<MissaoAutorizacaoItemResponseDTO>();
    for (var l : logistica) {
      if (l == null)
        continue;

      var item = new MissaoAutorizacaoItemResponseDTO();
      item.setLogisticaId(l.getId());
      item.setReferencia(l.getReferencia());
      item.setNome(resolveNome(l, detByLogId.get(l.getId())));
      item.setValorTotal(l.getValorTotal());
      item.setNumeroCabimento(l.getCabId());
      item.setEstadoCabimento(l.getEstadoCabimento());
      itens.add(item);
    }

    var response = new MissaoAutorizacaoResponseDTO();
    response.setMissaoId(missao.getId());
    response.setEtapaAtual(missao.getEtapa());
    response.setItens(itens);
    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoLogisticaResponseDTO> getLogistica(GetMissaoServicoLogisticaQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var logistica = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(e -> e != null && ESTADO_ATIVO.equals(e.getEstado()))
        .toList();

    var detByLogId = new HashMap<Long, List<MissaoLogisticaDetEntity>>();
    var ids = logistica.stream()
        .map(MissaoLogisticaEntity::getId)
        .filter(Objects::nonNull)
        .toList();
    if (!ids.isEmpty()) {
      var dets = missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids);
      if (!CollectionUtils.isEmpty(dets)) {
        for (var d : dets) {
          if (d == null || d.getMissaoLogistId() == null || d.getMissaoLogistId().getId() == null)
            continue;
          detByLogId.computeIfAbsent(d.getMissaoLogistId().getId(), _ -> new ArrayList<>()).add(d);
        }
      }
    }

    var bilhetes = new ArrayList<BilhetePassagemResponseDTO>();
    var seguros = new ArrayList<SeguroViagemResponseDTO>();
    var alojamentos = new ArrayList<AlojamentoResponseDTO>();
    var ajudas = new ArrayList<AjudaCustoResponseDTO>();

    for (var l : logistica) {
      if (l == null || !StringUtils.hasText(l.getReferencia()))
        continue;

      var referencia = l.getReferencia();
      var dets = detByLogId.get(l.getId());

      var docs = l.getUuid() != null
          ? documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_LOGISTICA.name(),
              l.getUuid())
          : List.<cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity>of();
      var documento = docs == null
          ? null
          : docs.stream()
              .filter(d -> d != null && d.getEstado() != Estado.E)
              .max(Comparator.comparing(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity::getId,
                  Comparator.nullsLast(Comparator.naturalOrder())))
              .map(documentoMapper::toRespDto)
              .orElse(null);

      if ("BILHETE_PASSAGEM".equalsIgnoreCase(referencia)) {
        var bp = new BilhetePassagemResponseDTO();
        bp.setId(l.getId());
        bp.setUuid(l.getUuid());
        bp.setColaboradores(mapDet(dets));
        bp.setValor(l.getValorTotal());
        bp.setDocumento(documento);
        bp.setEstado(l.getEstado());
        bilhetes.add(bp);
        continue;
      }

      if ("SEGURO_VIAGEM".equalsIgnoreCase(referencia)) {
        var sv = new SeguroViagemResponseDTO();
        sv.setId(l.getId());
        sv.setUuid(l.getUuid());
        sv.setEntId(l.getEntId());
        sv.setNomeSeguradora(l.getNomeSeguradora());
        sv.setColaboradores(mapDet(dets));
        sv.setValor(l.getValorTotal());
        sv.setDocumento(documento);
        sv.setEstado(l.getEstado());
        seguros.add(sv);
        continue;
      }

      if ("ALOJAMENTO".equalsIgnoreCase(referencia)) {
        var al = new AlojamentoResponseDTO();
        al.setId(l.getId());
        al.setUuid(l.getUuid());
        al.setFlgAlimentacao(l.getFlgAlimentacao());
        al.setLugarHospedagem(l.getLugarHospedagem());
        al.setValorDiario(l.getValorDiario());
        al.setValorTotal(l.getValorTotal());
        al.setMoeda(l.getMoeda());
        al.setDataInicio(l.getDataInicio());
        al.setDataFim(l.getDataFim());
        al.setNrDias(l.getNrDias());
        al.setColaborador(firstDet(dets));
        al.setDocumento(documento);
        al.setEstado(l.getEstado());
        alojamentos.add(al);
        continue;
      }

      if ("AJUDA_CUSTO".equalsIgnoreCase(referencia)) {
        var ac = new AjudaCustoResponseDTO();
        ac.setId(l.getId());
        ac.setUuid(l.getUuid());
        ac.setColaborador(firstDet(dets));
        ac.setFlgAlojamento("SIM".equalsIgnoreCase(l.getFlgAlojamento()));
        ac.setNumeroDiasAlojamento(l.getNrDias());
        ac.setValorDiario(l.getValorDiario());
        ac.setValorTotal(l.getValorTotal());
        ac.setEstado(l.getEstado());
        ajudas.add(ac);
      }
    }

    var response = new MissaoLogisticaResponseDTO();
    response.setMissaoId(missao.getId());
    response.setMissaoUuid(missao.getUuid());
    response.setEtapaAtual(missao.getEtapa());
    response.setBilhetesPassagem(bilhetes);
    response.setSegurosViagem(seguros);
    response.setAlojamentos(alojamentos);
    response.setAjudasCusto(ajudas);
    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoPagamentoResponseDTO> getPagamento(GetMissaoServicoPagamentoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var response = new MissaoPagamentoResponseDTO();
    response.setMissaoId(missao.getId());
    response.setEtapaAtual(missao.getEtapa());
    response.setEstado(missao.getEstado());
    response.setReferenciaPagamento(missao.getReferenciaPagamento());
    response.setDataPagamento(missao.getDataPagamento());
    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoAnaliseResponseDTO> getAnalise(GetAnaliseProcessoMissaoServicoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var prestadores = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(p -> p != null && ESTADO_ATIVO.equals(p.getEstado()))
        .map(p -> {
          var dto = new MissaoPrestadorResponseDTO();
          dto.setId(p.getId());
          dto.setUuid(p.getUuid());
          dto.setEntId(p.getEntId());
          dto.setNome(p.getNome());
          dto.setEmail(p.getEmail());
          dto.setEstado(p.getEstado());
          return dto;
        })
        .toList();

    MissaoNotificacaoResponseDTO notificacao = null;
    var notificacoes = notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missaoUuid);
    if (!CollectionUtils.isEmpty(notificacoes)) {
      var latest = notificacoes.stream()
          .filter(n -> n != null && (StringUtils.hasText(n.getAssunto()) || StringUtils.hasText(n.getMessage())))
          .max(Comparator.comparing(cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity::getId,
              Comparator.nullsLast(Comparator.naturalOrder())))
          .orElse(null);
      if (latest != null) {
        notificacao = new MissaoNotificacaoResponseDTO();
        notificacao.setAssunto(latest.getAssunto());
        notificacao.setCorpoEmail(latest.getMessage());
      }
    }

    var response = new MissaoAnaliseResponseDTO();
    response.setMissaoId(missao.getId());
    response.setEtapaAtual(missao.getEtapa());
    response.setPrestadores(prestadores);
    response.setNotificacao(notificacao);
    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoSubmissaoResponseDTO> getSubmissao(GetSubmissaoServicoProcessQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUuid()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var colaboradores = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(c -> c != null && ESTADO_ATIVO.equals(c.getEstado()))
        .map(c -> {
          var dto = new MissaoColaboradorResponseDTO();
          dto.setId(c.getId());
          dto.setUuid(c.getUuid());
          dto.setEstado(c.getEstado());
          dto.setNumDocumento(c.getNumDocumento() != null ? String.valueOf(c.getNumDocumento()) : null);
          dto.setFunId(c.getFunId() != null ? c.getFunId().getId() : null);
          dto.setFunUuid(c.getFunId() != null ? c.getFunId().getUuid() : null);
          dto.setNomeColaborador(c.getFunId() != null ? c.getFunId().getNome() : null);
          return dto;
        })
        .toList();

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
    response.setEtapaAtual(missao.getEtapa());
    response.setPaisDestinoId(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getId() : null);
    response.setPaisDestinoNome(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getNome() : null);
    response.setFlgDestino(missao.getFlgDestino());
    response.setDescricaoDestino(missao.getDescricaoDestino());
    response.setAmbitoMissao(resolveAmbitoMissao(missao.getFlgDestino()));
    response.setDataInicio(missao.getDataInicio());
    response.setDataFim(missao.getDataFim());
    response.setNrDias(missao.getNrDias());
    response.setAutorizadoPor(missao.getAutorizadoPor());
    response.setDataAutorizacao(missao.getDataAutorizacao());
    response.setEtapa(missao.getEtapa());
    response.setEstado(missao.getEstado());
    response.setColaboradores(colaboradores);
    response.setDocumentos(documentos);

    response.setDataRegisto(toLocalDate(missao.getCreatedDate()));
    response.setUserRegistoId(missao.getCreatedById());
    response.setUserRegistoName(missao.getCreatedBy());
    response.setUserAlteracaoId(missao.getLastModifiedById());
    response.setUserAlteracaoName(missao.getLastModifiedBy());
    response.setDataAlteracao(toLocalDate(missao.getLastModifiedDate()));

    return ResponseEntity.ok(response);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<MissaoEmissaoReqResponseDTO> getEmissaoRequisicao(
      GetSubmissaoServicoEmissaoRequisicaoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query.getUui()).valor();
    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);

    var requisicoes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(r -> r != null && ESTADO_ATIVO.equals(r.getEstado()))
        .toList();

    var byPrest = new HashMap<Long, List<MissaoRequisicaoEntity>>();
    for (var r : requisicoes) {
      var prestId = r.getMissaoPrestId() != null ? r.getMissaoPrestId().getId() : null;
      if (prestId == null)
        continue;
      byPrest.computeIfAbsent(prestId, _ -> new ArrayList<>()).add(r);
    }

    var itens = new ArrayList<MissaoReqItemResponseDTO>();
    var prestadores = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missaoUuid)
        .stream()
        .filter(p -> p != null && ESTADO_ATIVO.equals(p.getEstado()))
        .toList();

    if (!CollectionUtils.isEmpty(prestadores)) {
      for (var prest : prestadores) {
        if (prest == null || prest.getId() == null)
          continue;

        var list = byPrest.getOrDefault(prest.getId(), List.of());

        var colaboradores = new ArrayList<MissaoColaboradorResponseDTO>();
        for (var r : list) {
          var c = r != null ? r.getMissaoColabId() : null;
          if (c == null || !ESTADO_ATIVO.equals(c.getEstado()))
            continue;
          colaboradores.add(toColaboradorDto(c));
        }

        AnexoRespDTO proposta = null;
        for (var r : list) {
          if (r == null || r.getUuid() == null)
            continue;
          var docs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
              TableName.RH_T_MISSAO_REQUISICAO.name(),
              r.getUuid());
          if (CollectionUtils.isEmpty(docs))
            continue;
          proposta = docs.stream()
              .filter(d -> d != null && d.getEstado() != Estado.E)
              .max(Comparator.comparing(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity::getId,
                  Comparator.nullsLast(Comparator.naturalOrder())))
              .map(documentoMapper::toRespDto)
              .orElse(null);
          if (proposta != null)
            break;
        }

        var item = new MissaoReqItemResponseDTO();
        item.setId(prest.getId());
        item.setUuid(prest.getUuid());
        item.setMissaoPrestId(prest.getId());
        item.setNomePrestador(prest.getNome());
        item.setEmailPrestador(prest.getEmail());
        item.setColaboradores(colaboradores);
        item.setProposta(proposta);
        item.setEstado(ESTADO_ATIVO);
        itens.add(item);
      }
    } else {
      for (var entry : byPrest.entrySet()) {
        var list = entry.getValue();
        if (CollectionUtils.isEmpty(list))
          continue;
        var any = list.get(0);
        var prest = any != null ? any.getMissaoPrestId() : null;
        if (prest == null)
          continue;

        var colaboradores = new ArrayList<MissaoColaboradorResponseDTO>();
        for (var r : list) {
          var c = r != null ? r.getMissaoColabId() : null;
          if (c == null || !ESTADO_ATIVO.equals(c.getEstado()))
            continue;
          colaboradores.add(toColaboradorDto(c));
        }

        AnexoRespDTO proposta = null;
        for (var r : list) {
          if (r == null || r.getUuid() == null)
            continue;
          var docs = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
              TableName.RH_T_MISSAO_REQUISICAO.name(),
              r.getUuid());
          if (CollectionUtils.isEmpty(docs))
            continue;
          proposta = docs.stream()
              .filter(d -> d != null && d.getEstado() != Estado.E)
              .max(Comparator.comparing(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity::getId,
                  Comparator.nullsLast(Comparator.naturalOrder())))
              .map(documentoMapper::toRespDto)
              .orElse(null);
          if (proposta != null)
            break;
        }

        var item = new MissaoReqItemResponseDTO();
        item.setId(prest.getId());
        item.setUuid(prest.getUuid());
        item.setMissaoPrestId(prest.getId());
        item.setNomePrestador(prest.getNome());
        item.setEmailPrestador(prest.getEmail());
        item.setColaboradores(colaboradores);
        item.setProposta(proposta);
        item.setEstado(ESTADO_ATIVO);
        itens.add(item);
      }
    }

    var response = new MissaoEmissaoReqResponseDTO();
    response.setMissaoId(missao.getId());
    response.setEtapaAtual(missao.getEtapa());
    response.setRequisicoes(itens);
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
    response.setPaisDestinoId(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getId() : null);
    response.setPaisDestinoNome(missao.getPaisDestinoId() != null ? missao.getPaisDestinoId().getNome() : null);
    response.setFlgDestino(missao.getFlgDestino());
    response.setDescricaoDestino(missao.getDescricaoDestino());
    response.setAmbitoMissao(resolveAmbitoMissao(missao.getFlgDestino()));
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
    var nrMissao = parseLongSafe(query != null ? query.getNrMissao() : null);
    var periodoDe = parseDateSafe(query != null ? query.getPeriodoDe() : null);
    var periodoAte = parseDateSafe(query != null ? query.getPeriodoAte() : null);
    int pageNumber = parseIntSafe(query != null ? query.getPageNumber() : null, 0);
    int pageSize = parseIntSafe(query != null ? query.getPageSize() : null, 10);

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "nrMissao"));

    Specification<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity> spec = (root, q, cb) -> {
      var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
      if (nrMissao != null) {
        predicates.add(cb.equal(root.get("nrMissao"), nrMissao));
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
    var missoes = page.getContent() != null ? page.getContent()
        : List.<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity>of();

    var missaoById = new HashMap<Long, cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity>();
    var missaoIds = new ArrayList<Long>();
    for (var m : missoes) {
      if (m == null || m.getId() == null)
        continue;
      missaoById.put(m.getId(), m);
      missaoIds.add(m.getId());
    }

    var totalsByMissao = new HashMap<Long, Map<String, BigDecimal>>();
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
        }
      }
    }

    var content = new ArrayList<MissaoServicoResumoDTO>();
    for (var m : missoes) {
      if (m == null)
        continue;
      var sums = totalsByMissao.getOrDefault(m.getId(), java.util.Map.of());
      var dto = new MissaoServicoResumoDTO();
      dto.setId(m.getId());
      dto.setUuid(m.getUuid());
      dto.setNrMissao(m.getNrMissao());
      dto.setDestino(m.getDescricaoDestino());
      dto.setNacionalInternacional(resolveNacionalInternacional(m.getFlgDestino()));
      dto.setDataMissao(m.getDataInicio());
      dto.setEtapa(m.getEtapa());
      dto.setEstado(resolveEstadoLista(m));
      dto.setValorAC(sums.get("AJUDA_CUSTO"));
      dto.setValorBP(sums.get("BILHETE_PASSAGEM"));
      dto.setValorAlojamento(sums.get("ALOJAMENTO"));
      dto.setValorSeguro(sums.get("SEGURO_VIAGEM"));
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

  private String resolveNome(
      MissaoLogisticaEntity logistica,
      List<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity> dets) {
    if (logistica == null)
      return null;

    if ("AJUDA_CUSTO".equalsIgnoreCase(logistica.getReferencia())) {
      if (!CollectionUtils.isEmpty(dets)) {
        var d0 = dets.get(0);
        if (d0 != null && d0.getMissaoColabId() != null && d0.getMissaoColabId().getFunId() != null) {
          return d0.getMissaoColabId().getFunId().getNome();
        }
      }
    }

    if ("SEGURO_VIAGEM".equalsIgnoreCase(logistica.getReferencia())
        && org.springframework.util.StringUtils.hasText(logistica.getNomeSeguradora())) {
      return logistica.getNomeSeguradora();
    }

    return logistica.getPrestadorServId() != null ? logistica.getPrestadorServId().getNome() : null;
  }

  private List<MissaoLogisticaDetResponseDTO> mapDet(
      List<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity> dets) {
    if (CollectionUtils.isEmpty(dets))
      return List.of();
    var out = new ArrayList<MissaoLogisticaDetResponseDTO>();
    for (var d : dets) {
      var dto = toDetDto(d);
      if (dto != null)
        out.add(dto);
    }
    return out;
  }

  private MissaoLogisticaDetResponseDTO firstDet(
      List<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity> dets) {
    if (CollectionUtils.isEmpty(dets))
      return null;
    return toDetDto(dets.get(0));
  }

  private MissaoLogisticaDetResponseDTO toDetDto(
      cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity d) {
    if (d == null)
      return null;
    var dto = new MissaoLogisticaDetResponseDTO();
    dto.setId(d.getId());
    dto.setEstado(d.getEstado());
    dto.setMissaoColabUuid(d.getMissaoColabId() != null ? d.getMissaoColabId().getUuid() : null);
    dto.setFuncionarioUuid(d.getMissaoColabId() != null && d.getMissaoColabId().getFunId() != null
        ? d.getMissaoColabId().getFunId().getUuid()
        : null);
    dto.setNomeColaborador(d.getMissaoColabId() != null && d.getMissaoColabId().getFunId() != null
        ? d.getMissaoColabId().getFunId().getNome()
        : null);
    return dto;
  }

  private MissaoColaboradorResponseDTO toColaboradorDto(
      cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity c) {
    if (c == null)
      return null;
    var dto = new MissaoColaboradorResponseDTO();
    dto.setId(c.getId());
    dto.setUuid(c.getUuid());
    dto.setEstado(c.getEstado());
    dto.setNumDocumento(c.getNumDocumento() != null ? String.valueOf(c.getNumDocumento()) : null);
    dto.setFunId(c.getFunId() != null ? c.getFunId().getId() : null);
    dto.setFunUuid(c.getFunId() != null ? c.getFunId().getUuid() : null);
    dto.setNomeColaborador(c.getFunId() != null ? c.getFunId().getNome() : null);
    return dto;
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

  private String resolveEstadoLista(cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity missao) {
    if (missao == null || !StringUtils.hasText(missao.getEtapa()))
      return null;
    var etapa = missao.getEtapa();
    if ("ETAPA_7_PAGAMENTO_FINANCEIRO".equals(etapa)) {
      return (missao.getReferenciaPagamento() != null || missao.getDataPagamento() != null) ? "PAGO" : "POR_PAGAR";
    }
    if ("ETAPA_6_AUTORIZACAO_RH".equals(etapa) || "ETAPA_5_CABIMENTACAO_SGAL".equals(etapa)) {
      return "POR_PAGAR";
    }
    if ("ETAPA_4_PROCESSAMENTO_LOGISTICO".equals(etapa)) {
      return "PENDENTE_FATURA";
    }
    return "PENDENTE_REQUISICAO";
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
}
