package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssociarVinculoSituacaoCommand implements Command {

  @NotBlank(message = "The field <vinculoId> is required")
  private String vinculoId;
  @NotBlank(message = "The field <situacaoId> is required")
  private String situacaoId;

}
