package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetResumoProcessamentoQuery implements Query {

  @NotBlank(message = "The field <processamentoId> is required")
  private String processamentoId;
  @NotBlank(message = "The field <ccId> is required")
  private String ccId;
  @NotBlank(message = "The field <ano> is required")
  private String ano;
  @NotBlank(message = "The field <mes> is required")
  private String mes;

}
