package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Banco;
import cv.inps.rh.shared.domain.models.Entidade;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DadosBancarios {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Banco banco;
  private Long numConta;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Estado estado;
  private String observacoes;

  private DadosBancarios(
      Long id,
      IdentificadorUnico uuid,
      Banco banco,
      Long numConta,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado,
      String observacoes
  ) {
    this.id = id;
    this.uuid = uuid;
    this.banco = banco;
    this.numConta = numConta;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.estado = estado;
    this.observacoes = observacoes;
  }

  // Factory para criar novo registro
  public static DadosBancarios create(
      Long id,
      Banco banco,
      Long numConta,
      LocalDate dataInicio,
      LocalDate dataFim,
      String observacoes
  ) {
    return new DadosBancarios(
        id != null && id > 0 ? id : null,
        IdentificadorUnico.create(),
        banco,
        numConta,
        dataInicio,
        dataFim,
        Estado.A,
        observacoes
    );
  }

  // Reconstrução para repositório
  public static DadosBancarios rebuild(
      Long id,
      java.util.UUID uuid,
      Banco banco,
      Long numConta,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado,
      String observacoes
  ) {
    return new DadosBancarios(
        id,
        IdentificadorUnico.from(uuid),
        banco,
        numConta,
        dataInicio,
        dataFim,
        estado,
        observacoes
    );
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  // Update parcial
  public void update(
      Banco banco,
      Long numConta,
      LocalDate dataInicio,
      LocalDate dataFim,
      String observacoes
  ) {
    if (banco != null) this.banco = banco;
    if (numConta != null) this.numConta = numConta;
    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;
    if (observacoes != null) this.observacoes = observacoes;
  }

}
