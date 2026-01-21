package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaCommand;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.PedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoFaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoFaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
  private final TipoFaltaEntityRepository tipoFaltaRepository;
  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final AssiduidadeParametroEntityRepository assiduidadeParametroRepository;

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

    var funcionario = funcionarioRepository.findByIdOrThrow(req.getColaboradorId());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var tipoFalta = resolveTipoFalta(req.getTipoFalta());

    var pedido = new PedidoEntity();
    pedido.setFunId(funcionario);
    pedido.setTipoPedido("JUSTIFICACAO_FALTA");
    pedido.setOrigem("ASSIDUIDADE");
    pedido.setEtapa("DESPACHO_RH");
    pedido.setEstado(Estado.P);
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido = pedidoRepository.save(pedido);

    var datas = expandirDias(req.getDataInicio(), req.getDataFim());
    var idsFaltas = new ArrayList<Long>();
    for (var dia : datas) {
      var falta = buildFalta(req, pedido, tipoFalta, dia, funcionario);
      falta = faltaRepository.save(falta);
      idsFaltas.add(falta.getId());
      var sintese = buildOrCreateSinteseDia(funcionario, dia, req.getTotalDeHorasAusentes());
      sinteseRepository.save(sintese);
    }

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.FALTA.name(),
        Estado.P);
    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRelAtual);
    funcionario.getValidacoes().add(validacao);
    funcionarioRepository.saveAndFlush(funcionario);

    PedidoEntity finalPedido = pedido;

    validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
      v.setReferenciaId(finalPedido.getId());
      validacaoEntityRepository.save(v);
    });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", pedido.getId());
    resp.put("uuid", pedido.getUuid() != null ? pedido.getUuid().toString() : null);
    resp.put("totalRegistos", idsFaltas.size());
    return resp;
  }

  @Transactional
  public Map<String, ?> validarFalta(ValidarFaltaCommand command) {
    var req = command.getFaltareq();
    if (!StringUtils.hasText(command.getFaltaId()))
      throw IgrpResponseStatusException.badRequest("Identificador da falta é obrigatório");
    Long faltaId;
    try {
      faltaId = Long.parseLong(command.getFaltaId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("Identificador da falta inválido");
    }

    var falta = faltaRepository.findByIdOrThrow(faltaId);
    var pedido = falta.getPedidoId();
    var funcionario = pedido != null ? pedido.getFunId() : null;
    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Pedido sem colaborador associado");

    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var estado = Estado.I;
    if (req != null && StringUtils.hasText(req.getValidar())) {
      var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
      estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
    }

    falta.setDecisaoResponsavel(req != null ? req.getParecer() : null);
    falta.setObsResponsavel(req != null ? req.getObservacao() : null);
    falta.setDespachoRh(req != null ? req.getDespachoRh() : null);
    falta.setEstado(estado);
    faltaRepository.save(falta);

    if (pedido != null) {
      pedido.setEstado(estado);
      pedidoRepository.save(pedido);
    }

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.FALTA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", falta.getId());
    resp.put("uuid", falta.getUuid() != null ? falta.getUuid().toString() : null);
    resp.put("estado", falta.getEstado().name());
    return resp;
  }

  private TipoFaltaEntity resolveTipoFalta(String tipoFaltaId) {
    if (!StringUtils.hasText(tipoFaltaId))
      throw IgrpResponseStatusException.badRequest("Tipo de falta obrigatório");
    return tipoFaltaRepository.findByUuidOrThrow(tipoFaltaId);
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

  private FaltaEntity buildFalta(FaltaReqDTO req, PedidoEntity pedido, TipoFaltaEntity tipo, LocalDate dia,
      FuncionarioEntity fun) {
    var falta = new FaltaEntity();
    falta.setPedidoId(pedido);
    falta.setTfId(tipo);
    falta.setDescricaoMotivo(req.getMotivoAusencia());
    falta.setHorasAusencia(req.getTotalDeHorasAusentes());
    falta.setDataInicio(LocalDateTime.of(dia, LocalTime.MIN));
    falta.setDataFim(LocalDateTime.of(dia, LocalTime.of(23, 59, 59)));
    falta.setFlgJustificativo(req.getJustificar());
    if (StringUtils.hasText(tipo.getDescontoRemuneracao()) && tipo.getDescontoRemuneracao().equalsIgnoreCase("SIM")) {
      falta.setFlgDesconto(1);
    } else {
      falta.setFlgDesconto(0);
    }
    falta.setEstado(Estado.P);
    falta.setUuid(UuidCreator.getTimeOrderedEpoch());
    return falta;
  }

  private AssiduidadeSinteseDiarioEntity buildOrCreateSinteseDia(FuncionarioEntity fun, LocalDate dia,
      String horasAusencia) {
    var e = new AssiduidadeSinteseDiarioEntity();
    e.setFuncionarioId(fun);
    e.setData(dia);
    e.setMes(dia.getMonthValue());
    e.setAno(dia.getYear());
    e.setHorasAusencia(horasAusencia);
    var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    var diaria = jornada != null && !jornada.isEmpty() ? jornada.getFirst().getDiaria() : "08:00";
    var ht = calcularHorasTrabalhadas(diaria, horasAusencia);
    e.setHorasTrabalhadas(ht);
    e.setFalta("00:00".equals(ht) ? "SIM" : "NAO");
    e.setEstado(Estado.A);
    return e;
  }

  private String calcularHorasTrabalhadas(String diaria, String ausencia) {
    var totalMin = parseMin(diaria);
    var ausMin = parseMin(ausencia);
    var trab = Math.max(0, totalMin - ausMin);
    var h = trab / 60;
    var m = trab % 60;
    var hh = h < 10 ? "0" + h : String.valueOf(h);
    var mm = m < 10 ? "0" + m : String.valueOf(m);
    return hh + ":" + mm;
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
}
