package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class FormacaoFeita {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Geografia pais;
  private String estabelecimento;
  private String tipoFormacao; // rhtpfor
  private String curso;
  private String nivel;
  private Estado estado;

  private FormacaoFeita(
      Long id,
      IdentificadorUnico uuid,
      Geografia pais,
      String estabelecimento,
      String tipoFormacao,
      String curso,
      String nivel,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.pais = pais;
    this.estabelecimento = estabelecimento;
    this.tipoFormacao = tipoFormacao;
    this.curso = curso;
    this.nivel = nivel;
    this.estado = estado;
  }

  // factory para criar nova formação
  public static FormacaoFeita create(
      Long id,
      Geografia pais,
      String estabelecimento,
      String tipoFormacao,
      String curso,
      String nivel
  ) {
    return new FormacaoFeita(
        id != null && id > 0 ? id : null,
        IdentificadorUnico.create(),
        pais,
        estabelecimento,
        tipoFormacao,
        curso,
        nivel,
        Estado.P
    );
  }

  // reconstrução para repositório
  public static FormacaoFeita rebuild(
      Long id,
      java.util.UUID uuid,
      Geografia pais,
      String estabelecimento,
      String tipoFormacao,
      String curso,
      String nivel,
      Estado estado
  ) {
    return new FormacaoFeita(
        id,
        IdentificadorUnico.from(uuid),
        pais,
        estabelecimento,
        tipoFormacao,
        curso,
        nivel,
        estado
    );
  }

  public void eliminar() {
    this.estado = Estado.E;
  }

  public void update(
      Geografia pais,
      String estabelecimento,
      String tipoFormacao,
      String curso,
      String nivel
  ) {
    if (pais != null) this.pais = pais;
    if (estabelecimento != null) this.estabelecimento = estabelecimento;
    if (tipoFormacao != null) this.tipoFormacao = tipoFormacao;
    if (curso != null) this.curso = curso;
    if (nivel != null) this.nivel = nivel;
  }
}
