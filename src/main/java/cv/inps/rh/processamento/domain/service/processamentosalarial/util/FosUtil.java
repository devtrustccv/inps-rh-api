package cv.inps.rh.processamento.domain.service.processamentosalarial.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;

public final class FosUtil {

  private FosUtil() {
  }

  public static LocalDate getReferenceDate(Integer ano, Integer mes) {

    if (mes == null || mes < 1 || mes > 12)
      throw IgrpResponseStatusException.badRequest("Mês inválido. Deve estar entre 1 e 12");

    return YearMonth.of(ano, mes).atDay(1);
  }

  public static String buildReferenceMonth(LocalDate referenceDate) {
    return "%04d%02d".formatted(referenceDate.getYear(), referenceDate.getMonthValue());
  }

  public static String normalizeIdRow(Long detailId) {
    return detailId == null ? null : String.valueOf(detailId);
  }

  public static void validateDeliveryDate(LocalDate deliveryDate) {
    if (deliveryDate != null)
      throw IgrpResponseStatusException.badRequest("Já existe uma declaração entregue!");
  }
}
