package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmarProgressaoCommand implements Command {

  @NotBlank(message = "The field <validacaoId> is required")
  private String validacaoId;

}
