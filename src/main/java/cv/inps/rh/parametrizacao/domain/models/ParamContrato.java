package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import lombok.Getter;

@Getter
public class ParamContrato {

  private Long id;
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
