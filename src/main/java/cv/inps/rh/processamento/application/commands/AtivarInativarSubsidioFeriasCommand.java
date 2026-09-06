package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtivarInativarSubsidioFeriasCommand implements Command {

  @NotNull(message = "The field <subsidioId> is required")
  private Long subsidioId;
}
