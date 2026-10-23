package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaFosQuery implements Query {

  private String dataInicio;
  private String dataFim;
  @NotBlank(message = "The field <page> is required")
  private String page;
  @NotBlank(message = "The field <size> is required")
  private String size;
  /** Filtro opcional Mês Referência (AAAAMM). */
  private String mesReferencia;

}
