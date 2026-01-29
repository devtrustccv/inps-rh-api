package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaCommand;
import cv.inps.rh.assiduidade.application.constants.AssiduidadeDiariaEstado;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final DocumentoMapper documentoMapper;
  private final ParamSituacaoEntityRepository paramSituacaoRepository;
  private final EntityManager entityManager;

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


    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("JUSTIFICACAO_FALTA");
    pedido.setEtapa("DESPACHO_RH");
    pedido.setEstado(Estado.P);
    pedido.setOrigem("MANUAL");
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    var datas = expandirDias(req.getDataInicio(), req.getDataFim());
    var idsFaltas = new ArrayList<Long>();

    for (var dia : datas) {
      var falta = buildFalta(req, pedido, dia, tipoRelAtual);

      var sintese = buildOrCreateSinteseDia(funcionario, dia, req.getTotalDeHorasAusentes());

      falta.setSinteseDiarioId(sintese);
      falta = faltaRepository.save(falta);

      idsFaltas.add(falta.getId());
      sinteseRepository.save(sintese);
    }

    PedidoEntity finalPedido = pedido;

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.FALTA.name(),
        Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    validacao.setReferenciaId(finalPedido.getId());
    validacaoEntityRepository.save(validacao);


    Map<String, Object> resp = new HashMap<>();
    resp.put("id", pedido.getId());
    resp.put("uuid", pedido.getUuid() != null ? pedido.getUuid().toString() : null);
    resp.put("totalRegistos", idsFaltas.size());
    return resp;
  }

  @Transactional
  public Map<String, ?> validarFalta(ValidarFaltaCommand command) {

    var req = command.getFaltareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getFaltaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da falta é obrigatório");

    var faltaUuid = IdentificadorUnico.from(command.getFaltaId());

    var falta = faltaRepository.findByUuid(faltaUuid.valor())
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Falta não encontrada"));

    var pedido = falta.getPedidoId();
    var funcionario = pedido != null ? pedido.getFunId() : null;

    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    falta.setDecisaoResponsavel(req.getParecer());
    falta.setObsResponsavel(req.getObservacao());
    falta.setDespachoRh(req.getDespachoRh());
    falta.setEstado(estado);
    faltaRepository.save(falta);

    pedido.setEstado(estado);
    pedidoRepository.save(pedido);

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.FALTA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    if (Estado.A.equals(estado) && falta.getFlgDescontoSal() != null && falta.getFlgDescontoSal() == 1) {
      var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
      var diaria = jornada != null && !jornada.isEmpty() ? jornada.getFirst().getDiaria() : "08:00";
      var totalMin = parseMin(diaria);
      var ausMin = parseMin(falta.getHorasAusencia());
      if (tipoRelAtual != null && tipoRelAtual.getSalario() != null && totalMin > 0 && ausMin > 0) {
        var salarioDia = tipoRelAtual.getSalario().divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
        var valorDesconto = salarioDia.multiply(BigDecimal.valueOf(ausMin))
            .divide(BigDecimal.valueOf(totalMin), 2, RoundingMode.HALF_UP);
        var tm = tipoMovimentoHelper.getTipoMovimentoEntityFaltaDesconto();
        var defRem = definicaoRemuneracaoMapper.createRenumeracao(
            valorDesconto,
            tm,
            falta.getDataInicio().toLocalDate(),
            falta.getDataFim().toLocalDate(),
            funcionario,
            tipoRelAtual.getMoeda()
        );
        defRem.setEstado(Estado.A);
        defRem = definicaoRemuneracaoRepository.save(defRem);
        falta.setDefRemId(defRem);
        faltaRepository.save(falta);
      }
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", falta.getId());
    resp.put("uuid", falta.getUuid() != null ? falta.getUuid().toString() : null);
    resp.put("estado", falta.getEstado().name());
    return resp;
  }

 /* @Transactional
  public Map<String, ?> validarPedidoFalta(ValidarFaltaCommand command) {
    var req = command.getFaltareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");
    Long pedidoId;
    try {
      pedidoId = Long.parseLong(command.getPedidoId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("Identificador do pedido inválido");
    }

    var pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Pedido não encontrado"));
    var funcionario = pedido.getFunId();
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    var faltas = faltaRepository.findAllByPedidoId(pedido);
    for (var falta : faltas) {
      falta.setDecisaoResponsavel(req.getParecer());
      falta.setObsResponsavel(req.getObservacao());
      falta.setDespachoRh(req.getDespachoRh());
      falta.setEstado(estado);
      faltaRepository.save(falta);

      if (Estado.A.equals(estado) && falta.getFlgDescontoSal() != null && falta.getFlgDescontoSal() == 1) {
        var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
        var diaria = jornada != null && !jornada.isEmpty() ? jornada.getFirst().getDiaria() : "08:00";
        var totalMin = parseMin(diaria);
        var ausMin = parseMin(falta.getHorasAusencia());
        if (tipoRelAtual != null && tipoRelAtual.getSalario() != null && totalMin > 0 && ausMin > 0) {
          var salarioDia = tipoRelAtual.getSalario().divide(new java.math.BigDecimal("30"), 2, java.math.RoundingMode.HALF_UP);
          var valorDesconto = salarioDia.multiply(java.math.BigDecimal.valueOf(ausMin))
              .divide(java.math.BigDecimal.valueOf(totalMin), 2, java.math.RoundingMode.HALF_UP);
          var tm = tipoMovimentoHelper.getTipoMovimentoEntityFaltaDesconto();
          var defRem = definicaoRemuneracaoMapper.createRenumeracao(
              valorDesconto,
              tm,
              falta.getDataInicio().toLocalDate(),
              falta.getDataFim().toLocalDate(),
              funcionario,
              tipoRelAtual.getMoeda()
          );
          defRem.setEstado(Estado.A);
          defRem = definicaoRemuneracaoRepository.save(defRem);
          falta.setDefRemId(defRem);
          faltaRepository.save(falta);
        }
      }
    }

    pedido.setEstado(estado);
    pedido.setEtapa("FINALIZADO");
    pedidoRepository.save(pedido);

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.FALTA)
        .ifPresent(v -> {
          v.setEstado(estado);
          v.setReferenciaId(pedido.getId());
          validacaoEntityRepository.save(v);
        });

    java.util.Map<String, Object> resp = new java.util.HashMap<>();
    resp.put("id", pedido.getId());
    resp.put("uuid", pedido.getUuid() != null ? pedido.getUuid().toString() : null);
    resp.put("totalRegistos", faltas.size());
    return resp;
  }
*/
  private List<LocalDate> expandirDias(LocalDate inicio, LocalDate fim) {
    var dias = new ArrayList<LocalDate>();
    var d = inicio;
    while (!d.isAfter(fim)) {
      dias.add(d);
      d = d.plusDays(1);
    }
    return dias;
  }

  private FaltaEntity buildFalta(FaltaReqDTO req, PedidoEntity pedido, LocalDate dia,
                                 TiposRelacionamentoEntity tiposRelacionamento) {
    var falta = new FaltaEntity();
    falta.setPedidoId(pedido);
    falta.setTiprelId(tiposRelacionamento);
    falta.setDescricaoMotivo(req.getMotivoAusencia());

      falta.setHorasAusencia(parseInterval(req.getTotalDeHorasAusentes()));

    falta.setDataInicio(LocalDateTime.of(dia, LocalTime.MIN));
    falta.setDataFim(LocalDateTime.of(dia, LocalTime.of(23, 59, 59)));
    falta.setFlgJustificativo(req.getJustificar());
    falta.setEstado(Estado.P);
    falta.setUuid(UuidCreator.getTimeOrderedEpoch());


    if(req.getResponsavel() != null && req.getResponsavel() > 0) {
      var resp = entityManager.find(ResponsavelEntity.class, req.getResponsavel());
      falta.setResponsavelId(resp);
    }

    if(req.getTipoJustificacao() != null && req.getTipoJustificacao() > 0) {
      var paramSituacao = paramSituacaoRepository.findById(req.getTipoJustificacao())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Tipo justificativo inválido"));
      falta.setParamSitId(paramSituacao);
    }

    return falta;
  }

  private AssiduidadeSinteseDiarioEntity buildOrCreateSinteseDia(FuncionarioEntity fun, LocalDate dia,
                                                                 String horasAusencia) {
    var e = new AssiduidadeSinteseDiarioEntity();
    e.setFuncionarioId(fun);
    e.setData(dia);
    e.setMes(dia.getMonthValue());
    e.setAno(dia.getYear());

    // Converter horasAusencia para INTERVAL
    String horasAusenciaInterval = parseInterval(horasAusencia);
    e.setHorasAusencia(horasAusenciaInterval);

    // Obter jornada diária (padrão 8:00 se não houver)
    var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    String diaria = (jornada != null && !jornada.isEmpty()) ? jornada.getFirst().getDiaria() : "08:00";

    // Parse minutos
    int totalMinutos = parseMin(diaria);
    int ausenciaMinutos = parseMin(horasAusencia);

    // Calcular minutos trabalhados (não pode ser negativo)
    int trabalhadosMinutos = Math.max(0, totalMinutos - ausenciaMinutos);
    int h = trabalhadosMinutos / 60;
    int m = trabalhadosMinutos % 60;

    // Converter para INTERVAL para Oracle
    String horasTrabalhadasStr = String.format("%02d:%02d", h, m);
    e.setHorasTrabalhadas(parseInterval(horasTrabalhadasStr));

    // Se trabalhou 0, falta total
    e.setFalta(trabalhadosMinutos == 0 ? 0 : 1);

    e.setEstado(AssiduidadeDiariaEstado.INJUSTIFICADA.name());

    // Debug logs (opcional)
    System.out.println("Diária: " + diaria);
    System.out.println("Horas Ausência: " + horasAusencia + " -> " + horasAusenciaInterval);
    System.out.println("Horas Trabalhadas: " + horasTrabalhadasStr + " -> " + parseInterval(horasTrabalhadasStr));
    System.out.println("Falta: " + e.getFalta());

    return e;
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
    if (!StringUtils.hasText(hhmm)) return null;

    try {
      String[] parts = hhmm.split(":");
      int hours = Integer.parseInt(parts[0]);
      int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      return String.format("+0 %02d:%02d:00", hours, minutes);
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Horas inválidas: " + hhmm, e);
    }
  }



}
