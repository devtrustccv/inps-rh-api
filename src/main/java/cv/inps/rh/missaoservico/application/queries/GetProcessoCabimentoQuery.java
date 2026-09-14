package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Linhas do processo para os ecrãs Cabimentação e Autorização. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessoCabimentoQuery implements Query {

  @NotBlank(message = "The field <uuid> is required")
  private String uuid;
  @NotBlank(message = "The field <tipoProcesso> is required")
  private String tipoProcesso;

}
