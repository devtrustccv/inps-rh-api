package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComponenteAvaliacaoCommand implements Command {


  private ComponenteAvaliacaoRequestDTO componenteavaliacaorequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
