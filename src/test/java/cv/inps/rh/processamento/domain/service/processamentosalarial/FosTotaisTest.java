package cv.inps.rh.processamento.domain.service.processamentosalarial;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cv.inps.rh.processamento.domain.service.processamentosalarial.FosService.totalContribuicao;
import static cv.inps.rh.processamento.domain.service.processamentosalarial.FosService.totalRemuneracao;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** PS-024: totais da FOS no formato do procedimento (inteiros, contribuição de 24,5% arredondada). */
class FosTotaisTest {

  @Test
  void remuneracaoSemCasasDecimais() {
    assertEquals("657680", totalRemuneracao(657680.0).toPlainString());   // antes "657680.0"
    assertEquals("1001", totalRemuneracao(1000.5).toPlainString());
    assertEquals("0", totalRemuneracao(null).toPlainString());          // FOS sem linhas
  }

  @Test
  void contribuicaoArredondadaComoOProcedimento() {
    assertEquals("161132", totalContribuicao(new BigDecimal("657680")).toPlainString()); // FOS 15; antes 161131.60
    assertEquals(161132L, totalContribuicao(new BigDecimal("657680")).longValue());      // TT_CONTRIB_CALC; antes 161131
    assertEquals("0", totalContribuicao(BigDecimal.ZERO).toPlainString());
  }
}
