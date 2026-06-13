package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class EliminarVinculoMovimentoCommand implements Command {

  @NotNull(message = "The field <vinculoId> is required")
  private Long vinculoId;

  @NotNull(message = "The field <id> is required")
  private Long id;

}
