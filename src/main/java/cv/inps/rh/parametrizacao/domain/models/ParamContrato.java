package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class ParamContrato {

  private Long id;
  private IdentificadorUnico uuid;
  private String codigo;
  private String nome;
  private String natureza;
  private Integer flgRenovavel;
  private Integer duracaoRenovavel;
  private Integer prazoObrigatorio;
  private ParamVinculo paramVinculo;
  private Estado estado;

  private ParamContrato(
      Long id,
      IdentificadorUnico uuid,
      String codigo,
      String nome,
      String natureza,
      Integer flgRenovavel,
      Integer duracaoRenovavel,
      Integer prazoObrigatorio,
      ParamVinculo paramVinculo,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.codigo = codigo;
    this.nome = nome;
    this.natureza = natureza;
    this.flgRenovavel = flgRenovavel;
    this.duracaoRenovavel = duracaoRenovavel;
    this.prazoObrigatorio = prazoObrigatorio;
    this.paramVinculo = paramVinculo;
    this.estado = estado;
  }

  public static ParamContrato create(
      String codigo,
      String nome,
      String natureza,
      Integer flgRenovavel,
      Integer duracaoRenovavel,
      Integer prazoObrigatorio,
      ParamVinculo paramVinculo,
      Estado estado
  ) {
    return new ParamContrato(
        null,
        IdentificadorUnico.create(),
        codigo,
        nome,
        natureza,
        flgRenovavel,
        duracaoRenovavel,
        prazoObrigatorio,
        paramVinculo,
        estado
    );
  }

  public static ParamContrato rebuild(
      Long id,
      java.util.UUID uuid,
      String codigo,
      String nome,
      String natureza,
      Integer flgRenovavel,
      Integer duracaoRenovavel,
      Integer prazoObrigatorio,
      ParamVinculo paramVinculo,
      Estado estado
  ) {
    return new ParamContrato(
        id,
        IdentificadorUnico.from(uuid),
        codigo,
        nome,
        natureza,
        flgRenovavel,
        duracaoRenovavel,
        prazoObrigatorio,
        paramVinculo,
        estado
    );
  }

  public static ParamContrato rebuild(Long id) {
    return new ParamContrato(id, null, null, null, null, null, null, null, null, null);
  }

  public void update(
      String nome,
      String natureza,
      Integer flgRenovavel,
      Integer duracaoRenovavel,
      Integer prazoObrigatorio
  ) {
    this.nome = nome;
    this.natureza = natureza;
    this.flgRenovavel = flgRenovavel;
    this.duracaoRenovavel = duracaoRenovavel;
    this.prazoObrigatorio = prazoObrigatorio;
  }
}
