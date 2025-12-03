package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ParamVinculo {

  private final Long id;
  private IdentificadorUnico uuid;
  private String codigo;
  private String nome;
  private Integer flgCarreira;
  private Integer flgSalario;
  private Integer flgContrato;
  private Integer flgTempoServico;
  private Estado estado;

  private ParamVinculo(
      Long id,
      IdentificadorUnico uuid,
      String codigo,
      String nome,
      Integer flgCarreira,
      Integer flgSalario,
      Integer flgContrato,
      Integer flgTempoServico,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.codigo = codigo;
    this.nome = nome;
    this.flgCarreira = flgCarreira;
    this.flgSalario = flgSalario;
    this.flgContrato = flgContrato;
    this.flgTempoServico = flgTempoServico;
    this.estado = estado;
  }

  private ParamVinculo(Long id) {
    this.id = id;
  }


  public static ParamVinculo create(
      String codigo,
      String nome,
      Integer flgCarreira,
      Integer flgSalario,
      Integer flgContrato,
      Integer flgTempoServico,
      Estado estado
  ) {
    return new ParamVinculo(null, IdentificadorUnico.create(), codigo, nome, flgCarreira, flgSalario, flgContrato, flgTempoServico, estado);
  }

  public static ParamVinculo rebuild(
      Long id,
      UUID uuid,
      String codigo,
      String nome,
      Integer flgCarreira,
      Integer flgSalario,
      Integer flgContrato,
      Integer flgTempoServico,
      Estado estado
  ) {
    return new ParamVinculo(id, IdentificadorUnico.from(uuid), codigo, nome, flgCarreira, flgSalario, flgContrato, flgTempoServico, estado);
  }

  public static ParamVinculo rebuild(Long id) {
    return new ParamVinculo(id);
  }

  public void update(
      String codigo,
      String nome,
      Integer flgCarreira,
      Integer flgSalario,
      Integer flgContrato,
      Integer flgTempoServico,
      Estado estado
  ) {
    this.codigo = codigo;
    this.nome = nome;
    this.flgCarreira = flgCarreira;
    this.flgSalario = flgSalario;
    this.flgContrato = flgContrato;
    this.flgTempoServico = flgTempoServico;
    this.estado = estado;
  }
}
