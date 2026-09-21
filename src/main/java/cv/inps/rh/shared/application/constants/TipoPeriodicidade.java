package cv.inps.rh.shared.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Tipo de periodicidade do ciclo de avaliação de desempenho.
 *
 * <p>Espelha as linhas do domínio {@link Domains#PERIODICIDADE} em RH_T_DOMAINS com
 * {@code REFERENCIA = 'PERIODICIDADE'}. É o que se grava em
 * {@code RH_T_PARAM_OBJETIVO_DET.PERIODICIDADE} e define quantos períodos o ano tem.</p>
 *
 * <p>Os <em>períodos</em> concretos ({@code SEMESTRE1}, {@code TRIMESTRE1}, ...) vivem no
 * mesmo domínio mas com {@code REFERENCIA = <tipo>}, e são o que se grava em
 * {@code RH_T_AVD_DETALHE.PERIODICIDADE} e {@code RH_T_AVD_PERIODICIDADE.PERIODICIDADE}.
 * O peso de cada período na nota do ano está em {@link Domains#AVD_PONDERACAO_FINAL},
 * indexado pela mesma referência.</p>
 */
public enum TipoPeriodicidade {

  SEMESTRAL("SEMESTRE", 2),
  TRIMESTRAL("TRIMESTRE", 4),
  ANUAL(null, 1);

  /** Prefixo dos períodos deste tipo; {@code null} quando o período é o próprio tipo. */
  private final String prefixoPeriodo;

  /** Quantos períodos o ano tem neste tipo. */
  private final int numeroPeriodos;

  TipoPeriodicidade(String prefixoPeriodo, int numeroPeriodos) {
    this.prefixoPeriodo = prefixoPeriodo;
    this.numeroPeriodos = numeroPeriodos;
  }

  public int getNumeroPeriodos() {
    return numeroPeriodos;
  }

  /**
   * Os códigos dos períodos deste tipo, pela ordem cronológica — exactamente como estão
   * gravados no domínio (ex.: SEMESTRAL → {@code [SEMESTRE1, SEMESTRE2]}; ANUAL → {@code [ANUAL]}).
   */
  public List<String> periodos() {
    if (prefixoPeriodo == null) {
      return List.of(name());
    }
    return java.util.stream.IntStream.rangeClosed(1, numeroPeriodos)
        .mapToObj(i -> prefixoPeriodo + i)
        .toList();
  }

  /** {@code true} se {@code periodo} é um período válido deste tipo. */
  public boolean aceita(String periodo) {
    if (periodo == null) {
      return false;
    }
    return periodos().contains(periodo.trim().toUpperCase());
  }

  public static Optional<TipoPeriodicidade> fromValor(String valor) {
    if (valor == null) {
      return Optional.empty();
    }
    var chave = valor.trim().toUpperCase();
    return Arrays.stream(values()).filter(v -> v.name().equals(chave)).findFirst();
  }

  public static TipoPeriodicidade fromValorOrThrow(String valor) {
    return fromValor(valor).orElseThrow(() -> IgrpResponseStatusException.of(
        HttpStatus.BAD_REQUEST,
        "Periodicidade inválida: " + valor + ". Valores aceites: "
            + Arrays.stream(values()).map(Enum::name).toList()));
  }
}
