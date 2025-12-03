package cv.inps.rh.shared.domain.models;

public record Geografia(Long id, String nome, String nacionalidade, Long geografiaPai, Long pais,
                        Long nivelDetalhe, String nomeOficial, String flagAlter, String nomeNorm,
                        String tipoGeografia, String situacao) {


  public static Geografia rebuild(
      Long id,
      String nome,
      String nacionalidade,
      Long geografiaPai,
      Long pais,
      Long nivelDetalhe,
      String nomeOficial,
      String flagAlter,
      String nomeNorm,
      String tipoGeografia,
      String situacao
  ) {
    return new Geografia(
        id,
        nome,
        nacionalidade,
        geografiaPai,
        pais,
        nivelDetalhe,
        nomeOficial,
        flagAlter,
        nomeNorm,
        tipoGeografia,
        situacao
    );
  }
}
