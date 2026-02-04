package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.AlterarPedidoFeriaCommand;
import cv.inps.rh.assiduidade.application.commands.MarcarFeriaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarPedidoFeriaCommand;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
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

  @Transactional
  public Map<String, ?> marcarFeria(MarcarFeriaCommand command) {
    var req = command.getPedidoferiareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de férias ausentes");
    validatePedido(req);

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaborador());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

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
    ferias.setTiprelIdSubstituido(req.getSubstituidoPor());
    ferias.setObsInfoConveniencia(req.getObsConvinienciaServico());
    ferias.setResponsavelId(req.getResponsavel());
    ferias.setObsResponsavel(req.getObsParecer());
    ferias.setEstado(Estado.P);
    ferias.setUuid(UuidCreator.getTimeOrderedEpoch());
    ferias = feriasGozadasRepository.save(ferias);

    var validacao = buildValidacao(funcionario, tipoRelAtual, TipoAcao.INSERT.name(), Referencia.FERIA.name(), Estado.P);
    funcionario.getValidacoes().add(validacao);
    funcionarioRepository.saveAndFlush(funcionario);

    var finalPedido = pedido;
    validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
      v.setReferenciaId(finalPedido.getId());
      v.setReferenciaUuid(finalPedido.getUuid());
      validacaoEntityRepository.save(v);
    });

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



    var ferias = feriasGozadasRepository.findByPedidoId_Uuid(UuidCreator.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"Ferias Gozadas not found for id: " + command.getPedidoId()));

    var funcionario = ferias.getFunId();
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    ferias.setEstado(estado);
    feriasGozadasRepository.save(ferias);

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
      throw IgrpResponseStatusException.badRequest("Identificador de pedido ferias é obrigatório");


    var existing = feriasGozadasRepository.findByPedidoId_Uuid(UuidCreator.fromString(command.getPedidoId()))
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"Ferias Gozadas not found for id: " + command.getPedidoId()));

    var funcionario = existing.getFunId();
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    existing.setEstado(Estado.I);
    feriasGozadasRepository.save(existing);

    var base = req.getFeria() != null ? req.getFeria() : new PedidoFeriaReqDTO();
    var di = base.getDataInicio() != null ? base.getDataInicio() : existing.getDataInicio();
    var df = req.getNovaDataFim() != null ? req.getNovaDataFim() : base.getDataFim();
    if (di == null || df == null)
      throw IgrpResponseStatusException.badRequest("Data de início e fim são obrigatórias");

    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("FERIA");
    pedido.setOrigem("ASSIDUIDADE");
    pedido.setEtapa("DESPACHO_RH");
    pedido.setEstado(Estado.P.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    var nova = new FeriasGozadasEntity();
    nova.setPedidoId(pedido);
    nova.setFunId(funcionario);
    nova.setAnoId(resolveAno(di));
    nova.setDataInicio(di);
    nova.setDataFim(df);
    nova.setNumDia(diffDays(di, df));
    nova.setTiprelIdSubstituido(base.getSubstituidoPor());
    nova.setObsInfoConveniencia(base.getObsConvinienciaServico());
    nova.setResponsavelId(base.getResponsavel());
    nova.setObsResponsavel(base.getObsParecer());
    nova.setMotivoAlteracao(req.getMotivo());
    nova.setEstado(Estado.P);
    nova.setUuid(UuidCreator.getTimeOrderedEpoch());
    nova.setFeriasGozadasId(existing.getId());
    nova = feriasGozadasRepository.save(nova);

    var validacao = buildValidacao(funcionario, tipoRelAtual, TipoAcao.UPDATE.name(), Referencia.FERIA.name(), Estado.P);
    funcionario.getValidacoes().add(validacao);
    funcionarioRepository.saveAndFlush(funcionario);

    var finalPedido = pedido;
    validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
      v.setReferenciaId(finalPedido.getId());
      validacaoEntityRepository.save(v);
    });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", nova.getId());
    resp.put("uuid", nova.getUuid());
    return resp;
  }

  private void validatePedido(PedidoFeriaReqDTO req) {
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
      Estado estado
  ) {
    var v = new ValidacaoEntity();
    v.setTipoAccao(tipoAcao);
    v.setReferenciaName(referencia);
    v.setEstado(estado);
    v.setUuid(UuidCreator.getTimeOrderedEpoch());
    v.setFunId(funcionario);
    v.setTiprelId(tipoRelAtual);
    return validacaoEntityRepository.save(v);
  }
}
