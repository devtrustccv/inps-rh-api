package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaSubstituicaoQuery implements Query {

  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotBlank(message = "The field <idFuncionario> is required")
  private String idFuncionario;

}
