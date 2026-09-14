package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.missaoservico.application.queries.GetProcessoPrestadoresQuery;
import cv.inps.rh.missaoservico.application.queries.GetProcessoRequisicoesQuery;
import cv.inps.rh.missaoservico.application.queries.GetRequisicaoPdfQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.ESTADO_ATIVO;
import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.REF_DOC_REQUISICAO_PDF;

/** Leitura das etapas dos processos de missão (modelo por processo, spec 14/09). */
@RequiredArgsConstructor
@Service
public class MissaoProcessoServiceRead {

  private final MissaoProcessoSupport support;
  private final RequisicaoPdfService requisicaoPdfService;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;
  private final MissaoRequisicaoColabEntityRepository missaoRequisicaoColabRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final ParamPrestadorDetEntityRepository paramPrestadorDetRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final DocumentoMapper documentoMapper;

  // ---------------------------------------------------------------------------------------------
  // Etapa Prestadores Serviço
  // ---------------------------------------------------------------------------------------------

  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoPrestadoresResponseDTO> getPrestadores(GetProcessoPrestadoresQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());

    var prestadores = prestadoresAtivos(processo);
    var extrasPorParam = emailsAdicionais(prestadores);

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

  // ---------------------------------------------------------------------------------------------
  // Etapa Emissão de Requisição
  // ---------------------------------------------------------------------------------------------

  /**
   * Um item por prestador activo do processo — com ou sem requisição — para o ecrã poder
   * seleccionar, associar colaboradores e anexar a proposta.
   */
  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoRequisicoesResponseDTO> getRequisicoes(GetProcessoRequisicoesQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();

    var requisicoes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoProcessoId_IdOrderByIdAsc(processo.getId())
        .stream()
        .filter(r -> ESTADO_ATIVO.equals(r.getEstado()))
        .toList();
    var requisicaoPorPrestador = new HashMap<Long, MissaoRequisicaoEntity>();
    requisicoes.forEach(r -> requisicaoPorPrestador.putIfAbsent(r.getMissaoPrestId().getId(), r));

    var colabsPorRequisicao = colaboradoresPorRequisicao(requisicoes);

    var itens = new ArrayList<ProcessoRequisicaoItemResponseDTO>();
    for (var prestador : prestadoresAtivos(processo)) {
      var item = new ProcessoRequisicaoItemResponseDTO();
      item.setMissaoPrestUuid(prestador.getUuid());
      item.setNomePrestador(prestador.getNome());
      item.setEmailPrestador(prestador.getEmail());

      var r = requisicaoPorPrestador.get(prestador.getId());
      item.setSelecionado(r != null);
      if (r != null) {
        item.setRequisicaoUuid(r.getUuid());
        item.setNrRequisicao(r.getNrRequisacao());
        item.setAnoRequisicao(r.getAno());
        item.setNotaEncomenda(RequisicaoPdfService.notaEncomenda(r));
        item.setValorTotal(r.getValorTotal());
        item.setColaboradores(colabsPorRequisicao.getOrDefault(r.getId(), List.of()).stream()
            .map(rc -> support.toColaboradorDto(rc.getMissaoColabId()))
            .toList());
        item.setProposta(documentoMaisRecente(TableName.RH_T_MISSAO_REQUISICAO.name(), r.getUuid(), false));
        item.setDocumentoRequisicao(documentoMaisRecente(REF_DOC_REQUISICAO_PDF, r.getUuid(), true));
      } else {
        item.setColaboradores(List.of());
      }
      itens.add(item);
    }

    var response = new ProcessoRequisicoesResponseDTO();
    response.setMissaoUuid(missao.getUuid());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
    response.setProcesso(support.toProcessoDto(processo));
    response.setRequisicoes(itens);
    response.setColaboradoresMissao(missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid).stream()
        .filter(c -> ESTADO_ATIVO.equals(c.getEstado()))
        .map(support::toColaboradorDto)
        .toList());

    requisicoes.stream()
        .max(Comparator.comparing(MissaoRequisicaoEntity::getId))
        .ifPresent(r -> {
          response.setExecutadoPor(r.getLastModifiedBy() != null ? r.getLastModifiedBy() : r.getCreatedBy());
          var data = r.getLastModifiedDate() != null ? r.getLastModifiedDate() : r.getCreatedDate();
          response.setDataExecucao(data != null ? data.toLocalDate() : null);
        });

    return ResponseEntity.ok(response);
  }

  /** "Extrair Requisição": gera o PDF da nota de encomenda com os dados actuais (também serve de pré-visualização). */
  @Transactional(readOnly = true)
  public ResponseEntity<byte[]> getRequisicaoPdf(GetRequisicaoPdfQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var requisicaoUuid = IdentificadorUnico.from(query.getRequisicaoUuid()).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);

    var requisicao = missaoRequisicaoRepository.findByUuid(requisicaoUuid)
        .filter(r -> r.getMissaoPrestId().getMissaoProcessoId() != null
            && processo.getId().equals(r.getMissaoPrestId().getMissaoProcessoId().getId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Requisição não encontrada neste processo: " + requisicaoUuid));

    var pdf = requisicaoPdfService.gerar(requisicao);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RequisicaoPdfService.nomeFicheiro(requisicao))
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(pdf.length)
        .body(pdf);
  }

  // ---------------------------------------------------------------------------------------------

  private List<MissaoPrestadorEntity> prestadoresAtivos(MissaoProcessoEntity processo) {
    return missaoPrestadorRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId())
        .stream()
        .filter(p -> ESTADO_ATIVO.equals(p.getEstado()))
        .toList();
  }

  private Map<Long, List<String>> emailsAdicionais(List<MissaoPrestadorEntity> prestadores) {
    var paramIds = prestadores.stream()
        .filter(p -> p.getParamPrestId() != null)
        .map(p -> p.getParamPrestId().getId())
        .toList();
    var out = new HashMap<Long, List<String>>();
    if (!paramIds.isEmpty()) {
      for (var det : paramPrestadorDetRepository.findAllByParamPrestId_IdInAndEstado(paramIds, ESTADO_ATIVO)) {
        out.computeIfAbsent(det.getParamPrestId().getId(), _ -> new ArrayList<>()).add(det.getEmail());
      }
    }
    return out;
  }

  private Map<Long, List<MissaoRequisicaoColabEntity>> colaboradoresPorRequisicao(List<MissaoRequisicaoEntity> requisicoes) {
    var out = new HashMap<Long, List<MissaoRequisicaoColabEntity>>();
    var ids = requisicoes.stream().map(MissaoRequisicaoEntity::getId).toList();
    if (ids.isEmpty())
      return out;
    missaoRequisicaoColabRepository.findAllByMissaoRequisicaoId_IdIn(ids).stream()
        .filter(rc -> ESTADO_ATIVO.equals(rc.getEstado()))
        .sorted(Comparator.comparing(MissaoRequisicaoColabEntity::getId))
        .forEach(rc -> out.computeIfAbsent(rc.getMissaoRequisicaoId().getId(), _ -> new ArrayList<>()).add(rc));
    return out;
  }

  private AnexoRespDTO documentoMaisRecente(String referenciaName, UUID referenciaUuid, boolean soAtivos) {
    return documentoRepository.findAllByReferenciaNameAndReferenciaUuid(referenciaName, referenciaUuid).stream()
        .filter(d -> soAtivos ? d.getEstado() == Estado.A : d.getEstado() != Estado.E)
        .max(Comparator.comparing(DocumentoEntity::getId))
        .map(documentoMapper::toRespDto)
        .orElse(null);
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
