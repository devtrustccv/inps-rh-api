package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovoSeguradoCommand implements Command {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotNull(message = "The field <mes> is required")
  private Integer mes;

}
