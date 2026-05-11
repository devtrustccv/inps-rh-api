package cv.inps.rh.transversal.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.transversal.application.dto.DossierRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObterDossierColaboradorCommand implements Command {


  private DossierRequestDTO dossierrequest;

}
