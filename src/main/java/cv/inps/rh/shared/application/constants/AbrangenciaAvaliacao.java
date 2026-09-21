package cv.inps.rh.shared.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;

/**
 * Abrangência de uma avaliação — espelha o domínio {@link Domains#ABRANGENCIA_AVD}.
 *
 * <p>Determina o preenchimento de {@code RH_T_AVD.FUN_ID} e {@code INSTIT_ID}:</p>
 * <ul>
 *   <li>{@link #INPS} — objectivos comuns a toda a instituição: ambos a {@code null}</li>
 *   <li>{@link #DIRECAO} — comuns a uma direção: {@code FUN_ID} nulo, {@code INSTIT_ID} preenchido</li>
 *   <li>{@link #INDIVIDUAL} — por colaborador: ambos preenchidos</li>
 * </ul>
 */
public enum AbrangenciaAvaliacao {

  INPS,
  DIRECAO,
  INDIVIDUAL;

  public boolean exigeColaborador() {
    return this == INDIVIDUAL;
  }

  public boolean exigeDirecao() {
    return this == DIRECAO;
  }

  public static Optional<AbrangenciaAvaliacao> fromValor(String valor) {
    if (valor == null) {
      return Optional.empty();
    }
    var chave = valor.trim().toUpperCase();
    return Arrays.stream(values()).filter(v -> v.name().equals(chave)).findFirst();
  }

  public static AbrangenciaAvaliacao fromValorOrThrow(String valor) {
    return fromValor(valor).orElseThrow(() -> IgrpResponseStatusException.of(
        HttpStatus.BAD_REQUEST,
        "Abrangência inválida: " + valor + ". Valores aceites: "
            + Arrays.stream(values()).map(Enum::name).toList()));
  }
}
