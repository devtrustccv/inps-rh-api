package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.avaliacao.application.dto.ObservacaoGeralDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoObservacaoGeralCommand implements Command {

  
  private ObservacaoGeralDTO observacaogeral;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}