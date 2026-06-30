package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDetalhesAumentoSalarialQuery implements Query {

  @NotBlank(message = "The field <aumentoSalarialId> is required")
  private String aumentoSalarialId;
}
