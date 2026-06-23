package cv.inps.rh.shared.application.constants.custom;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public enum TipoMobilidade {
  INICIO("Início"),
  LOCAL_TRABALHO("Local Trabalho"),
  DIRECAO("Direção"),
  SECAO("Seção"),
  NOVO_CONTRATO("Novo Contrato"),
  RENOVACAO("Renovação");

  private final String descricao;

  TipoMobilidade(String descricao) {
    this.descricao = descricao;
  }

  public static Optional<TipoMobilidade> fromString(String value) {
    if (value == null || value.isBlank()) return Optional.empty();
    try {
      return Optional.of(TipoMobilidade.valueOf(value.trim().toUpperCase()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public static String traduzir(String valor) {
    if (valor == null || valor.isBlank()) return null;
    return Arrays.stream(valor.split(","))
        .map(String::trim)
        .map(v -> fromString(v).map(TipoMobilidade::getDescricao).orElse(v))
        .collect(Collectors.joining(", "));
  }
}
