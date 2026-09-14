package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.missaoservico.application.queries.GetProcessoPrestadoresQuery;
import cv.inps.rh.missaoservico.application.queries.GetProcessoRequisicoesQuery;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.ResponsavelParecer;
import cv.inps.rh.missaoservico.application.queries.GetListaProcessosEtapaQuery;
import cv.inps.rh.missaoservico.application.queries.GetAvaliacaoPrestadorQuery;
import cv.inps.rh.missaoservico.application.queries.GetProcessoAprovacaoRhQuery;
import cv.inps.rh.missaoservico.application.queries.GetProcessoCabimentoQuery;
import cv.inps.rh.missaoservico.application.queries.GetProcessoLogisticaQuery;
import cv.inps.rh.missaoservico.application.queries.GetProcessoValidacaoUgalQuery;
import cv.inps.rh.missaoservico.application.queries.GetRequisicaoPdfQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;
  private final MissaoProcessoDetEntityRepository missaoProcessoDetRepository;
  private final MissaoPrestadorAvalEntityRepository missaoPrestadorAvalRepository;
  private final MissaoProcessoEntityRepository missaoProcessoRepository;

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
  // Etapa Logística
  // ---------------------------------------------------------------------------------------------

  /**
   * Linhas de logística do processo — só a secção do seu tipo vem preenchida — e os colaboradores
   * que podem entrar numa linha. No bilhete e no alojamento só entram colaboradores com requisição,
   * e cada um vem com o prestador, para o ecrã agrupar o multiselect.
   */
  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoLogisticaResponseDTO> getLogistica(GetProcessoLogisticaQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());

    var linhas = missaoLogisticaRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado()))
        .toList();
    var detsPorLinha = new HashMap<Long, List<MissaoLogisticaDetEntity>>();
    var ids = linhas.stream().map(MissaoLogisticaEntity::getId).toList();
    if (!ids.isEmpty()) {
      missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids).stream()
          .filter(d -> ESTADO_ATIVO.equals(d.getEstado()))
          .sorted(Comparator.comparing(MissaoLogisticaDetEntity::getId))
          .forEach(d -> detsPorLinha.computeIfAbsent(d.getMissaoLogistId().getId(), _ -> new ArrayList<>()).add(d));
    }

    var bilhetes = new ArrayList<BilhetePassagemResponseDTO>();
    var seguros = new ArrayList<SeguroViagemResponseDTO>();
    var alojamentos = new ArrayList<AlojamentoResponseDTO>();
    var ajudas = new ArrayList<AjudaCustoResponseDTO>();

    for (var l : linhas) {
      var colaboradores = detsPorLinha.getOrDefault(l.getId(), List.of()).stream().map(this::toDetDto).toList();
      var primeiro = colaboradores.isEmpty() ? null : colaboradores.getFirst();
      var documento = documentoMaisRecente(TableName.RH_T_MISSAO_LOGISTICA.name(), l.getUuid(), false);

      switch (tipo) {
        case BILHETE_PASSAGEM -> {
          var dto = new BilhetePassagemResponseDTO();
          dto.setId(l.getId());
          dto.setUuid(l.getUuid());
          dto.setColaboradores(colaboradores);
          dto.setValor(l.getValorTotal());
          dto.setDocumento(documento);
          dto.setEstado(l.getEstado());
          bilhetes.add(dto);
        }
        case SEGURO_VIAGEM -> {
          var dto = new SeguroViagemResponseDTO();
          dto.setId(l.getId());
          dto.setUuid(l.getUuid());
          dto.setEntId(l.getEntId());
          dto.setNomeSeguradora(l.getNomeSeguradora());
          dto.setColaboradores(colaboradores);
          dto.setValor(l.getValorTotal());
          dto.setDocumento(documento);
          dto.setEstado(l.getEstado());
          seguros.add(dto);
        }
        case ALOJAMENTO -> {
          var dto = new AlojamentoResponseDTO();
          dto.setId(l.getId());
          dto.setUuid(l.getUuid());
          dto.setFlgAlimentacao(l.getFlgAlimentacao());
          dto.setLugarHospedagem(l.getLugarHospedagem());
          dto.setValorDiario(l.getValorDiario());
          dto.setValorTotal(l.getValorTotal());
          dto.setMoeda(l.getMoeda());
          dto.setDataInicio(l.getDataInicio());
          dto.setDataFim(l.getDataFim());
          dto.setNrDias(l.getNrDias());
          dto.setColaborador(primeiro);
          dto.setColaboradores(colaboradores);
          dto.setDocumento(documento);
          dto.setEstado(l.getEstado());
          alojamentos.add(dto);
        }
        case AJUDA_CUSTO -> {
          var dto = new AjudaCustoResponseDTO();
          dto.setId(l.getId());
          dto.setUuid(l.getUuid());
          dto.setColaborador(primeiro);
          dto.setFlgAlojamento("SIM".equalsIgnoreCase(l.getFlgAlojamento()));
          dto.setNumeroDiasAlojamento(l.getNrDias());
          dto.setValorDiario(l.getValorDiario());
          dto.setValorTotal(l.getValorTotal());
          dto.setEstado(l.getEstado());
          ajudas.add(dto);
        }
      }
    }

    var ativos = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid).stream()
        .filter(c -> ESTADO_ATIVO.equals(c.getEstado()))
        .toList();
    List<MissaoColaboradorResponseDTO> disponiveis;
    if (tipo.temPrestador()) {
      var prestadorPorColab = support.prestadorPorColaborador(processo);
      disponiveis = ativos.stream()
          .filter(c -> prestadorPorColab.containsKey(c.getId()))
          .map(c -> {
            var dto = support.toColaboradorDto(c);
            var prestador = prestadorPorColab.get(c.getId());
            dto.setMissaoPrestId(prestador.getId());
            dto.setNomePrestador(prestador.getNome());
            return dto;
          })
          .toList();
    } else {
      disponiveis = ativos.stream().map(support::toColaboradorDto).toList();
    }

    var conteudo = support.conteudoLogisticaColaborador(support.varsMissao(missao, tipo), null);
    var notificacao = new MissaoNotificacaoResponseDTO();
    notificacao.setAssunto(conteudo.assunto());
    notificacao.setCorpoEmail(conteudo.corpo());

    var response = new ProcessoLogisticaResponseDTO();
    response.setMissaoUuid(missao.getUuid());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
    response.setProcesso(support.toProcessoDto(processo));
    response.setDataInicioMissao(missao.getDataInicio());
    response.setDataFimMissao(missao.getDataFim());
    response.setBilhetesPassagem(bilhetes);
    response.setSegurosViagem(seguros);
    response.setAlojamentos(alojamentos);
    response.setAjudasCusto(ajudas);
    response.setColaboradoresDisponiveis(disponiveis);
    response.setNotificacao(notificacao);

    linhas.stream()
        .max(Comparator.comparing(MissaoLogisticaEntity::getId))
        .ifPresent(l -> {
          response.setExecutadoPor(l.getLastModifiedBy() != null ? l.getLastModifiedBy() : l.getCreatedBy());
          var data = l.getLastModifiedDate() != null ? l.getLastModifiedDate() : l.getCreatedDate();
          response.setDataExecucao(data != null ? data.toLocalDate() : null);
        });

    return ResponseEntity.ok(response);
  }

  private MissaoLogisticaDetResponseDTO toDetDto(MissaoLogisticaDetEntity d) {
    var dto = new MissaoLogisticaDetResponseDTO();
    dto.setId(d.getId());
    dto.setEstado(d.getEstado());
    var colab = d.getMissaoColabId();
    dto.setMissaoColabUuid(colab != null ? colab.getUuid() : null);
    dto.setFuncionarioUuid(colab != null && colab.getFunId() != null ? colab.getFunId().getUuid() : null);
    dto.setNomeColaborador(colab != null && colab.getFunId() != null ? colab.getFunId().getNome() : null);
    return dto;
  }

  // ---------------------------------------------------------------------------------------------
  // Etapas de parecer: Validação UGAL e Aprovação RH
  // ---------------------------------------------------------------------------------------------

  /** Validação UGAL: os três documentos do processo (autorização, requisição, fatura) e os pareceres UGAL. */
  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoValidacaoUgalResponseDTO> getValidacaoUgal(GetProcessoValidacaoUgalQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();

    var requisicaoUuids = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(r -> ESTADO_ATIVO.equals(r.getEstado()))
        .map(MissaoRequisicaoEntity::getUuid)
        .toList();
    var linhaUuids = missaoLogisticaRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado()))
        .map(MissaoLogisticaEntity::getUuid)
        .toList();

    var ugal = pareceres(processo, Set.of(ResponsavelParecer.UGAL.name()));

    var response = new ProcessoValidacaoUgalResponseDTO();
    response.setMissaoUuid(missao.getUuid());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
    response.setProcesso(support.toProcessoDto(processo));
    response.setAutorizacao(documentos(TableName.RH_T_MISSAO_SERVICO.name(), List.of(missao.getUuid()), false));
    response.setRequisicoes(documentos(REF_DOC_REQUISICAO_PDF, requisicaoUuids, true));
    response.setFaturas(documentos(TableName.RH_T_MISSAO_LOGISTICA.name(), linhaUuids, false));
    response.setParecerAtual(parecerDoCiclo(ugal));
    response.setHistorico(ugal.stream().map(this::toParecerDto).toList());
    return ResponseEntity.ok(response);
  }

  /** Aprovação RH: pareceres do Coordenador e do Director no ciclo actual, o parecer UGAL e o histórico. */
  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoAprovacaoRhResponseDTO> getAprovacaoRh(GetProcessoAprovacaoRhQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();

    var coordenador = pareceres(processo, Set.of(ResponsavelParecer.COORDENADOR_RH.name()));
    var director = pareceres(processo, Set.of(ResponsavelParecer.DIRECTOR_RH.name()));
    var ugal = pareceres(processo, Set.of(ResponsavelParecer.UGAL.name()));

    var response = new ProcessoAprovacaoRhResponseDTO();
    response.setMissaoUuid(missao.getUuid());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
    response.setProcesso(support.toProcessoDto(processo));
    response.setParecerCoordenador(parecerDoCiclo(coordenador));
    response.setParecerDirector(parecerDoCiclo(director));
    response.setParecerUgal(ugal.stream()
        .filter(d -> "A".equals(d.getEstado()))
        .findFirst()
        .map(this::toParecerDto)
        .orElse(null));
    response.setHistorico(pareceres(processo, Set.of(ResponsavelParecer.COORDENADOR_RH.name(), ResponsavelParecer.DIRECTOR_RH.name()))
        .stream()
        .map(this::toParecerDto)
        .toList());
    return ResponseEntity.ok(response);
  }

  /** Pareceres do processo para os responsáveis indicados, do mais recente para o mais antigo. */
  private List<MissaoProcessoDetEntity> pareceres(MissaoProcessoEntity processo, Set<String> responsaveis) {
    return missaoProcessoDetRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(d -> responsaveis.contains(d.getResponsavel()))
        .sorted(Comparator.comparing(MissaoProcessoDetEntity::getId).reversed())
        .toList();
  }

  /** Rascunho ou parecer emitido do ciclo actual (os anulados por devolução ficam só no histórico). */
  private ParecerResponseDTO parecerDoCiclo(List<MissaoProcessoDetEntity> pareceres) {
    return pareceres.stream()
        .filter(d -> "P".equals(d.getEstado()) || "A".equals(d.getEstado()))
        .findFirst()
        .map(this::toParecerDto)
        .orElse(null);
  }

  private ParecerResponseDTO toParecerDto(MissaoProcessoDetEntity d) {
    var dto = new ParecerResponseDTO();
    dto.setUuid(d.getUuid());
    dto.setResponsavel(d.getResponsavel());
    dto.setParecer(d.getParecer());
    dto.setParecerDesc("FAVORAVEL".equals(d.getParecer()) ? "Favorável" : "DESFAVORAVEL".equals(d.getParecer()) ? "Desfavorável" : d.getParecer());
    dto.setObservacao(d.getObservacao());
    dto.setEstado(d.getEstado());
    dto.setEstadoDesc(switch (d.getEstado()) {
      case "P" -> "Rascunho";
      case "A" -> "Emitido";
      case "I" -> "Anulado (processo devolvido à logística)";
      default -> d.getEstado();
    });
    dto.setExecutadoPor(d.getLastModifiedBy() != null ? d.getLastModifiedBy() : d.getCreatedBy());
    var data = d.getLastModifiedDate() != null ? d.getLastModifiedDate() : d.getCreatedDate();
    dto.setDataExecucao(data != null ? data.toLocalDate() : null);
    return dto;
  }

  private List<AnexoRespDTO> documentos(String referenciaName, List<UUID> referencias, boolean soAtivos) {
    return referencias.stream()
        .flatMap(uuid -> documentoRepository.findAllByReferenciaNameAndReferenciaUuid(referenciaName, uuid).stream())
        .filter(d -> soAtivos ? d.getEstado() == Estado.A : d.getEstado() != Estado.E && d.getEstado() != Estado.I)
        .sorted(Comparator.comparing(DocumentoEntity::getId))
        .map(documentoMapper::toRespDto)
        .toList();
  }

  // ---------------------------------------------------------------------------------------------
  // Avaliar Prestador
  // ---------------------------------------------------------------------------------------------

  @Transactional(readOnly = true)
  public ResponseEntity<AvaliacaoPrestadorResponseDTO> getAvaliacao(GetAvaliacaoPrestadorQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var prestador = support.prestadorDoProcesso(processo, query.getMissaoPrestUuid());

    var opcoes = support.dominioAvaliacaoFornecedor("AVALIACAO");
    var designacoes = support.dominioAvaliacaoFornecedor("DESIGNACAO");
    var pesos = support.pesosAvaliacao();
    var avaliacao = missaoPrestadorAvalRepository
        .findFirstByMissaoPrestId_IdAndEstadoOrderByIdDesc(prestador.getId(), ESTADO_ATIVO)
        .orElse(null);

    var criterios = new ArrayList<CriterioAvaliacaoResponseDTO>();
    for (var criterio : AvaliacaoPrestadorCalculo.CRITERIOS) {
      var dto = new CriterioAvaliacaoResponseDTO();
      dto.setCriterio(criterio);
      dto.setPeso(pesos.getOrDefault(criterio, 0));
      var valor = avaliacao != null ? valorDoCriterio(avaliacao, criterio) : null;
      dto.setAvaliacao(valor);
      dto.setAvaliacaoDesc(valor != null ? opcoes.get(valor) : null);
      if (valor != null) {
        try {
          dto.setPontos(AvaliacaoPrestadorCalculo.pontos(dto.getPeso(), Integer.parseInt(valor)));
        } catch (NumberFormatException ignored) {
          // valor gravado fora do domínio: sem pontos
        }
      }
      criterios.add(dto);
    }

    var response = new AvaliacaoPrestadorResponseDTO();
    response.setMissaoPrestUuid(prestador.getUuid());
    response.setNomePrestador(prestador.getNome());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(processo.getMissaoServId()));
    response.setTipoProcesso(processo.getTipoProcesso());
    response.setPodeAvaliar(missaoRequisicaoRepository.existsByMissaoPrestId_IdAndEstado(prestador.getId(), ESTADO_ATIVO));
    response.setAvaliado(avaliacao != null);
    response.setCriterios(criterios);
    response.setOpcoesAvaliacao(opcoes.entrySet().stream()
        .map(e -> new OpcaoDominioResponseDTO(e.getKey(), e.getValue()))
        .toList());
    if (avaliacao != null) {
      response.setTotal(avaliacao.getTotal());
      response.setDesignacao(avaliacao.getDesignacao());
      response.setDesignacaoDesc(designacoes.get(avaliacao.getDesignacao()));
      response.setExecutadoPor(avaliacao.getLastModifiedBy() != null ? avaliacao.getLastModifiedBy() : avaliacao.getCreatedBy());
      var data = avaliacao.getLastModifiedDate() != null ? avaliacao.getLastModifiedDate() : avaliacao.getCreatedDate();
      response.setDataExecucao(data != null ? data.toLocalDate() : null);
    }
    return ResponseEntity.ok(response);
  }

  private String valorDoCriterio(MissaoPrestadorAvalEntity a, String criterio) {
    return switch (criterio) {
      case AvaliacaoPrestadorCalculo.SISTEMA_QUALIDADE -> a.getSistemaQualidade();
      case AvaliacaoPrestadorCalculo.PRAZO_FORNECIMENTO -> a.getPrazoFornecimento();
      case AvaliacaoPrestadorCalculo.QUALIDADE_PRODUTO -> a.getQualidadeProduto();
      case AvaliacaoPrestadorCalculo.CAPACIDADE_RESPOSTA -> a.getCapacidadeResposta();
      case AvaliacaoPrestadorCalculo.PRECO -> a.getPreco();
      default -> null;
    };
  }

  // ---------------------------------------------------------------------------------------------
  // Etapas Cabimento e Autorização
  // ---------------------------------------------------------------------------------------------

  /** Linhas do processo com o estado do cabimento — alimenta os ecrãs Cabimentação e Autorização. */
  @Transactional(readOnly = true)
  public ResponseEntity<ProcessoCabimentoResponseDTO> getCabimento(GetProcessoCabimentoQuery query) {
    var missaoUuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var processo = support.processo(missaoUuid, query.getTipoProcesso(), false);
    var missao = processo.getMissaoServId();
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());

    var linhas = missaoLogisticaRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado()))
        .toList();
    var detsPorLinha = new HashMap<Long, List<MissaoLogisticaDetEntity>>();
    var ids = linhas.stream().map(MissaoLogisticaEntity::getId).toList();
    if (!ids.isEmpty()) {
      missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids).stream()
          .filter(d -> ESTADO_ATIVO.equals(d.getEstado()))
          .sorted(Comparator.comparing(MissaoLogisticaDetEntity::getId))
          .forEach(d -> detsPorLinha.computeIfAbsent(d.getMissaoLogistId().getId(), _ -> new ArrayList<>()).add(d));
    }

    var itens = new ArrayList<ProcessoCabimentoItemResponseDTO>();
    var total = java.math.BigDecimal.ZERO;
    for (var l : linhas) {
      var colaboradores = detsPorLinha.getOrDefault(l.getId(), List.of()).stream().map(this::toDetDto).toList();
      var item = new ProcessoCabimentoItemResponseDTO();
      item.setLogisticaUuid(l.getUuid());
      item.setReferencia(l.getReferencia());
      item.setNome(switch (tipo) {
        case SEGURO_VIAGEM -> l.getNomeSeguradora();
        case AJUDA_CUSTO -> colaboradores.isEmpty() ? null : colaboradores.getFirst().getNomeColaborador();
        default -> l.getPrestadorServId() != null ? l.getPrestadorServId().getNome() : null;
      });
      item.setValorTotal(l.getValorTotal());
      item.setMoeda(l.getMoeda());
      item.setCabId(l.getCabId());
      item.setEstadoCabimento(l.getEstadoCabimento());
      item.setColaboradores(colaboradores);
      item.setDocumento(documentoMaisRecente(TableName.RH_T_MISSAO_LOGISTICA.name(), l.getUuid(), false));
      itens.add(item);
      if (l.getValorTotal() != null) {
        total = total.add(l.getValorTotal());
      }
    }

    var response = new ProcessoCabimentoResponseDTO();
    response.setMissaoUuid(missao.getUuid());
    response.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
    response.setEstadoMissao(missao.getEstado());
    response.setProcesso(support.toProcessoDto(processo));
    response.setItens(itens);
    response.setValorTotal(total);
    linhas.stream()
        .max(Comparator.comparing(l -> l.getLastModifiedDate() != null ? l.getLastModifiedDate() : l.getCreatedDate(),
            Comparator.nullsFirst(Comparator.naturalOrder())))
        .ifPresent(l -> {
          response.setExecutadoPor(l.getLastModifiedBy() != null ? l.getLastModifiedBy() : l.getCreatedBy());
          var data = l.getLastModifiedDate() != null ? l.getLastModifiedDate() : l.getCreatedDate();
          response.setDataExecucao(data != null ? data.toLocalDate() : null);
        });
    return ResponseEntity.ok(response);
  }

  // ---------------------------------------------------------------------------------------------
  // Lista Etapa Missão
  // ---------------------------------------------------------------------------------------------

  /**
   * Processos activos de missões activas, filtrados por etapa e/ou tipo — o ecrã "Lista Etapa
   * Missão", de onde o utilizador abre o processo na etapa em que está.
   */
  @Transactional(readOnly = true)
  public ResponseEntity<WrapperListProcessoEtapaDTO> listarPorEtapa(GetListaProcessosEtapaQuery query) {
    var etapa = query != null && StringUtils.hasText(query.getEtapa())
        ? EtapaProcesso.fromCodeOrThrow(query.getEtapa().trim().toUpperCase())
        : null;
    var tipo = query != null && StringUtils.hasText(query.getTipoProcesso())
        ? TipoProcesso.fromCodeOrThrow(query.getTipoProcesso().trim().toUpperCase())
        : null;
    var pageNumber = parseIntOr(query != null ? query.getPageNumber() : null, 0);
    var pageSize = parseIntOr(query != null ? query.getPageSize() : null, 10);

    Specification<MissaoProcessoEntity> spec = (root, q, cb) -> {
      var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
      predicates.add(cb.equal(root.get("estado"), ESTADO_ATIVO));
      predicates.add(cb.equal(root.get("missaoServId").get("estado"), ESTADO_ATIVO));
      if (etapa != null) {
        predicates.add(cb.equal(root.get("etapa"), etapa.name()));
      }
      if (tipo != null) {
        predicates.add(cb.equal(root.get("tipoProcesso"), tipo.name()));
      }
      return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
    };

    var page = missaoProcessoRepository.findAll(spec,
        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id")));

    var wrapper = new WrapperListProcessoEtapaDTO();
    wrapper.setContent(page.getContent().stream().map(p -> {
      var missao = p.getMissaoServId();
      var dto = new ProcessoEtapaListaItemDTO();
      dto.setMissaoUuid(missao.getUuid());
      dto.setNrMissaoFormatado(support.nrMissaoFormatado(missao));
      dto.setNacionalInternacional(Integer.valueOf(1).equals(missao.getFlgDestino()) ? "Nacional"
          : Integer.valueOf(2).equals(missao.getFlgDestino()) ? "Internacional" : null);
      dto.setDestino(missao.getDescricaoDestino());
      dto.setDataInicio(missao.getDataInicio());
      dto.setDataFim(missao.getDataFim());
      dto.setProcessoUuid(p.getUuid());
      dto.setTipoProcesso(p.getTipoProcesso());
      dto.setTipoProcessoDesc(TipoProcesso.fromCodeOrThrow(p.getTipoProcesso()).getDescricao());
      dto.setEtapa(p.getEtapa());
      var e = EtapaProcesso.fromCode(p.getEtapa());
      dto.setEtapaDesc(e != null ? e.getDescricao() : p.getEtapa());
      return dto;
    }).toList());
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());
    return ResponseEntity.ok(wrapper);
  }

  private int parseIntOr(String raw, int fallback) {
    if (!StringUtils.hasText(raw))
      return fallback;
    try {
      return Math.max(0, Integer.parseInt(raw.trim()));
    } catch (NumberFormatException e) {
      return fallback;
    }
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
