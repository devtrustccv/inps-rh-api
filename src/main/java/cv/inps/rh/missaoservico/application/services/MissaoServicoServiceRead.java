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
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.SeguroViagemResponseDTO;
import cv.inps.rh.missaoservico.application.queries.GetAnaliseProcessoMissaoServicoQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoCabimentoQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoAutorizacaoQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoLogisticaQuery;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoPagamentoQuery;
import cv.inps.rh.missaoservico.application.queries.GetSubmissaoServicoProcessQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    var detByLogId = new HashMap<Long, List<cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity>>();
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

  private String resolveAmbitoMissao(Integer flgDestino) {
    if (flgDestino == null)
      return null;
    if (Integer.valueOf(1).equals(flgDestino))
      return "NACIONAL";
    if (Integer.valueOf(2).equals(flgDestino))
      return "INTERNACIONAL";
    return null;
  }

  private java.time.LocalDate toLocalDate(LocalDateTime dt) {
    return dt != null ? dt.toLocalDate() : null;
  }
}
