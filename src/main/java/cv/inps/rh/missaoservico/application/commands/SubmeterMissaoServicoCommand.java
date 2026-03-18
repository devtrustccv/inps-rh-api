package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmeterMissaoServicoCommand implements Command {

  
  private MissaoSubmissaoRequestDTO missaosubmissaorequest;

}