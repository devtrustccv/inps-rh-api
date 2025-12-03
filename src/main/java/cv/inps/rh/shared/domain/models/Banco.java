package cv.inps.rh.shared.domain.models;

import lombok.Getter;

@Getter
public class Banco {

  private final Long id;
  private String codigoBanco;
  private String sigla;
  private String nomeBanco;
  private Long numeroConta;
  private Long entId;
  private String nib;
  private Long tmId;

  private Banco(Long id, String codigoBanco, String sigla, String nomeBanco,
                Long numeroConta, Long entId, String nib, Long tmId) {
    this.id = id;
    this.codigoBanco = codigoBanco;
    this.sigla = sigla;
    this.nomeBanco = nomeBanco;
    this.numeroConta = numeroConta;
    this.entId = entId;
    this.nib = nib;
    this.tmId = tmId;
  }

  private Banco(Long id) {
    this.id = id;
  }

  public static Banco rebuild(Long id,
                       String codigoBanco,
                       String sigla,
                       String nomeBanco,
                       Long numeroConta,
                       Long entId,
                       String nib,
                       Long tmId) {
    return new Banco(id, codigoBanco, sigla, nomeBanco, numeroConta, entId, nib, tmId);
  }

  public static Banco rebuild(Long id) {
    return new Banco(id);
  }

}
