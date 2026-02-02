package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarFaltaCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarFaltaCommand;
import cv.inps.rh.assiduidade.application.constants.AssiduidadeDiariaEstado;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
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
  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final DocumentoMapper documentoMapper;
  private final ParamSituacaoEntityRepository paramSituacaoRepository;
  private final EntityManager entityManager;
  private final DefPagamentoEntityRepository defPagamentoRepository;
  private final DefPagamentoMapper defPagamentoMapper;

  private final PedidoEntityRepository pedidoEntityRepository;

  @Transactional
  public Map<String, ?> marcarFalta(MarcarFaltaCommand command) {

    var req = command.getFaltareq();
    if (req == null) throw IgrpResponseStatusException.badRequest("Dados de falta ausentes");
    if (req.getColaboradorId() == null) throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");
    if (req.getDataInicio() == null || req.getDataFim() == null)
      throw IgrpResponseStatusException.badRequest("Intervalo de datas obrigatório");
    if (req.getDataFim().isBefore(req.getDataInicio()))
      throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");

    var funcionario = funcionarioRepository.findByUuidOrThrow(req.getColaboradorId());
    var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    boolean deveJustificar = "SIM".equalsIgnoreCase(req.getJustificar());

    //  Criar pedido apenas se justificar
    PedidoEntity pedido = null;
    if (deveJustificar) {
      pedido = new PedidoEntity();
      pedido.setFunId(funcionario);
      pedido.setTipoPedido("JUSTIFICACAO_FALTA");
      pedido.setEtapa("DESPACHO_RH");
      pedido.setEstado(Estado.P.name());
      pedido.setOrigem("MANUAL");
      pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
      pedido = pedidoRepository.save(pedido);
    }

    // Expandir dias do período
    var datas = expandirDias(req.getDataInicio(), req.getDataFim());
    int totalRegistos = 0;

    for (var dia : datas) {
      // Criar síntese diária (sempre)
      var sintese = createSinteseDiaria(funcionario, dia,
          req.getTotalDeHorasAusentes(), deveJustificar);
      sinteseRepository.save(sintese);

      // Criar falta apenas se justificar
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
          Estado.P
      );
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

    // Buscar pedido
    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest(
            "Pedido marcação de falta não encontrado com id: " + pedidoUuid));

    // Determinar novo estado
    var ev = req.getValidar();
    var novoEstado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    //  Atualizar faltas do pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);
    for (FaltaEntity f : faltas) {
      f.setEstado(novoEstado);

      // Desconto de salário
      if (novoEstado == Estado.A && f.getParamSitId() != null &&
          f.getParamSitId().getFlgFaltaDecontoSal() != null &&
          f.getParamSitId().getFlgFaltaDecontoSal() == 1) {

        // TODO: Criar registro em RH_T_DEF_PAGAMENTOS
        // Exemplo de campos: funId, tiprelId, referenciaId(pedido.getId), valor, data, estado
      }

      // TODO: Desconto de férias (RH_T_FERIAS_GOZADAS)
      // TODO: Desconto de horas de dispensa (RH_T_DISPENSA)
    }
    faltaRepository.saveAll(faltas);

    // Atualizar estado e etapa do pedido
    pedido.setEstado(novoEstado.name());
    if (novoEstado == Estado.A) {
      pedido.setEtapa("FINALIZADO");
    }
    pedidoRepository.save(pedido);

    // Atualizar validação
    funcionarioRules.getValidacaoPendente(pedido.getFunId().getUuid(), TipoAcao.INSERT, Referencia.FALTA)
        .ifPresent(v -> {
          v.setEstado(novoEstado);
          validacaoEntityRepository.save(v);
        });

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedido.getUuid() != null ? pedido.getUuid().toString() : null);
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
    falta.setEstado(Estado.P); // Pendente
    falta.setUuid(UuidCreator.getTimeOrderedEpoch());
    falta.setSinteseDiarioId(sintese);

    // Responsável
    if (req.getResponsavel() != null && req.getResponsavel() > 0) {
      var resp = entityManager.find(ResponsavelEntity.class, req.getResponsavel());
      falta.setResponsavelId(resp);
    }

    // Tipo justificativa
    if (req.getTipoJustificacao() != null && req.getTipoJustificacao() > 0) {
      var paramSituacao = paramSituacaoRepository.findById(req.getTipoJustificacao())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Tipo justificativo inválido"));
      falta.setParamSitId(paramSituacao);
    }

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

    // Converter horas de ausência para INTERVAL
    String horasAusenciaInterval = parseInterval(horasAusencia);
    sintese.setHorasAusencia(horasAusenciaInterval);

    // Jornada diária (padrão 8:00)
    var jornada = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    String diaria = (jornada != null && !jornada.isEmpty()) ? jornada.getFirst().getDiaria() : "08:00";

    int totalMinutos = parseMin(diaria);
    int ausenciaMinutos = parseMin(horasAusencia);

    int trabalhadosMinutos = Math.max(0, totalMinutos - ausenciaMinutos);
    int h = trabalhadosMinutos / 60;
    int m = trabalhadosMinutos % 60;
    String horasTrabalhadasStr = String.format("%02d:%02d", h, m);
    sintese.setHorasTrabalhadas(parseInterval(horasTrabalhadasStr));

    // Falta: 1 = falta total, 0 = não falta
    sintese.setFalta(trabalhadosMinutos == 0 ? 1 : 0);

    sintese.setEstado(justificar ? AssiduidadeDiariaEstado.JUSTIFICADA.name() :AssiduidadeDiariaEstado.INJUSTIFICADA.name());

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
