package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRowDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEscalaAvaliacaoCommand implements Command {

  
  private EscalaAvaliacaoRowDTO escalaavaliacaorow;
  @NotBlank(message = "The field <id> is required")
  private String id;

}