package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFuncioarioSaldoFeriasQuery implements Query {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}