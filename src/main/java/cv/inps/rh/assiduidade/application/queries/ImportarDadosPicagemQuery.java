package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportarDadosPicagemQuery implements Query {

  @NotBlank(message = "The field dataInicio is required")
  private String dataInicio;

  @NotBlank(message = "The field dataFim is required")
  private String dataFim;

}
