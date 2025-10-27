package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ParamEscalao {

  private Long id;
  private IdentificadorUnico uuid;
  private String codigo;
  private ParamCarreira paramCarreira;
  private ParamCategoria paramCategoria;
  private Integer nivelReferencia;
  private String escalao;
  private BigDecimal valor;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Estado estado;

  private ParamEscalao(
      Long id,
      IdentificadorUnico uuid,
      String codigo,
      ParamCarreira paramCarreira,
      ParamCategoria paramCategoria,
      Integer nivelReferencia,
      String escalao,
      BigDecimal valor,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.codigo = codigo;
    this.paramCarreira = paramCarreira;
    this.paramCategoria = paramCategoria;
    this.nivelReferencia = nivelReferencia;
    this.escalao = escalao;
    this.valor = valor;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.estado = estado;
  }

  public static ParamEscalao create(
      String codigo,
      ParamCarreira paramCarreira,
      ParamCategoria paramCategoria,
      Integer nivelReferencia,
      String escalao,
      BigDecimal valor,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado
  ) {
    return new ParamEscalao(
        null,
        IdentificadorUnico.create(),
        codigo,
        paramCarreira,
        paramCategoria,
        nivelReferencia,
        escalao,
        valor,
        dataInicio,
        dataFim,
        estado
    );
  }

  public static ParamEscalao rebuild(
      Long id,
      java.util.UUID uuid,
      String codigo,
      ParamCarreira paramCarreira,
      ParamCategoria paramCategoria,
      Integer nivelReferencia,
      String escalao,
      BigDecimal valor,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado
  ) {
    return new ParamEscalao(
        id,
        IdentificadorUnico.from(uuid),
        codigo,
        paramCarreira,
        paramCategoria,
        nivelReferencia,
        escalao,
        valor,
        dataInicio,
        dataFim,
        estado
    );
  }

  public void update(
      String codigo,
      Integer nivelReferencia,
      String escalao,
      BigDecimal valor,
      LocalDate dataInicio,
      LocalDate dataFim
  ) {
    this.codigo = codigo;
    this.nivelReferencia = nivelReferencia;
    this.escalao = escalao;
    this.valor = valor;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
  }
}
