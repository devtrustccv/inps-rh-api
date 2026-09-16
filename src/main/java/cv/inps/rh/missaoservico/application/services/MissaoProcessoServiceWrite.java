package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.missaoservico.application.dto.AvaliacaoPrestadorGravadaResponseDTO;
import cv.inps.rh.missaoservico.application.dto.ProcessoEtapaGravadaResponseDTO;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoPrestadoresCommand;
import cv.inps.rh.missaoservico.application.commands.SaveAvaliacaoPrestadorCommand;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoAutorizacaoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoCabimentoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoLogisticaCommand;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoParecerCommand;
import cv.inps.rh.missaoservico.application.constants.Parecer;
import cv.inps.rh.missaoservico.application.constants.ResponsavelParecer;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoRequisicoesCommand;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.AjudaCustoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.AlojamentoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.BilhetePassagemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.ProcessoLogisticaRequestDTO;
import cv.inps.rh.missaoservico.application.dto.ProcessoRequisicaoItemRequestDTO;
import cv.inps.rh.missaoservico.application.dto.SeguroViagemRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.*;

/** Escrita das etapas dos processos de missão (modelo por processo, spec 14/09). */
@RequiredArgsConstructor
@Service
public class MissaoProcessoServiceWrite {

  private static final Logger LOGGER = LoggerFactory.getLogger(MissaoProcessoServiceWrite.class);

  private static final int MAX_PRESTADORES = 3;
  private static final String TIPO_NOTIF_PEDIDO_PROPOSTA = "MISSAO_PRESTADOR";
  private static final String TIPO_NOTIF_REQUISICAO = "MISSAO_EMISSAO_REQUISICAO";
  private static final String TIPO_DOC_REQUISICAO = "EMISSAO_REQUISICAO";

  private final MissaoProcessoSupport support;
  private final ProcessoEtapaGuard guard;
  private final RequisicaoPdfService requisicaoPdfService;
  private final MissaoProcessoEntityRepository missaoProcessoRepository;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;
  private final MissaoRequisicaoColabEntityRepository missaoRequisicaoColabRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final ParamPrestadorEntityRepository paramPrestadorRepository;
  private final ParamPrestadorDetEntityRepository paramPrestadorDetRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final DocumentoMapper documentoMapper;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final EmailService emailService;
  private final StorageService storageService;
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;
  private final EntidadeEntityRepository entidadeRepository;
  private final MissaoProcessoDetEntityRepository missaoProcessoDetRepository;
  private final MissaoPrestadorAvalEntityRepository missaoPrestadorAvalRepository;
  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoNotificacaoColaborador notificacaoColaborador;

  // ---------------------------------------------------------------------------------------------
  // Etapa Prestadores Serviço
  // ---------------------------------------------------------------------------------------------

  /**
   * Selecção de 1 a 3 prestadores parametrizados para o processo. A lista enviada é a selecção
   * completa: quem sai é inactivado — excepto se já tiver requisição emitida.
   *
   * <p>NEXT envia o pedido de proposta a todos os emails activos de cada prestador e avança para
   * Emissão de Requisição. Num NEXT com o processo já adiante, só os prestadores acrescentados
   * nesta gravação são notificados — os restantes já receberam o pedido.
   */
  @Transactional
  public ResponseEntity<ProcessoEtapaGravadaResponseDTO> salvarPrestadores(SaveProcessoPrestadoresCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getProcessoprestadoresrequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var missao = processo.getMissaoServId();
    var avancar = support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, EtapaProcesso.PRESTADOR_SERVICO, avancar);

    var selecionados = resolverPrestadoresParam(dto.getPrestadores());

    var existentes = missaoPrestadorRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId());
    var porParam = new HashMap<Long, MissaoPrestadorEntity>();
    for (var e : existentes) {
      if (e.getParamPrestId() != null) {
        porParam.putIfAbsent(e.getParamPrestId().getId(), e);
      }
    }

    var toSave = new LinkedHashSet<MissaoPrestadorEntity>();
    for (var e : existentes) {
      var paramId = e.getParamPrestId() != null ? e.getParamPrestId().getId() : null;
      var mantido = paramId != null && selecionados.containsKey(paramId) && porParam.get(paramId) == e;
      if (mantido || !ESTADO_ATIVO.equals(e.getEstado()))
        continue;
      if (missaoRequisicaoRepository.existsByMissaoPrestId_IdAndEstado(e.getId(), ESTADO_ATIVO)) {
        throw IgrpResponseStatusException.badRequest(
            "O prestador " + e.getNome() + " já tem requisição emitida neste processo e não pode ser retirado");
      }
      e.setEstado(ESTADO_INATIVO);
      toSave.add(e);
    }

    var novosOuReactivados = new ArrayList<MissaoPrestadorEntity>();
    var ativos = new ArrayList<MissaoPrestadorEntity>();
    for (var param : selecionados.values()) {
      var e = porParam.get(param.getId());
      if (e == null) {
        e = new MissaoPrestadorEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setMissaoServId(missao);
        e.setMissaoProcessoId(processo);
        e.setParamPrestId(param);
        e.setEstado(ESTADO_ATIVO);
        novosOuReactivados.add(e);
      } else if (!ESTADO_ATIVO.equals(e.getEstado())) {
        e.setEstado(ESTADO_ATIVO);
        novosOuReactivados.add(e);
      }
      // Fotografia do prestador no momento da selecção (colunas do modelo antigo, ainda NOT NULL)
      e.setEntId(param.getEntId());
      e.setNome(param.getNome());
      e.setEmail(param.getEmail());
      toSave.add(e);
      ativos.add(e);
    }

    missaoPrestadorRepository.saveAll(toSave);

    if (avancar) {
      var etapaAntes = processo.getEtapa();
      guard.avancarApos(processo, EtapaProcesso.PRESTADOR_SERVICO);
      missaoProcessoRepository.save(processo);

      var aNotificar = EtapaProcesso.PRESTADOR_SERVICO.name().equals(etapaAntes) ? ativos : novosOuReactivados;
      notificarPedidoProposta(missao, processo, aNotificar, dto.getNotificacao());
    }

    return ResponseEntity.ok(new ProcessoEtapaGravadaResponseDTO(
        processo.getUuid().toString(), processo.getEtapa(), null, null));
  }

  private LinkedHashMap<Long, ParamPrestadorEntity> resolverPrestadoresParam(List<UUID> uuids) {
    var distintos = uuids == null ? List.<UUID>of() : uuids.stream().filter(Objects::nonNull).distinct().toList();
    if (distintos.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um prestador");
    }
    if (distintos.size() > MAX_PRESTADORES) {
      throw IgrpResponseStatusException.badRequest(
          "Máximo de " + MAX_PRESTADORES + " prestadores por processo");
    }

    var out = new LinkedHashMap<Long, ParamPrestadorEntity>();
    for (var uuid : distintos) {
      var param = paramPrestadorRepository.findByUuid(uuid)
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Prestador inválido: " + uuid));
      if (!ESTADO_ATIVO.equals(param.getEstado())) {
        throw IgrpResponseStatusException.badRequest("Prestador inactivo: " + param.getNome());
      }
      out.put(param.getId(), param);
    }
    return out;
  }

  private void notificarPedidoProposta(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                       List<MissaoPrestadorEntity> prestadores, MissaoNotificacaoRequestDTO editado) {
    if (prestadores.isEmpty())
      return;

    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    var conteudo = support.conteudoPedidoProposta(support.varsMissao(missao, tipo), editado);
    var extras = emailsAdicionais(prestadores);

    for (var prest : prestadores) {
      enviarAoPrestador(prest, extras, conteudo, TIPO_NOTIF_PEDIDO_PROPOSTA,
          prest.getId(), TableName.RH_T_MISSAO_PRESTADOR.name(), prest.getUuid());
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Etapa Emissão de Requisição
  // ---------------------------------------------------------------------------------------------

  private record RequisicaoPedida(MissaoPrestadorEntity prestador,
                                  List<MissaoColaboradorEntity> colaboradores,
                                  ProcessoRequisicaoItemRequestDTO item) {}

  /**
   * Uma requisição por prestador seleccionado, com os colaboradores que ele serve. O nº é
   * sequencial no ano e mantém-se nas gravações seguintes; um colaborador só pode estar numa
   * requisição de cada processo.
   *
   * <p>NEXT gera a nota de encomenda em PDF (guardada no MinIO e em RH_T_DOCUMENTO), envia-a por
   * email ao prestador e avança para a Logística. O email segue em texto: a infraestrutura de
   * correio (sipsv0.SEND_MAIL_V1) não suporta anexos.
   */
  @Transactional
  public ResponseEntity<ProcessoEtapaGravadaResponseDTO> salvarRequisicoes(SaveProcessoRequisicoesCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getProcessorequisicoesrequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var missao = processo.getMissaoServId();
    var avancar = support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, EtapaProcesso.EMISSAO_REQUISICAO, avancar);

    var pedidas = resolverRequisicoesPedidas(missaoUuid, processo, dto.getRequisicoes());
    if (avancar && pedidas.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um prestador para emitir a requisição");
    }

    var existentes = missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoProcessoId_IdOrderByIdAsc(processo.getId());
    var porPrestador = new HashMap<Long, MissaoRequisicaoEntity>();
    existentes.forEach(r -> porPrestador.putIfAbsent(r.getMissaoPrestId().getId(), r));

    var colabsPorRequisicao = new HashMap<Long, List<MissaoRequisicaoColabEntity>>();
    var idsExistentes = existentes.stream().map(MissaoRequisicaoEntity::getId).toList();
    if (!idsExistentes.isEmpty()) {
      for (var rc : missaoRequisicaoColabRepository.findAllByMissaoRequisicaoId_IdIn(idsExistentes)) {
        colabsPorRequisicao.computeIfAbsent(rc.getMissaoRequisicaoId().getId(), _ -> new ArrayList<>()).add(rc);
      }
    }

    var requisicoesToSave = new LinkedHashSet<MissaoRequisicaoEntity>();
    var colabsToSave = new ArrayList<MissaoRequisicaoColabEntity>();

    // Prestadores que deixaram de ser seleccionados: a requisição e os seus colaboradores são inactivados.
    var etapaAtual = EtapaProcesso.fromCode(processo.getEtapa());
    for (var r : existentes) {
      var prestId = r.getMissaoPrestId().getId();
      if ((pedidas.containsKey(prestId) && porPrestador.get(prestId) == r) || !ESTADO_ATIVO.equals(r.getEstado()))
        continue;
      if (etapaAtual != null && etapaAtual.ordinal() > EtapaProcesso.EMISSAO_REQUISICAO.ordinal()) {
        throw IgrpResponseStatusException.badRequest(
            "A requisição " + RequisicaoPdfService.notaEncomenda(r) + " já seguiu para a logística e não pode ser retirada");
      }
      r.setEstado(ESTADO_INATIVO);
      requisicoesToSave.add(r);
      for (var rc : colabsPorRequisicao.getOrDefault(r.getId(), List.of())) {
        if (ESTADO_ATIVO.equals(rc.getEstado())) {
          rc.setEstado(ESTADO_INATIVO);
          colabsToSave.add(rc);
        }
      }
    }

    var ano = LocalDate.now().getYear();
    Long proximoNr = null;
    var novas = new HashSet<MissaoRequisicaoEntity>();
    var ativas = new LinkedHashMap<MissaoRequisicaoEntity, RequisicaoPedida>();
    for (var pedida : pedidas.values()) {
      var r = porPrestador.get(pedida.prestador().getId());
      if (r == null) {
        r = new MissaoRequisicaoEntity();
        r.setUuid(UuidCreator.getTimeOrderedEpoch());
        r.setMissaoPrestId(pedida.prestador());
        if (proximoNr == null) {
          var max = missaoRequisicaoRepository.findMaxNrRequisacaoByAno(ano);
          proximoNr = (max != null ? max : 0L) + 1L;
        }
        r.setNrRequisacao(proximoNr++);
        r.setAno(ano);
        novas.add(r);
      }
      if (!ESTADO_ATIVO.equals(r.getEstado())) {
        r.setEstado(ESTADO_ATIVO);
        novas.add(r);
      }
      if (pedida.item().getValorTotal() != null) {
        r.setValorTotal(pedida.item().getValorTotal());
      }
      requisicoesToSave.add(r);
      ativas.put(r, pedida);
    }

    missaoRequisicaoRepository.saveAll(requisicoesToSave);

    for (var entry : ativas.entrySet()) {
      var r = entry.getKey();
      var pedida = entry.getValue();
      sincronizarColaboradores(r, pedida.colaboradores(), colabsPorRequisicao.getOrDefault(r.getId(), List.of()), colabsToSave);
      if (pedida.item().getProposta() != null) {
        sincronizarProposta(r, pedida.item().getProposta());
      }
    }
    if (!colabsToSave.isEmpty()) {
      missaoRequisicaoColabRepository.saveAll(colabsToSave);
    }

    if (avancar) {
      var etapaAntes = processo.getEtapa();
      guard.avancarApos(processo, EtapaProcesso.EMISSAO_REQUISICAO);
      missaoProcessoRepository.save(processo);
      var avancou = EtapaProcesso.EMISSAO_REQUISICAO.name().equals(etapaAntes);

      var extras = emailsAdicionais(ativas.values().stream().map(RequisicaoPedida::prestador).toList());
      for (var entry : ativas.entrySet()) {
        var r = entry.getKey();
        guardarPdfRequisicao(r);
        if (avancou || novas.contains(r)) {
          notificarRequisicao(missao, processo, r, entry.getValue(), extras);
        }
      }
    }

    return ResponseEntity.ok(new ProcessoEtapaGravadaResponseDTO(
        processo.getUuid().toString(), processo.getEtapa(), null, null));
  }

  private LinkedHashMap<Long, RequisicaoPedida> resolverRequisicoesPedidas(
      UUID missaoUuid, MissaoProcessoEntity processo, List<ProcessoRequisicaoItemRequestDTO> itens) {
    var prestadoresPorUuid = new HashMap<UUID, MissaoPrestadorEntity>();
    missaoPrestadorRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(p -> ESTADO_ATIVO.equals(p.getEstado()))
        .forEach(p -> prestadoresPorUuid.put(p.getUuid(), p));

    var pedidas = new LinkedHashMap<Long, RequisicaoPedida>();
    var prestadorDoColaborador = new HashMap<Long, String>();

    for (var item : itens == null ? List.<ProcessoRequisicaoItemRequestDTO>of() : itens) {
      if (item == null || !Boolean.TRUE.equals(item.getSelecionado()))
        continue;
      if (item.getMissaoPrestUuid() == null) {
        throw IgrpResponseStatusException.badRequest("missaoPrestUuid é obrigatório");
      }
      var prestador = prestadoresPorUuid.get(item.getMissaoPrestUuid());
      if (prestador == null) {
        throw IgrpResponseStatusException.badRequest(
            "Prestador não seleccionado neste processo: " + item.getMissaoPrestUuid());
      }
      if (pedidas.containsKey(prestador.getId())) {
        throw IgrpResponseStatusException.badRequest("Prestador repetido: " + prestador.getNome());
      }

      var funUuids = item.getFuncionarioUuids() == null
          ? List.<UUID>of()
          : item.getFuncionarioUuids().stream().filter(Objects::nonNull).distinct().toList();
      if (funUuids.isEmpty()) {
        throw IgrpResponseStatusException.badRequest(
            "Associe pelo menos um colaborador à requisição de " + prestador.getNome());
      }

      var colaboradores = new ArrayList<MissaoColaboradorEntity>();
      for (var funUuid : funUuids) {
        // Aceita o uuid do funcionário ou o do colaborador da missão — o ecrã expõe os dois.
        var colab = missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missaoUuid, funUuid)
            .or(() -> missaoColaboradorRepository.findByMissaoServId_UuidAndUuid(missaoUuid, funUuid))
            .filter(c -> ESTADO_ATIVO.equals(c.getEstado()))
            .orElseThrow(() -> IgrpResponseStatusException.badRequest("Colaborador não pertence à missão: " + funUuid));
        var outro = prestadorDoColaborador.putIfAbsent(colab.getId(), prestador.getNome());
        if (outro != null) {
          var nome = colab.getFunId() != null ? colab.getFunId().getNome() : String.valueOf(funUuid);
          throw IgrpResponseStatusException.badRequest(
              "O colaborador " + nome + " já está associado à requisição de " + outro);
        }
        colaboradores.add(colab);
      }
      pedidas.put(prestador.getId(), new RequisicaoPedida(prestador, colaboradores, item));
    }
    return pedidas;
  }

  private void sincronizarColaboradores(MissaoRequisicaoEntity requisicao, List<MissaoColaboradorEntity> desejados,
                                        List<MissaoRequisicaoColabEntity> existentes,
                                        List<MissaoRequisicaoColabEntity> toSave) {
    var idsDesejados = desejados.stream().map(MissaoColaboradorEntity::getId).collect(Collectors.toSet());
    var porColab = new HashMap<Long, MissaoRequisicaoColabEntity>();
    existentes.forEach(rc -> porColab.putIfAbsent(rc.getMissaoColabId().getId(), rc));

    for (var rc : existentes) {
      var colabId = rc.getMissaoColabId().getId();
      var estado = idsDesejados.contains(colabId) && porColab.get(colabId) == rc ? ESTADO_ATIVO : ESTADO_INATIVO;
      if (!estado.equals(rc.getEstado())) {
        rc.setEstado(estado);
        toSave.add(rc);
      }
    }
    for (var colab : desejados) {
      if (porColab.containsKey(colab.getId()))
        continue;
      var rc = new MissaoRequisicaoColabEntity();
      rc.setUuid(UuidCreator.getTimeOrderedEpoch());
      rc.setMissaoRequisicaoId(requisicao);
      rc.setMissaoColabId(colab);
      rc.setEstado(ESTADO_ATIVO);
      toSave.add(rc);
    }
  }

  /** Proposta (fatura proforma) anexada: com id actualiza, sem id cria e marca a anterior como eliminada. */
  private void sincronizarProposta(MissaoRequisicaoEntity requisicao, AnexoReqDTO proposta) {
    sincronizarAnexo(TableName.RH_T_MISSAO_REQUISICAO.name(), requisicao.getId(), requisicao.getUuid(), proposta);
  }

  /** Anexo único de um registo: com id actualiza, sem id cria e marca o anterior como eliminado. */
  private void sincronizarAnexo(String referenciaName, Long referenciaId, UUID referenciaUuid, AnexoReqDTO anexo) {
    var existentes = new ArrayList<>(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
        referenciaName, referenciaUuid));
    var sync = documentoMapper.syncDocumentos(existentes, List.of(anexo),
        referenciaName, referenciaId, referenciaUuid, 1L, null);
    sync.forEach(d -> {
      if (d.getUuid() == null)
        d.setUuid(UuidCreator.getTimeOrderedEpoch());
      if (d.getEstado() == null)
        d.setEstado(Estado.A);
    });
    documentoRepository.saveAll(sync);
  }

  /**
   * Gera o PDF da nota de encomenda, guarda-o no MinIO e regista-o; a versão anterior fica inactiva.
   *
   * <p>Nada do que corra mal aqui trava a emissão da requisição: falha a gravação do documento, fica
   * o log, e a nota de encomenda continua disponível em "Extrair Requisição", que a gera a partir
   * dos dados.
   */
  private void guardarPdfRequisicao(MissaoRequisicaoEntity requisicao) {
    try {
      var tipoDocumento = support.tipoDocumento(TIPO_DOC_REQUISICAO).orElse(null);
      if (tipoDocumento == null) {
        LOGGER.warn("Tipo de documento {} não parametrizado — PDF da requisição {} não guardado",
            TIPO_DOC_REQUISICAO, requisicao.getUuid());
        return;
      }

      var pdf = requisicaoPdfService.gerar(requisicao);
      var nomeFicheiro = "%d_%s".formatted(System.currentTimeMillis(), RequisicaoPdfService.nomeFicheiro(requisicao));
      storageService.uploadFile(pdf, nomeFicheiro, "application/pdf");

      var anteriores = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(REF_DOC_REQUISICAO_PDF, requisicao.getUuid());
      anteriores.stream().filter(d -> d.getEstado() == Estado.A).forEach(d -> d.setEstado(Estado.I));

      var doc = new DocumentoEntity();
      doc.setUuid(UuidCreator.getTimeOrderedEpoch());
      doc.setTpDocumentoId(tipoDocumento);
      doc.setUrl(nomeFicheiro);
      doc.setReferenciaName(REF_DOC_REQUISICAO_PDF);
      doc.setReferenciaId(String.valueOf(requisicao.getId()));
      doc.setReferenciaUuid(requisicao.getUuid());
      doc.setEstado(Estado.A);

      var toSave = new ArrayList<>(anteriores);
      toSave.add(doc);
      documentoRepository.saveAll(toSave);
    } catch (Exception e) {
      LOGGER.error("Falha ao gerar/guardar o PDF da requisição {} — requisição emitida sem documento",
          requisicao.getUuid(), e);
    }
  }

  private void notificarRequisicao(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                   MissaoRequisicaoEntity requisicao, RequisicaoPedida pedida,
                                   Map<Long, List<String>> extras) {
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    var vars = support.varsMissao(missao, tipo);
    vars.put("nrRequisicao", RequisicaoPdfService.notaEncomenda(requisicao));
    vars.put("colaboradores", pedida.colaboradores().stream()
        .map(c -> c.getFunId() != null ? c.getFunId().getNome() : "")
        .collect(Collectors.joining(", ")));
    vars.put("valorTotal", formatarValor(requisicao));

    enviarAoPrestador(pedida.prestador(), extras, support.conteudoRequisicao(vars), TIPO_NOTIF_REQUISICAO,
        requisicao.getId(), TableName.RH_T_MISSAO_REQUISICAO.name(), requisicao.getUuid());
  }

  private String formatarValor(MissaoRequisicaoEntity requisicao) {
    if (requisicao.getValorTotal() == null)
      return "—";
    var nf = NumberFormat.getNumberInstance(Locale.of("pt", "PT"));
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    return nf.format(requisicao.getValorTotal()) + " CVE";
  }

  // ---------------------------------------------------------------------------------------------
  // Etapa Logística
  // ---------------------------------------------------------------------------------------------

  private static final String TIPO_NOTIF_LOGISTICA_COLAB = "MISSAO_LOGISTICA_COLABORADOR";

  /** Linha de logística pedida pelo ecrã, já validada: dados a gravar, colaboradores e anexo. */
  private record LinhaLogistica(MissaoLogisticaEntity dados, List<MissaoColaboradorEntity> colaboradores, AnexoReqDTO anexo) {}

  /**
   * Logística de um processo — só a secção do seu tipo (bilhete, seguro, alojamento ou ajuda de
   * custo). A lista enviada é a secção completa; {@code null} não mexe. As linhas reaproveitam-se
   * pelo conjunto de colaboradores, pelo que ids e anexos se mantêm entre gravações. Linhas já
   * cabimentadas não podem ser removidas nem mudar de valor.
   *
   * <p>NEXT exige pelo menos uma linha, avança para Validação UGAL e avisa os colaboradores.
   */
  @Transactional
  public ResponseEntity<ProcessoEtapaGravadaResponseDTO> salvarLogistica(SaveProcessoLogisticaCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getProcessologisticarequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var missao = processo.getMissaoServId();
    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    var avancar = support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, EtapaProcesso.LOGISTICA, avancar);
    validarSeccoes(dto, tipo);

    List<LinhaLogistica> pedidas = switch (tipo) {
      case BILHETE_PASSAGEM -> dto.getBilhetesPassagem() == null ? null : linhasBilhete(missao, processo, dto.getBilhetesPassagem());
      case SEGURO_VIAGEM -> dto.getSegurosViagem() == null ? null : linhasSeguro(missao, processo, dto.getSegurosViagem());
      case ALOJAMENTO -> dto.getAlojamentos() == null ? null : linhasAlojamento(missao, processo, dto.getAlojamentos());
      case AJUDA_CUSTO -> dto.getAjudasCusto() == null ? null : linhasAjudaCusto(missao, processo, dto.getAjudasCusto());
    };
    if (pedidas != null) {
      validarColaboradoresUnicos(pedidas, tipo);
      sincronizarLogistica(processo, pedidas);
    }

    if (avancar) {
      var ativas = missaoLogisticaRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
          .filter(l -> ESTADO_ATIVO.equals(l.getEstado()))
          .toList();
      if (ativas.isEmpty()) {
        throw IgrpResponseStatusException.badRequest(
            "Registe pelo menos uma linha de " + tipo.getDescricao() + " antes de avançar");
      }
      var etapaAntes = processo.getEtapa();
      guard.avancarApos(processo, EtapaProcesso.LOGISTICA);
      missaoProcessoRepository.save(processo);
      if (EtapaProcesso.LOGISTICA.name().equals(etapaAntes)) {
        notificarColaboradoresLogistica(missao, tipo, ativas, dto.getNotificacao());
      }
    }

    return ResponseEntity.ok(new ProcessoEtapaGravadaResponseDTO(
        processo.getUuid().toString(), processo.getEtapa(), null, null));
  }

  private void validarSeccoes(ProcessoLogisticaRequestDTO dto, TipoProcesso tipo) {
    var seccoes = new LinkedHashMap<TipoProcesso, List<?>>();
    seccoes.put(TipoProcesso.BILHETE_PASSAGEM, dto.getBilhetesPassagem());
    seccoes.put(TipoProcesso.SEGURO_VIAGEM, dto.getSegurosViagem());
    seccoes.put(TipoProcesso.ALOJAMENTO, dto.getAlojamentos());
    seccoes.put(TipoProcesso.AJUDA_CUSTO, dto.getAjudasCusto());
    for (var e : seccoes.entrySet()) {
      if (e.getKey() != tipo && e.getValue() != null && !e.getValue().isEmpty()) {
        throw IgrpResponseStatusException.badRequest(
            "O processo " + tipo.name() + " só aceita a secção do seu tipo — recebida a secção de " + e.getKey().name());
      }
    }
  }

  private List<LinhaLogistica> linhasBilhete(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                             List<BilhetePassagemRequestDTO> itens) {
    var prestadorPorColab = support.prestadorPorColaborador(processo);
    var out = new ArrayList<LinhaLogistica>();
    for (var item : itens) {
      if (item == null)
        continue;
      if (item.getValor() == null) {
        throw IgrpResponseStatusException.badRequest("bilhetesPassagem: valor é obrigatório");
      }
      var colabs = colaboradoresDaLinha(missao.getUuid(), item.getColaboradorIds(), "bilhetesPassagem");
      var log = novaLinha(missao, processo);
      log.setPrestadorServId(prestadorDaLinha(colabs, prestadorPorColab, "bilhetesPassagem"));
      log.setValorTotal(item.getValor());
      log.setMoeda("CVE");
      out.add(new LinhaLogistica(log, colabs, item.getAnexo()));
    }
    return out;
  }

  private List<LinhaLogistica> linhasSeguro(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                            List<SeguroViagemRequestDTO> itens) {
    var out = new ArrayList<LinhaLogistica>();
    for (var item : itens) {
      if (item == null)
        continue;
      if (item.getEntId() == null) {
        throw IgrpResponseStatusException.badRequest("segurosViagem: entId é obrigatório");
      }
      if (item.getValor() == null) {
        throw IgrpResponseStatusException.badRequest("segurosViagem: valor é obrigatório");
      }
      var seguradora = entidadeRepository.findById(item.getEntId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("segurosViagem: seguradora inválida: " + item.getEntId()));
      var colabs = colaboradoresDaLinha(missao.getUuid(), item.getColaboradorIds(), "segurosViagem");

      var log = novaLinha(missao, processo);
      log.setEntId(seguradora.getId());
      log.setNomeSeguradora(StringUtils.hasText(item.getNomeSeguradora()) ? item.getNomeSeguradora().trim() : seguradora.getNome());
      log.setValorTotal(item.getValor());
      log.setMoeda("CVE");
      out.add(new LinhaLogistica(log, colabs, item.getAnexo()));
    }
    return out;
  }

  private List<LinhaLogistica> linhasAlojamento(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                                List<AlojamentoRequestDTO> itens) {
    var prestadorPorColab = support.prestadorPorColaborador(processo);
    var out = new ArrayList<LinhaLogistica>();
    for (var item : itens) {
      if (item == null)
        continue;
      if (!StringUtils.hasText(item.getLugarHospedagem())) {
        throw IgrpResponseStatusException.badRequest("alojamentos: lugarHospedagem é obrigatório");
      }
      var alimentacao = item.getFlgAlimentacao() != null ? item.getFlgAlimentacao().trim().toUpperCase() : null;
      if (!"SIM".equals(alimentacao) && !"NAO".equals(alimentacao)) {
        throw IgrpResponseStatusException.badRequest("alojamentos: flgAlimentacao é obrigatório (SIM ou NAO)");
      }
      if (item.getValorDiario() == null) {
        throw IgrpResponseStatusException.badRequest("alojamentos: valorDiario é obrigatório");
      }
      var inicio = item.getDataInicio() != null ? item.getDataInicio() : missao.getDataInicio();
      var fim = item.getDataFim() != null ? item.getDataFim() : missao.getDataFim();
      if (inicio == null || fim == null) {
        throw IgrpResponseStatusException.badRequest("alojamentos: dataInicio e dataFim são obrigatórias");
      }
      if (fim.isBefore(inicio)) {
        throw IgrpResponseStatusException.badRequest("alojamentos: dataFim não pode ser anterior a dataInicio");
      }
      var nrDias = (int) ChronoUnit.DAYS.between(inicio, fim) + 1;

      var funUuids = item.getColaboradorIds() != null && !item.getColaboradorIds().isEmpty()
          ? item.getColaboradorIds()
          : item.getColaboradorId() != null ? List.of(item.getColaboradorId()) : List.<UUID>of();
      var colabs = colaboradoresDaLinha(missao.getUuid(), funUuids, "alojamentos");

      var log = novaLinha(missao, processo);
      log.setPrestadorServId(prestadorDaLinha(colabs, prestadorPorColab, "alojamentos"));
      log.setLugarHospedagem(item.getLugarHospedagem().trim());
      log.setFlgAlimentacao(alimentacao);
      log.setValorDiario(item.getValorDiario());
      log.setValorTotal(item.getValorTotal() != null
          ? item.getValorTotal()
          : item.getValorDiario().multiply(BigDecimal.valueOf(nrDias)));
      log.setMoeda(StringUtils.hasText(item.getMoeda()) ? item.getMoeda().trim().toUpperCase() : "CVE");
      log.setDataInicio(inicio);
      log.setDataFim(fim);
      log.setNrDias(nrDias);
      out.add(new LinhaLogistica(log, colabs, item.getAnexo()));
    }
    return out;
  }

  private List<LinhaLogistica> linhasAjudaCusto(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                                List<AjudaCustoRequestDTO> itens) {
    var alimentacaoPorColab = alimentacaoPorColaborador(missao.getUuid());
    var out = new ArrayList<LinhaLogistica>();
    for (var item : itens) {
      if (item == null)
        continue;
      if (item.getColaboradorId() == null) {
        throw IgrpResponseStatusException.badRequest("ajudasCusto: colaboradorId é obrigatório");
      }
      if (item.getFlgAlojamento() == null) {
        throw IgrpResponseStatusException.badRequest("ajudasCusto: flgAlojamento é obrigatório");
      }
      if (item.getNumeroDiasAlojamento() == null || item.getNumeroDiasAlojamento() < 0) {
        throw IgrpResponseStatusException.badRequest("ajudasCusto: numeroDiasAlojamento é obrigatório e não pode ser negativo");
      }
      if (item.getValorDiario() == null) {
        throw IgrpResponseStatusException.badRequest("ajudasCusto: valorDiario é obrigatório");
      }
      var colab = colaboradoresDaLinha(missao.getUuid(), List.of(item.getColaboradorId()), "ajudasCusto").getFirst();
      var diario = calcularValorDiarioAjudaCusto(item.getValorDiario(), item.getFlgAlojamento(), alimentacaoPorColab.get(colab.getId()));

      var log = novaLinha(missao, processo);
      log.setFlgAlojamento(item.getFlgAlojamento() ? "SIM" : "NAO");
      log.setNrDias(item.getNumeroDiasAlojamento());
      log.setValorDiario(diario);
      log.setValorTotal(diario.multiply(BigDecimal.valueOf(item.getNumeroDiasAlojamento())));
      log.setMoeda("CVE");
      out.add(new LinhaLogistica(log, List.of(colab), null));
    }
    return out;
  }

  /**
   * Fração do valor diário base da ajuda de custo (spec): 100% com alojamento próprio ou em casa de
   * família, ⅔ se a instituição paga alojamento sem alimentação, ⅓ se paga alojamento com alimentação.
   *
   * <p>O valor base vem do cliente — a tabela de preços da ajuda de custo (por função e missão
   * nacional/internacional) não está especificada.
   */
  private BigDecimal calcularValorDiarioAjudaCusto(BigDecimal base, boolean incluiAlojamento, String flgAlimentacao) {
    if (!incluiAlojamento)
      return base;
    var fracao = "SIM".equalsIgnoreCase(flgAlimentacao) ? 1 : 2;
    return base.multiply(BigDecimal.valueOf(fracao)).divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
  }

  /** Alimentação incluída no alojamento de cada colaborador (linhas activas do processo ALOJAMENTO). */
  private Map<Long, String> alimentacaoPorColaborador(UUID missaoUuid) {
    var linhas = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missaoUuid).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado()) && TipoProcesso.ALOJAMENTO.name().equals(l.getReferencia()))
        .toList();
    var out = new HashMap<Long, String>();
    if (linhas.isEmpty())
      return out;
    var porId = new HashMap<Long, MissaoLogisticaEntity>();
    linhas.forEach(l -> porId.put(l.getId(), l));
    for (var det : missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(new ArrayList<>(porId.keySet()))) {
      if (ESTADO_ATIVO.equals(det.getEstado())) {
        out.putIfAbsent(det.getMissaoColabId().getId(), porId.get(det.getMissaoLogistId().getId()).getFlgAlimentacao());
      }
    }
    return out;
  }

  private MissaoLogisticaEntity novaLinha(MissaoServicoEntity missao, MissaoProcessoEntity processo) {
    var log = new MissaoLogisticaEntity();
    log.setUuid(UuidCreator.getTimeOrderedEpoch());
    log.setEstado(ESTADO_ATIVO);
    log.setMissaoServId(missao);
    log.setMissaoProcessoId(processo);
    log.setReferencia(processo.getTipoProcesso());
    log.setDataInicio(missao.getDataInicio());
    log.setDataFim(missao.getDataFim());
    log.setNrDias(missao.getNrDias());
    return log;
  }

  /**
   * Resolve os colaboradores de uma linha de logística.
   *
   * <p>O ecrã recebe, para cada colaborador, dois identificadores lado a lado
   * ({@code colaboradoresDisponiveis[].funUuid} e {@code .uuid}); aceitam-se ambos, para que a
   * escolha do campo errado não se traduza num 400 "não pertence à missão" difícil de diagnosticar.
   */
  private List<MissaoColaboradorEntity> colaboradoresDaLinha(UUID missaoUuid, List<UUID> colaboradorUuids, String seccao) {
    var distintos = colaboradorUuids == null
        ? List.<UUID>of()
        : colaboradorUuids.stream().filter(Objects::nonNull).distinct().toList();
    if (distintos.isEmpty()) {
      throw IgrpResponseStatusException.badRequest(seccao + ": indique pelo menos um colaborador");
    }
    return distintos.stream()
        .map(uuid -> missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missaoUuid, uuid)
            .or(() -> missaoColaboradorRepository.findByMissaoServId_UuidAndUuid(missaoUuid, uuid))
            .filter(c -> ESTADO_ATIVO.equals(c.getEstado()))
            .orElseThrow(() -> IgrpResponseStatusException.badRequest(seccao + ": colaborador não pertence à missão: " + uuid)))
        .toList();
  }

  private MissaoPrestadorEntity prestadorDaLinha(List<MissaoColaboradorEntity> colabs,
                                                 Map<Long, MissaoPrestadorEntity> prestadorPorColab, String seccao) {
    MissaoPrestadorEntity prestador = null;
    for (var colab : colabs) {
      var p = prestadorPorColab.get(colab.getId());
      if (p == null) {
        throw IgrpResponseStatusException.badRequest(
            seccao + ": o colaborador " + nomeColaborador(colab) + " não tem requisição neste processo");
      }
      if (prestador != null && !prestador.getId().equals(p.getId())) {
        throw IgrpResponseStatusException.badRequest(
            seccao + ": os colaboradores de uma linha têm de pertencer à requisição do mesmo prestador");
      }
      prestador = p;
    }
    return prestador;
  }

  private void validarColaboradoresUnicos(List<LinhaLogistica> linhas, TipoProcesso tipo) {
    var vistos = new HashSet<Long>();
    for (var linha : linhas) {
      for (var colab : linha.colaboradores()) {
        if (!vistos.add(colab.getId())) {
          throw IgrpResponseStatusException.badRequest(
              tipo.getDescricao() + ": o colaborador " + nomeColaborador(colab) + " aparece em mais do que uma linha");
        }
      }
    }
  }

  private void sincronizarLogistica(MissaoProcessoEntity processo, List<LinhaLogistica> pedidas) {
    var existentes = missaoLogisticaRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado()))
        .toList();

    var detsPorLinha = new HashMap<Long, List<MissaoLogisticaDetEntity>>();
    var idsExistentes = existentes.stream().map(MissaoLogisticaEntity::getId).toList();
    if (!idsExistentes.isEmpty()) {
      for (var det : missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(idsExistentes)) {
        if (ESTADO_ATIVO.equals(det.getEstado())) {
          detsPorLinha.computeIfAbsent(det.getMissaoLogistId().getId(), _ -> new ArrayList<>()).add(det);
        }
      }
    }

    // Linhas existentes indexadas pelo conjunto de colaboradores — a chave estável de uma linha.
    var porChave = new HashMap<String, ArrayDeque<MissaoLogisticaEntity>>();
    for (var e : existentes) {
      var chave = chaveLogistica(detsPorLinha.getOrDefault(e.getId(), List.of()).stream()
          .map(d -> d.getMissaoColabId().getId())
          .toList());
      porChave.computeIfAbsent(chave, _ -> new ArrayDeque<>()).add(e);
    }

    var destinoPorLinha = new LinkedHashMap<LinhaLogistica, MissaoLogisticaEntity>();
    var novas = new ArrayList<LinhaLogistica>();
    var toSave = new ArrayList<MissaoLogisticaEntity>();

    for (var pedida : pedidas) {
      var chave = chaveLogistica(pedida.colaboradores().stream().map(MissaoColaboradorEntity::getId).toList());
      var existente = Optional.ofNullable(porChave.get(chave)).map(ArrayDeque::poll).orElse(null);
      if (existente != null) {
        if (existente.getEstadoCabimento() != null && !mesmoValor(existente.getValorTotal(), pedida.dados().getValorTotal())) {
          throw IgrpResponseStatusException.badRequest(
              "A linha de " + existente.getReferencia() + " já foi cabimentada e o valor não pode ser alterado");
        }
        copiarDadosLogistica(pedida.dados(), existente);
        destinoPorLinha.put(pedida, existente);
        toSave.add(existente);
      } else {
        novas.add(pedida);
        destinoPorLinha.put(pedida, pedida.dados());
        toSave.add(pedida.dados());
      }
    }

    // O que sobrou deixou de ser pedido: linha, detalhes e documentos são inactivados.
    var detsToSave = new ArrayList<MissaoLogisticaDetEntity>();
    var docsToSave = new ArrayList<DocumentoEntity>();
    for (var orfa : porChave.values().stream().flatMap(Collection::stream).toList()) {
      if (orfa.getEstadoCabimento() != null) {
        throw IgrpResponseStatusException.badRequest(
            "A linha de " + orfa.getReferencia() + " já foi cabimentada e não pode ser removida");
      }
      orfa.setEstado(ESTADO_INATIVO);
      toSave.add(orfa);
      for (var det : detsPorLinha.getOrDefault(orfa.getId(), List.of())) {
        det.setEstado(ESTADO_INATIVO);
        detsToSave.add(det);
      }
      for (var doc : documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_LOGISTICA.name(), orfa.getUuid())) {
        doc.setEstado(Estado.I);
        docsToSave.add(doc);
      }
    }

    missaoLogisticaRepository.saveAll(toSave);

    // Só as linhas novas precisam de detalhes: as reaproveitadas casaram precisamente pelos colaboradores.
    for (var nova : novas) {
      for (var colab : nova.colaboradores()) {
        var det = new MissaoLogisticaDetEntity();
        det.setEstado(ESTADO_ATIVO);
        det.setMissaoLogistId(nova.dados());
        det.setMissaoColabId(colab);
        detsToSave.add(det);
      }
    }
    if (!detsToSave.isEmpty()) {
      missaoLogisticaDetRepository.saveAll(detsToSave);
    }
    if (!docsToSave.isEmpty()) {
      documentoRepository.saveAll(docsToSave);
    }

    for (var entry : destinoPorLinha.entrySet()) {
      var anexo = entry.getKey().anexo();
      var log = entry.getValue();
      if (anexo != null) {
        sincronizarAnexo(TableName.RH_T_MISSAO_LOGISTICA.name(), log.getId(), log.getUuid(), anexo);
      }
    }
  }

  private String chaveLogistica(List<Long> colaboradorIds) {
    return colaboradorIds.stream().sorted().map(String::valueOf).collect(Collectors.joining("-"));
  }

  /** Copia os dados de negócio para a linha já persistida, preservando id, uuid e cabimento. */
  private void copiarDadosLogistica(MissaoLogisticaEntity origem, MissaoLogisticaEntity destino) {
    destino.setPrestadorServId(origem.getPrestadorServId());
    destino.setNomeSeguradora(origem.getNomeSeguradora());
    destino.setEntId(origem.getEntId());
    destino.setValorTotal(origem.getValorTotal());
    destino.setMoeda(origem.getMoeda());
    destino.setLugarHospedagem(origem.getLugarHospedagem());
    destino.setFlgAlimentacao(origem.getFlgAlimentacao());
    destino.setValorDiario(origem.getValorDiario());
    destino.setDataInicio(origem.getDataInicio());
    destino.setDataFim(origem.getDataFim());
    destino.setNrDias(origem.getNrDias());
    destino.setFlgAlojamento(origem.getFlgAlojamento());
    destino.setMissaoProcessoId(origem.getMissaoProcessoId());
    destino.setEstado(ESTADO_ATIVO);
  }

  private static boolean mesmoValor(BigDecimal a, BigDecimal b) {
    return a == null ? b == null : b != null && a.compareTo(b) == 0;
  }

  private static String nomeColaborador(MissaoColaboradorEntity colab) {
    return colab.getFunId() != null ? colab.getFunId().getNome() : String.valueOf(colab.getUuid());
  }

  /**
   * Um aviso por colaborador das linhas do processo (spec: "O colaborador receberá email"). Vai por
   * email para os contactos do funcionário; sem email fica gravado "Pendente" para o portal.
   */
  private void notificarColaboradoresLogistica(MissaoServicoEntity missao, TipoProcesso tipo,
                                               List<MissaoLogisticaEntity> linhas, MissaoNotificacaoRequestDTO editado) {
    var conteudo = support.conteudoLogisticaColaborador(tipo, support.varsMissao(missao, tipo), editado);
    var colaboradores = new LinkedHashMap<Long, MissaoColaboradorEntity>();
    var ids = linhas.stream().map(MissaoLogisticaEntity::getId).toList();
    for (var det : missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(ids)) {
      if (ESTADO_ATIVO.equals(det.getEstado())) {
        colaboradores.putIfAbsent(det.getMissaoColabId().getId(), det.getMissaoColabId());
      }
    }

    colaboradores.values().forEach(colab -> notificacaoColaborador.enviar(colab, TIPO_NOTIF_LOGISTICA_COLAB, conteudo));
  }

  // ---------------------------------------------------------------------------------------------
  // Etapas de parecer: Validação UGAL e Aprovação RH
  // ---------------------------------------------------------------------------------------------

  private static final String PARECER_RASCUNHO = "P";
  private static final String PARECER_EMITIDO = "A";

  /**
   * Parecer da Validação UGAL ou da Aprovação RH (decisão D3). SAVE guarda um rascunho; NEXT emite-o,
   * e só com o processo exactamente nessa etapa. Um parecer emitido não se altera no mesmo ciclo.
   *
   * <p>Decidem o parecer da UGAL e o do Director: favorável avança (a Aprovação RH segue para
   * Cabimento); desfavorável devolve o processo à Logística e anula os pareceres do ciclo, para que a
   * nova ronda comece do zero. O parecer do Coordenador é obrigatório antes do do Director, mas não
   * é vinculativo.
   */
  @Transactional
  public ResponseEntity<ProcessoEtapaGravadaResponseDTO> salvarParecer(SaveProcessoParecerCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getParecerrequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }
    var etapa = EtapaProcesso.fromCodeOrThrow(command.getEtapa());
    if (etapa != EtapaProcesso.VALIDACAO_UGAL && etapa != EtapaProcesso.APROVACAO_RH) {
      throw IgrpResponseStatusException.badRequest("A etapa " + etapa.name() + " não tem parecer");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var avancar = support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, etapa, avancar);
    if (avancar && !etapa.name().equals(processo.getEtapa())) {
      throw IgrpResponseStatusException.badRequest(
          "O parecer só pode ser emitido com o processo na etapa " + etapa.name() + " (etapa actual: " + processo.getEtapa() + ")");
    }

    if (!StringUtils.hasText(dto.getParecer())) {
      throw IgrpResponseStatusException.badRequest("parecer é obrigatório (FAVORAVEL ou DESFAVORAVEL)");
    }
    var parecer = Parecer.fromCodeOrThrow(dto.getParecer().trim().toUpperCase());
    if (parecer == Parecer.DESFAVORAVEL && !StringUtils.hasText(dto.getObservacao())) {
      throw IgrpResponseStatusException.badRequest("A observação é obrigatória num parecer desfavorável");
    }
    if (dto.getObservacao() != null && dto.getObservacao().trim().length() > 500) {
      throw IgrpResponseStatusException.badRequest("A observação não pode ter mais de 500 caracteres");
    }
    var responsavel = responsavelDoParecer(etapa, dto.getResponsavel());

    if (avancar && responsavel == ResponsavelParecer.DIRECTOR_RH
        && missaoProcessoDetRepository.findAllByMissaoProcessoId_IdAndResponsavelAndEstadoInOrderByIdDesc(
            processo.getId(), ResponsavelParecer.COORDENADOR_RH.name(), List.of(PARECER_EMITIDO)).isEmpty()) {
      throw IgrpResponseStatusException.badRequest("O parecer do Director exige o parecer emitido do Coordenador RH");
    }

    var ciclo = missaoProcessoDetRepository.findAllByMissaoProcessoId_IdAndResponsavelAndEstadoInOrderByIdDesc(
        processo.getId(), responsavel.name(), List.of(PARECER_RASCUNHO, PARECER_EMITIDO));
    if (ciclo.stream().anyMatch(d -> PARECER_EMITIDO.equals(d.getEstado()))) {
      throw IgrpResponseStatusException.badRequest("O parecer " + responsavel.name() + " já foi emitido neste ciclo do processo");
    }
    var det = ciclo.stream().findFirst().orElseGet(() -> {
      var novo = new MissaoProcessoDetEntity();
      novo.setUuid(UuidCreator.getTimeOrderedEpoch());
      novo.setMissaoProcessoId(processo);
      novo.setResponsavel(responsavel.name());
      return novo;
    });
    det.setParecer(parecer.name());
    det.setObservacao(StringUtils.hasText(dto.getObservacao()) ? dto.getObservacao().trim() : null);
    det.setEstado(avancar ? PARECER_EMITIDO : PARECER_RASCUNHO);
    missaoProcessoDetRepository.save(det);

    if (avancar && responsavel != ResponsavelParecer.COORDENADOR_RH) {
      if (parecer == Parecer.FAVORAVEL) {
        guard.avancarApos(processo, etapa);
        if (etapa == EtapaProcesso.APROVACAO_RH) {
          gerarCabimentoAutomatico(processo);
        }
      } else {
        devolverParaLogistica(processo);
      }
      missaoProcessoRepository.save(processo);
    }

    return ResponseEntity.ok(new ProcessoEtapaGravadaResponseDTO(
        processo.getUuid().toString(), processo.getEtapa(), det.getUuid().toString(), null));
  }

  private ResponsavelParecer responsavelDoParecer(EtapaProcesso etapa, String responsavel) {
    if (etapa == EtapaProcesso.VALIDACAO_UGAL) {
      if (StringUtils.hasText(responsavel) && !ResponsavelParecer.UGAL.name().equalsIgnoreCase(responsavel.trim())) {
        throw IgrpResponseStatusException.badRequest("Na Validação UGAL o responsável é UGAL");
      }
      return ResponsavelParecer.UGAL;
    }
    if (!StringUtils.hasText(responsavel)) {
      throw IgrpResponseStatusException.badRequest("responsavel é obrigatório (COORDENADOR_RH ou DIRECTOR_RH)");
    }
    var r = responsavel.trim().toUpperCase();
    if (ResponsavelParecer.COORDENADOR_RH.name().equals(r))
      return ResponsavelParecer.COORDENADOR_RH;
    if (ResponsavelParecer.DIRECTOR_RH.name().equals(r))
      return ResponsavelParecer.DIRECTOR_RH;
    throw IgrpResponseStatusException.badRequest("responsavel inválido: " + responsavel + " (COORDENADOR_RH ou DIRECTOR_RH)");
  }

  /** Parecer desfavorável decisivo: pareceres do ciclo anulados (histórico mantido) e processo de volta à Logística. */
  private void devolverParaLogistica(MissaoProcessoEntity processo) {
    var pareceres = missaoProcessoDetRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(d -> PARECER_RASCUNHO.equals(d.getEstado()) || PARECER_EMITIDO.equals(d.getEstado()))
        .toList();
    pareceres.forEach(d -> d.setEstado(ESTADO_INATIVO));
    missaoProcessoDetRepository.saveAll(pareceres);
    guard.devolver(processo, EtapaProcesso.LOGISTICA);
  }

  /**
   * Cabimento automático pedido pela spec no fim da Aprovação RH. A integração com o SGAL continua sem
   * contrato (endpoint, payload, origem do nº de cabimento): as linhas seguem para a etapa Cabimento
   * sem CAB_ID, onde o cabimento é confirmado.
   *
   * <p>TODO: para integrar, obter do financeiro/SGAL: o endpoint de cabimento aplicável a uma linha de
   * RH_T_MISSAO_LOGISTICA; o contrato do payload (1 cabimento por tipo de serviço, individual por
   * colaborador na ajuda de custo); o campo da resposta com o CAB_ID; e a direcção (somos nós a chamar
   * o SGAL, ou é o SGAL a escrever o CAB_ID). O único precedente, {@code ProcessarSalarioApi#processarCabimento},
   * recebe {@code p_proc_sal_id} e não devolve nº de cabimento — não serve aqui.
   */
  private void gerarCabimentoAutomatico(MissaoProcessoEntity processo) {
    LOGGER.warn("Integração SGAL pendente: cabimento automático não gerado para o processo {}", processo.getUuid());
  }

  // ---------------------------------------------------------------------------------------------
  // Avaliar Prestador
  // ---------------------------------------------------------------------------------------------

  /**
   * Avaliação do prestador neste processo (spec: 5 critérios com peso, total e classe A–D). Só para
   * prestadores com requisição activa — foram eles que prestaram o serviço. Regravar actualiza a
   * avaliação existente.
   */
  @Transactional
  public ResponseEntity<AvaliacaoPrestadorGravadaResponseDTO> salvarAvaliacao(SaveAvaliacaoPrestadorCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getAvaliacaoprestadorrequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var prestador = support.prestadorDoProcesso(processo, command.getMissaoPrestUuid());
    if (!missaoRequisicaoRepository.existsByMissaoPrestId_IdAndEstado(prestador.getId(), ESTADO_ATIVO)) {
      throw IgrpResponseStatusException.badRequest(
          "Só é possível avaliar um prestador com requisição emitida neste processo");
    }

    var opcoes = support.dominioAvaliacaoFornecedor("AVALIACAO");
    var pesos = support.pesosAvaliacao();
    var escolhas = new LinkedHashMap<String, String>();
    escolhas.put(AvaliacaoPrestadorCalculo.SISTEMA_QUALIDADE, dto.getSistemaQualidade());
    escolhas.put(AvaliacaoPrestadorCalculo.PRAZO_FORNECIMENTO, dto.getPrazoFornecimento());
    escolhas.put(AvaliacaoPrestadorCalculo.QUALIDADE_PRODUTO, dto.getQualidadeProduto());
    escolhas.put(AvaliacaoPrestadorCalculo.CAPACIDADE_RESPOSTA, dto.getCapacidadeResposta());
    escolhas.put(AvaliacaoPrestadorCalculo.PRECO, dto.getPreco());

    var total = BigDecimal.ZERO;
    for (var e : escolhas.entrySet()) {
      if (!StringUtils.hasText(e.getValue())) {
        throw IgrpResponseStatusException.badRequest("Avaliação obrigatória para o critério " + e.getKey());
      }
      var valor = e.getValue().trim();
      if (!opcoes.containsKey(valor)) {
        throw IgrpResponseStatusException.badRequest(
            "Avaliação inválida para " + e.getKey() + ": " + valor + " (valores: " + String.join(", ", opcoes.keySet()) + ")");
      }
      int percentagem;
      try {
        percentagem = Integer.parseInt(valor);
      } catch (NumberFormatException ex) {
        throw IgrpResponseStatusException.badRequest("Avaliação sem valor numérico no domínio: " + valor);
      }
      e.setValue(valor);
      total = total.add(AvaliacaoPrestadorCalculo.pontos(pesos.getOrDefault(e.getKey(), 0), percentagem));
    }

    var avaliacao = missaoPrestadorAvalRepository.findFirstByMissaoPrestId_IdAndEstadoOrderByIdDesc(prestador.getId(), ESTADO_ATIVO)
        .orElseGet(() -> {
          var nova = new MissaoPrestadorAvalEntity();
          nova.setUuid(UuidCreator.getTimeOrderedEpoch());
          nova.setMissaoPrestId(prestador);
          nova.setEstado(ESTADO_ATIVO);
          return nova;
        });
    avaliacao.setSistemaQualidade(escolhas.get(AvaliacaoPrestadorCalculo.SISTEMA_QUALIDADE));
    avaliacao.setPrazoFornecimento(escolhas.get(AvaliacaoPrestadorCalculo.PRAZO_FORNECIMENTO));
    avaliacao.setQualidadeProduto(escolhas.get(AvaliacaoPrestadorCalculo.QUALIDADE_PRODUTO));
    avaliacao.setCapacidadeResposta(escolhas.get(AvaliacaoPrestadorCalculo.CAPACIDADE_RESPOSTA));
    avaliacao.setPreco(escolhas.get(AvaliacaoPrestadorCalculo.PRECO));
    avaliacao.setTotal(total);
    avaliacao.setDesignacao(AvaliacaoPrestadorCalculo.designacao(total));
    missaoPrestadorAvalRepository.save(avaliacao);

    return ResponseEntity.ok(new AvaliacaoPrestadorGravadaResponseDTO(
        avaliacao.getUuid().toString(), total, avaliacao.getDesignacao()));
  }

  // ---------------------------------------------------------------------------------------------
  // Etapas Cabimento e Autorização
  // ---------------------------------------------------------------------------------------------

  private static final String ESTADO_CABIMENTADO = "CABIMENTADO";
  private static final String ESTADO_AUTORIZADO = "AUTORIZADO";
  private static final String ESTADO_MISSAO_FINALIZADA = "FINALIZADO";

  /**
   * Cabimentação das linhas do processo. SAVE grava anexos e nº de cabimento manual; NEXT (Cabimentar)
   * marca as linhas seleccionadas como CABIMENTADO e só avança para Autorização quando todas as linhas
   * activas estão cabimentadas — não há autorização parcial.
   *
   * <p>O nº de cabimento do SGAL não é gerado (integração sem contrato); os cabimentos manuais e
   * internacionais podem enviar o {@code cabId}.
   */
  @Transactional
  public ResponseEntity<ProcessoEtapaGravadaResponseDTO> salvarCabimento(SaveProcessoCabimentoCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getProcessocabimentorequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var avancar = support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, EtapaProcesso.CABIMENTO, avancar);
    if (avancar && !EtapaProcesso.CABIMENTO.name().equals(processo.getEtapa())) {
      throw IgrpResponseStatusException.badRequest(
          "O cabimento só pode ser confirmado com o processo na etapa CABIMENTO (etapa actual: " + processo.getEtapa() + ")");
    }

    var linhas = linhasAtivas(processo);
    var porUuid = new HashMap<UUID, MissaoLogisticaEntity>();
    linhas.forEach(l -> porUuid.put(l.getUuid(), l));

    var toSave = new LinkedHashSet<MissaoLogisticaEntity>();
    for (var item : dto.getItens() == null ? List.<cv.inps.rh.missaoservico.application.dto.ProcessoCabimentoItemRequestDTO>of() : dto.getItens()) {
      if (item == null)
        continue;
      if (item.getLogisticaUuid() == null) {
        throw IgrpResponseStatusException.badRequest("logisticaUuid é obrigatório");
      }
      var linha = porUuid.get(item.getLogisticaUuid());
      if (linha == null) {
        throw IgrpResponseStatusException.badRequest("A linha " + item.getLogisticaUuid() + " não pertence a este processo");
      }
      if (item.getCabId() != null && !Objects.equals(item.getCabId(), linha.getCabId())) {
        if (ESTADO_AUTORIZADO.equals(linha.getEstadoCabimento())) {
          throw IgrpResponseStatusException.badRequest("A linha " + linha.getUuid() + " já foi autorizada e o nº de cabimento não pode mudar");
        }
        linha.setCabId(item.getCabId());
      }
      if (avancar && Boolean.TRUE.equals(item.getSelecionado()) && linha.getEstadoCabimento() == null) {
        if (linha.getCabId() == null) {
          LOGGER.warn("Integração SGAL pendente: cabimento não gerado para a linha de logística {}", linha.getUuid());
        }
        linha.setEstadoCabimento(ESTADO_CABIMENTADO);
      }
      if (item.getAnexo() != null) {
        sincronizarAnexo(TableName.RH_T_MISSAO_LOGISTICA.name(), linha.getId(), linha.getUuid(), item.getAnexo());
      }
      toSave.add(linha);
    }
    if (!toSave.isEmpty()) {
      missaoLogisticaRepository.saveAll(toSave);
    }

    if (avancar) {
      var porCabimentar = linhas.stream().filter(l -> l.getEstadoCabimento() == null).count();
      if (linhas.isEmpty() || porCabimentar > 0) {
        throw IgrpResponseStatusException.badRequest(linhas.isEmpty()
            ? "O processo não tem linhas de logística para cabimentar"
            : "Faltam cabimentar " + porCabimentar + " linha(s) do processo — seleccione todas para confirmar o cabimento");
      }
      guard.avancarApos(processo, EtapaProcesso.CABIMENTO);
      missaoProcessoRepository.save(processo);
    }

    return ResponseEntity.ok(new ProcessoEtapaGravadaResponseDTO(
        processo.getUuid().toString(), processo.getEtapa(), null, null));
  }

  /**
   * Autorização dos cabimentos do processo. NEXT põe todas as linhas cabimentadas em AUTORIZADO e o
   * processo em PAGAMENTO; quando todos os processos activos da missão lá chegam, a missão fica
   * FINALIZADO (spec). SAVE não altera nada — o ecrã não tem campos.
   */
  @Transactional
  public ResponseEntity<ProcessoEtapaGravadaResponseDTO> salvarAutorizacao(SaveProcessoAutorizacaoCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getProcessoetapaactionrequest();

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var missao = processo.getMissaoServId();
    var avancar = dto != null && support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, EtapaProcesso.AUTORIZACAO, avancar);

    if (avancar) {
      if (!EtapaProcesso.AUTORIZACAO.name().equals(processo.getEtapa())) {
        throw IgrpResponseStatusException.badRequest(
            "A autorização só pode ser confirmada com o processo na etapa AUTORIZACAO (etapa actual: " + processo.getEtapa() + ")");
      }
      var linhas = linhasAtivas(processo);
      if (linhas.stream().anyMatch(l -> l.getEstadoCabimento() == null)) {
        throw IgrpResponseStatusException.badRequest("Há linhas do processo sem cabimento");
      }
      linhas.forEach(l -> l.setEstadoCabimento(ESTADO_AUTORIZADO));
      missaoLogisticaRepository.saveAll(linhas);

      guard.avancarApos(processo, EtapaProcesso.AUTORIZACAO);
      missaoProcessoRepository.save(processo);
      finalizarMissaoSeConcluida(missao);
    }

    return ResponseEntity.ok(new ProcessoEtapaGravadaResponseDTO(
        processo.getUuid().toString(), processo.getEtapa(), null, missao.getEstado()));
  }

  private List<MissaoLogisticaEntity> linhasAtivas(MissaoProcessoEntity processo) {
    return missaoLogisticaRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId()).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado()))
        .toList();
  }

  /** Missão FINALIZADO quando todos os processos activos chegaram a PAGAMENTO (os inactivos não contam). */
  private void finalizarMissaoSeConcluida(MissaoServicoEntity missao) {
    var ativos = missaoProcessoRepository.findAllByMissaoServId_UuidOrderByIdAsc(missao.getUuid()).stream()
        .filter(p -> ESTADO_ATIVO.equals(p.getEstado()))
        .toList();
    if (!ativos.isEmpty() && ativos.stream().allMatch(p -> EtapaProcesso.PAGAMENTO.name().equals(p.getEtapa()))) {
      missao.setEstado(ESTADO_MISSAO_FINALIZADA);
      missaoServicoRepository.save(missao);
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Notificações a prestadores
  // ---------------------------------------------------------------------------------------------

  /** Emails adicionais activos (RH_T_PARAM_PRESTADOR_DET) indexados pelo id do prestador parametrizado. */
  private Map<Long, List<String>> emailsAdicionais(Collection<MissaoPrestadorEntity> prestadores) {
    var paramIds = prestadores.stream()
        .map(MissaoPrestadorEntity::getParamPrestId)
        .filter(Objects::nonNull)
        .map(ParamPrestadorEntity::getId)
        .distinct()
        .toList();
    var out = new HashMap<Long, List<String>>();
    if (paramIds.isEmpty())
      return out;
    for (var det : paramPrestadorDetRepository.findAllByParamPrestId_IdInAndEstado(paramIds, ESTADO_ATIVO)) {
      out.computeIfAbsent(det.getParamPrestId().getId(), _ -> new ArrayList<>()).add(det.getEmail());
    }
    return out;
  }

  /**
   * Um email — e uma linha em RH_T_NOTIFICACAO — por cada endereço activo do prestador (principal +
   * adicionais). Uma falha de envio não interrompe os restantes: fica gravada com estado "Erro".
   */
  private void enviarAoPrestador(MissaoPrestadorEntity prestador, Map<Long, List<String>> extrasPorParam,
                                 MissaoProcessoSupport.Conteudo conteudo, String tipoNotificacao,
                                 Long referenciaId, String referenciaName, UUID referenciaUuid) {
    var param = prestador.getParamPrestId();
    var destinos = new LinkedHashSet<String>();
    if (param != null) {
      destinos.add(param.getEmail());
      destinos.addAll(extrasPorParam.getOrDefault(param.getId(), List.of()));
    } else if (prestador.getEmail() != null) {
      destinos.add(prestador.getEmail());
    }
    var nome = param != null ? param.getNome() : prestador.getNome();

    for (var email : destinos) {
      var estado = "Enviado";
      try {
        emailService.sendEmail(email, conteudo.assunto(), MissaoProcessoSupport.corpoHtml(conteudo.corpo()));
      } catch (Exception ex) {
        LOGGER.warn("Erro ao enviar notificação {} para {}: {}", tipoNotificacao, email, ex.getMessage());
        estado = "Erro";
      }

      var n = new NotificacaoEntity();
      n.setUuid(UuidCreator.getTimeOrderedEpoch());
      n.setTipoNotificacao(tipoNotificacao);
      n.setReferenciaId(referenciaId);
      n.setReferenciaName(referenciaName);
      n.setReferenciaUuid(referenciaUuid);
      n.setAssunto(conteudo.assunto());
      n.setMessage(conteudo.corpo());
      n.setEmail(email);
      n.setNomeReceptor(nome);
      n.setDataEnvio(LocalDate.now());
      n.setEstado(estado);
      notificacaoRepository.save(n);
    }
  }
}
