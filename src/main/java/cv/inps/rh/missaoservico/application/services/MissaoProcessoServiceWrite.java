package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoPrestadoresCommand;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoRequisicoesCommand;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.ProcessoRequisicaoItemRequestDTO;
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

import java.text.NumberFormat;
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
  public ResponseEntity<Map<String, ?>> salvarPrestadores(SaveProcessoPrestadoresCommand command) {
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

    return ResponseEntity.ok(Map.of(
        "id", processo.getUuid().toString(),
        "etapa", processo.getEtapa()));
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
  public ResponseEntity<Map<String, ?>> salvarRequisicoes(SaveProcessoRequisicoesCommand command) {
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

    return ResponseEntity.ok(Map.of(
        "id", processo.getUuid().toString(),
        "etapa", processo.getEtapa()));
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
        var colab = missaoColaboradorRepository.findByMissaoServId_UuidAndFunId_Uuid(missaoUuid, funUuid)
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
    var existentes = new ArrayList<>(documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_REQUISICAO.name(), requisicao.getUuid()));
    var sync = documentoMapper.syncDocumentos(existentes, List.of(proposta),
        TableName.RH_T_MISSAO_REQUISICAO.name(), requisicao.getId(), requisicao.getUuid(), 1L, null);
    sync.forEach(d -> {
      if (d.getUuid() == null)
        d.setUuid(UuidCreator.getTimeOrderedEpoch());
      if (d.getEstado() == null)
        d.setEstado(Estado.A);
    });
    documentoRepository.saveAll(sync);
  }

  /** Gera o PDF da nota de encomenda, guarda-o no MinIO e regista-o; a versão anterior fica inactiva. */
  private void guardarPdfRequisicao(MissaoRequisicaoEntity requisicao) {
    var tipoDocumento = support.tipoDocumento(TIPO_DOC_REQUISICAO).orElse(null);
    if (tipoDocumento == null) {
      LOGGER.warn("Tipo de documento {} não parametrizado — PDF da requisição {} não guardado",
          TIPO_DOC_REQUISICAO, requisicao.getUuid());
      return;
    }

    var pdf = requisicaoPdfService.gerar(requisicao);
    var nomeFicheiro = "%d_%s".formatted(System.currentTimeMillis(), RequisicaoPdfService.nomeFicheiro(requisicao));
    try {
      storageService.uploadFile(pdf, nomeFicheiro, "application/pdf");
    } catch (Exception e) {
      LOGGER.error("Falha ao guardar o PDF da requisição {} no storage", requisicao.getUuid(), e);
      throw IgrpResponseStatusException.internalServerError("Não foi possível guardar o documento da requisição");
    }

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
        emailService.sendEmail(email, conteudo.assunto(), conteudo.corpo());
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
