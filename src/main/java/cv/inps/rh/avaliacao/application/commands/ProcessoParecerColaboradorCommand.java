package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.avaliacao.application.dto.ParecerColaboradorDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoParecerColaboradorCommand implements Command {

  
  private ParecerColaboradorDTO parecercolaborador;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}