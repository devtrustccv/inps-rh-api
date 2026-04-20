package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdicionarFuncionarioCommand implements Command {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotNull(message = "The field <mes> is required")
  private Integer mes;
  @NotNull(message = "The field <fosId> is required")
  private Long fosId;
  @NotNull(message = "The field <numeroSegurado> is required")
  private Long numeroSegurado;

}
