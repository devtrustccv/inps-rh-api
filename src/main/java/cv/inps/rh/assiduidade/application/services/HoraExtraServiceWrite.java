package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HoraExtraServiceWrite {

  private final HoraExtraEntityRepository horaExtraRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final AssiduidadeParametroEntityRepository assiduidadeParametroRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final TipoMovimentoHelper tipoMovimentoHelper;

  private final PedidoEntityRepository pedidoRepository;

  @Transactional
  public Map<String, ?> marcarHoraExtra(MarcarHoraExtraCommand command) {

    var req = command.getHoraextrareq();
    if (req == null || req.getHoraExtra() == null || req.getHoraExtra().isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Dados de hora extra ausentes");
    }


    var pedido = new PedidoEntity();
    pedido.setEstado(Estado.P.name());
    pedido.setTipoPedido(Referencia.HORA_EXTRA.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());

    pedido = pedidoRepository.save(pedido);

    Long firstHoraExtraId = null;
    String pedidoUuid = pedido.getUuid().toString();
    int totalRegistos = 0;


    for (HoraExtraDTO dto : req.getHoraExtra()) {

      if (dto.getColaborador() == null)
        throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");

      if (dto.getDataInicio() == null || dto.getDataFim() == null)
        throw IgrpResponseStatusException.badRequest("Intervalo de datas obrigatório");

      if (dto.getDataFim().isBefore(dto.getDataInicio()))
        throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");

      if (dto.getHorasDiaria() == null)
        throw IgrpResponseStatusException.badRequest("Horas diárias obrigatórias");

      var funcionario = funcionarioRepository.findByUuidOrThrow(dto.getColaborador());
      var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

      // Se o pedido for por funcionário (opcional, depende do modelo)
      pedido.setFunId(funcionario);

      var dias = expandirDias(dto.getDataInicio(), dto.getDataFim());

      for (var dia : dias) {

        var sintese = buildSinteseDia(funcionario, dia, dto.getHorasDiaria());
        sintese = sinteseRepository.save(sintese);

        // =========================
        // Cálculo valor
        // =========================
        int valorDiario = calcularValorHoraExtra(
            tipoRelAtual,
            dto.getHorasDiaria(),
            dto.getPercentagemHora()
        );

        var he = new HoraExtraEntity();
        he.setPedidoId(pedido);
        he.setTiprelId(tipoRelAtual);
        he.setSinteseDiarioId(sintese);
        he.setDataInicio(dia);
        he.setDataFim(dia);
        he.setHorasDiarias(dto.getHorasDiaria());
        he.setPercentagem(dto.getPercentagemHora());
        he.setValorDiario(valorDiario);
        he.setEstado(Estado.P);
        he.setUuid(UuidCreator.getTimeOrderedEpoch());

        he = horaExtraRepository.save(he);

        if (firstHoraExtraId == null) {
          firstHoraExtraId = he.getId();
        }

        totalRegistos++;
      }
    }


    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(),
        Referencia.HORA_EXTRA.name(),
        Estado.P
    );

    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());

    validacaoEntityRepository.save(validacao);


    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedidoUuid);
    resp.put("totalRegistos", totalRegistos);

    return resp;
  }


  // Método para calcular valor da hora extra
  private int calcularValorHoraExtra(TiposRelacionamentoEntity tipoRel, int horasDiarias, Integer percentagem) {
    if (percentagem == null) percentagem = 100;

    /*var valorSalario = tipoRel.getCarreiraId()!= null ? tipoRel.getCarreiraId().getEscalaoId().getValor()
        : null;*/

    // Exemplo: pega salário diário do tipoRel
    BigDecimal salarioDiario = tipoRel.getSalario() != null ? tipoRel.getSalario() : BigDecimal.ZERO;
    BigDecimal valor = salarioDiario.multiply(BigDecimal.valueOf(horasDiarias))
        .multiply(BigDecimal.valueOf(percentagem))
        .divide(BigDecimal.valueOf(100));
    return valor.intValue();
  }

  // Expande intervalo de datas
  private List<LocalDate> expandirDias(LocalDate inicio, LocalDate fim) {
    var dias = new ArrayList<LocalDate>();
    var d = inicio;
    while (!d.isAfter(fim)) {
      dias.add(d);
      d = d.plusDays(1);
    }
    return dias;
  }


  @Transactional
  public Map<String, ?> validarHoraExtra(ValidarHoraExtraCommand command) {

    var req = command.getHoraextrareq();

    if (req == null || req.getValidar() == null)
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");

    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");

    var pedidoUuid = UUID.fromString(command.getPedidoId());

    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Pedido não encontrado"));

    var horasExtra = horaExtraRepository
        .findAllByPedidoId_UuidAndEstado(pedidoUuid, Estado.P);

    if (horasExtra.isEmpty())
      throw IgrpResponseStatusException.badRequest("Pedido já validado ou sem registos pendentes");

    Estado estado = Objects.equals(req.getValidar(), EstadoValidacao.SIM)
        ? Estado.A
        : Estado.I;

    Map<LocalDate, HoraExtraDTO> ajustes = new HashMap<>();

    if (req.getHoraExtra() != null) {
      for (HoraExtraDTO dto : req.getHoraExtra()) {
        if (dto.getDataInicio() != null) {
          ajustes.put(dto.getDataInicio(), dto);
        }
      }
    }

    for (HoraExtraEntity he : horasExtra) {

      var ajuste = ajustes.get(he.getDataInicio());

      if (ajuste != null) {
        if (ajuste.getHorasDiaria() != null)
          he.setHorasDiarias(ajuste.getHorasDiaria());

        if (ajuste.getPercentagemHora() != null)
          he.setPercentagem(ajuste.getPercentagemHora());

        if (ajuste.getValorDiario() != null)
          he.setValorDiario(ajuste.getValorDiario());
      }

      he.setEstado(estado);
      horaExtraRepository.save(he);
    }

    funcionarioRules.getValidacaoPendenteByReferenciaUuid(
        pedido.getUuid(),
        TipoAcao.INSERT,
        Referencia.HORA_EXTRA
    ).ifPresent(v -> {
      v.setEstado(estado);
      validacaoEntityRepository.save(v);
    });

    pedido.setEstado(estado.name());
    pedidoRepository.save(pedido);

    /*if (estado == Estado.A) {

  var horas = he.getHorasDiarias() != null ? he.getHorasDiarias() : 0;

  if (horas > 0) {

    BigDecimal valorHora;

    if (he.getValorDiario() != null && he.getValorDiario() > 0) {
      valorHora = BigDecimal.valueOf(he.getValorDiario());
    } else {

      var params = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
      var p = params != null && !params.isEmpty() ? params.getFirst() : null;

      var isFimDeSemana =
          he.getDataInicio() != null &&
          (he.getDataInicio().getDayOfWeek() == DayOfWeek.SATURDAY
              || he.getDataInicio().getDayOfWeek() == DayOfWeek.SUNDAY);

      valorHora = p != null
          ? (isFimDeSemana ? p.getHeValorDnutil() : p.getHeValorDutil())
          : BigDecimal.ZERO;
    }

    var valor = valorHora
        .multiply(BigDecimal.valueOf(horas))
        .setScale(2, RoundingMode.HALF_UP);

    if (he.getPercentagem() != null && he.getPercentagem() > 0) {
      valor = valor.multiply(BigDecimal.valueOf(he.getPercentagem()))
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    var tm = tipoMovimentoHelper.getTipoMovimentoEntityHoraExtra();

    var defRem = definicaoRemuneracaoMapper.createRenumeracao(
        valor,
        tm,
        he.getDataInicio(),
        he.getDataFim(),
        funcionario,
        tipoRel.getMoeda()
    );

    defRem.setEstado(Estado.A);
    defRem = definicaoRemuneracaoRepository.save(defRem);

    he.setDefRemId(defRem);
    horaExtraRepository.save(he);
  }
}
*/

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedido.getUuid());
    resp.put("estado", pedido.getEstado());
    resp.put("totalRegistos", horasExtra.size());

    return resp;
  }


  private AssiduidadeSinteseDiarioEntity buildSinteseDia(FuncionarioEntity fun, LocalDate dia, Integer horasExtra) {
    var e = new AssiduidadeSinteseDiarioEntity();
    e.setFuncionarioId(fun);
    e.setData(dia);
    e.setMes(dia.getMonthValue());
    e.setAno(dia.getYear());
    e.setHorasExtras(formatHoras(horasExtra));
    e.setEstado(null);
    return e;
  }

  private String formatHoras(Integer horas) {
    if (horas == null || horas < 0) return "00:00";
    var h = horas;
    var hh = h < 10 ? "0" + h : String.valueOf(h);
    return hh + ":00";
  }

}
