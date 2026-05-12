package cv.inps.rh.processamento.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubsidioNatalStatus {

  ATIVAR("A"),
  INATIVAR("I");

  private final String code;
}
