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
}
