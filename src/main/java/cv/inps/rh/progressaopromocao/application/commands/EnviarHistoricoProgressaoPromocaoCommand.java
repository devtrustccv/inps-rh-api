package cv.inps.rh.progressaopromocao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.progressaopromocao.application.dto.HistoricoIdsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarHistoricoProgressaoPromocaoCommand implements Command {


  private HistoricoIdsDTO historicoids;

}
