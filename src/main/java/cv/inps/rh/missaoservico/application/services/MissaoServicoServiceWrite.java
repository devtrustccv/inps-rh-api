package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.*;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

  /** Ordem das etapas do processo — usada para nunca retroceder a etapa (ver avancarEtapa). */
  private static final List<String> ORDEM_ETAPAS = List.of(
      ETAPA_1, ETAPA_2, ETAPA_3, ETAPA_4, ETAPA_5, ETAPA_7);

  private static final String TIPO_NOTIF_CANCELAMENTO = "MISSAO_CANCELAMENTO";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final MissaoLogisticaEntityRepository missaoLogisticaRepository;
  private final MissaoLogisticaDetEntityRepository missaoLogisticaDetRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;
  private final MissaoProcessoEntityRepository missaoProcessoRepository;
  private final MissaoProcessoDetEntityRepository missaoProcessoDetRepository;
  private final MissaoRequisicaoColabEntityRepository missaoRequisicaoColabRepository;
  private final MissaoPrestadorAvalEntityRepository missaoPrestadorAvalRepository;
  private final GeografiaEntityRepository geografiaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final DocumentoMapper documentoMapper;
  private final EmailService emailService;
  private final MissaoProcessoSupport support;
  private final MissaoNotificacaoColaborador notificacaoColaborador;

  @Transactional
  public ResponseEntity<MissaoSubmissaoGravadaResponseDTO> submeter(SubmeterMissaoServicoCommand command) {
    var dto = command != null ? command.getMissaosubmissaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarSubmissao(dto);

    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisDestinoId());

    var ano = LocalDate.now().getYear();

    var missao = new MissaoServicoEntity();
    missao.setUuid(UuidCreator.getTimeOrderedEpoch());
    missao.setAno(ano);
    missao.setNrMissao(nextNrMissao(ano));
    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(isCaboVerde(pais) ? DESTINO_NACIONAL : DESTINO_ESTRANGEIRO);
    aplicarIlhaConcelho(missao, pais, dto);
    missao.setDescricaoDestino(dto.getDescricaoDestino());
    missao.setAmbitoMissao(dto.getAmbitoMissao());
    missao.setDataInicio(dto.getDataInicio());
    missao.setDataFim(dto.getDataFim());
    missao.setNrDias(calcularNrDias(dto.getDataInicio(), dto.getDataFim()));
    missao.setAutorizadoPor(dto.getAutorizadoPor());
    missao.setDataAutorizacao(dto.getDataAutorizacao());
    missao.setEtapa(ETAPA_1);
    missao.setEstado(StringUtils.hasText(dto.getEstado()) ? dto.getEstado() : ESTADO_ATIVO);

    missao = missaoServicoRepository.save(missao);
    sincronizarProcessos(missao, dto.getAlojamento());

    var colaboradores = persistirColaboradores(dto.getColaboradores(), missao);
    if (!colaboradores.isEmpty()) {
      missaoColaboradorRepository.saveAll(colaboradores);
    }

    persistirDocumentos(dto, missao);
    notificarConfirmacaoPedido(missao, colaboradores);

    return ResponseEntity.ok(new MissaoSubmissaoGravadaResponseDTO(
        missao.getUuid().toString(), missao.getNrMissao(), support.nrMissaoFormatado(missao)));
  }

  @Transactional
  public ResponseEntity<MissaoSubmissaoGravadaResponseDTO> salvarSubmissao(SaveSubmissaoServicoCommand command) {
    var uuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaosubmissaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarSubmissao(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(uuid);
    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisDestinoId());
    var antes = DadosAviso.de(missao);
    var colabsAtivosAntes = missaoColaboradorRepository.findAllByMissaoServId_Uuid(uuid).stream()
        .filter(c -> ESTADO_ATIVO.equals(c.getEstado()))
        .map(MissaoColaboradorEntity::getId)
        .collect(java.util.stream.Collectors.toSet());

    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(isCaboVerde(pais) ? DESTINO_NACIONAL : DESTINO_ESTRANGEIRO);
    aplicarIlhaConcelho(missao, pais, dto);
    missao.setDescricaoDestino(dto.getDescricaoDestino());
    missao.setAmbitoMissao(dto.getAmbitoMissao());
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
    sincronizarProcessos(missao, dto.getAlojamento());

    var colaboradores = syncColaboradores(missao, dto.getColaboradores());
    if (!colaboradores.isEmpty()) {
      missaoColaboradorRepository.saveAll(colaboradores);
    }

    persistirDocumentos(dto, missao);

    // Colaborador acrescentado (ou reactivado) recebe a confirmação, que já leva os dados novos;
    // os que já estavam recebem o aviso de alteração, se destino ou datas mudaram.
    var ativos = colaboradores.stream().filter(c -> ESTADO_ATIVO.equals(c.getEstado())).toList();
    notificarConfirmacaoPedido(missao, ativos.stream().filter(c -> !colabsAtivosAntes.contains(c.getId())).toList());
    var alteracoes = antes.diferencas(DadosAviso.de(missao));
    if (!alteracoes.isEmpty() && ESTADO_ATIVO.equals(missao.getEstado())) {
      notificarAlteracao(missao, alteracoes,
          ativos.stream().filter(c -> colabsAtivosAntes.contains(c.getId())).toList());
    }

    return ResponseEntity.ok(new MissaoSubmissaoGravadaResponseDTO(
        missao.getUuid().toString(), missao.getNrMissao(), support.nrMissaoFormatado(missao)));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> salvarPagamento(SaveMissaoServicoPagamentoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaopagamentorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarPagamento(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);
    // Registo do pagamento pelo financeiro: no modelo por processo só depois de todos os processos
    // activos autorizados (missão FINALIZADO) — a etapa da missão já não avança até PAGAMENTO.
    if (!"FINALIZADO".equals(missao.getEstado())) {
      throw IgrpResponseStatusException.badRequest(
          "O pagamento só pode ser registado com a missão finalizada (todos os processos autorizados)");
    }

    // A informação ao colaborador segue só no primeiro registo: uma correção da referência ou da
    // data não é um novo pagamento.
    var primeiroRegisto = missao.getReferenciaPagamento() == null && missao.getDataPagamento() == null;
    missao.setReferenciaPagamento(dto.getReferenciaPagamento());
    missao.setDataPagamento(dto.getDataPagamento());
    missaoServicoRepository.save(missao);
    if (primeiroRegisto) {
      notificarAjudaCusto(missao);
    }

    return ResponseEntity.ok(sucesso(missao, "Pagamento registado"));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> cancelar(CancelarMissaoServicoCommand command) {
    var missaoUuid = parseUuid(command != null ? command.getId() : null, "id");
    var dto = command != null ? command.getMissaocancelarrequest() : null;

    // O motivo fica no registo e segue nas notificações de cancelamento — sem ele perde-se o rasto.
    if (dto == null || !StringUtils.hasText(dto.getMotivoCancelamento())) {
      throw IgrpResponseStatusException.badRequest("motivoCancelamento é obrigatório");
    }

    var missao = missaoServicoRepository.findByUuidOrThrow(missaoUuid);
    if (ESTADO_INATIVO.equals(missao.getEstado())) {
      throw IgrpResponseStatusException.badRequest("A missão já está cancelada");
    }
    if ("FINALIZADO".equals(missao.getEstado())) {
      throw IgrpResponseStatusException.badRequest("A missão está finalizada e não pode ser cancelada");
    }
    // Decide-se antes de inactivar: depende da etapa dos processos, não do seu estado
    var notificar = deveNotificarCancelamento(missao);

    missao.setMotivoCancelamento(dto != null ? dto.getMotivoCancelamento() : null);
    missao.setEstado(ESTADO_INATIVO);
    missaoServicoRepository.save(missao);

    var colaboradores = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missaoUuid);
    // Só é avisado quem estava na missão: um colaborador já retirado não tem de saber do cancelamento.
    var colaboradoresAtivos = colaboradores.stream().filter(c -> ESTADO_ATIVO.equals(c.getEstado())).toList();
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

      var colabsRequisicao = missaoRequisicaoColabRepository.findAllByMissaoRequisicaoId_IdIn(
          requisicoes.stream().map(MissaoRequisicaoEntity::getId).toList());
      colabsRequisicao.forEach(rc -> rc.setEstado(ESTADO_INATIVO));
      missaoRequisicaoColabRepository.saveAll(colabsRequisicao);
    }

    // Modelo por processo (spec 14/09): pareceres, avaliações e os próprios processos
    var pareceres = missaoProcessoDetRepository.findAllByMissaoProcessoId_MissaoServId_Uuid(missaoUuid);
    pareceres.forEach(d -> d.setEstado(ESTADO_INATIVO));
    missaoProcessoDetRepository.saveAll(pareceres);

    var avaliacoes = missaoPrestadorAvalRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missaoUuid);
    avaliacoes.forEach(a -> a.setEstado(ESTADO_INATIVO));
    missaoPrestadorAvalRepository.saveAll(avaliacoes);

    var processos = missaoProcessoRepository.findAllByMissaoServId_UuidOrderByIdAsc(missaoUuid);
    processos.forEach(p -> p.setEstado(ESTADO_INATIVO));
    missaoProcessoRepository.saveAll(processos);

    var documentos = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_MISSAO_SERVICO.name(),
        missaoUuid);
    if (!CollectionUtils.isEmpty(documentos)) {
      documentos.forEach(d -> d.setEstado(Estado.I));
      documentoRepository.saveAll(documentos);
    }

    if (notificar) {
      persistirNotificacaoCancelamento(missao, dto, colaboradoresAtivos);
    }

    return ResponseEntity.ok(sucesso(missao, "Missão cancelada"));
  }

  private static SuccessResponseDTO sucesso(MissaoServicoEntity missao, String mensagem) {
    return new SuccessResponseDTO(true, missao.getUuid().toString(), mensagem, new ArrayList<>());
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

  private void validarSubmissao(MissaoSubmissaoRequestDTO dto) {
    if (dto.getPaisDestinoId() == null) {
      throw IgrpResponseStatusException.badRequest("paisDestinoId é obrigatório");
    }
    if (!StringUtils.hasText(dto.getDescricaoDestino())) {
      throw IgrpResponseStatusException.badRequest("descricaoDestino é obrigatório");
    }
    // Obrigatório no ecrã "Registo de Missão" (Âmbito da Missão), e é o que justifica a deslocação.
    if (!StringUtils.hasText(dto.getAmbitoMissao())) {
      throw IgrpResponseStatusException.badRequest("ambitoMissao é obrigatório");
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

  private boolean isNext(ProcessStepAction action) {
    return action != null && ACTION_NEXT.equals(action.getCode());
  }

  /** Avança a etapa da missão para {@code etapaAlvo}, nunca retrocedendo. */
  private void avancarEtapa(MissaoServicoEntity missao, String etapaAlvo) {
    var atual = ORDEM_ETAPAS.indexOf(missao.getEtapa());
    var alvo = ORDEM_ETAPAS.indexOf(etapaAlvo);
    if (alvo > atual) {
      missao.setEtapa(etapaAlvo);
    }
  }

  /**
   * Próximo nº de missão do ano indicado. A numeração é sequencial dentro do ano e reinicia a 1
   * em cada ano civil — apresentada como "nr/ano".
   *
   * <p>O índice único (ANO, NR_MISSAO) garante que duas criações simultâneas não ficam com o mesmo
   * número: a segunda falha na gravação em vez de duplicar.
   */
  private Long nextNrMissao(Integer ano) {
    var max = missaoServicoRepository.findMaxNrMissaoByAno(ano);
    return (max != null ? max : 0L) + 1L;
  }

  /**
   * Garante os quatro processos da missão — a spec manda registar por defeito 4 linhas em
   * RH_T_MISSAO_PROCESSO ao gravar a submissão — e aplica o campo Alojamento ao estado do processo
   * ALOJAMENTO. Idempotente: só cria os que faltam. {@code alojamento} null não mexe (na criação,
   * o processo fica activo).
   *
   * <p>Retirar o alojamento só é permitido enquanto o processo não saiu da primeira etapa — depois
   * disso já há prestadores, requisição ou logística associados a ele.
   */
  private void sincronizarProcessos(MissaoServicoEntity missao, Boolean alojamento) {
    var porTipo = new HashMap<String, MissaoProcessoEntity>();
    missaoProcessoRepository.findAllByMissaoServId_UuidOrderByIdAsc(missao.getUuid())
        .forEach(p -> porTipo.put(p.getTipoProcesso(), p));

    var toSave = new ArrayList<MissaoProcessoEntity>();
    for (var tipo : TipoProcesso.values()) {
      var processo = porTipo.get(tipo.name());
      var ehAlojamento = tipo == TipoProcesso.ALOJAMENTO;

      if (processo == null) {
        processo = new MissaoProcessoEntity();
        processo.setUuid(UuidCreator.getTimeOrderedEpoch());
        processo.setMissaoServId(missao);
        processo.setTipoProcesso(tipo.name());
        processo.setEtapa(tipo.primeiraEtapa().name());
        processo.setEstado(ehAlojamento && Boolean.FALSE.equals(alojamento) ? ESTADO_INATIVO : ESTADO_ATIVO);
        toSave.add(processo);
        continue;
      }

      if (ehAlojamento && alojamento != null) {
        var estado = alojamento ? ESTADO_ATIVO : ESTADO_INATIVO;
        if (estado.equals(processo.getEstado()))
          continue;
        if (!alojamento && !tipo.primeiraEtapa().name().equals(processo.getEtapa())) {
          throw IgrpResponseStatusException.badRequest(
              "Não é possível retirar o alojamento: o processo ALOJAMENTO já está na etapa " + processo.getEtapa());
        }
        processo.setEstado(estado);
        toSave.add(processo);
      }
    }

    if (!toSave.isEmpty()) {
      missaoProcessoRepository.saveAll(toSave);
    }
  }

  /** Ilha e concelho só se aplicam a missões nacionais (spec: só aparecem se o destino for Cabo Verde). */
  private void aplicarIlhaConcelho(MissaoServicoEntity missao, GeografiaEntity pais, MissaoSubmissaoRequestDTO dto) {
    if (!isCaboVerde(pais)) {
      missao.setIlhaId(null);
      missao.setConcelhoId(null);
      return;
    }
    missao.setIlhaId(dto.getIlhaId() != null ? geografiaRepository.findByIdOrThrow(dto.getIlhaId()) : null);
    missao.setConcelhoId(dto.getConcelhoId() != null ? geografiaRepository.findByIdOrThrow(dto.getConcelhoId()) : null);
  }

  /**
   * Nº de documento a gravar na missão: o que o utilizador escreveu no ecrã, ou o do funcionário
   * quando o campo vem vazio (a spec descreve-o como "preenchido automaticamente"). É um snapshot
   * — permite registar o documento usado naquela missão, ex.: passaporte quando o cadastro tem BI.
   */
  private String numDocumentoOuDoFuncionario(MissaoColaboradorRequestDTO dto, FuncionarioEntity fun) {
    if (dto != null && StringUtils.hasText(dto.getNumeroDocumento())) {
      return dto.getNumeroDocumento().trim();
    }
    return fun != null ? fun.getNumDocumento() : null;
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
      e.setNumDocumento(numDocumentoOuDoFuncionario(c, fun));
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
          e.setNumDocumento(numDocumentoOuDoFuncionario(dto, fun));
          toSave.add(e);
        } else if (e != null) {
          e.setEstado(ESTADO_INATIVO);
          toSave.add(e);
        }
      }
    }

    for (var entry : incoming.entrySet()) {
      var fun = funcionarioRepository.findByUuidOrThrow(entry.getKey());
      var e = new MissaoColaboradorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setFunId(fun);
      e.setMissaoServId(missao);
      e.setNumDocumento(numDocumentoOuDoFuncionario(entry.getValue(), fun));
      toSave.add(e);
    }

    return toSave;
  }

  /**
   * O cancelamento é notificado quando a missão já saiu da análise (spec): algum processo passou a
   * primeira etapa do seu percurso — houve pedidos de proposta, requisições ou avisos de logística.
   * Missões sem processos (modelo antigo) usam a etapa da missão.
   */
  private boolean deveNotificarCancelamento(MissaoServicoEntity missao) {
    if (missao == null)
      return false;
    var processos = missaoProcessoRepository.findAllByMissaoServId_UuidOrderByIdAsc(missao.getUuid());
    if (!processos.isEmpty()) {
      return processos.stream().anyMatch(p ->
          !TipoProcesso.fromCodeOrThrow(p.getTipoProcesso()).primeiraEtapa().name().equals(p.getEtapa()));
    }
    if (!StringUtils.hasText(missao.getEtapa()))
      return false;
    return !ETAPA_1.equals(missao.getEtapa()) && !ETAPA_2.equals(missao.getEtapa());
  }

  private void persistirNotificacaoCancelamento(MissaoServicoEntity missao, MissaoCancelarRequestDTO dto,
                                                List<MissaoColaboradorEntity> colaboradores) {
    String assunto = "Cancelamento de Missão Nº " + support.nrMissaoFormatado(missao);
    String message = buildMensagemCancelamento(missao, dto);

    var toSave = new ArrayList<NotificacaoEntity>();

    // 1. Prestadores e outros destinatários externos já contactados — por email
    destinatariosExternos(missao).forEach((email, nome) ->
        toSave.add(notificacaoExterna(missao, TIPO_NOTIF_CANCELAMENTO, email, nome, assunto, message)));

    if (!toSave.isEmpty()) {
      notificacaoRepository.saveAll(toSave);
    }

    // 2. Colaboradores que estavam na missão — por email (sem email fica "Pendente" para o portal)
    var conteudo = new MissaoProcessoSupport.Conteudo(assunto, message);
    colaboradores.forEach(c -> notificacaoColaborador.enviar(c, TIPO_NOTIF_CANCELAMENTO, conteudo));
  }

  /**
   * Destinatários externos da missão (email → nome), sem repetidos: os prestadores da missão e quem
   * mais recebeu notificações com referência à missão, a um prestador ou a uma requisição (no modelo
   * por processo, um registo por email do prestador, incluindo os adicionais).
   */
  private Map<String, String> destinatariosExternos(MissaoServicoEntity missao) {
    var prestadores = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missao.getUuid());

    var anteriores = new ArrayList<>(notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getUuid()));
    for (var prest : prestadores) {
      if (prest != null && prest.getUuid() != null) {
        anteriores.addAll(notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_MISSAO_PRESTADOR.name(), prest.getUuid()));
      }
    }
    for (var req : missaoRequisicaoRepository.findAllByMissaoPrestId_MissaoServId_Uuid(missao.getUuid())) {
      if (req != null && req.getUuid() != null) {
        anteriores.addAll(notificacaoRepository.findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_MISSAO_REQUISICAO.name(), req.getUuid()));
      }
    }

    var seen = new HashSet<String>();
    var out = new LinkedHashMap<String, String>();
    for (var prest : prestadores) {
      if (prest != null && StringUtils.hasText(prest.getEmail()) && seen.add(prest.getEmail().trim().toLowerCase())) {
        out.put(prest.getEmail(), prest.getNome());
      }
    }
    for (var n0 : anteriores) {
      var email = n0 != null ? n0.getEmail() : null;
      if (StringUtils.hasText(email) && seen.add(email.trim().toLowerCase())) {
        out.put(email, n0.getNomeReceptor());
      }
    }
    return out;
  }

  /** Envia um email a um destinatário externo e devolve o registo (com referência à missão) por gravar. */
  private NotificacaoEntity notificacaoExterna(MissaoServicoEntity missao, String tipoNotificacao,
                                               String email, String nome, String assunto, String message) {
    String estado = "Enviado";
    try {
      emailService.sendEmail(email, assunto, message);
    } catch (Exception e) {
      LOGGER.warn("Erro ao enviar notificação {} para {}: {}", tipoNotificacao, email, e.getMessage());
      estado = "Erro";
    }

    var n = new NotificacaoEntity();
    n.setUuid(UuidCreator.getTimeOrderedEpoch());
    n.setTipoNotificacao(tipoNotificacao);
    n.setReferenciaId(missao.getId());
    n.setReferenciaName(TableName.RH_T_MISSAO_SERVICO.name());
    n.setReferenciaUuid(missao.getUuid());
    n.setAssunto(assunto);
    n.setMessage(message);
    n.setEmail(email);
    n.setNomeReceptor(nome);
    n.setDataEnvio(LocalDate.now());
    n.setEstado(estado);
    return n;
  }

  // ---------------------------------------------------------------------------------------------
  // Notificações ao colaborador e aviso de alteração
  // ---------------------------------------------------------------------------------------------

  /** Confirmação do pedido (spec: "após submissão e autorização") a cada colaborador indicado. */
  private void notificarConfirmacaoPedido(MissaoServicoEntity missao, List<MissaoColaboradorEntity> colaboradores) {
    if (colaboradores.isEmpty())
      return;
    var conteudo = support.conteudoConfirmacaoPedido(support.varsMissao(missao));
    colaboradores.forEach(c ->
        notificacaoColaborador.enviar(c, MissaoNotificacaoColaborador.TIPO_CONFIRMACAO_PEDIDO, conteudo));
  }

  /**
   * Informação sobre a ajuda de custo (spec: "após pagamento efetuado"): a cada colaborador com linha
   * activa de ajuda de custo, com os valores dessa linha.
   */
  private void notificarAjudaCusto(MissaoServicoEntity missao) {
    var linhas = missaoLogisticaRepository.findAllByMissaoServId_Uuid(missao.getUuid()).stream()
        .filter(l -> ESTADO_ATIVO.equals(l.getEstado())
            && TipoProcesso.AJUDA_CUSTO.name().equals(l.getReferencia())
            && l.getMissaoProcessoId() != null
            && ESTADO_ATIVO.equals(l.getMissaoProcessoId().getEstado()))
        .toList();
    if (linhas.isEmpty())
      return;

    var linhaPorId = new HashMap<Long, MissaoLogisticaEntity>();
    linhas.forEach(l -> linhaPorId.put(l.getId(), l));
    for (var det : missaoLogisticaDetRepository.findAllByMissaoLogistId_IdIn(List.copyOf(linhaPorId.keySet()))) {
      if (!ESTADO_ATIVO.equals(det.getEstado()) || det.getMissaoColabId() == null)
        continue;
      var linha = linhaPorId.get(det.getMissaoLogistId().getId());
      var vars = support.varsMissao(missao);
      vars.put("valorDiario", valorOuVazio(linha.getValorDiario()));
      vars.put("nrDiasAjuda", linha.getNrDias() != null ? String.valueOf(linha.getNrDias()) : "");
      vars.put("valorTotal", valorOuVazio(linha.getValorTotal()));
      vars.put("referenciaPagamento", missao.getReferenciaPagamento());
      vars.put("dataPagamento", missao.getDataPagamento() != null ? missao.getDataPagamento().toString() : "");
      notificacaoColaborador.enviar(det.getMissaoColabId(), MissaoNotificacaoColaborador.TIPO_AJUDA_CUSTO,
          support.conteudoAjudaCusto(vars));
    }
  }

  private static String valorOuVazio(java.math.BigDecimal valor) {
    return valor != null ? valor.toPlainString() + " CVE" : "";
  }

  /**
   * Aviso de alteração (spec: "a todos os envolvidos"): aos colaboradores que já estavam na missão e,
   * se a missão já saiu da análise, aos prestadores e restantes destinatários externos contactados.
   */
  private void notificarAlteracao(MissaoServicoEntity missao, List<String> alteracoes,
                                  List<MissaoColaboradorEntity> colaboradores) {
    var vars = support.varsMissao(missao);
    vars.put("alteracoes", String.join("\n", alteracoes));
    var conteudo = support.conteudoAlteracao(vars);

    colaboradores.forEach(c ->
        notificacaoColaborador.enviar(c, MissaoNotificacaoColaborador.TIPO_ALTERACAO, conteudo));

    if (deveNotificarCancelamento(missao)) {
      var externas = new ArrayList<NotificacaoEntity>();
      destinatariosExternos(missao).forEach((email, nome) -> externas.add(notificacaoExterna(
          missao, MissaoNotificacaoColaborador.TIPO_ALTERACAO, email, nome, conteudo.assunto(), conteudo.corpo())));
      notificacaoRepository.saveAll(externas);
    }
  }

  /** Dados da missão que, quando mudam, obrigam a avisar os envolvidos. */
  private record DadosAviso(String pais, String ilha, String concelho, String destino,
                            LocalDate dataInicio, LocalDate dataFim) {

    static DadosAviso de(MissaoServicoEntity m) {
      return new DadosAviso(
          m.getPaisDestinoId() != null ? m.getPaisDestinoId().getNome() : null,
          m.getIlhaId() != null ? m.getIlhaId().getNome() : null,
          m.getConcelhoId() != null ? m.getConcelhoId().getNome() : null,
          m.getDescricaoDestino(), m.getDataInicio(), m.getDataFim());
    }

    /** Uma linha por campo alterado, no formato "- Campo: novo (antes: antigo)". */
    List<String> diferencas(DadosAviso novo) {
      var out = new ArrayList<String>();
      linha(out, "País de destino", pais, novo.pais);
      linha(out, "Ilha", ilha, novo.ilha);
      linha(out, "Concelho", concelho, novo.concelho);
      linha(out, "Destino", destino, novo.destino);
      linha(out, "Data de início", dataInicio, novo.dataInicio);
      linha(out, "Data de fim", dataFim, novo.dataFim);
      return out;
    }

    private static void linha(List<String> out, String campo, Object antes, Object depois) {
      if (!Objects.equals(antes, depois)) {
        out.add("- " + campo + ": " + (depois != null ? depois : "—") + " (antes: " + (antes != null ? antes : "—") + ")");
      }
    }
  }

  private String buildMensagemCancelamento(MissaoServicoEntity missao, MissaoCancelarRequestDTO dto) {
    var motivo = dto != null ? dto.getMotivoCancelamento() : null;
    if (StringUtils.hasText(motivo)) {
      return "A missão Nº " + support.nrMissaoFormatado(missao) + " foi cancelada. Motivo: " + motivo;
    }
    return "A missão Nº " + support.nrMissaoFormatado(missao) + " foi cancelada.";
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
