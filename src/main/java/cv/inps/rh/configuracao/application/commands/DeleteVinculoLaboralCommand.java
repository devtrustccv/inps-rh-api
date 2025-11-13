package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteVinculoLaboralCommand implements Command {

  @NotBlank(message = "The field <vinculoLaboralId> is required")
  private String vinculoLaboralId;

}