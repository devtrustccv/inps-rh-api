package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JustificarFaltaCommand implements Command {


  private JustificarFaltaDTO justificarfalta;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}
