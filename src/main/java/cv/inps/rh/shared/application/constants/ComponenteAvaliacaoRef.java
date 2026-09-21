package cv.inps.rh.shared.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;

/**
 * Valor de {@code RH_T_AVD_PERIODICIDADE.REFERENCIA} — diz a que componente pertence a
 * medição, e portanto em que tabela procurar o {@code REFERENCIA_ID}.
 *
 * <p>Atenção ao plural em {@link #COMPETENCIA_COMPORTAMENTAIS}: é assim que a spec o
 * escreve, e as competências comportamentais e técnicas vivem ambas em
 * RH_T_AVD_COMPETENCIA — é a referência que as distingue.</p>
 */
public enum ComponenteAvaliacaoRef {

  OBJECTIVO,
  COMPETENCIA_COMPORTAMENTAIS,
  COMPETENCIA_TECNICA,
  ATITUDE_PESSOAL;

  /** O valor de {@code COMPONENTE} em RH_T_PARAM_OBJETIVO que alimenta esta componente. */
  public String componenteParametrizacao() {
    return switch (this) {
      case OBJECTIVO -> "OBJETIVO";
      case COMPETENCIA_COMPORTAMENTAIS -> "COMPETENCIA_COMPORTAMENTAL";
      case COMPETENCIA_TECNICA -> "COMPETENCIA_TECNICA";
      case ATITUDE_PESSOAL -> "ATITUDE_PESSOAL";
    };
  }

  public static Optional<ComponenteAvaliacaoRef> fromValor(String valor) {
    if (valor == null) {
      return Optional.empty();
    }
    var chave = valor.trim().toUpperCase();
    return Arrays.stream(values()).filter(v -> v.name().equals(chave)).findFirst();
  }

  public static ComponenteAvaliacaoRef fromValorOrThrow(String valor) {
    return fromValor(valor).orElseThrow(() -> IgrpResponseStatusException.of(
        HttpStatus.BAD_REQUEST, "Componente de avaliação inválida: " + valor));
  }
}
