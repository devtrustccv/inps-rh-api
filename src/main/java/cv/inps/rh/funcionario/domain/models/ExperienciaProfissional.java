package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ExperienciaProfissional {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Geografia pais;
  private String empresa;
  private String cargo;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private String observacao;
  private Estado estado;

  private ExperienciaProfissional(
      Long id,
      IdentificadorUnico uuid,
      Geografia pais,
      String empresa,
      String cargo,
      LocalDate dataInicio,
      LocalDate dataFim,
      String observacao,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.pais = pais;
    this.empresa = empresa;
    this.cargo = cargo;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.observacao = observacao;
    this.estado = estado;
  }

  // Factory para criar nova experiência
  public static ExperienciaProfissional create(
      Long id,
      Geografia pais,
      String empresa,
      String cargo,
      LocalDate dataInicio,
      LocalDate dataFim,
      String observacao
  ) {
    return new ExperienciaProfissional(
        id != null && id > 0 ? id : null,
        IdentificadorUnico.create(),
        pais,
        empresa,
        cargo,
        dataInicio,
        dataFim,
        observacao,
        Estado.A
    );
  }

  // Factory para reconstrução do repositório
  public static ExperienciaProfissional rebuild(
      Long id,
      java.util.UUID uuid,
      Geografia pais,
      String empresa,
      String cargo,
      LocalDate dataInicio,
      LocalDate dataFim,
      String observacao,
      Estado estado
  ) {
    return new ExperienciaProfissional(
        id,
        IdentificadorUnico.from(uuid),
        pais,
        empresa,
        cargo,
        dataInicio,
        dataFim,
        observacao,
        estado
    );
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  // Update parcial
  public void update(
      Geografia pais,
      String empresa,
      String cargo,
      LocalDate dataInicio,
      LocalDate dataFim,
      String observacao
  ) {
    if (pais != null) this.pais = pais;
    if (empresa != null) this.empresa = empresa;
    if (cargo != null) this.cargo = cargo;
    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;
    if (observacao != null) this.observacao = observacao;
  }
}
