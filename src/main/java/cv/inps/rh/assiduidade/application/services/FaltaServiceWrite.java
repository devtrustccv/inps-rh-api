package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaCommand;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefPagamentoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FaltaServiceWrite {

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
  private final EntityManager entityManager;
  private final DefPagamentoEntityRepository defPagamentoRepository;
  private final DefPagamentoMapper defPagamentoMapper;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasRepository;
  private final AnoEntityRepository anoEntityRepository;
  private final DispensaEntityRepository dispensaRepository;
  private final ResponsavelEntityRepository responsavelEntityRepository;


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

    PedidoEntity pedido = null;
    if (deveJustificar) {
      if (req.getTipoJustificacao() == null || req.getTipoJustificacao() <= 0) {
        throw IgrpResponseStatusException.badRequest("Tipo de justificação é obrigatório");
      }

      pedido = new PedidoEntity();
      pedido.setFunId(funcionario);
      pedido.setTipoPedido("JUSTIFICACAO_FALTA");
      pedido.setEtapa("DESPACHO_RH");
      pedido.setEstado(Estado.P.name());
      pedido.setOrigem("MANUAL");
      pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
      pedido = pedidoRepository.save(pedido);

      if (req.getDocumentos() != null && !req.getDocumentos().isEmpty()) {
        List<DocumentoEntity> docs = new ArrayList<>();
        for (var d : req.getDocumentos()) {
          var doc = documentoMapper.toEntity(
              d,
              Estado.P,
              Referencia.JUSTIFICAR_FALTA.name(),
              pedido.getId(),
              pedido.getUuid(),
              1L,
              funcionario);
          doc.setUuid(UuidCreator.getTimeOrderedEpoch());
          docs.add(doc);
        }
        documentoEntityRepository.saveAll(docs);
      }
    }

    var datas = expandirDias(req.getDataInicio(), req.getDataFim());
    int totalRegistos = 0;

    for (var dia : datas) {
      var sintese = createSinteseDiaria(funcionario, dia,
          req.getTotalDeHorasAusentes(), deveJustificar);
      sinteseRepository.save(sintese);

      if (deveJustificar) {
        var falta = createFaltaDoDia(req, pedido, dia, tipoRelAtual, sintese);
        faltaRepository.save(falta);
      }

      totalRegistos++;
    }

    if (deveJustificar) {
      var validacao = dadosContratuaisMapper.toValidacaoInsert(
          TipoAcao.INSERT.name(),
          Referencia.FALTA.name(),
          Estado.P);
      validacao.setFunId(funcionario);
      validacao.setTiprelId(tipoRelAtual);
      validacao.setReferenciaId(pedido.getId());
      validacao.setReferenciaUuid(pedido.getUuid());
      validacaoEntityRepository.save(validacao);
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("totalRegistos", totalRegistos);
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

      // 🔹 desconto salário
      if (novoEstado == Estado.A && f.getParamSitId() != null &&
          f.getParamSitId().getFlgFaltaDecontoSal() != null &&
          f.getParamSitId().getFlgFaltaDecontoSal() == 1) {

        var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(pedido.getFunId().getUuid());
        var vinculoId = tipoRel.getContrVinculoId().getVinculoId().getId();

        var mov = paramVinculoMovimentoEntityRepository
            .findByVinculoId_IdAndTipo(vinculoId, "PAG_FALTA")
            .getFirst();

        var dp = defPagamentoMapper.createPagamento(
            f.getValor(),
            mov.getTmId(),
            f.getDataInicio().toLocalDate(),
            f.getDataFim().toLocalDate(),
            pedido.getFunId());

        defPagamentoRepository.save(dp);
        f.setFlgDescontoSal(1);
      }

      // desconto férias
      if (novoEstado == Estado.A && f.getParamSitId() != null) {
        var fg = new FeriasGozadasEntity();
        fg.setFunId(pedido.getFunId());
        fg.setPedidoId(pedido);
        fg.setAnoId(resolveAno(f.getDataInicio().toLocalDate()));
        fg.setDataInicio(f.getDataInicio().toLocalDate());
        fg.setDataFim(f.getDataInicio().toLocalDate());
        fg.setNumDia(1);
        fg.setEstado(Estado.A);
        fg.setUuid(UuidCreator.getTimeOrderedEpoch());
        feriasGozadasRepository.save(fg);
      }

      // desconto dispensa
      if (novoEstado == Estado.A && f.getParamSitId() != null) {
        var disp = new DispensaEntity();
        disp.setPedidoId(pedido);
        disp.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(pedido.getFunId().getUuid()));
        disp.setData(f.getDataInicio().toLocalDate());
        disp.setHoraInicio("00:00");
        disp.setHoraFim(intervalToHHmm(f.getHorasAusencia()));
        disp.setEstado(Estado.A);
        disp.setUuid(UuidCreator.getTimeOrderedEpoch());
        dispensaRepository.save(disp);
      }
    }

    faltaRepository.saveAll(faltas);

    pedido.setEstado(novoEstado.name());
    if (novoEstado == Estado.A)
      pedido.setEtapa("FINALIZADO");

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
      AssiduidadeSinteseDiarioEntity sintese) {

    FaltaEntity falta = new FaltaEntity();
    falta.setPedidoId(pedido);
    falta.setTiprelId(tipoRel);
    falta.setDescricaoMotivo(req.getMotivoAusencia());
    falta.setHorasAusencia(parseInterval(req.getTotalDeHorasAusentes()));

    falta.setDataInicio(LocalDateTime.of(dia, LocalTime.MIN));
    falta.setDataFim(LocalDateTime.of(dia, LocalTime.of(23, 59, 59)));
    falta.setFlgJustificativo(req.getJustificar());
    falta.setEstado(Estado.P);
    falta.setUuid(UuidCreator.getTimeOrderedEpoch());
    falta.setSinteseDiarioId(sintese);

    ResponsavelEntity responsavel = null;
    if (req.getResponsavel()!=null) {
      responsavel = responsavelEntityRepository.findByFunId_Uuid(req.getResponsavel()).orElseThrow(
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

    if (req.getTipoJustificacao() != null && req.getTipoJustificacao() > 0) {
      var paramSituacao = paramSituacaoRepository.findById(req.getTipoJustificacao())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Tipo justificativo inválido"));
      if (!"1".equalsIgnoreCase(paramSituacao.getFlgFalta())) {
        throw IgrpResponseStatusException.badRequest(
            "Tipo justificativo não permitido para falta");
      }
      falta.setParamSitId(paramSituacao);
    }

    var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    String diaria = (jornada != null && !jornada.isEmpty())
        ? jornada.getFirst().getDiaria()
        : "08:00";
    int totalMinutosDia = parseMin(diaria);
    int ausenciaMinutos = parseMin(req.getTotalDeHorasAusentes());
    java.math.BigDecimal salarioDiario = tipoRel.getSalario() != null ? tipoRel.getSalario()
        : java.math.BigDecimal.ZERO;
    java.math.BigDecimal ratio = totalMinutosDia > 0
        ? java.math.BigDecimal.valueOf(ausenciaMinutos)
            .divide(java.math.BigDecimal.valueOf(totalMinutosDia), 8, java.math.RoundingMode.HALF_UP)
        : java.math.BigDecimal.ZERO;
    java.math.BigDecimal valorDesconto = salarioDiario.multiply(ratio).setScale(2, java.math.RoundingMode.HALF_UP);
    falta.setValor(valorDesconto);

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
      return String.format("+0 %02d:%02d:00", hours, minutes);
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Horas inválidas: " + hhmm, e);
    }
  }

  private String intervalToHHmm(String interval) {
    if (!StringUtils.hasText(interval))
      return "00:00";
    try {
      String[] parts = interval.trim().split("\\s+");
      if (parts.length >= 2) {
        String time = parts[1];
        String[] hm = time.split(":");
        String hh = hm.length > 0 ? hm[0] : "00";
        String mm = hm.length > 1 ? hm[1] : "00";
        return String.format("%02d:%02d", Integer.parseInt(hh), Integer.parseInt(mm));
      }
      return "00:00";
    } catch (Exception e) {
      return "00:00";
    }
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
}
