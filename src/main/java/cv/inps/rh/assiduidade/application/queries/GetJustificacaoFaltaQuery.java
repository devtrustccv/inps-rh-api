package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetJustificacaoFaltaQuery implements Query {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotNull(message = "The field <mes> is required")
  private Integer mes;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}