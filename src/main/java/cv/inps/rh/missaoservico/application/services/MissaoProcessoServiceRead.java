package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.ProcessoPrestadorResponseDTO;
import cv.inps.rh.missaoservico.application.dto.ProcessoPrestadoresResponseDTO;
import cv.inps.rh.missaoservico.application.queries.GetProcessoPrestadoresQuery;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamPrestadorDetEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.ESTADO_ATIVO;

/** Leitura das etapas dos processos de missão (modelo por processo, spec 14/09). */
@RequiredArgsConstructor
@Service
public class MissaoProcessoServiceRead {

  private final MissaoProcessoSupport support;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final ParamPrestadorDetEntityRepository paramPrestadorDetRepository;

  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoPrestadoresResponseDTO> getPrestadores(GetProcessoPrestadoresQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());

    var prestadores = missaoPrestadorRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId())
        .stream()
        .filter(p -> ESTADO_ATIVO.equals(p.getEstado()))
        .toList();

    var paramIds = prestadores.stream()
        .filter(p -> p.getParamPrestId() != null)
        .map(p -> p.getParamPrestId().getId())
        .toList();
    var extrasPorParam = new HashMap<Long, List<String>>();
    if (!paramIds.isEmpty()) {
      for (var det : paramPrestadorDetRepository.findAllByParamPrestId_IdInAndEstado(paramIds, ESTADO_ATIVO)) {
        extrasPorParam.computeIfAbsent(det.getParamPrestId().getId(), _ -> new ArrayList<>()).add(det.getEmail());
      }
    }

    var conteudo = support.conteudoPedidoProposta(support.varsMissao(missao, tipo), null);
    var notificacao = new MissaoNotificacaoResponseDTO();
    notificacao.setAssunto(conteudo.assunto());
    notificacao.setCorpoEmail(conteudo.corpo());

    var response = new ProcessoPrestadoresResponseDTO();
    response.setMissaoUuid(missao.getUuid());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
    response.setProcesso(support.toProcessoDto(processo));
    response.setPrestadores(prestadores.stream().map(p -> toPrestadorDto(p, extrasPorParam)).toList());
    response.setNotificacao(notificacao);

    // "Executado por / Data execução": quem gravou a selecção mais recente
    prestadores.stream()
        .max(Comparator.comparing(MissaoPrestadorEntity::getId))
        .ifPresent(p -> {
          response.setExecutadoPor(p.getLastModifiedBy() != null ? p.getLastModifiedBy() : p.getCreatedBy());
          var data = p.getLastModifiedDate() != null ? p.getLastModifiedDate() : p.getCreatedDate();
          response.setDataExecucao(data != null ? data.toLocalDate() : null);
        });

    return ResponseEntity.ok(response);
  }

  private ProcessoPrestadorResponseDTO toPrestadorDto(MissaoPrestadorEntity p, Map<Long, List<String>> extrasPorParam) {
    var dto = new ProcessoPrestadorResponseDTO();
    dto.setId(p.getId());
    dto.setUuid(p.getUuid());
    dto.setEntId(p.getEntId());
    dto.setNome(p.getNome());
    dto.setEmail(p.getEmail());
    dto.setEstado(p.getEstado());

    var emails = new LinkedHashSet<String>();
    if (p.getParamPrestId() != null) {
      dto.setParamPrestUuid(p.getParamPrestId().getUuid());
      emails.add(p.getParamPrestId().getEmail());
      emails.addAll(extrasPorParam.getOrDefault(p.getParamPrestId().getId(), List.of()));
    } else if (p.getEmail() != null) {
      emails.add(p.getEmail());
    }
    dto.setEmails(List.copyOf(emails));
    return dto;
  }
}
