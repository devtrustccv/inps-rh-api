package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarDispensaCommand implements Command {


  private DispensaReqDTO dispensareq;

}
