package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.avaliacao.application.dto.ComissaoExecutivaDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoComissaoExecutivaCommand implements Command {

  
  private ComissaoExecutivaDTO comissaoexecutiva;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}