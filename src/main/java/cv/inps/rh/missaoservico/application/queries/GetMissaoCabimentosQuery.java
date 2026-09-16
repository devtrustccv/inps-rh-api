package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cabimentos de toda a missão — vista de consulta, com as linhas dos quatro processos. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMissaoCabimentosQuery implements Query {

  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
