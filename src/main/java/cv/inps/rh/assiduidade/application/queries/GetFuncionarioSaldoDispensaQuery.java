package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFuncionarioSaldoDispensaQuery implements Query {

  @NotBlank(message = "The field <data> is required")
  private String data;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}