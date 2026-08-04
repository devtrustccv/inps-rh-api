package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.assiduidade.application.dto.CalcValorFaltaDTO;
import cv.inps.rh.assiduidade.application.services.FaltaValorCalculator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.util.TimeUtils;
import cv.inps.rh.shared.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pré-cálculo do valor da falta, para os campos "Valor Diário (calculado)" e
 * "Valor Total (calculado)" se poderem preencher antes de gravar.
 *
 * <p>Não introduz regra nova: aplica a da especificação (linhas 415-428) —
 * {@code CALCULO_FALTA_DIARIO} dá o valor à hora, multiplica-se pelas horas de ausência
 * do dia e depois pelo total de dias. É o mesmo {@link FaltaValorCalculator} que a
 * gravação usa, para o valor mostrado e o valor gravado nunca divergirem.
 */
@Component
public class GetCalcValorFaltaQueryHandler
    implements QueryHandler<GetCalcValorFaltaQuery, ResponseEntity<CalcValorFaltaDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetCalcValorFaltaQueryHandler.class);

  private final FaltaValorCalculator faltaValorCalculator;
  private final FuncionarioRules funcionarioRules;

  public GetCalcValorFaltaQueryHandler(FaltaValorCalculator faltaValorCalculator,
                                       FuncionarioRules funcionarioRules) {
    this.faltaValorCalculator = faltaValorCalculator;
    this.funcionarioRules = funcionarioRules;
  }

  @IgrpQueryHandler
  public ResponseEntity<CalcValorFaltaDTO> handle(GetCalcValorFaltaQuery query) {

    LOGGER.debug("GetCalcValorFaltaQuery: {}", query);

    var funcUuid = ValidationUtil.parseUuid(query.getFuncionarioUuid(), "Identificador do colaborador");

    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(funcUuid);
    if (tiprel == null)
      throw IgrpResponseStatusException.badRequest("Colaborador sem tipo de relacionamento activo");

    LocalDate dataInicio;
    LocalDate dataFim;
    try {
      dataInicio = LocalDate.parse(query.getDataInicio());
      dataFim = LocalDate.parse(query.getDataFim());
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("Datas inválidas — use o formato YYYY-MM-DD");
    }

    if (dataFim.isBefore(dataInicio))
      throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");

    int totalDias = (int) (dataFim.toEpochDay() - dataInicio.toEpochDay()) + 1;

    // O total informado cobre o período inteiro; o valor é por dia.
    int minutosTotais = TimeUtils.parseHorasFlexivel(query.getTotalDeHorasAusentes());
    if (minutosTotais <= 0)
      throw IgrpResponseStatusException.badRequest(
          "Total de horas ausente tem de ser superior a zero");

    int minutosPorDia = minutosTotais / totalDias;
    var horasPorDia = TimeUtils.formatMinutesToHHmm(minutosPorDia);

    var valorHora = faltaValorCalculator.valorHora(tiprel.getId(), dataInicio);
    var valorDiario = faltaValorCalculator.valorDia(tiprel.getId(), dataInicio, horasPorDia);
    var valorTotal = valorDiario.multiply(BigDecimal.valueOf(totalDias));

    return ResponseEntity.ok(new CalcValorFaltaDTO(
        valorHora, valorDiario, valorTotal, totalDias, horasPorDia));
  }

}
