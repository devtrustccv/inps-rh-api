package cv.inps.rh.shared.domain.models;

import lombok.Getter;

@Getter
public class Instituicao {

  private Long id;
  private String nome;
  private String codigo;

  private Instituicao(Long id, String nome, String codigo) {
    this.id = id;
    this.nome = nome;
    this.codigo = codigo;
  }

  public static Instituicao rebuild(Long id, String nome, String codigo) {
    return new Instituicao(id,  nome, codigo);
  }

}
