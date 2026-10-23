package cv.inps.rh.processamento.domain.service.processamentosalarial.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FosUtilTest {

  @Test
  void shouldBuildReferenceMonthAsYearThenZeroPaddedMonth() {
    assertEquals("202604", FosUtil.buildReferenceMonth("2026", "04"));
    assertEquals("202604", FosUtil.buildReferenceMonth("2026", "4"));
    assertEquals("202612", FosUtil.buildReferenceMonth("2026", "12"));
  }

  @Test
  void shouldParseReferenceMonthFilter() {
    assertEquals("202604", FosUtil.parseReferenceMonth("2026-04"));
    assertEquals("202604", FosUtil.parseReferenceMonth("202604"));
    assertNull(FosUtil.parseReferenceMonth(" "));
    assertThrows(IgrpResponseStatusException.class, () -> FosUtil.parseReferenceMonth("04-2026x"));
    assertThrows(IgrpResponseStatusException.class, () -> FosUtil.parseReferenceMonth("2026-13"));
  }

  @Test
  void shouldDropTrailingDecimalZeroFromStoredAmounts() {
    assertEquals("57003", FosUtil.formatAmount("57003.0"));
    assertEquals("57003.5", FosUtil.formatAmount("57003.50"));
    assertEquals("abc", FosUtil.formatAmount("abc"));
    assertNull(FosUtil.formatAmount(null));
  }

  @Test
  void shouldTranslateOracleErrors() {
    assertEquals("Mês sem processamento",
        FosUtil.mensagemErroOracle("ORA-20001: Mês sem processamento\nORA-06512: at line 1"));
    assertEquals(
        "Não foi possível gerar a FOS: há colaboradores com o Nº de Segurado maior do que o permitido (10 caracteres).",
        FosUtil.mensagemErroOracle(
            "ORA-12899: value too large for column \"INPSRH\".\"RH_DET_XML_FOS\".\"NU_SEGURADO\" (actual: 12, maximum: 10)"));
    assertEquals("Não foi possível gerar a FOS: erro na base de dados.",
        FosUtil.mensagemErroOracle("ORA-00942: table or view does not exist"));
  }
}
