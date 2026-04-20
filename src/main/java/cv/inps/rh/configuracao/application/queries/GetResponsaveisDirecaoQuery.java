package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetResponsaveisDirecaoQuery implements Query {

  @NotBlank(message = "The field <seccaoId> is required")
  private String seccaoId;
  @NotBlank(message = "The field <institutoId> is required")
  private String institutoId;

}
