package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoItemResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoResponseDTO;
import cv.inps.rh.missaoservico.application.queries.GetMissaoServicoCabimentoQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoLogisticaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MissaoServicoServiceRead {

  private static final String ESTADO_ATIVO = "A";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final DocumentoMapper documentoMapper;

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
}
