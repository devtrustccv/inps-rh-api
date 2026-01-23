package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificarFaltaCommand implements Command {

  
  private JustificarFaltaDTO justificarfalta;
  @NotBlank(message = "The field <faltaId> is required")
  private String faltaId;

}