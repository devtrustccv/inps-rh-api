package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Types;
import java.time.LocalDate;

/**
 * Calcula o valor de falta a descontar.
 *
 * <p>A especificação manda usar
 * {@code RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_DIARIO(P_TIPREL_ID, P_DATA_INICIO)},
 * que devolve o <strong>valor à hora</strong> (apesar do nome "DIARIO"). A fórmula, lida
 * do corpo do package (linha 2204), é:
 *
 * <pre>
 *   jornadaDiaria = RH_T_ASSIDUIDADE_PARAMETRO.DIARIA  (estado = 'A', formato 'HH:MM')
 *   salario       = GET_SALARIO_BASE(tiprelId, dataInicio)
 *   valorHora     = (salario / 30) / jornadaDiaria      -- 30 = v_divisor_falta
 * </pre>
 *
 * <p>À data desta implementação o procedimento devolve sempre {@code NULL}: o corpo faz
 * {@code SELECT DIARIA INTO v_jorn_diaria (NUMBER)} sobre uma coluna {@code VARCHAR2 'HH:MM'},
 * o {@code ORA-06502} é engolido por um {@code EXCEPTION WHEN OTHERS THEN NULL} e a variável
 * fica nula. Além disso {@code GET_SALARIO_BASE} não trata {@code TOO_MANY_ROWS} e rebenta
 * com {@code ORA-01422} para colaboradores com mais do que um salário activo.
 *
 * <p>Por isso o cálculo replica a mesma fórmula em Java e o procedimento é usado apenas
 * quando explicitamente activado. Em qualquer falha do lado Oracle há <strong>fallback
 * automático</strong> para Java, com registo em log — nunca se deixa cair o pedido do
 * utilizador por causa disto. Quando o corpo do procedimento estiver corrigido, basta
 * pôr {@code rh.assiduidade.calculo-falta=oracle}.
 *
 * @see <a href="file:../../../../../../../docs/sql/assiduidade_ddl_pendente.sql">docs/sql/assiduidade_ddl_pendente.sql</a>
 */
@Service
@RequiredArgsConstructor
public class FaltaValorCalculator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FaltaValorCalculator.class);

  /** {@code v_divisor_falta} — constante do package RH_PROCESSAMENTO_SALARIAL_DB (linha 16). */
  private static final BigDecimal DIVISOR_FALTA = BigDecimal.valueOf(30);

  private static final String JORNADA_PADRAO = "08:00";

  private final JdbcTemplate jdbcTemplate;
  private final AssiduidadeParametroEntityRepository assiduidadeParametroRepository;

  @Value("${rh.assiduidade.calculo-falta:java}")
  private String modo;

  /**
   * Valor à hora de falta para um colaborador numa data.
   *
   * @param tiprelId    id de RH_T_TIPOS_RELACIONAMENTO (o activo, EST_ACT_ADM = 1)
   * @param dataInicio  data de referência para o salário base em vigor
   */
  public BigDecimal valorHora(Long tiprelId, LocalDate dataInicio) {

    if (tiprelId == null || dataInicio == null)
      throw IgrpResponseStatusException.badRequest(
          "Tipo de relacionamento e data são obrigatórios para calcular o valor da falta");

    if ("oracle".equalsIgnoreCase(modo)) {
      var viaOracle = calcularViaOracle(tiprelId, dataInicio);
      if (viaOracle != null)
        return viaOracle;
      // fallback — já registado em log dentro de calcularViaOracle
    }

    return calcularEmJava(tiprelId, dataInicio);
  }

  /**
   * Valor da falta de um dia = valor à hora × horas de ausência nesse dia.
   *
   * @param horasAusencia horas de ausência do dia, no formato {@code HH:MM}
   */
  public BigDecimal valorDia(Long tiprelId, LocalDate dia, String horasAusencia) {
    int minutos = TimeUtils.hhmmToMinutes(horasAusencia);
    if (minutos <= 0)
      return BigDecimal.ZERO;

    var horas = BigDecimal.valueOf(minutos)
        .divide(BigDecimal.valueOf(60), 8, RoundingMode.HALF_UP);

    return valorHora(tiprelId, dia)
        .multiply(horas)
        .setScale(2, RoundingMode.HALF_UP);
  }

  // ------------------------------------------------------------------

  /** @return o valor calculado pelo procedimento, ou {@code null} se falhar/devolver nulo. */
  private BigDecimal calcularViaOracle(Long tiprelId, LocalDate dataInicio) {
    try {
      var valor = jdbcTemplate.execute((ConnectionCallback<BigDecimal>) conn -> {
        try (CallableStatement cs = conn.prepareCall(
            "{ ? = call INPSRH.RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_FALTA_DIARIO(?, ?) }")) {
          cs.registerOutParameter(1, Types.NUMERIC);
          cs.setLong(2, tiprelId);
          cs.setDate(3, java.sql.Date.valueOf(dataInicio));
          cs.execute();
          return cs.getBigDecimal(1);
        }
      });

      if (valor == null || valor.signum() <= 0) {
        LOGGER.warn(
            "CALCULO_FALTA_DIARIO devolveu {} para tiprelId={} data={} — a usar cálculo Java. "
                + "Provável causa: conversão de RH_T_ASSIDUIDADE_PARAMETRO.DIARIA no corpo do "
                + "procedimento (ver docs/sql/assiduidade_ddl_pendente.sql, secção 2).",
            valor, tiprelId, dataInicio);
        return null;
      }

      return valor.setScale(2, RoundingMode.HALF_UP);

    } catch (Exception e) {
      LOGGER.warn(
          "CALCULO_FALTA_DIARIO falhou para tiprelId={} data={} — a usar cálculo Java. Causa: {}",
          tiprelId, dataInicio, e.getMessage(), e);
      return null;
    }
  }

  private BigDecimal calcularEmJava(Long tiprelId, LocalDate dataInicio) {

    var salarioBase = getSalarioBase(tiprelId, dataInicio);

    if (salarioBase == null || salarioBase.signum() <= 0) {
      LOGGER.warn("Salário base nulo ou zero para tiprelId={} data={} — valor de falta fica 0",
          tiprelId, dataInicio);
      return BigDecimal.ZERO;
    }

    int jornadaMinutos = getJornadaDiariaMinutos();
    if (jornadaMinutos <= 0)
      throw IgrpResponseStatusException.badRequest(
          "Jornada diária não parametrizada em RH_T_ASSIDUIDADE_PARAMETRO (estado A)");

    var jornadaHoras = BigDecimal.valueOf(jornadaMinutos)
        .divide(BigDecimal.valueOf(60), 8, RoundingMode.HALF_UP);

    return salarioBase
        .divide(DIVISOR_FALTA, 8, RoundingMode.HALF_UP)
        .divide(jornadaHoras, 2, RoundingMode.HALF_UP);
  }

  /**
   * Salário base via {@code GET_SALARIO_BASE} — é pública e está a funcionar. Trata
   * {@code ORA-01422} (colaborador com mais do que um salário activo), caso em que o
   * procedimento rebenta por não ter handler de {@code TOO_MANY_ROWS}.
   */
  private BigDecimal getSalarioBase(Long tiprelId, LocalDate data) {
    try {
      return jdbcTemplate.execute((ConnectionCallback<BigDecimal>) conn -> {
        try (CallableStatement cs = conn.prepareCall(
            "{ ? = call INPSRH.RH_PROCESSAMENTO_SALARIAL_DB.GET_SALARIO_BASE(?, ?) }")) {
          cs.registerOutParameter(1, Types.NUMERIC);
          cs.setLong(2, tiprelId);
          cs.setDate(3, java.sql.Date.valueOf(data));
          cs.execute();
          return cs.getBigDecimal(1);
        }
      });
    } catch (Exception e) {
      LOGGER.warn(
          "GET_SALARIO_BASE falhou para tiprelId={} data={} — a usar RH_T_TIPOS_RELACIONAMENTO.SALARIO. "
              + "Causa: {}. Se for ORA-01422, o colaborador tem mais do que uma remuneração SAL/SBNT "
              + "activa e o procedimento não trata TOO_MANY_ROWS "
              + "(ver docs/sql/assiduidade_ddl_pendente.sql, secção 4).",
          tiprelId, data, e.getMessage());
      return getSalarioDoTiprel(tiprelId);
    }
  }

  /** Último recurso: o salário guardado no próprio vínculo. */
  private BigDecimal getSalarioDoTiprel(Long tiprelId) {
    try {
      return jdbcTemplate.queryForObject(
          "SELECT salario FROM rh_t_tipos_relacionamento WHERE id = ?",
          BigDecimal.class, tiprelId);
    } catch (Exception e) {
      LOGGER.error("Não foi possível obter o salário do tiprelId={}: {}", tiprelId, e.getMessage());
      return null;
    }
  }

  private int getJornadaDiariaMinutos() {
    var parametros = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    String diaria = (parametros != null && !parametros.isEmpty() && parametros.getFirst().getDiaria() != null)
        ? parametros.getFirst().getDiaria()
        : JORNADA_PADRAO;
    return TimeUtils.hhmmToMinutes(diaria);
  }
}
