package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.FeriadoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveFeriadosCommand implements Command {


  private FeriadoDTO feriado;

}
