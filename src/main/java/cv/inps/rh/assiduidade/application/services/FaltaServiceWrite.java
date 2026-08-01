package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaCommand;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.TipoDescontoFalta;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FaltaServiceWrite {

  /** RH_ASSIDUIDADE_SINTESE_DIARIA.FORMA — registo criado à mão pelo RH. */
  private static final String FORMA_MANUAL = "MANUAL";

  private final FaltaEntityRepository faltaRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final AssiduidadeParametroEntityRepository assiduidadeParametroRepository;
  private final DocumentoMapper documentoMapper;
  private final ParamSituacaoEntityRepository paramSituacaoRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final ResponsavelEntityRepository responsavelEntityRepository;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final FaltaDescontoService faltaDescontoService;
  private final FaltaValorCalculator faltaValorCalculator;


  @Transactional
  public Map<String, ?> marcarFalta(MarcarFaltaCommand command) {

    var req = command.getFaltareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de falta ausentes");
    if (req.getColaboradorId() == null)
      throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");
    if (req.getDataInicio() == null || req.getDataFim() == null)
      throw IgrpResponseStatusException.badRequest("Intervalo de datas obrigatório");
    if (req.getDataFim().isBefore(req.getDataInicio()))
      throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaboradorId());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    boolean deveJustificar = Objects.equals(req.getJustificar(), "SIM");

    var datas = expandirDias(req.getDataInicio(), req.getDataFim());

    // Regra: só vai a validação se forem mais de 3 dias E o tipo de justificação
    // descontar no salário. Caso contrário fica logo activo.
    var paramSituacao = resolverParamSituacao(req.getTipoJustificacao());
    boolean requerValidacao = deveJustificar
        && faltaDescontoService.requerValidacao(datas.size(), paramSituacao);
    var estadoInicial = requerValidacao ? Estado.P : Estado.A;

    PedidoEntity pedido = null;
    if (deveJustificar) {
      pedido = new PedidoEntity();
      pedido.setFunId(funcionario);
      pedido.setTipoPedido("JUSTIFICACAO_FALTA");
      pedido.setEtapa(requerValidacao ? "DESPACHO_RH" : "FINALIZADO");
      pedido.setEstado(estadoInicial.name());
      pedido.setOrigem("RH");
      pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
      pedido = pedidoRepository.save(pedido);
    }

    int totalRegistos = 0;

    // RH_T_FALTA.HORAS_AUSENCIA e RH_ASSIDUIDADE_SINTESE_DIARIA.HORAS_AUSENCIA são
    // INTERVAL DAY(0) TO SECOND(0) — só admitem 0-23h por registo (um por dia).
    // O total informado no pedido cobre o período inteiro, por isso é dividido
    // pelo nº de dias antes de gravar cada registo diário.
    String horasAusenciaPorDia = dividirPorDias(req.getTotalDeHorasAusentes(), datas.size());

    var faltas = new ArrayList<FaltaEntity>();
    BigDecimal valorTotal = BigDecimal.ZERO;

    for (var dia : datas) {
      var sintese = createSinteseDiaria(funcionario, dia,
          horasAusenciaPorDia, deveJustificar);
      sinteseRepository.save(sintese);

      if (deveJustificar) {
        var falta = createFaltaDoDia(req, pedido, dia, tipoRelAtual, sintese, horasAusenciaPorDia);
        falta.setEstado(estadoInicial);
        falta.setParamSitId(paramSituacao);
        faltaRepository.save(falta);
        faltas.add(falta);
        if (falta.getValor() != null)
          valorTotal = valorTotal.add(falta.getValor());
      }

      totalRegistos++;
    }

    // Anexos: REFERENCIA_ID aponta para o registo de RH_T_FALTA, conforme a
    // especificação — antes apontava para o pedido.
    if (deveJustificar && req.getDocumentos() != null && !req.getDocumentos().isEmpty()
        && !faltas.isEmpty()) {
      var primeiraFalta = faltas.getFirst();
      List<DocumentoEntity> docs = new ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            estadoInicial,
            TableName.RH_T_FALTA.name(),
            primeiraFalta.getId(),
            primeiraFalta.getUuid(),
            1L,
            funcionario);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        docs.add(doc);
      }
      documentoEntityRepository.saveAll(docs);
    }

    if (requerValidacao) {
      var validacao = dadosContratuaisMapper.toValidacaoInsert(
          TipoAcao.INSERT.name(),
          Referencia.FALTA.name(),
          Estado.P);
      validacao.setFunId(funcionario);
      validacao.setTiprelId(tipoRelAtual);
      validacao.setReferenciaId(pedido.getId());
      validacao.setReferenciaUuid(pedido.getUuid());
      validacaoEntityRepository.save(validacao);
    } else if (deveJustificar) {
      // Sem validação, os descontos são aplicados de imediato.
      for (var falta : faltas)
        faltaDescontoService.aplicar(falta, pedido, tipoRelAtual);
      faltaRepository.saveAll(faltas);
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("totalRegistos", totalRegistos);
    resp.put("estado", estadoInicial.name());
    resp.put("requerValidacao", requerValidacao);
    if (!faltas.isEmpty()) {
      resp.put("valorDiario", faltas.getFirst().getValor());
      resp.put("valorTotal", valorTotal);
    }
    if (pedido != null) {
      resp.put("pedidoId", pedido.getId());
      resp.put("pedidoUuid", pedido.getUuid() != null ? pedido.getUuid().toString() : null);
    }

    return resp;
  }

  @Transactional
  public Map<String, Object> validarFalta(ValidarFaltaCommand command) {

    var req = command.getFaltareq();
    if (req == null || req.getValidar() == null)
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");

    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador do pedido de falta é obrigatório");

    UUID pedidoUuid = UUID.fromString(command.getPedidoId());

    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Pedido marcação de falta não encontrado com id: " + pedidoUuid));

    var novoEstado = req.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(pedido.getFunId().getUuid());

    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);

    for (FaltaEntity f : faltas) {

      f.setEstado(novoEstado);

      if (StringUtils.hasText(req.getParecer()))
        f.setDecisaoResponsavel(req.getParecer());
      if (StringUtils.hasText(req.getObservacao()))
        f.setObsResponsavel(req.getObservacao());
      if (StringUtils.hasText(req.getDespachoRh()))
        f.setDespachoRh(req.getDespachoRh());

      if (req.getTipoJustificacao() != null && req.getTipoJustificacao() > 0) {
        var ps = paramSituacaoRepository.findByIdOrThrow(req.getTipoJustificacao());
        f.setParamSitId(ps);
      }

      if (StringUtils.hasText(req.getDeduzirFaltaEm()))
        f.setFlgDescontoFalta(TipoDescontoFalta.fromCodeOrThrow(req.getDeduzirFaltaEm()).getCode());

      if (novoEstado == Estado.A)
        faltaDescontoService.aplicar(f, pedido, tipoRelAtual);
    }

    faltaRepository.saveAll(faltas);

    pedido.setEstado(novoEstado.name());
    if (novoEstado == Estado.A) {
      pedido.setEtapa("FINALIZADO");
      ordemServicoWriteService.criar(pedido.getFunId(), tipoRelAtual, req.getTipoOrdemServico());
    }

    pedidoRepository.save(pedido);

    funcionarioRules.getValidacaoPendente(
            pedido.getFunId().getUuid(), TipoAcao.INSERT, Referencia.FALTA)
        .ifPresent(v -> {
          v.setEstado(novoEstado);
          validacaoEntityRepository.save(v);
        });

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("estado", novoEstado);
    resp.put("totalFaltas", faltas.size());

    return resp;
  }


  private ParamSituacaoEntity resolverParamSituacao(Long tipoJustificacao) {
    if (tipoJustificacao == null || tipoJustificacao <= 0)
      return null;
    return paramSituacaoRepository.findById(tipoJustificacao)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Tipo justificativo inválido"));
  }

  private List<LocalDate> expandirDias(LocalDate inicio, LocalDate fim) {
    var dias = new ArrayList<LocalDate>();
    var d = inicio;
    while (!d.isAfter(fim)) {
      dias.add(d);
      d = d.plusDays(1);
    }
    return dias;
  }

  private FaltaEntity createFaltaDoDia(
      FaltaReqDTO req,
      PedidoEntity pedido,
      LocalDate dia,
      TiposRelacionamentoEntity tipoRel,
      AssiduidadeSinteseDiarioEntity sintese,
      String horasAusenciaPorDia) {

    FaltaEntity falta = new FaltaEntity();
    falta.setPedidoId(pedido);
    falta.setTiprelId(tipoRel);
    falta.setTipo(FaltaDescontoService.TIPO_FALTA);
    falta.setDescricaoMotivo(req.getMotivoAusencia());
    falta.setHorasAusencia(parseInterval(horasAusenciaPorDia));

    falta.setDataInicio(LocalDateTime.of(dia, LocalTime.MIN));
    falta.setDataFim(LocalDateTime.of(dia, LocalTime.of(23, 59, 59)));
    falta.setFlgJustificativo(req.getJustificar());
    falta.setUuid(UuidCreator.getTimeOrderedEpoch());
    falta.setSinteseDiarioId(sintese);

    if (StringUtils.hasText(req.getDeduzirFaltaEm()))
      falta.setFlgDescontoFalta(TipoDescontoFalta.fromCodeOrThrow(req.getDeduzirFaltaEm()).getCode());

    if (req.getResponsavel() != null) {
      var responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
          () ->
              IgrpResponseStatusException.notFound("Responsável não encontrado para o funcionário " + req.getResponsavel()));
      falta.setResponsavelId(responsavel);
    }

    if (StringUtils.hasText(req.getParecer())) {
      falta.setDecisaoResponsavel(req.getParecer());
    }
    if (StringUtils.hasText(req.getObservacao())) {
      falta.setObsResponsavel(req.getObservacao());
    }

    // Valor do dia = valor à hora x horas de ausência. O valor à hora vem de
    // CALCULO_FALTA_DIARIO (salário base / 30 / jornada diária), com fallback em Java.
    falta.setValor(faltaValorCalculator.valorDia(tipoRel.getId(), dia, horasAusenciaPorDia));

    return falta;
  }

  private AssiduidadeSinteseDiarioEntity createSinteseDiaria(
      FuncionarioEntity funcionario,
      LocalDate dia,
      String horasAusencia,
      boolean justificar) {

    AssiduidadeSinteseDiarioEntity sintese = new AssiduidadeSinteseDiarioEntity();
    sintese.setFuncionarioId(funcionario);
    sintese.setData(dia);
    sintese.setMes(dia.getMonthValue());
    sintese.setAno(dia.getYear());

    String horasAusenciaInterval = parseInterval(horasAusencia);
    sintese.setHorasAusencia(horasAusenciaInterval);

    var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    String diaria = (jornada != null && !jornada.isEmpty())
        ? jornada.getFirst().getDiaria()
        : "08:00";

    int totalMinutos = parseMin(diaria);
    int ausenciaMinutos = parseMin(horasAusencia);

    int trabalhadosMinutos = Math.max(0, totalMinutos - ausenciaMinutos);
    int h = trabalhadosMinutos / 60;
    int m = trabalhadosMinutos % 60;
    String horasTrabalhadasStr = String.format("%02d:%02d", h, m);
    sintese.setHorasTrabalhadas(parseInterval(horasTrabalhadasStr));

    sintese.setFalta(trabalhadosMinutos == 0 ? 1 : 0);
    sintese.setEstado(Estado.A.name());
    sintese.setFlagRececao("1");
    // Distingue este registo dos que vêm da importação do relógio de ponto.
    sintese.setForma(FORMA_MANUAL);

    return sintese;
  }

  private int parseMin(String hhmm) {
    if (!StringUtils.hasText(hhmm))
      return 0;
    var parts = hhmm.split(":");
    try {
      int h = Integer.parseInt(parts[0]);
      int m = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      return h * 60 + m;
    } catch (Exception ignored) {
      return 0;
    }
  }

  private String parseInterval(String hhmm) {
    if (!StringUtils.hasText(hhmm))
      return null;

    try {
      String[] parts = hhmm.split(":");
      int hours = Integer.parseInt(parts[0]);
      int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      // RH_T_FALTA/RH_ASSIDUIDADE_SINTESE_DIARIA.HORAS_AUSENCIA é
      // INTERVAL DAY(0) TO SECOND(0): não há campo de dias, logo horas > 23
      // não é representável (Oracle rejeitaria com ORA-01850).
      if (hours < 0 || hours > 23) {
        throw IgrpResponseStatusException.badRequest(
            "Horas de ausência por dia inválidas (" + hhmm + "h) — não pode exceder 23h/dia");
      }
      return String.format("+0 %02d:%02d:00", hours, minutes);
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Horas inválidas: " + hhmm, e);
    }
  }

  private String dividirPorDias(String totalHoras, int totalDias) {
    if (!StringUtils.hasText(totalHoras) || totalDias <= 0)
      return totalHoras;
    int minutosPorDia = parseMin(totalHoras) / totalDias;
    return String.format("%02d:%02d", minutosPorDia / 60, minutosPorDia % 60);
  }

}
