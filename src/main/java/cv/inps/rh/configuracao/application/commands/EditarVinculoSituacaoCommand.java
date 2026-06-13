package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarVinculoSituacaoCommand implements Command {

  @NotNull(message = "The field <id> is required")
  private Long id;

  @NotNull(message = "The field <vinculoId> is required")
  private String vinculoId;

  @NotNull(message = "The field <situacaoId> is required")
  private String situacaoId;

}
