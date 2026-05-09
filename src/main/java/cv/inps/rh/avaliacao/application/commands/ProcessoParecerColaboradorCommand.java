package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.avaliacao.application.dto.ParecerColaboradorDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoParecerColaboradorCommand implements Command {


  private ParecerColaboradorDTO parecercolaborador;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
