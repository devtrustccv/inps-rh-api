package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarDispensaCommand implements Command {

  
  private DispensaReqDTO dispensareq;

}