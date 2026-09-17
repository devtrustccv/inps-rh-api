package cv.inps.rh.missaoservico.application.constants;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;

import java.util.Arrays;

/** Domínio PARECER. */
public enum Parecer {

  FAVORAVEL("Favorável"),
  DESFAVORAVEL("Desfavorável");

  private final String descricao;

  Parecer(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  /** Descrição do código gravado; devolve o próprio código se for desconhecido. */
  public static String descricaoDe(String code) {
    return Arrays.stream(values())
        .filter(p -> p.name().equals(code))
        .map(Parecer::getDescricao)
        .findFirst()
        .orElse(code);
  }

  public static Parecer fromCodeOrThrow(String code) {
    return Arrays.stream(values())
        .filter(p -> p.name().equals(code))
        .findFirst()
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Parecer inválido: " + code));
  }
}
