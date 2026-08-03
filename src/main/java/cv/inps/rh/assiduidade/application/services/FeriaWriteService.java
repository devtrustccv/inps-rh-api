package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.AlterarPedidoFeriaCommand;
import cv.inps.rh.assiduidade.application.commands.MarcarFeriaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarPedidoFeriaCommand;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.application.enums.SituacaoFalta;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.service.NotificacaoDispatchService;
import cv.inps.rh.assiduidade.application.commands.EnviarDireitoFeriasCommand;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeriaWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeriaWriteService.class);

  /** RH_T_FERIAS_GOZADAS.TIPO_ALTERACAO — alteração da data do gozo. */
  private static final String TIPO_ALTERACAO_DATA = "ALTERACAO_DATA";

  private final FeriasGozadasEntityRepository feriasGozadasRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final AnoEntityRepository anoEntityRepository;
  private final SubstituicaoEntityRepository substituicaoRepository;
  private final AusenciaEntityRepository ausenciaRepository;
  private final ParamSituacaoEntityRepository paramSituacaoRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final TipoDocumentoEntityRepository tipoDocumentoEntityRepository;
  private final EmailService emailService;
  private final ResponsavelEntityRepository responsavelEntityRepository;
  private final SaldoFeriaService saldoFeriaService;
  private final DocumentoMapper documentoMapper;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final NotificacaoDispatchService notificacaoDispatchService;


  @Transactional
  public Map<String, ?> marcarFeria(MarcarFeriaCommand command) {
    var req = command.getPedidoferiareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de férias ausentes");
    validatePedido(req);

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());


    boolean temSubstituicao = req.getSubstituidoPor() != null;

    // Regra: substituição só permitida para férias >= 15 dias
    if (temSubstituicao && req.getNumDias() != null && req.getNumDias() < 15)
      throw IgrpResponseStatusException.badRequest("Substituição só aplicável a férias com duração igual ou superior a 15 dias");

    // Regra: substituição só permitida se colaborador ocupa um cargo (cargoId != null)
    if (temSubstituicao && tipoRelAtual.getCargoId() == null)
      throw IgrpResponseStatusException.badRequest("Substituição não aplicável: colaborador não ocupa nenhum cargo");

    TiposRelacionamentoEntity tipoRelAtualFuncSubstituto = null;
    if (temSubstituicao) {
      var funcionarioSubstituto = funcionarioRepository.findByUuidOrThrow(req.getSubstituidoPor());
      tipoRelAtualFuncSubstituto = funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid());
      var s = new SubstituicaoEntity();
      s.setSubstituidoTiprelId(tipoRelAtual);
      s.setSubstitutoTiprelId(tipoRelAtualFuncSubstituto);
      s.setDataInicio(req.getDataInicio());
      s.setDataFim(req.getDataFim());
      s.setEstado(Estado.P);
      s.setUuid(UuidCreator.getTimeOrderedEpoch());
      substituicaoRepository.save(s);
    }

    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("FERIA");
    pedido.setOrigem("ASSIDUIDADE");
    // Regra: só vai para validação (DESPACHO_RH) se tiver substituição
    pedido.setEtapa(temSubstituicao ? "DESPACHO_RH" : "FINALIZADO");
    pedido.setEstado(temSubstituicao ? Estado.P.name() : Estado.A.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    var ferias = new FeriasGozadasEntity();
    ferias.setPedidoId(pedido);
    ferias.setFunId(funcionario);
    ferias.setAnoId(resolveAno(req.getDataInicio()));
    ferias.setDataInicio(req.getDataInicio());
    ferias.setDataFim(req.getDataFim());
    ferias.setNumDia(diffDays(req.getDataInicio(), req.getDataFim()));
    ferias.setTiprelIdSubstituido(tipoRelAtualFuncSubstituto!=null ? tipoRelAtualFuncSubstituto.getId() : null);
    ferias.setObsInfoConveniencia(req.getObsConvinienciaServico());

    ferias.setDecisaoResponsavel(req.getParecer());
    ferias.setObsResponsavel(req.getObsParecer());
    ferias.setEstado(Estado.P);
    ferias.setUuid(UuidCreator.getTimeOrderedEpoch());

    ResponsavelEntity responsavel = null;
    if (req.getResponsavel()!=null) {
      responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
          () ->
              IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + req.getResponsavel()));
      ferias.setResponsavelId(responsavel.getId());
    }

    // Férias sem substituição ficam directamente activas
    ferias.setEstado(temSubstituicao ? Estado.P : Estado.A);
    ferias = feriasGozadasRepository.save(ferias);

    // Regra: validação RH só necessária quando há substituição (implica salário)
    if (temSubstituicao) {
      var validacao = buildValidacao(funcionario, tipoRelAtual, TipoAcao.INSERT.name(), Referencia.FERIA.name(), Estado.P);
      validacao.setReferenciaId(pedido.getId());
      validacao.setReferenciaUuid(pedido.getUuid());
      validacaoEntityRepository.save(validacao);
    } else {
      // Sem substituição: criar ausência directamente (já aprovado)
      criarAusenciaNaValidacao(ferias);
    }

    saveDocuments(req.getDocumentos(), funcionario, pedido);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", pedido.getId());
    resp.put("uuid", pedido.getUuid());
    return resp;
  }

  @Transactional
  public Map<String, ?> validarFeria(ValidarPedidoFeriaCommand command) {
    var req = command.getPedidoferiareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador de pedido ferias é obrigatório");

    var ferias = feriasGozadasRepository.findByPedidoId_Uuid(UuidCreator.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "Ferias Gozadas not found for id: " + command.getPedidoId()));

    var funcionario = ferias.getFunId();

    // Verificar saldo usando funcionario das ferias (colaborador pode não vir no request de validação)
    var saldoFeria = saldoFeriaService.getSaldo(funcionario.getUuid());
    if (req.getNumDias() != null && req.getNumDias() > saldoFeria)
      throw IgrpResponseStatusException.badRequest("Funcionario não tem saldo de ferias suficiente");
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    ResponsavelEntity responsavel = null;
    if (req.getResponsavel()!=null) {
      responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
          () ->
              IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + req.getResponsavel()));
      ferias.setResponsavelId(responsavel.getId());
      ferias.setDecisaoResponsavel(req.getParecer());
    }


    ferias.setDecisaoRh(req.getValidar());
    ferias.setObsRh(req.getObsValidacao());
    ferias.setEstado(estado);
    feriasGozadasRepository.save(ferias);

    var pedido = ferias.getPedidoId();
    if (pedido != null) {
      pedido.setEstado(estado.name());
      if (estado == Estado.A) {
        pedido.setEtapa("FINALIZADO");
      }
      pedidoRepository.save(pedido);
    }

    if (estado == Estado.A) {
      ordemServicoWriteService.criar(funcionario, tipoRelAtual, req.getTipoOrdemServico());
      criarAusenciaNaValidacao(ferias);
      enviarNotificacaoValidacaoFerias(ferias, funcionario);
    }

    if (pedido != null) {
      funcionarioRules.getValidacaoPendenteByReferenciaUuid(pedido.getUuid(), TipoAcao.INSERT, Referencia.FERIA)
          .ifPresent(v -> {
            v.setEstado(estado);
            validacaoEntityRepository.save(v);
          });
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", ferias.getId());
    resp.put("uuid", ferias.getUuid());
    resp.put("estado", ferias.getEstado().name());
    return resp;
  }

  @Transactional
  public Map<String, ?> alterarPedidoFeria(AlterarPedidoFeriaCommand command) {

    var req = command.getPedidoferiaalterarreq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de alteração de férias ausentes");

    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");

    // 1. Buscar férias existentes PELO MESMO PEDIDO
    var ferias = feriasGozadasRepository
        .findByPedidoId_Uuid(UuidCreator.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Pedido de férias não encontrado"));

    var funcionario = ferias.getFunId();
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var base = req.getFeria();
    if (base == null)
      throw IgrpResponseStatusException.badRequest("Dados da férias são obrigatórios");

    LocalDate dataInicio = base.getDataInicio() != null
        ? base.getDataInicio()
        : ferias.getDataInicio();

    LocalDate dataFim = req.getNovaDataFim() != null
        ? req.getNovaDataFim()
        : base.getDataFim();

    if (dataInicio == null || dataFim == null)
      throw IgrpResponseStatusException.badRequest("Data início e fim são obrigatórias");

    // 2. Inactivar registo antigo (spec: inativa e cria novo)
    ferias.setEstado(Estado.I);
    feriasGozadasRepository.save(ferias);

    // Spec, "Alteração": ponto 1 — "Inativa RH_T_PEDIDO ANTERIOR". Sem isto o pedido
    // antigo ficava activo em paralelo com o novo.
    var pedidoAnterior = ferias.getPedidoId();
    if (pedidoAnterior != null) {
      pedidoAnterior.setEstado(Estado.I.name());
      pedidoAnterior.setEtapa("FINALIZADO");
      pedidoRepository.save(pedidoAnterior);
    }

    // F4: Inactivar ausência anterior ligada às férias antigas
    var ausenciasAnteriores = ausenciaRepository
        .findAllByReferenciaNameAndReferenciaId("RH_T_FERIAS_GOZADAS", ferias.getId());
    if (!ausenciasAnteriores.isEmpty()) {
      ausenciasAnteriores.forEach(a -> a.setEstado(Estado.I));
      ausenciaRepository.saveAll(ausenciasAnteriores);
    }

    // 3. Criar novo pedido
    var novoPedido = new PedidoEntity();
    novoPedido.setFunId(funcionario);
    novoPedido.setTipoPedido("FERIA");
    novoPedido.setOrigem("ASSIDUIDADE");
    novoPedido.setEtapa("DESPACHO_RH");
    novoPedido.setEstado(Estado.P.name());
    novoPedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    novoPedido = pedidoRepository.save(novoPedido);

    // 4. Criar novo registo de férias (com referência ao antigo via feriasGozadasId)
    var novasFerias = new FeriasGozadasEntity();
    novasFerias.setPedidoId(novoPedido);
    novasFerias.setFunId(funcionario);
    novasFerias.setAnoId(ferias.getAnoId());
    novasFerias.setDataInicio(dataInicio);
    novasFerias.setDataFim(dataFim);
    novasFerias.setNumDia(diffDays(dataInicio, dataFim));
    novasFerias.setFeriasGozadasId(ferias.getId());
    novasFerias.setTipoAlteracao(TIPO_ALTERACAO_DATA);
    novasFerias.setMotivoAlteracao(req.getMotivo());
    novasFerias.setObsInfoConveniencia(base.getObsConvinienciaServico());
    novasFerias.setObsResponsavel(base.getObsParecer());
    novasFerias.setEstado(Estado.P);
    novasFerias.setUuid(UuidCreator.getTimeOrderedEpoch());

    if (base.getResponsavel() != null) {
      var responsavel = responsavelEntityRepository
          .findByFunId_Uuid(base.getResponsavel())
          .orElseThrow(() -> IgrpResponseStatusException.notFound("Responsável não encontrado"));
      novasFerias.setResponsavelId(responsavel.getId());
    }

    novasFerias = feriasGozadasRepository.save(novasFerias);

    // Spec, "Alteração": ponto 3 — gravar RH_T_SUBSTITUICAO caso haja substituto.
    // Faltava por completo no fluxo de alteração.
    if (base.getSubstituidoPor() != null) {
      if (tipoRelAtual.getCargoId() == null)
        throw IgrpResponseStatusException.badRequest(
            "Substituição não aplicável: colaborador não ocupa nenhum cargo");

      var funcionarioSubstituto = funcionarioRepository.findByUuidOrThrow(base.getSubstituidoPor());
      var tipoRelSubstituto = funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid());

      var substituicao = new SubstituicaoEntity();
      substituicao.setSubstituidoTiprelId(tipoRelAtual);
      substituicao.setSubstitutoTiprelId(tipoRelSubstituto);
      substituicao.setDataInicio(dataInicio);
      substituicao.setDataFim(dataFim);
      substituicao.setEstado(Estado.P);
      substituicao.setUuid(UuidCreator.getTimeOrderedEpoch());
      substituicaoRepository.save(substituicao);

      novasFerias.setTiprelIdSubstituido(tipoRelSubstituto.getId());
      novasFerias = feriasGozadasRepository.save(novasFerias);
    }

    // 5. Validação de UPDATE no novo pedido
    var validacao = buildValidacao(funcionario, tipoRelAtual, TipoAcao.UPDATE.name(), Referencia.FERIA.name(), Estado.P);
    validacao.setReferenciaId(novoPedido.getId());
    validacao.setReferenciaUuid(novoPedido.getUuid());
    validacaoEntityRepository.save(validacao);

    // 6. Documentos (se houver)
    saveDocuments(base.getDocumentos(), funcionario, novoPedido);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", novoPedido.getId());
    resp.put("uuid", novoPedido.getUuid());
    resp.put("estado", novasFerias.getEstado().name());
    return resp;
  }


  @Transactional
  public Map<String, ?> enviarDireitoFerias(EnviarDireitoFeriasCommand command) {
    var ferias = feriasGozadasRepository
        .findByPedidoId_Uuid(UuidCreator.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Pedido de férias não encontrado"));

    var funcionario = ferias.getFunId();
    int saldo = saldoFeriaService.getSaldo(funcionario.getUuid());

    var vars = Map.of(
        "nome", funcionario.getNome() != null ? funcionario.getNome() : "",
        "dataInicio", ferias.getDataInicio() != null ? ferias.getDataInicio().toString() : "",
        "dataFim", ferias.getDataFim() != null ? ferias.getDataFim().toString() : "",
        "numDias", ferias.getNumDia() != null ? ferias.getNumDia().toString() : "0",
        "saldoRestante", String.valueOf(saldo)
    );

    funcionario.getContactos().stream()
        .filter(c -> "EMAIL".equalsIgnoreCase(c.getTipoContacto()))
        .map(ContactoEntity::getContacto)
        .findFirst()
        .ifPresentOrElse(
            email -> notificacaoDispatchService.enviar(
                "DIREITO_FERIAS", email, funcionario.getNome(),
                ferias.getId(), "RH_T_FERIAS_GOZADAS", ferias.getUuid(), funcionario, vars),
            () -> LOGGER.warn("Funcionário {} sem email para envio de direito de férias", funcionario.getUuid())
        );

    if (ferias.getResponsavelId() != null) {
      responsavelEntityRepository.findById(ferias.getResponsavelId()).ifPresent(responsavel -> {
        String emailResp = responsavel.getEmail();
        if (emailResp != null && !emailResp.isBlank()) {
          var respFun = responsavel.getFunId();
          notificacaoDispatchService.enviar(
              "DIREITO_FERIAS", emailResp,
              respFun != null ? respFun.getNome() : emailResp,
              ferias.getId(), "RH_T_FERIAS_GOZADAS", ferias.getUuid(), respFun, vars);
        }
      });
    }

    return Map.of("pedidoId", command.getPedidoId(), "enviado", true);
  }

  private void enviarNotificacaoValidacaoFerias(FeriasGozadasEntity ferias, FuncionarioEntity funcionario) {
    var emailOpt = funcionario.getContactos().stream()
        .filter(c -> "EMAIL".equalsIgnoreCase(c.getTipoContacto()))
        .map(ContactoEntity::getContacto)
        .findFirst();
    if (emailOpt.isEmpty()) {
      LOGGER.warn("Funcionário {} sem email para notificação de validação de férias", funcionario.getUuid());
      return;
    }
    int saldo = saldoFeriaService.getSaldo(funcionario.getUuid());
    var vars = Map.of(
        "nome", funcionario.getNome() != null ? funcionario.getNome() : "",
        "dataInicio", ferias.getDataInicio() != null ? ferias.getDataInicio().toString() : "",
        "dataFim", ferias.getDataFim() != null ? ferias.getDataFim().toString() : "",
        "numDias", ferias.getNumDia() != null ? ferias.getNumDia().toString() : "0",
        "saldoRestante", String.valueOf(saldo)
    );
    notificacaoDispatchService.enviar(
        "VALIDACAO_FERIAS", emailOpt.get(), funcionario.getNome(),
        ferias.getId(), "RH_T_FERIAS_GOZADAS", ferias.getUuid(), funcionario, vars);
  }

  private void validatePedido(PedidoFeriaReqDTO req) {

    if (req.getColaborador() == null)
      throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");
    if (req.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início obrigatória");
    if (req.getDataFim() == null)
      throw IgrpResponseStatusException.badRequest("Data de fim obrigatória");
    if (req.getNumDias() == null)
      throw IgrpResponseStatusException.badRequest("Número de dias obrigatório");

    var saldoFeria = saldoFeriaService.getSaldo(req.getColaborador());

    if (req.getNumDias() > saldoFeria)
      throw IgrpResponseStatusException.badRequest("Funcionario não tem saldo de ferias suficiente");
  }

  private AnoEntity resolveAno(LocalDate data) {
    if (data == null)
      throw IgrpResponseStatusException.badRequest("Data inválida");
    var anoStr = String.valueOf(data.getYear());
    return anoEntityRepository.findAll().stream()
        .filter(a -> anoStr.equals(a.getAno()))
        .findFirst()
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Ano não encontrado"));
  }

  private int diffDays(LocalDate inicio, LocalDate fim) {
    if (inicio == null || fim == null)
      return 0;
    var dias = 0;
    var d = inicio;
    while (!d.isAfter(fim)) {
      dias++;
      d = d.plusDays(1);
    }
    return dias;
  }

  private ValidacaoEntity buildValidacao(
      FuncionarioEntity funcionario,
      TiposRelacionamentoEntity tipoRelAtual,
      String tipoAcao,
      String referencia,
      Estado estado) {
    var v = new ValidacaoEntity();
    v.setTipoAccao(tipoAcao);
    v.setReferenciaName(referencia);
    v.setEstado(estado);
    v.setUuid(UuidCreator.getTimeOrderedEpoch());
    v.setFunId(funcionario);
    v.setTiprelId(tipoRelAtual);
    return validacaoEntityRepository.save(v);
  }

  /**
   * Regista a ausência correspondente às férias validadas.
   *
   * <p>Esta ausência não é acessória: é ela que faz o colaborador desaparecer da lista
   * de faltas no período (regra da especificação — quem está de férias tem a falta
   * justificada automaticamente). Uma falha aqui produziria faltas indevidas, por isso
   * propaga em vez de ser silenciada.
   */
  private void criarAusenciaNaValidacao(FeriasGozadasEntity ferias) {
    var params = paramSituacaoRepository.findByFlgAusenciaAndTipoAusencia(1, SituacaoFalta.FERIAS.name());
    if (params == null || params.isEmpty())
      throw IgrpResponseStatusException.badRequest(
          "Não existe parametrização de situação para FERIAS em RH_T_PARAM_SITUACAO "
              + "(FLG_AUSENCIA=1, TIPO_AUSENCIA='FERIAS') — sem ela o colaborador continuaria "
              + "a aparecer na lista de faltas durante as férias.");

    var ausencia = new AusenciaEntity();
    ausencia.setParamSitId(params.getFirst());
    ausencia.setFunId(ferias.getFunId());
    ausencia.setReferenciaName(TableName.RH_T_FERIAS_GOZADAS.name());
    ausencia.setReferenciaId(ferias.getId());
    ausencia.setObs(ferias.getFeriasGozadasId() != null ? "ALTERACAO DE FERIAS" : null);
    ausencia.setDataInicio(ferias.getDataInicio());
    ausencia.setDataFim(ferias.getDataFim());
    ausencia.setEstado(Estado.A);
    ausencia.setUuid(UuidCreator.getTimeOrderedEpoch());
    ausenciaRepository.save(ausencia);
  }


  private void saveDocuments(List<AnexoReqDTO> documentos, FuncionarioEntity funId, PedidoEntity pedido){
    var anexosExistentes = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(Referencia.FERIA.name(), pedido.getUuid());

    var sincronizados = documentoMapper.syncDocumentos(
        anexosExistentes != null ? anexosExistentes : new ArrayList<>(),
        documentos,
        TableName.RH_T_FERIAS_GOZADAS.name(),
        pedido.getId(),
        pedido.getUuid(),
        1L,
        funId);
    if (CollectionsUtils.hasItems(sincronizados)) {
      sincronizados.forEach(d -> { if (d.getUuid() == null) d.setUuid(UuidCreator.getTimeOrderedEpoch()); });
      documentoEntityRepository.saveAll(sincronizados);
    }
  }
}
