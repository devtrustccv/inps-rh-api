package cv.inps.rh.processamento.domain.service.processamentosalarial.util;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

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
    } catch (NumberFormatException e) {
      return amount;
    }
  }

  /**
   * Mensagem para o utilizador a partir do erro Oracle de um procedimento do FOS. A mensagem ORA crua
   * fica só no log.
   */
  public static String mensagemErroOracle(String oraMessage) {
    if (oraMessage == null)
      return "Não foi possível gerar a FOS.";

    // RAISE_APPLICATION_ERROR (ORA-20000..20999): a mensagem já é para o utilizador
    var applicationError = Pattern.compile("ORA-20\\d{3}:\\s*([^\\n]*)").matcher(oraMessage);
    if (applicationError.find())
      return applicationError.group(1).trim();

    if (oraMessage.contains("ORA-12899") || oraMessage.contains("ORA-06502"))
      return oraMessage.contains("NU_SEGURADO")
          ? "Não foi possível gerar a FOS: há colaboradores com o Nº de Segurado maior do que o permitido (10 caracteres)."
          : "Não foi possível gerar a FOS: há valores maiores do que o permitido (verifique o Nº de Segurado dos colaboradores).";

    if (oraMessage.contains("ORA-01400"))
      return "Não foi possível gerar a FOS: há colaboradores sem dados obrigatórios (ex.: Nº de Segurado).";

    return "Não foi possível gerar a FOS: erro na base de dados.";
  }

  public static String normalizeIdRow(Long detailId) {
    return detailId == null ? null : String.valueOf(detailId);
  }

  public static void validateDeliveryDate(LocalDate deliveryDate) {
    if (deliveryDate != null)
      throw IgrpResponseStatusException.badRequest("Já existe uma declaração entregue!");
  }
}
