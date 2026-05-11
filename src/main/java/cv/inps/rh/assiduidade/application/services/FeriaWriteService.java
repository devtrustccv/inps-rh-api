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
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.CollectionsUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeriaWriteService {

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


  @Transactional
  public Map<String, ?> marcarFeria(MarcarFeriaCommand command) {
    var req = command.getPedidoferiareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de férias ausentes");
    validatePedido(req);

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());


    TiposRelacionamentoEntity tipoRelAtualFuncSubstituto = null;
    if(req.getSubstituidoPor()!=null) {
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
    pedido.setEtapa("DESPACHO_RH");
    pedido.setEstado(Estado.P.name());
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

    ferias = feriasGozadasRepository.save(ferias);


    var validacao = buildValidacao(funcionario, tipoRelAtual, TipoAcao.INSERT.name(), Referencia.FERIA.name(),
        Estado.P);
    funcionario.getValidacoes().add(validacao);
    funcionarioRepository.saveAndFlush(funcionario);

    var finalPedido = pedido;
    validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
      v.setReferenciaId(finalPedido.getId());
      v.setReferenciaUuid(finalPedido.getUuid());
      validacaoEntityRepository.save(v);
    });

    saveDocuments(req.getDocumentos(), funcionario, pedido);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", ferias.getId());
    resp.put("uuid", ferias.getUuid());
    return resp;
  }

  @Transactional
  public Map<String, ?> validarFeria(ValidarPedidoFeriaCommand command) {
    var req = command.getPedidoferiareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador de pedido ferias é obrigatório");

    var saldoFeria = saldoFeriaService.getSaldo(req.getColaborador());

    if (req.getNumDias() > saldoFeria)
      throw IgrpResponseStatusException.badRequest("Funcionario não tem saldo de ferias suficiente");

    var ferias = feriasGozadasRepository.findByPedidoId_Uuid(UuidCreator.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "Ferias Gozadas not found for id: " + command.getPedidoId()));

    var funcionario = ferias.getFunId();
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
      // Send email notification
      /*String subject = "Validação do Pedido de Férias";
      String text = String.format(
          "O seu pedido de férias com início em %s e fim em %s foi aprovado.",
          ferias.getDataInicio(),
          ferias.getDataFim());
      var funcEmail = funcionario.getContactos().stream()
          .filter(c -> c.getTipoContacto().equals("EMAIL"))
          .findFirst()
          .map(ContactoEntity::getContacto)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
              "Email not found for funcionario: " + funcionario.getUuid()));
      emailService.sendSimpleMessage(funcEmail, subject, text);*/
    }

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.FERIA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

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

    var pedido = ferias.getPedidoId();
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

    // 2. UPDATE das férias
    ferias.setDataInicio(dataInicio);
    ferias.setDataFim(dataFim);
    ferias.setNumDia(diffDays(dataInicio, dataFim));
    ferias.setObsInfoConveniencia(base.getObsConvinienciaServico());
    ferias.setObsResponsavel(base.getObsParecer());
    ferias.setMotivoAlteracao(req.getMotivo());
    ferias.setEstado(Estado.P); // volta a pendente

    if (base.getResponsavel() != null) {
      var responsavel = responsavelEntityRepository
          .findByFunId_Uuid(base.getResponsavel())
          .orElseThrow(() ->
              IgrpResponseStatusException.notFound("Responsável não encontrado"));
      ferias.setResponsavelId(responsavel.getId());
    }

    feriasGozadasRepository.save(ferias);

    // 3. Pedido também volta a pendente
    pedido.setEstado(Estado.P.name());
    pedido.setEtapa("DESPACHO_RH");
    pedidoRepository.save(pedido);

    // 4. Validação de UPDATE
    var validacao = buildValidacao(
        funcionario,
        tipoRelAtual,
        TipoAcao.UPDATE.name(),
        Referencia.FERIA.name(),
        Estado.P
    );

    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());
    validacaoEntityRepository.save(validacao);

    // 5. Documentos (se houver)
    saveDocuments(base.getDocumentos(), funcionario, pedido);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", ferias.getId());
    resp.put("uuid", ferias.getUuid());
    resp.put("estado", ferias.getEstado().name());
    return resp;
  }


  private void validatePedido(PedidoFeriaReqDTO req) {

    var saldoFeria = saldoFeriaService.getSaldo(req.getColaborador());

    if (req.getNumDias() > saldoFeria)
      throw IgrpResponseStatusException.badRequest("Funcionario não tem saldo de ferias suficiente");

    if (req.getColaborador() == null)
      throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");
    if (req.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início obrigatória");
    if (req.getDataFim() == null)
      throw IgrpResponseStatusException.badRequest("Data de fim obrigatória");
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

  private void criarAusenciaNaValidacao(FeriasGozadasEntity ferias) {
    try {
      var params = paramSituacaoRepository.findByFlgAusenciaAndTipoAusencia(1,SituacaoFalta.FERIAS.name());
      if (params == null || params.isEmpty())
        return;
      var param = params.getFirst();
      var ausencia = new AusenciaEntity();
      ausencia.setParamSitId(param);
      ausencia.setReferenciaName("RH_T_FERIAS_GOZADAS");
      ausencia.setReferenciaId(ferias.getId());
      ausencia.setObs(ferias.getFeriasGozadasId() != null ? "ALTERACAO DE FERIAS" : null);
      ausencia.setDataInicio(ferias.getDataInicio());
      ausencia.setDataFim(ferias.getDataFim());
      ausencia.setEstado(Estado.A);
      ausencia.setUuid(UuidCreator.getTimeOrderedEpoch());
      ausenciaRepository.save(ausencia);
    } catch (Exception ignored) {
    }
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
