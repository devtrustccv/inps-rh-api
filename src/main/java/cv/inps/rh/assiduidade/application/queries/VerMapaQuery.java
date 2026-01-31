package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerMapaQuery implements Query {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotNull(message = "The field <direcaoId> is required")
  private Long direcaoId;
  @NotBlank(message = "The field <funcionarioUuid> is required")
  private String funcionarioUuid;

}