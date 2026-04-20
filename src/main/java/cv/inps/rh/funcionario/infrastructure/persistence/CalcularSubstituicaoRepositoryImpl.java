package cv.inps.rh.funcionario.infrastructure.persistence;

import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseItemDTO;
import cv.inps.rh.funcionario.application.queries.CalcularSubstituicaoQuery;
import cv.inps.rh.funcionario.domain.repository.ICalcularSubstituicaoRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CalcularSubstituicaoRepositoryImpl implements ICalcularSubstituicaoRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularSubstituicaoRepositoryImpl.class);

  private static final String PACKAGE = "RH_PROCESSAMENTO_SALARIAL_DB";
  private static final String PROCEDURE = "CALCULAR_SUBSTITUICAO";

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<CalcularSubstituicaoResponseItemDTO> calcularSubstituicao(CalcularSubstituicaoQuery query) {

    String sql = buildAnonymousBlock();

    return entityManager.unwrap(Session.class).doReturningWork(connection -> {
      try (CallableStatement cs = connection.prepareCall(sql)) {

        cs.setString(1, query.getDataInicio());
        cs.setString(2, query.getDataFim());
        cs.setLong(3, query.getTiprelDeId());
        cs.setLong(4, query.getTiprelParaId());

        cs.registerOutParameter(5, Types.VARCHAR);

        cs.execute();

        String raw = cs.getString(5);

        LOGGER.info("{}.{} raw result: {}", PACKAGE, PROCEDURE, raw);

        return parseResult(raw);

      } catch (Exception e) {
        LOGGER.error("Erro ao chamar {}.{}: {}", PACKAGE, PROCEDURE, e.getMessage(), e);
        throw IgrpResponseStatusException.internalServerError("Erro ao calcular substituição");
      }
    });
  }

  private String buildAnonymousBlock() {
    return """
        DECLARE
          v_mes_ano    OWA.vc_arr;
          v_nr_dias    OWA.vc_arr;
          v_valor_de   OWA.vc_arr;
          v_valor_para OWA.vc_arr;
          v_receber    NUMBER;
          v_result     VARCHAR2(32767) := '';
        BEGIN
          RH_PROCESSAMENTO_SALARIAL_DB.CALCULAR_SUBSTITUICAO(
            P_DATA_DE     => TO_DATE(?, 'YYYY-MM-DD'),
            P_DATA_ATE    => TO_DATE(?, 'YYYY-MM-DD'),
            P_TIPREL_DE   => ?,
            P_TIPREL_PARA => ?,
            P_MES_ANO           => v_mes_ano,
            P_NR_DIAS           => v_nr_dias,
            P_VALOR_TIPREL_DE   => v_valor_de,
            P_VALOR_TIPREL_PARA => v_valor_para
          );
          FOR i IN 1..v_mes_ano.COUNT LOOP
            v_receber := 0;
            RH_PROCESSAMENTO_SALARIAL_DB.CALCULAR_SUBSTITUICAO(
              P_NR_DIAS           => TO_NUMBER(v_nr_dias(i)),
              P_VALOR_TIPREL_DE   => TO_NUMBER(v_valor_de(i)),
              P_VALOR_TIPREL_PARA => TO_NUMBER(v_valor_para(i)),
              P_VALOR_RECEBER     => v_receber
            );
            IF i > 1 THEN v_result := v_result || ';'; END IF;
            v_result := v_result
              || v_mes_ano(i)    || '|'
              || v_nr_dias(i)    || '|'
              || v_valor_de(i)   || '|'
              || v_valor_para(i) || '|'
              || v_receber;
          END LOOP;
          ? := v_result;
        END;
        """;
  }

  private List<CalcularSubstituicaoResponseItemDTO> parseResult(String raw) {
    List<CalcularSubstituicaoResponseItemDTO> items = new ArrayList<>();

    if (!StringUtils.hasText(raw)) {
      return items;
    }

    for (String row : raw.split(";")) {
      String[] cols = row.split("\\|", -1);
      if (cols.length < 5) continue;

      var item = new CalcularSubstituicaoResponseItemDTO();
      item.setMesAno(cols[0]);
      item.setNrDias(parseInteger(cols[1]));
      item.setValorSubstituto(parseBigDecimal(cols[2]));
      item.setValorSubstituido(parseBigDecimal(cols[3]));
      item.setValorReceber(parseBigDecimal(cols[4]));
      items.add(item);
    }

    return items;
  }

  private Integer parseInteger(String value) {
    try {
      return StringUtils.hasText(value) ? new BigDecimal(value.trim()).intValue() : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private BigDecimal parseBigDecimal(String value) {
    try {
      return StringUtils.hasText(value) ? new BigDecimal(value.trim()) : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

}
