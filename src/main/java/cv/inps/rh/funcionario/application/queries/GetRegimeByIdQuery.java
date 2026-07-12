package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRegimeByIdQuery implements Query {

  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;
  @NotBlank(message = "The field <regimeId> is required")
  private String regimeId;

}
