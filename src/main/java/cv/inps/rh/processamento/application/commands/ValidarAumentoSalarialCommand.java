package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarAumentoSalarialCommand implements Command {

  @NotBlank(message = "The field <aumentoSalarialId> is required")
  private String aumentoSalarialId;

}
