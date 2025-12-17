package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetContratoByIdQuery implements Query {

  @NotBlank(message = "The field <contratoId> is required")
  private String contratoId;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
