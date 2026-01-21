package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarHoraExtraCommand implements Command {

  
  private HoraExtraReqDTO horaextrareq;

}