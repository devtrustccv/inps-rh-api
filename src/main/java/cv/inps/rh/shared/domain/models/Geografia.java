package cv.inps.rh.shared.domain.models;

import lombok.Getter;

@Getter
public class Geografia {

  private final Long id;
  private final String nome;
  private final String nacionalidade;
  private final Long geografiaPai;
  private final Long pais;
  private final Long nivelDetalhe;
  private final String nomeOficial;
  private final String flagAlter;
  private final String nomeNorm;
  private final String tipoGeografia;
  private final String situacao;

  private Geografia(
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
    this.id = id;
    this.nome = nome;
    this.nacionalidade = nacionalidade;
    this.geografiaPai = geografiaPai;
    this.pais = pais;
    this.nivelDetalhe = nivelDetalhe;
    this.nomeOficial = nomeOficial;
    this.flagAlter = flagAlter;
    this.nomeNorm = nomeNorm;
    this.tipoGeografia = tipoGeografia;
    this.situacao = situacao;
  }


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
