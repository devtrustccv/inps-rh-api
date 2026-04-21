package cv.inps.rh.funcionario.domain.repository;

import java.math.BigDecimal;

public interface ICalcularSubstituicaoRepository {

  BigDecimal calcularValorReceber(int nrDias, BigDecimal valorDe, BigDecimal valorPara);

}
