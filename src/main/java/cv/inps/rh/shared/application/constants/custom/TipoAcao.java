package cv.inps.rh.shared.application.constants.custom;

import io.prometheus.client.SimpleCollector;

import java.util.Optional;

public enum TipoAcao {
  INSERT("Registo"),
  UPDATE("Atualização"),
  DELETE("Eliminação");


  private final String descricao;

  TipoAcao(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  public static Optional<TipoAcao> fromString(String value) {
    if (value == null || value.isBlank()) {
      return Optional.empty();
    }

    try {
      return Optional.of(TipoAcao.valueOf(value.trim().toUpperCase()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
