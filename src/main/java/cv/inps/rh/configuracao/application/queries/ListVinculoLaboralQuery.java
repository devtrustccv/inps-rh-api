package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListVinculoLaboralQuery implements Query {

  @NotBlank(message = "The field <pagina> is required")
  private String pagina;
  @NotBlank(message = "The field <tamanho> is required")
  private String tamanho;

}