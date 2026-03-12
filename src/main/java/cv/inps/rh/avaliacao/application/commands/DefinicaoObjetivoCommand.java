package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.avaliacao.application.dto.DefinicaoObjectivoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefinicaoObjetivoCommand implements Command {

  
  private DefinicaoObjectivoDTO definicaoobjectivo;

}