package cv.inps.rh.funcionario.infrastructure.persistence;

import cv.inps.rh.funcionario.domain.repository.ICalcularSubstituicaoRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;

@Repository
public class CalcularSubstituicaoRepositoryImpl implements ICalcularSubstituicaoRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularSubstituicaoRepositoryImpl.class);

  private static final String PACKAGE = "RH_PROCESSAMENTO_SALARIAL_DB";
  private static final String PROCEDURE = "CALCULAR_SUBSTITUICAO";

  private static final String SQL = """
      DECLARE
        v_receber NUMBER := 0;
      BEGIN
        RH_PROCESSAMENTO_SALARIAL_DB.CALCULAR_SUBSTITUICAO(
          P_NR_DIAS           => ?,
          P_VALOR_TIPREL_DE   => ?,
          P_VALOR_TIPREL_PARA => ?,
          P_VALOR_RECEBER     => v_receber
        );
        ? := v_receber;
      END;
      """;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public BigDecimal calcularValorReceber(int nrDias, BigDecimal valorDe, BigDecimal valorPara) {
    return entityManager.unwrap(Session.class).doReturningWork(connection -> {
      try (CallableStatement cs = connection.prepareCall(SQL)) {

        cs.setInt(1, nrDias);
        cs.setBigDecimal(2, valorDe);
        cs.setBigDecimal(3, valorPara);
        cs.registerOutParameter(4, Types.NUMERIC);

        cs.execute();

        BigDecimal result = cs.getBigDecimal(4);
        LOGGER.info("{}.{} nrDias={} valorDe={} valorPara={} => {}", PACKAGE, PROCEDURE, nrDias, valorDe, valorPara, result);

        return result != null ? result : BigDecimal.ZERO;

      } catch (Exception e) {
        LOGGER.error("Erro ao chamar {}.{}: {}", PACKAGE, PROCEDURE, e.getMessage(), e);
        throw IgrpResponseStatusException.internalServerError(e.getMessage());
      }
    });
  }

}
