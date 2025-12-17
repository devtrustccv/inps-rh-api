package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProcessoDisciplinarCommand implements Command {

  @NotBlank(message = "The field <processoDisciplinarId> is required")
  private String processoDisciplinarId;

}
