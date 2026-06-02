package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaBaixamedicaQuery implements Query {

  @NotBlank(message = "The field <dataInicio> is required")
  private String dataInicio;
  @NotBlank(message = "The field <dataFim> is required")
  private String dataFim;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;
  @NotNull(message = "The field <direccaoId> is required")
  private Long direccaoId;
  @NotNull(message = "The field <page> is required")
  private Integer page;
  @NotNull(message = "The field <size> is required")
  private Integer size;
  @NotNull(message = "The field <tipoAbonoBeneficioId> is required")
  private Long tipoAbonoBeneficioId;

}
