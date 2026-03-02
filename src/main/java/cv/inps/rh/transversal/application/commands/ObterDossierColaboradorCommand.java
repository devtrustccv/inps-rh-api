package cv.inps.rh.transversal.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.transversal.application.dto.DossierRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObterDossierColaboradorCommand implements Command {

  
  private DossierRequestDTO dossierrequest;

}