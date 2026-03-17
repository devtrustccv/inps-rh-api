package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComponenteAvaliacaoCommand implements Command {

  
  private ComponenteAvaliacaoRequestDTO componenteavaliacaorequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}