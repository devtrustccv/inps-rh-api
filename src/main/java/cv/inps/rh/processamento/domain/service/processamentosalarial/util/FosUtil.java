package cv.inps.rh.processamento.domain.service.processamentosalarial.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.math.BigDecimal;
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

  /**
   * Mês de referência YYYYMM a partir das colunas RH_XML_FOS.ANO / MES (o MES pode vir sem zero à esquerda).
   */
  public static String buildReferenceMonth(String ano, String mes) {
    return buildReferenceMonth(getReferenceDate(Integer.parseInt(ano.trim()), Integer.parseInt(mes.trim())));
  }

  /**
   * Aceita o mês de referência do filtro em YYYYMM ou YYYY-MM e devolve YYYYMM; null se vazio.
   */
  public static String parseReferenceMonth(String mesReferencia) {
    if (mesReferencia == null || mesReferencia.isBlank())
      return null;

    var digits = mesReferencia.replace("-", "").replace("/", "").trim();
    if (!digits.matches("\\d{6}"))
      throw IgrpResponseStatusException.badRequest("Mês de Referência inválido. Use o formato AAAA-MM");

    return buildReferenceMonth(getReferenceDate(Integer.parseInt(digits.substring(0, 4)), Integer.parseInt(digits.substring(4))));
  }

  /**
   * Valores monetários gravados como texto: tira o ".0" que o Double deixa (ex.: "57003.0" -> "57003").
   */
  public static String formatAmount(String amount) {
    if (amount == null || amount.isBlank())
      return amount;
    try {
      return new BigDecimal(amount.trim()).stripTrailingZeros().toPlainString();
    } catch (NumberFormatException _) {
      return amount;
    }
  }

  public static String normalizeIdRow(Long detailId) {
    return detailId == null ? null : String.valueOf(detailId);
  }

  public static void validateDeliveryDate(LocalDate deliveryDate) {
    if (deliveryDate != null)
      throw IgrpResponseStatusException.badRequest("Já existe uma declaração entregue!");
  }
}
