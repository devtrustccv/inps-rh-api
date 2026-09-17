package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Avaliações dos prestadores de toda a missão — ecrã "Detalhe de Avaliação". */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMissaoAvaliacoesQuery implements Query {

  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
